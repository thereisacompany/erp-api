package com.jsh.erp.service.depotHead;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jsh.erp.datasource.mappers.DepotItemMapperEx;
import com.jsh.erp.datasource.mappers.MaterialMapperEx;
import com.jsh.erp.datasource.vo.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.account.AccountService;
import com.jsh.erp.service.accountHead.AccountHeadService;
import com.jsh.erp.service.accountItem.AccountItemService;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotCounter.DepotCounterService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.materialExtend.MaterialExtendService;
import com.jsh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jsh.erp.service.person.PersonService;
import com.jsh.erp.service.role.RoleService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.serialNumber.SerialNumberService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.systemConfig.SystemConfigService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.service.userBusiness.UserBusinessService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ExcelUtils;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import jxl.Sheet;
import jxl.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.jsh.erp.utils.Tools.getCenternTime;
import static com.jsh.erp.utils.Tools.getNow3;

@Service
public class DepotHeadService {
    private Logger logger = LoggerFactory.getLogger(DepotHeadService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static DateTimeFormatter formatterChange = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static DateTimeFormatter formatterChangeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    private DepotHeadMapper depotHeadMapper;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private DepotService depotService;
    @Resource
    private DepotCounterService depotCounterService;
    @Resource
    DepotItemService depotItemService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private UserBusinessService userBusinessService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private OrgaUserRelService orgaUserRelService;
    @Resource
    private PersonService personService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountHeadService accountHeadService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private MaterialService materialService;
    @Resource
    private MaterialExtendService materialExtendService;
    @Resource
    private DepotItemMapperEx depotItemMapperEx;
    @Resource
    private MaterialMapperEx materialMapperEx;
    @Resource
    private LogService logService;

    public DepotHead getDepotHead(long id)throws Exception {
        DepotHead result=null;
        try{
            result=depotHeadMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHead()throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list=null;
        try{
            list=depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> select(String type, String subType, String roleType, String hasDebt, String status,
                                         String purchaseStatus, String number, String linkNumber, String beginTime,
                                         String endTime, String materialParam, String keyword, Long organId, String MNumber, Long creator,
                                         Long depotId, Long counterId, Long accountId, String remark, int offset, int rows) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String [] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String [] organArray = getOrganArray(subType, purchaseStatus);
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.selectByConditionDepotHead(type, subType, creatorArray, hasDebt, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                 materialParam, keyword, organId, organArray, MNumber, creator, depotId, counterId, depotArray, accountId, remark, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                //通过批量查询去构造map
                Map<String,BigDecimal> finishDepositMap = getFinishDepositMapByNumberList(numberList);
                Map<Long,Integer> financialBillNoMap = getFinancialBillNoMapByBillIdList(idList);
                Map<String,Integer> billSizeMap = getBillSizeMapByLinkNumberList(numberList);
                Map<String, MaterialsListVo> materialsListMap = findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
//                Map<Long,BigDecimal> materialCountListMap = getMaterialCountListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    String mKey = dh.getId()+""+dh.getSubId()+""+dh.getMNumber();

                    //客單編號
                    if(dh.getCustomNumber()!=null) {
                        dh.setCustomNumber(dh.getCustomNumber().split("-")[0]);
                    }
                    //原始客編
                    if(dh.getSourceNumber()!=null) {
                        dh.setSourceNumber(dh.getSourceNumber().split("-")[0]);
                    }

                    if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if(dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if(dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    //已经完成的欠款
                    if(finishDepositMap!=null) {
                        dh.setFinishDeposit(finishDepositMap.get(dh.getNumber()) != null ? finishDepositMap.get(dh.getNumber()) : BigDecimal.ZERO);
                    }
                    //欠款计算
                    BigDecimal discountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
                    BigDecimal otherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
                    BigDecimal changeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount():BigDecimal.ZERO;
                    dh.setDebt(discountLastMoney.add(otherMoney).subtract((dh.getDeposit().add(changeAmount))));
                    //是否有付款单或收款单
                    if(financialBillNoMap!=null) {
                        Integer financialBillNoSize = financialBillNoMap.get(dh.getId());
                        dh.setHasFinancialFlag(financialBillNoSize!=null && financialBillNoSize>0);
                    }
                    //是否有退款单
                    if(billSizeMap!=null) {
                        Integer billListSize = billSizeMap.get(dh.getNumber());
                        dh.setHasBackFlag(billListSize!=null && billListSize>0);
                    }
                    if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    //商品信息简述
                    if(materialsListMap!=null) {
                        MaterialsListVo vo = materialsListMap.get(mKey);
                        dh.setMaterialsList(vo.getMaterialsList());
                        dh.setMaterialCount(vo.getMaterialCount());
                    }
                    //商品总数量
//                    if(materialCountListMap!=null) {
//                        dh.setMaterialCount(materialCountListMap.get(dh.getId()));
//                    }
                    //以销定购的情况（不能显示销售单据的金额和客户名称）
                    if(StringUtil.isNotEmpty(purchaseStatus)) {
                        dh.setOrganName("****");
                        dh.setTotalPrice(null);
                        dh.setDiscountLastMoney(null);
                    }
                    String showId = String.format("%03d", dh.getOrganId());
                    dh.setOrganName(showId + " " + dh.getOrganName());

                    if(dh.getCounterName() == null) {
                        dh.setCounterName("");
                    }

                    // 配送單要額外處理remark
                    if(dh.getType().equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
                        if (dh.getRemark() != null && !dh.getRemark().isEmpty()) {
                            JSONObject json = JSONObject.parseObject(dh.getRemark());
                            dh.setInstall(json.getString("install"));
                            dh.setRecycle(json.getString("recycle"));
                            dh.setRemark(json.getString("memo"));
                        } else {
                            dh.setInstall("");
                            dh.setRecycle("");
                        }
                    }

                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countDepotHead(String type, String subType, String roleType, String hasDebt, String status, String purchaseStatus, String number, String linkNumber,
           String beginTime, String endTime, String materialParam, String keyword, Long organId, Long creator, Long depotId, Long accountId, String remark) throws Exception{
        Long result=null;
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String [] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String [] organArray = getOrganArray(subType, purchaseStatus);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            result=depotHeadMapperEx.countsByDepotHead(type, subType, creatorArray, hasDebt, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                   materialParam, keyword, organId, organArray, creator, depotId, depotArray, accountId, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据单据类型获取仓库数组
     * @param subType
     * @return
     * @throws Exception
     */
    public String[] getDepotArray(String subType) throws Exception {
        String [] depotArray = null;
        if(!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(subType) && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)) {
            String depotIds = depotService.findDepotStrByCurrentUser();
            depotArray = StringUtil.isNotEmpty(depotIds) ? depotIds.split(",") : null;
        }
        return depotArray;
    }

    /**
     * 根据角色类型获取操作员数组
     * @param roleType
     * @return
     * @throws Exception
     */
    public String[] getCreatorArray(String roleType) throws Exception {
        String creator = getCreatorByRoleType(roleType);
        String [] creatorArray=null;
        if(StringUtil.isNotEmpty(creator)){
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    /**
     * 获取机构数组
     * @return
     */
    public String[] getOrganArray(String subType, String purchaseStatus) throws Exception {
        String [] organArray = null;
        String type = "UserCustomer";
        Long userId = userService.getCurrentUser().getId();
        //获取权限信息
        String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, userId.toString());
        List<Supplier> supplierList = supplierService.findBySelectCus();
        if(BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType) || BusinessConstants.SUB_TYPE_SALES.equals(subType)
                ||BusinessConstants.SUB_TYPE_SALES_RETURN.equals(subType) ) {
            //采购订单里面选择销售订单的时候不要过滤
            if(StringUtil.isEmpty(purchaseStatus)) {
                if (null != supplierList && supplierList.size() > 0) {
                    boolean customerFlag = systemConfigService.getCustomerFlag();
                    List<String> organList = new ArrayList<>();
                    for (Supplier supplier : supplierList) {
                        boolean flag = ubValue.contains("[" + supplier.getId().toString() + "]");
                        if (!customerFlag || flag) {
                            organList.add(supplier.getId().toString());
                        }
                    }
                    if(organList.size() > 0) {
                        organArray = StringUtil.listToStringArray(organList);
                    }
                }
            }
        }
        return organArray;
    }

    /**
     * 根据角色类型获取操作员
     * @param roleType
     * @return
     * @throws Exception
     */
    public String getCreatorByRoleType(String roleType) throws Exception {
        String creator = "";
        User user = userService.getCurrentUser();
        if(BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            creator = user.getId().toString();
        } else if(BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            creator = orgaUserRelService.getUserIdListByUserId(user.getId());
        }
        return creator;
    }

    public Map<String, BigDecimal> getFinishDepositMapByNumberList(List<String> numberList) {
        List<FinishDepositVo> list = depotHeadMapperEx.getFinishDepositByNumberList(numberList);
        Map<String,BigDecimal> finishDepositMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (FinishDepositVo finishDepositVo : list) {
                if(finishDepositVo!=null) {
                    finishDepositMap.put(finishDepositVo.getNumber(), finishDepositVo.getFinishDeposit());
                }
            }
        }
        return finishDepositMap;
    }

    public Map<String, Integer> getBillSizeMapByLinkNumberList(List<String> numberList) throws Exception {
        List<DepotHead> list = getBillListByLinkNumberList(numberList);
        Map<String, Integer> billListMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (DepotHead depotHead : list) {
                if(depotHead!=null) {
                    billListMap.put(depotHead.getLinkNumber(), list.size());
                }
            }
        }
        return billListMap;
    }

    public Map<Long,Integer> getFinancialBillNoMapByBillIdList(List<Long> idList) {
        List<AccountItem> list = accountHeadService.getFinancialBillNoByBillIdList(idList);
        Map<Long, Integer> billListMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (AccountItem accountItem : list) {
                if(accountItem!=null) {
                    billListMap.put(accountItem.getBillId(), list.size());
                }
            }
        }
        return billListMap;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertDepotHead(JSONObject obj, HttpServletRequest request)throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        int result=0;
        try{
            result=depotHeadMapper.insert(depotHead);
            logService.insertLog("单据", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateDepotHead(JSONObject obj, HttpServletRequest request) throws Exception{
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        DepotHead dh=null;
        try{
            dh = depotHeadMapper.selectByPrimaryKey(depotHead.getId());
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        depotHead.setStatus(dh.getStatus());
        depotHead.setCreateTime(dh.getCreateTime());
        int result=0;
        try{
            result = depotHeadMapper.updateByPrimaryKey(depotHead);
            logService.insertLog("单据",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getId()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteDepotHead(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHead(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteBillByIds(String ids)throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<DepotHead> dhList = getDepotHeadListByIds(ids);
        for(DepotHead depotHead: dhList){
            sb.append("[").append(depotHead.getNumber()).append("]");
            //只有未审核的单据才能被删除
            if("0".equals(depotHead.getStatus())) {
                User userInfo = userService.getCurrentUser();
                //删除出庫数据回收序列号
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && !BusinessConstants.SUB_TYPE_TRANSFER.equals(depotHead.getSubType())) {
                    //查询单据子表列表
                    List<DepotItem> depotItemList = null;
                    try {
                        depotItemList = depotItemMapperEx.findDepotItemListBydepotheadId(depotHead.getId(), BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED);
                    } catch (Exception e) {
                        JshException.readFail(logger, e);
                    }

                    /**回收序列号*/
                    if (depotItemList != null && depotItemList.size() > 0) {
                        for (DepotItem depotItem : depotItemList) {
                            //BasicNumber=OperNumber*ratio
                            serialNumberService.cancelSerialNumber(depotItem.getMaterialId(), depotHead.getNumber(), (depotItem.getBasicNumber() == null ? 0 : depotItem.getBasicNumber()).intValue(), userInfo);
                        }
                    }
                }
                //对于零售出庫单据，更新会员的预收款信息
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && BusinessConstants.SUB_TYPE_RETAIL.equals(depotHead.getSubType())){
                    if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())) {
                        if (depotHead.getOrganId() != null) {
                            supplierService.updateAdvanceIn(depotHead.getOrganId(), depotHead.getTotalPrice().abs());
                        }
                    }
                }
                List<DepotItem> list = depotItemService.getListByHeaderId(depotHead.getId());
                //删除单据子表数据
                depotItemMapperEx.batchDeleteDepotItemByDepotHeadIds(new Long[]{depotHead.getId()});
                //删除单据主表信息
                batchDeleteDepotHeadByIds(depotHead.getId().toString());
                //将关联的单据置为审核状态-针对采购入庫、销售出庫和盘点复盘
                if(StringUtil.isNotEmpty(depotHead.getLinkNumber())){
                    if((BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_PURCHASE.equals(depotHead.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_SALES.equals(depotHead.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_REPLAY.equals(depotHead.getSubType()))) {
                        String status = BusinessConstants.BILLS_STATUS_AUDIT;
                        //查询除当前单据之外的关联单据列表
                        List<DepotHead> exceptCurrentList = getListByLinkNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber(), depotHead.getType());
                        if(exceptCurrentList!=null && exceptCurrentList.size()>0) {
                            status = BusinessConstants.BILLS_STATUS_SKIPING;
                        }
                        DepotHead dh = new DepotHead();
                        dh.setStatus(status);
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //将关联的销售订单单据置为未采购状态-针对销售订单转采购订单的情况
                if(StringUtil.isNotEmpty(depotHead.getLinkNumber())){
                    if(BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(depotHead.getSubType())) {
                        DepotHead dh = new DepotHead();
                        //获取分批操作后单据的商品和商品数量（汇总）
                        List<DepotItemVo4MaterialAndSum> batchList = depotItemMapperEx.getBatchBillDetailMaterialSum(depotHead.getLinkNumber(), depotHead.getType());
                        if(batchList.size()>0) {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_SKIPING);
                        } else {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_UN_AUDIT);
                        }
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //更新当前库存
                for (DepotItem depotItem : list) {
                    depotItemService.updateCurrentStock(depotItem);
                }
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
        }
        logService.insertLog("单据", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    /**
     * 删除单据主表信息
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHeadByIds(String ids)throws Exception {
        User userInfo=userService.getCurrentUser();
        String [] idArray=ids.split(",");
        int result=0;
        try{
            result = depotHeadMapperEx.batchDeleteDepotHeadByIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHeadListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<DepotHead> list = new ArrayList<>();
        try{
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(idList);
            list = depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andIdNotEqualTo(id).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try{
            list = depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotHeadIDs)throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        for(Long id: ids) {
            DepotHead depotHead = getDepotHead(id);
            if("0".equals(status)){
                if("1".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_MSG));
                }
            } else if("1".equals(status)){
                if("0".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_MSG));
                }
            }
        }
        if(dhIds.size()>0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    public Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList(List<Long> idList, boolean isMNumber)throws Exception {
        List<MaterialsListVo> list = depotHeadMapperEx.findMaterialsListMapByHeaderIdList(idList);
        Map<String, MaterialsListVo> materialsListMap = new HashMap<>();
        for(MaterialsListVo materialsListVo : list){
            String key = String.valueOf(materialsListVo.getHeaderId());
            if(isMNumber) {
                key = materialsListVo.getHeaderId()+""+materialsListVo.getId()+""+materialsListVo.getMaterialNumber();
            }
            materialsListMap.put(key, materialsListVo);
        }
        return materialsListMap;
    }

    public Map<Long,BigDecimal> getMaterialCountListMapByHeaderIdList(List<Long> idList)throws Exception {
        List<MaterialCountVo> list = depotHeadMapperEx.getMaterialCountListByHeaderIdList(idList);
        Map<Long,BigDecimal> materialCountListMap = new HashMap<>();
        for(MaterialCountVo materialCountVo : list){
            materialCountListMap.put(materialCountVo.getHeaderId(), materialCountVo.getMaterialCount());
        }
        return materialCountListMap;
    }

    public List<DepotHeadVo4InDetail> findInOutDetail(String beginTime, String endTime, String type, String [] creatorArray,
                                                String [] organArray, String materialParam, List<Long> depotList, Integer oId, String number,
                                                String remark, Integer offset, Integer rows) throws Exception{
        List<DepotHeadVo4InDetail> list = null;
        try{
            list =depotHeadMapperEx.findInOutDetail(beginTime, endTime, type, creatorArray, organArray, materialParam, depotList, oId, number, remark, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutDetailCount(String beginTime, String endTime, String type, String [] creatorArray,
                              String [] organArray, String materialParam, List<Long> depotList, Integer oId, String number,
                              String remark) throws Exception{
        int result = 0;
        try{
            result =depotHeadMapperEx.findInOutDetailCount(beginTime, endTime, type, creatorArray, organArray, materialParam, depotList, oId, number, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InOutMCount> findInOutMaterialCount(String beginTime, String endTime, String type, String materialParam,
                              List<Long> depotList, Integer oId, String roleType, Integer offset, Integer rows)throws Exception {
        List<DepotHeadVo4InOutMCount> list = null;
        try{
            String [] creatorArray = getCreatorArray(roleType);
            String subType = "出庫".equals(type)? "销售" : "";
            String [] organArray = getOrganArray(subType, "");
            list =depotHeadMapperEx.findInOutMaterialCount(beginTime, endTime, type, materialParam, depotList, oId,
                    creatorArray, organArray, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutMaterialCountTotal(String beginTime, String endTime, String type, String materialParam,
                               List<Long> depotList, Integer oId, String roleType)throws Exception {
        int result = 0;
        try{
            String [] creatorArray = getCreatorArray(roleType);
            String subType = "出庫".equals(type)? "销售" : "";
            String [] organArray = getOrganArray(subType, "");
            result =depotHeadMapperEx.findInOutMaterialCountTotal(beginTime, endTime, type, materialParam, depotList, oId,
                    creatorArray, organArray);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InDetail> findAllocationDetail(String beginTime, String endTime, String subType, String number,
                            String [] creatorArray, String materialParam, List<Long> depotList, List<Long> depotFList,
                            String remark, Integer offset, Integer rows) throws Exception{
        List<DepotHeadVo4InDetail> list = null;
        try{
            list = depotHeadMapperEx.findAllocationDetail(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark, offset, rows);

            list.stream().forEach(detail->{
                try {
                    // 處理庫存
                    BigDecimal stock;

                    Long materialId = Long.parseLong(detail.getMId());
//                    Unit unitInfo = materialService.findUnit(materialId); //查询计量单位信息
//                    String materialUnit = diEx.getMaterialUnit();
                    Long organId = null;
                    DepotHead depotHead = getDepotHead(detail.getDId());
                    if (depotHead != null) {
                        organId = depotHead.getOrganId();
                    }

                    stock = depotItemService.getStockByParam(detail.getDId(), materialId, null, null, organId);
//                    if (StringUtil.isNotEmpty(unitInfo.getName())) {
//                        stock = unitService.parseStockByUnit(stock, unitInfo, materialUnit);
//                    }
                    detail.setStock(stock);
                } catch (Exception e) {
                    detail.setStock(BigDecimal.ZERO);
                }

                BigDecimal confirmNumber = detail.getConfirmNumber();
                // 處理status
                String status = detail.getStatus();
                if(status.equals(BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING)) {
                    if(confirmNumber != null) {
                        detail.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                    }
                }
            });
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findAllocationDetailCount(String beginTime, String endTime, String subType, String number,
                            String [] creatorArray, String materialParam, List<Long> depotList,  List<Long> depotFList,
                            String remark) throws Exception{
        int result = 0;
        try{
            result =depotHeadMapperEx.findAllocationDetailCount(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccount(String beginTime, String endTime, Integer organId, String [] organArray,
                                              String supplierType, String type, String subType, String typeBack, String subTypeBack, Integer offset, Integer rows) {
        List<DepotHeadVo4StatementAccount> list = null;
        try{
            list = depotHeadMapperEx.getStatementAccount(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack, offset, rows);
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int getStatementAccountCount(String beginTime, String endTime, Integer organId,
                                        String [] organArray, String supplierType, String type, String subType, String typeBack, String subTypeBack) {
        int result = 0;
        try{
            result = depotHeadMapperEx.getStatementAccountCount(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack);
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccountTotalPay(String beginTime, String endTime, Integer organId,
                                                                          String [] organArray, String supplierType,
                                        String type, String subType, String typeBack, String subTypeBack) {
        List<DepotHeadVo4StatementAccount> list = null;
        try{
            list = depotHeadMapperEx.getStatementAccountTotalPay(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack);
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public BigDecimal findAllMoney(Integer supplierId, String type, String subType, String mode, String endTime)throws Exception {
        String modeName = "";
        BigDecimal allOtherMoney = BigDecimal.ZERO;
        BigDecimal allDepositMoney = BigDecimal.ZERO;
        if (mode.equals("实际")) {
            modeName = "change_amount";
        } else if (mode.equals("合计")) {
            modeName = "discount_last_money";
            allOtherMoney = depotHeadMapperEx.findAllOtherMoney(supplierId, type, subType, endTime);
            allDepositMoney = depotHeadMapperEx.findDepositMoney(supplierId, type, subType, endTime);
        }
        BigDecimal result = BigDecimal.ZERO;
        try{
            result =depotHeadMapperEx.findAllMoney(supplierId, type, subType, modeName, endTime);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if(allOtherMoney!=null) {
            result = result.add(allOtherMoney);
        }
        if(allDepositMoney!=null) {
            result = result.subtract(allDepositMoney);
        }
        return result;
    }

    /**
     * 统计总金额
     * @param getS
     * @param type
     * @param subType
     * @param mode 合计或者金额
     * @return
     */
    public BigDecimal allMoney(String getS, String type, String subType, String mode, String endTime) {
        BigDecimal allMoney = BigDecimal.ZERO;
        try {
            Integer supplierId = Integer.valueOf(getS);
            BigDecimal sum = findAllMoney(supplierId, type, subType, mode, endTime);
            if(sum != null) {
                allMoney = sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回正数，如果负数也转为正数
        if ((allMoney.compareTo(BigDecimal.ZERO))==-1) {
            allMoney = allMoney.abs();
        }
        return allMoney;
    }

    /**
     * 查询单位的累计应收和累计应付，零售不能计入
     * @param supplierId
     * @param endTime
     * @param supType
     * @return
     */
    public BigDecimal findTotalPay(Integer supplierId, String endTime, String supType) {
        BigDecimal sum = BigDecimal.ZERO;
        String getS = supplierId.toString();
        if (("客户").equals(supType)) { //客户
            sum = allMoney(getS, "出庫", "销售", "合计",endTime).subtract(allMoney(getS, "出庫", "销售", "实际",endTime));
        } else if (("供应商").equals(supType)) { //供应商
            sum = allMoney(getS, "入庫", "采购", "合计",endTime).subtract(allMoney(getS, "入庫", "采购", "实际",endTime));
        }
        return sum;
    }

    public List<DepotHeadVo4List> getDetailByNumber(String[] number)throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try{
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            List<DepotHeadVo4List> list = depotHeadMapperEx.getDetailByNumber(number);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                //通过批量查询去构造map
                Map<Long,Integer> financialBillNoMap = getFinancialBillNoMapByBillIdList(idList);
                Map<String,Integer> billSizeMap = getBillSizeMapByLinkNumberList(numberList);
                Map<String, MaterialsListVo> materialsListMap = findMaterialsListMapByHeaderIdList(idList, Boolean.FALSE);
                for (DepotHeadVo4List dh : list) {
                    if(dh.getCustomNumber()!=null) {
                        dh.setCustomNumber(dh.getCustomNumber().split("-")[0]);
                    }
                    if(dh.getSourceNumber()!=null) {
                        dh.setSourceNumber(dh.getSourceNumber().split("-")[0]);
                    }
                    if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if(dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if(dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    //是否有付款单或收款单
                    if(financialBillNoMap!=null) {
                        Integer financialBillNoSize = financialBillNoMap.get(dh.getId());
                        dh.setHasFinancialFlag(financialBillNoSize!=null && financialBillNoSize>0);
                    }
                    //是否有退款单
                    if(billSizeMap!=null) {
                        Integer billListSize = billSizeMap.get(dh.getNumber());
                        dh.setHasBackFlag(billListSize!=null && billListSize>0);
                    }
                    if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                    }
                    dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    //商品信息简述
                    if(materialsListMap!=null) {
                        MaterialsListVo vo = materialsListMap.get(String.valueOf(dh.getId()));
                        dh.setMaterialsList(vo.getMaterialsList());
                        dh.setCategoryId(vo.getCategoryId());
                        dh.setMaterialNumber(vo.getMaterialNumber());
                        dh.setMaterialCount(vo.getMaterialCount());
                        dh.setDepotList(vo.getDepotList());
                    }
                    dh.setCreatorName(userService.getUser(dh.getCreator()).getUsername());
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    /**
     * 查询除当前单据之外的关联单据列表
     * @param linkNumber
     * @param number
     * @return
     * @throws Exception
     */
    public List<DepotHead> getListByLinkNumberExceptCurrent(String linkNumber, String number, String type)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andTypeEqualTo(type)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 根据原单号查询关联的单据列表(批量)
     * @param linkNumberList
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberList(List<String> linkNumberList)throws Exception {
        if(linkNumberList!=null && linkNumberList.size()>0) {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andLinkNumberIn(linkNumberList).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            return depotHeadMapper.selectByExample(example);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据原单号查询关联的单据列表
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumber(String linkNumber)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 根据原单号查询关联的单据列表(排除当前的单据编号)
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberExceptNumber(String linkNumber, String number)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 新增单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addDepotHeadAndDetail(String beanJson, String rows, HttpServletRequest request, User userInfo) throws Exception {
        /**处理单据主表数据*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);

        /**若有帶入客單編號及原始客編,檢查是否有重覆*/
        if (depotHead.getCustomNumber()!=null && !depotHead.getCustomNumber().isEmpty()
                && depotHead.getSourceNumber()!=null && !depotHead.getSourceNumber().isEmpty()) {
            boolean isExist = depotHeadMapperEx.checkIsExist(depotHead.getCustomNumber(), depotHead.getSourceNumber()) > 1;
            if(isExist) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_CUSTOM_SOURCE_EXIST_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_CUSTOM_SOURCE_EXIST_MSG));
            }
        }

        if(depotHead.getType()==null||depotHead.getType().isEmpty()) {
            if(depotHead.getDefaultNumber().contains("G")) {
                depotHead.setType(BusinessConstants.DEPOTHEAD_TYPE_IN);
            } else if (depotHead.getDefaultNumber().contains("S")) {
                depotHead.setType(BusinessConstants.DEPOTHEAD_TYPE_OUT);
            }
        }
        String subType = depotHead.getSubType();
        if(subType == null||subType.isEmpty()) {
            if(depotHead.getDefaultNumber().contains("G")) {
                depotHead.setSubType(BusinessConstants.DEPOTHEAD_SUBTYPE_IN);
            } else if (depotHead.getDefaultNumber().contains("S")) {
                depotHead.setSubType(BusinessConstants.DEPOTHEAD_SUBTYPE_OUT);
            }
        } else {
            //结算账户校验
            if ("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
                if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
                }
            }
            //欠款校验
            if ("采购退货".equals(subType) || "销售退货".equals(subType)) {
                checkDebtByParam(beanJson, depotHead);
            }
        }

        // 是否為匯入單
        if(depotHead.getImportFlag() == null ||
                (depotHead.getImportFlag() != null &&
                        (depotHead.getImportFlag().isEmpty() || depotHead.getImportFlag().equals("0")))) {
            if(depotHead.getType().equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
                JSONObject base = JSONObject.parseObject(beanJson);
                JSONArray rowArr = JSONArray.parseArray(rows);
                JSONObject firstObj = rowArr.getJSONObject(0);
                String install = "";
                String recycle = "";
                if(base.containsKey("install")) {
                    install = base.getString("install");
                }
                if(base.containsKey("recycle")) {
                    recycle = base.getString("recycle");
                }
                JSONObject json = new JSONObject();
                json.put("install", install);
                json.put("recycle", recycle);
                json.put("confirm", firstObj.getString("categoryName"));
                json.put("memo", depotHead.getRemark());
                depotHead.setRemark(json.toJSONString());
            }
        }

        //判断用户是否已经登录过，登录过不再处理
        if(userInfo == null) {
            userInfo = userService.getCurrentUser();
        }
        depotHead.setCreator(userInfo==null?null:userInfo.getId());
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if(StringUtil.isEmpty(depotHead.getStatus())) {
            depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        }
        depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        depotHead.setPayType(depotHead.getPayType()==null?"現付":depotHead.getPayType());
        if(StringUtil.isNotEmpty(depotHead.getAccountIdList())){
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if(manyAccountSum.compareTo(depotHead.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //校验累计扣除订金是否超出订单中的金额
        if(depotHead.getDeposit()!=null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //订单中的订金金额
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if(changeAmount!=null) {
                BigDecimal preDeposit = changeAmount.abs();
                if(depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit)>0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        try{
            depotHeadMapper.insertSelective(depotHead);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        /**入庫和出庫处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())){
            if(depotHead.getOrganId()!=null) {
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if(currentAdvanceIn.compareTo(depotHead.getTotalPrice())>=0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice()));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        //根据单据编号查询单据id
        DepotHeadExample dhExample = new DepotHeadExample();
        dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
        if(list!=null) {
            Long headId = list.get(0).getId();
            /**入庫和出庫处理单据子表信息*/
            depotItemService.saveDetails(rows,headId, "add", request, userInfo);
        }
        if(depotHead.getImportFlag() == null  ||
                (depotHead.getImportFlag() != null && depotHead.getImportFlag().equals("0"))) {
            logService.insertLog("单据",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(depotHead.getNumber()).toString(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        }
    }

    /**
     * 更新单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateDepotHeadAndDetail(String beanJson, String rows, HttpServletRequest request)throws Exception {
        /**更新单据主表信息*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);

        /**若有帶入客單編號及原始客編,檢查是否有重覆*/
        if (depotHead.getCustomNumber()!=null && !depotHead.getCustomNumber().isEmpty()
                && depotHead.getSourceNumber()!=null && !depotHead.getSourceNumber().isEmpty()) {
            boolean isExist = depotHeadMapperEx.checkIsExist(depotHead.getCustomNumber(), depotHead.getSourceNumber()) > 1;
            if(isExist) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_CUSTOM_SOURCE_EXIST_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_CUSTOM_SOURCE_EXIST_MSG));
            }
        }

        //获取之前的金额数据
        BigDecimal preTotalPrice = getDepotHead(depotHead.getId()).getTotalPrice().abs();
        String subType = depotHead.getSubType();
        //结算账户校验
        if("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
            if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //欠款校验
        if("采购退货".equals(subType) || "销售退货".equals(subType)) {
            checkDebtByParam(beanJson, depotHead);
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountIdList())){
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if(manyAccountSum.compareTo(depotHead.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //校验累计扣除订金是否超出订单中的金额
        if(depotHead.getDeposit()!=null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //订单中的订金金额
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if(changeAmount!=null) {
                BigDecimal preDeposit = changeAmount.abs();
                if(depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit)>0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        // 若為出庫配送庫，需額外處理remark
        if(depotHead.getType().equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
            DepotHead oldDepotHead = depotHeadMapper.selectByPrimaryKey(depotHead.getId());
            JSONObject json = JSONObject.parseObject(oldDepotHead.getRemark());
            JSONObject base = JSONObject.parseObject(beanJson);
            if(base.containsKey("install")) {
                json.put("install", base.getString("install"));
            }
            if(base.containsKey("recycle")) {
                json.put("recycle", base.getString("recycle"));
            }
            JSONArray rowArr = JSONArray.parseArray(rows);
            JSONObject firstObj = rowArr.getJSONObject(0);
            json.put("confirm", firstObj.getString("categoryName"));

            if(depotHead.getRemark() != null) {
                json.put("memo", depotHead.getRemark());
            }
            depotHead.setRemark(json.toJSONString());
        }

        try{
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        /**入庫和出庫处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())){
            if(depotHead.getOrganId()!=null){
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if(currentAdvanceIn.compareTo(depotHead.getTotalPrice())>=0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice().subtract(preTotalPrice)));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        /**入庫和出庫处理单据子表信息*/
        depotItemService.saveDetails(rows,depotHead.getId(), "update", request, null);
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 针对退货单，校验欠款
     * @param beanJson
     * @param depotHead
     * @throws Exception
     */
    public void checkDebtByParam(String beanJson, DepotHead depotHead) throws Exception {
        JSONObject billObj = JSONObject.parseObject(beanJson);
        if(StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            //退货单对应的原单实际欠款（这里面要除去收付款的金额）
            BigDecimal originalRealDebt = getOriginalRealDebt(depotHead.getLinkNumber(), depotHead.getNumber());
            if(billObj!=null && billObj.get("debt")!=null && originalRealDebt.compareTo(billObj.getBigDecimal("debt"))<0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_OVER_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_OVER_MSG));
            }
        } else {
            if(billObj!=null && billObj.get("debt")!=null && BigDecimal.ZERO.compareTo(billObj.getBigDecimal("debt"))!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_MSG));
            }
        }
    }

    /**
     * 退货单对应的原单实际欠款（这里面要除去收付款的金额）
     * @param linkNumber 原单单号
     * @param number 当前单号
     * @return
     */
    public BigDecimal getOriginalRealDebt(String linkNumber, String number) throws Exception {
        DepotHead depotHead = getDepotHead(linkNumber);
        BigDecimal discountLastMoney = depotHead.getDiscountLastMoney()!=null?depotHead.getDiscountLastMoney():BigDecimal.ZERO;
        BigDecimal otherMoney = depotHead.getOtherMoney()!=null?depotHead.getOtherMoney():BigDecimal.ZERO;
        BigDecimal deposit = depotHead.getDeposit()!=null?depotHead.getDeposit():BigDecimal.ZERO;
        BigDecimal changeAmount = depotHead.getChangeAmount()!=null?depotHead.getChangeAmount().abs():BigDecimal.ZERO;
        //原单欠款
        BigDecimal debt = discountLastMoney.add(otherMoney).subtract((deposit.add(changeAmount)));
        //完成欠款
        BigDecimal finishDebt = accountItemService.getEachAmountByBillId(depotHead.getId());
        finishDebt = finishDebt!=null?finishDebt:BigDecimal.ZERO;
        //原单对应的退货单欠款(总数)
        List<DepotHead> billList = getBillListByLinkNumberExceptNumber(linkNumber, number);
        BigDecimal allBillDebt = BigDecimal.ZERO;
        for(DepotHead dh: billList) {
            BigDecimal billDiscountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
            BigDecimal billOtherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
            BigDecimal billDeposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
            BigDecimal billChangeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount().abs():BigDecimal.ZERO;
            BigDecimal billDebt = billDiscountLastMoney.add(billOtherMoney).subtract((billDeposit.add(billChangeAmount)));
            allBillDebt = allBillDebt.add(billDebt);
        }
        //原单实际欠款
        return debt.subtract(finishDebt).subtract(allBillDebt);
    }

    public Map<String, Object> getBuyAndSaleStatistics(String today, String monthFirstDay, String yesterdayBegin, String yesterdayEnd,
                                                       String yearBegin, String yearEnd, String roleType, HttpServletRequest request) throws Exception {
        String [] creatorArray = getCreatorArray(roleType);
        Map<String, Object> map = new HashMap<>();
        //今日
        BigDecimal todayBuy = getBuyAndSaleBasicStatistics("入庫", "采购",
                1, today, getNow3(), creatorArray); //今日采购入庫
        BigDecimal todayBuyBack = getBuyAndSaleBasicStatistics("出庫", "采购退货",
                1, today, getNow3(), creatorArray); //今日采购退货
        BigDecimal todaySale = getBuyAndSaleBasicStatistics("出庫", "销售",
                1, today, getNow3(), creatorArray); //今日销售出庫
        BigDecimal todaySaleBack = getBuyAndSaleBasicStatistics("入庫", "销售退货",
                1, today, getNow3(), creatorArray); //今日销售退货
        BigDecimal todayRetailSale = getBuyAndSaleRetailStatistics("出庫", "零售",
                today, getNow3(), creatorArray); //今日零售出庫
        BigDecimal todayRetailSaleBack = getBuyAndSaleRetailStatistics("入庫", "零售退货",
                today, getNow3(), creatorArray); //今日零售退货
        //本月
        BigDecimal monthBuy = getBuyAndSaleBasicStatistics("入庫", "采购",
                1, monthFirstDay, getNow3(), creatorArray); //本月采购入庫
        BigDecimal monthBuyBack = getBuyAndSaleBasicStatistics("出庫", "采购退货",
                1, monthFirstDay, getNow3(), creatorArray); //本月采购退货
        BigDecimal monthSale = getBuyAndSaleBasicStatistics("出庫", "销售",
                1,monthFirstDay, getNow3(), creatorArray); //本月销售出庫
        BigDecimal monthSaleBack = getBuyAndSaleBasicStatistics("入庫", "销售退货",
                1,monthFirstDay, getNow3(), creatorArray); //本月销售退货
        BigDecimal monthRetailSale = getBuyAndSaleRetailStatistics("出庫", "零售",
                monthFirstDay, getNow3(), creatorArray); //本月零售出庫
        BigDecimal monthRetailSaleBack = getBuyAndSaleRetailStatistics("入庫", "零售退货",
                monthFirstDay, getNow3(), creatorArray); //本月零售退货
        //昨日
        BigDecimal yesterdayBuy = getBuyAndSaleBasicStatistics("入庫", "采购",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购入庫
        BigDecimal yesterdayBuyBack = getBuyAndSaleBasicStatistics("出庫", "采购退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购退货
        BigDecimal yesterdaySale = getBuyAndSaleBasicStatistics("出庫", "销售",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售出庫
        BigDecimal yesterdaySaleBack = getBuyAndSaleBasicStatistics("入庫", "销售退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售退货
        BigDecimal yesterdayRetailSale = getBuyAndSaleRetailStatistics("出庫", "零售",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售出庫
        BigDecimal yesterdayRetailSaleBack = getBuyAndSaleRetailStatistics("入庫", "零售退货",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售退货
        //今年
        BigDecimal yearBuy = getBuyAndSaleBasicStatistics("入庫", "采购",
                1, yearBegin, yearEnd, creatorArray); //今年采购入庫
        BigDecimal yearBuyBack = getBuyAndSaleBasicStatistics("出庫", "采购退货",
                1, yearBegin, yearEnd, creatorArray); //今年采购退货
        BigDecimal yearSale = getBuyAndSaleBasicStatistics("出庫", "销售",
                1, yearBegin, yearEnd, creatorArray); //今年销售出庫
        BigDecimal yearSaleBack = getBuyAndSaleBasicStatistics("入庫", "销售退货",
                1, yearBegin, yearEnd, creatorArray); //今年销售退货
        BigDecimal yearRetailSale = getBuyAndSaleRetailStatistics("出庫", "零售",
                yearBegin, yearEnd, creatorArray); //今年零售出庫
        BigDecimal yearRetailSaleBack = getBuyAndSaleRetailStatistics("入庫", "零售退货",
                yearBegin, yearEnd, creatorArray); //今年零售退货
        map.put("todayBuy", roleService.parsePriceByLimit(todayBuy.subtract(todayBuyBack), "buy", "***", request));
        map.put("todaySale", roleService.parsePriceByLimit(todaySale.subtract(todaySaleBack), "sale", "***", request));
        map.put("todayRetailSale", roleService.parsePriceByLimit(todayRetailSale.subtract(todayRetailSaleBack), "retail", "***", request));
        map.put("monthBuy", roleService.parsePriceByLimit(monthBuy.subtract(monthBuyBack), "buy", "***", request));
        map.put("monthSale", roleService.parsePriceByLimit(monthSale.subtract(monthSaleBack), "sale", "***", request));
        map.put("monthRetailSale", roleService.parsePriceByLimit(monthRetailSale.subtract(monthRetailSaleBack), "retail", "***", request));
        map.put("yesterdayBuy", roleService.parsePriceByLimit(yesterdayBuy.subtract(yesterdayBuyBack), "buy", "***", request));
        map.put("yesterdaySale", roleService.parsePriceByLimit(yesterdaySale.subtract(yesterdaySaleBack), "sale", "***", request));
        map.put("yesterdayRetailSale", roleService.parsePriceByLimit(yesterdayRetailSale.subtract(yesterdayRetailSaleBack), "retail", "***", request));
        map.put("yearBuy", roleService.parsePriceByLimit(yearBuy.subtract(yearBuyBack), "buy", "***", request));
        map.put("yearSale", roleService.parsePriceByLimit(yearSale.subtract(yearSaleBack), "sale", "***", request));
        map.put("yearRetailSale", roleService.parsePriceByLimit(yearRetailSale.subtract(yearRetailSaleBack), "retail", "***", request));
        return map;
    }

    public BigDecimal getBuyAndSaleBasicStatistics(String type, String subType, Integer hasSupplier,
                                                   String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleBasicStatistics(type, subType, hasSupplier, beginTime, endTime, creatorArray);
    }

    public BigDecimal getBuyAndSaleRetailStatistics(String type, String subType,
                                                    String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleRetailStatistics(type, subType, beginTime, endTime, creatorArray).abs();
    }

    public DepotHead getDepotHead(String number)throws Exception {
        DepotHead depotHead = new DepotHead();
        try{
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<DepotHead> list = depotHeadMapper.selectByExample(example);
            if(null!=list && list.size()>0) {
                depotHead = list.get(0);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return depotHead;
    }

    public List<DepotHeadVo4List> debtList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                              String type, String subType, String roleType, String status) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try{
            String depotIds = depotService.findDepotStrByCurrentUser();
            String [] depotArray=depotIds.split(",");
            String [] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list=depotHeadMapperEx.debtList(organId, type, subType, creatorArray, status, number, beginTime, endTime, materialParam, depotArray);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                }
                //通过批量查询去构造map
                Map<String, MaterialsListVo> materialsListMap = findMaterialsListMapByHeaderIdList(idList, Boolean.FALSE);
                for (DepotHeadVo4List dh : list) {
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    BigDecimal discountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
                    BigDecimal otherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
                    BigDecimal deposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
                    BigDecimal changeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount().abs():BigDecimal.ZERO;
                    //本单欠款
                    dh.setNeedDebt(discountLastMoney.add(otherMoney).subtract(deposit.add(changeAmount)));
                    List<DepotHead> billList = getBillListByLinkNumber(dh.getNumber());
                    //退货单欠款(总数)
                    BigDecimal allBillDebt = BigDecimal.ZERO;
                    for(DepotHead depotHead: billList) {
                        BigDecimal billDiscountLastMoney = depotHead.getDiscountLastMoney()!=null?depotHead.getDiscountLastMoney():BigDecimal.ZERO;
                        BigDecimal billOtherMoney = depotHead.getOtherMoney()!=null?depotHead.getOtherMoney():BigDecimal.ZERO;
                        BigDecimal billDeposit = depotHead.getDeposit()!=null?depotHead.getDeposit():BigDecimal.ZERO;
                        BigDecimal billChangeAmount = depotHead.getChangeAmount()!=null?depotHead.getChangeAmount().abs():BigDecimal.ZERO;
                        BigDecimal billDebt = billDiscountLastMoney.add(billOtherMoney).subtract((billDeposit.add(billChangeAmount)));
                        allBillDebt = allBillDebt.add(billDebt);
                    }
                    BigDecimal needDebt = dh.getNeedDebt()!=null?dh.getNeedDebt():BigDecimal.ZERO;
                    //实际欠款   实际欠款=本单欠款-退货单欠款（主要针对存在退货的情况）
                    dh.setRealNeedDebt(needDebt.subtract(allBillDebt));
                    BigDecimal finishDebt = accountItemService.getEachAmountByBillId(dh.getId());
                    finishDebt = finishDebt!=null?finishDebt:BigDecimal.ZERO;
                    //已收欠款
                    dh.setFinishDebt(finishDebt);
                    //待收欠款
                    dh.setDebt(needDebt.subtract(allBillDebt).subtract(finishDebt));
                    //商品信息简述
                    if(materialsListMap!=null) {
                        dh.setMaterialsList(materialsListMap.get(String.valueOf(dh.getId())).getMaterialsList());
                    }
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public BaseResponseInfo importExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();

        User userInfo = userService.getCurrentUser();

//        String customNumber = "";
        try {
            Long beginTime = System.currentTimeMillis();
            //文件副檔名只能為 xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }

//            List<Supplier> supplierList = supplierService.findBySelectSup();
            List<Depot> depotList = depotService.getAllList();
            List<DepotHead> depotHeadList = getDepotHead();
//            List<DepotCounter> depotCountList = depotCounterService.getAllList();
//            List<Material> materialList = materialService.getMaterial();
            List<MaterialVo4Unit> materialList =  materialMapperEx.selectByConditionMaterial(null, null, null, null, null,
                    null, null, null, null, null, null, null,1,10000);

            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet mainData = workbook.getSheet(0); // 主單資料

            JSONObject saveJson = null;
            int blockTimes = 0; // 用來判斷excel確認書及客單編號欄位，空白次數是否超過2次
            int importCount = 0; // 匯入筆數
            Map<String, String> importError = new HashMap<>(); // 匯入有缺欄位的客單編號+原始編號，或是重複的客單編號+原始編號

            for (int i = 1; i < mainData.getRows(); i++) {
                JSONObject beanJson = new JSONObject();

                // 確認書(必填)
                String confirm = ExcelUtils.getContent(mainData, i, 0);
                // 客單編號(必填)
                String excelCustomNum = ExcelUtils.getContent(mainData, i, 1);
//                String customNumber = excelCustomNum.split("-")[0];

                if (confirm == null || (confirm != null && confirm.isEmpty())
                        && excelCustomNum == null || (excelCustomNum != null && excelCustomNum.isEmpty())) {
                    blockTimes++;
                    if (blockTimes >= 2) {
                        break;
                    }
                    continue;
                }

//                if (confirm == null || (confirm != null && confirm.isEmpty())) {
//                    confirm = getJsonValue(saveJson, "confirm");
//                }

                // 原始編號(必填)
                String sourceNumber = ExcelUtils.getContent(mainData, i, 2);
                if(sourceNumber == null || (sourceNumber != null && sourceNumber.isEmpty())) {
                    importError.put(""+i, "原始客編未填寫");
                    continue;
                }
                Optional<DepotHead> tmpDepotHead = depotHeadList.parallelStream().filter(dh->{
                    if(dh.getCustomNumber() != null && dh.getSourceNumber() != null) {
                        if (dh.getCustomNumber().equals(excelCustomNum) && dh.getSourceNumber().equals(sourceNumber)) {
                            return true;
                        }
                    }
                    return false;
                }).findFirst();
                if(tmpDepotHead.isPresent()) {
                    importError.put(""+i, "此筆資料重覆匯入(客單編號:"+excelCustomNum+", 原始客編:"+sourceNumber+")");
                    continue;
                }

                // 收貨人
                String receiveName = ExcelUtils.getContent(mainData, i, 3);
//                if (receiveName == null || (receiveName != null && receiveName.isEmpty())) {
//                    importError.put(""+i, "收貨人未填寫");
//                    continue;
//                }
                // 電話
                String cellphone = ExcelUtils.getContent(mainData, i, 4);
//                if (cellphone == null || (cellphone != null && cellphone.isEmpty())) {
//                    importError.put(""+i, "電話未填寫");
//                    continue;
//                }
                // 發單日(必填)
                String issueDate = ExcelUtils.getContent(mainData, i, 5);
                if (issueDate == null || (issueDate != null && issueDate.isEmpty())) {
                    importError.put(""+i, "發單日未填寫");
                    continue;
                }
                // 裝機地址
                String address = ExcelUtils.getContent(mainData, i, 6);
//                if (address == null || (address != null && address.isEmpty())) {
//                    importError.put(""+i, "裝機地址未填寫");
//                    continue;
//                }
                // 出貨倉別(必填)
                String depotName = ExcelUtils.getContent(mainData, i, 7);
                if (depotName == null || (depotName != null && depotName.isEmpty())) {
                    importError.put(""+i, "出貨倉別未填寫");
                    continue;
                }
                Optional<Depot> tmpDepot = depotList.stream().filter(d -> d.getName().contains(depotName)).findFirst();
                Depot depot;
                if (tmpDepot.isPresent()) {
                    depot = tmpDepot.get();
                } else {
                    // TODO 記錄
                    importError.put(""+i, "查無此倉庫("+depotName+")");
                    continue;
                }
                // 儲位
//                String counter = ExcelUtils.getContent(mainData, i, 7);
//                if(counter == null || (counter != null && counter.isEmpty())) {
//                    counter = getJsonValue(saveJson, "counter");
//                }

                // 品號 (必填)
                String mNumber = ExcelUtils.getContent(mainData, i, 8);
                MaterialVo4Unit materialVo4Unit=null;
                if(mNumber ==null || (mNumber != null && mNumber.isEmpty())){
                    // TODO 記錄
                    importError.put("" + i, "品號未填寫");
                    continue;
                } else {
                    Optional<MaterialVo4Unit> tempM =
                            materialList.parallelStream().filter(m -> m.getNumber().equals(mNumber)).findFirst();
                    if (tempM.isPresent()) {
                        materialVo4Unit = tempM.get();
                    } else {
                        // TODO 記錄
                        importError.put("" + i, "查無此品號(" + mNumber + ")");
                        continue;
                    }
                }

                // 商品型號
                String materialName = ExcelUtils.getContent(mainData, i, 9);
                // 數量 (必填)
                String amount = ExcelUtils.getContent(mainData, i, 10);
                if (amount == null || (amount != null && amount.isEmpty())) {
                    // TODO 記錄
                    importError.put("" + i, "數量未填寫");
                    continue;
                }
                beanJson.put("amount", amount);
                // 安裝方式
                String install = ExcelUtils.getContent(mainData, i, 11);
//                if (install == null || (install != null && install.isEmpty())) {
//                    install = getJsonValue(saveJson, "install");
//                }
                beanJson.put("install", install);
                // 舊機回收
                String recycle = ExcelUtils.getContent(mainData, i, 12);
//                if (recycle == null || (recycle != null && recycle.isEmpty())) {
//                    recycle = getJsonValue(saveJson, "recycle");
//                }
                beanJson.put("recycle", recycle);
                // 配道備註
                String memo = ExcelUtils.getContent(mainData, i, 13);
//                if (memo == null || (memo != null && memo.isEmpty())) {
//                    memo = getJsonValue(saveJson, "memo");
//                }
                beanJson.put("memo", memo);

//                String[] organAndNumber = mNumber.split("-");
                // 客戶id
                Long organId = materialVo4Unit.getOrganId();
//                if(organAndNumber.length > 0) {
//                    organId = Long.valueOf(organAndNumber[0]);
//                }

                try {
                    LocalDate date = LocalDate.parse(issueDate, formatterDate);
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String operTime = LocalDateTime.parse(date.toString().concat(" ").concat(time), formatterChange).toString(); // 出庫時間
                    beanJson.put("operTime", operTime);
                } catch(DateTimeParseException e) {
                    // TODO 記錄
                    importError.put("" + i, "["+issueDate+"] 日期格式有誤，請按照 "+formatterDate.toString());
                    continue;
                }

                JSONObject json = new JSONObject();
                json.put("confirm", confirm);
                json.put("install", install);
                json.put("recycle", recycle);
                json.put("memo", memo);
                String remark = json.toJSONString(); // 備註

                // S20231123163920999
                String number = String.format("S%s", sequenceService.buildNumber(Boolean.TRUE));
                beanJson.put("number", number);
                beanJson.put("defaultNumber", number);
                beanJson.put("organId", organId);
                beanJson.put("changeAmount", 0);
                beanJson.put("totalPrice", 0);
//                beanJson.put("notiNumber", );
//                beanJson.put("taxid", taxId);
//                beanJson.put("buyerName", buyerName);
                beanJson.put("receiveName", receiveName);
//                beanJson.put("telephone", telephone);
                beanJson.put("cellphone", cellphone);
                beanJson.put("address", address);
                beanJson.put("remark", remark);
                beanJson.put("importFlag", 1);
                beanJson.put("customNumber", excelCustomNum);
                beanJson.put("sourceNumber", sourceNumber);
//                beanJson.put("mainArrival", mainArrival);
//                beanJson.put("extrasArrival", extrasArrival);
//                beanJson.put("agreedDelivery", agreedDelivery);
//                beanJson.put("delivered", delivered);
//                beanJson.put("tenantId", 63);

                beanJson.put("depotName", depotName);
//                beanJson.put("counter", counter);
                if (excelCustomNum.split("-").length == 1) {
                    saveJson = beanJson;
                }

                JSONArray ary = new JSONArray();
                JSONObject obj = new JSONObject();
//                MaterialExtend materialExtend = materialExtendService.getInfoByNumber(organAndNumber[1]);
//                if (materialExtend != null) {
                    Long materialId = materialVo4Unit.getId();
                    obj.put("materialId", materialId);
                    obj.put("barCode", materialVo4Unit.getmBarCode());
                    if (materialVo4Unit.getCommodityUnit() == null) {
                        obj.put("unit", "null");
                    } else {
                        obj.put("unit", materialVo4Unit.getCommodityUnit());
                    }
//                }
//                String finalDepotName = depotName;
//                Optional<Depot> depot = depotList.stream().filter(d -> d.getName().contains(finalDepotName)).findFirst();
//                if (depot.isPresent()) {
                    obj.put("depotId", depot.getId());
//                } else {
//                    continue;
//                }
//                String finalCounter = counter;
//                Optional<DepotCounter> depotCounter = depotCountList.stream().filter(dc -> dc.getName().contains(finalCounter)).findFirst();
//                if(depotCounter.isPresent()) {
//                    obj.put("counterId", depotCounter.get().getId());
//                    obj.put("counterName", depotCounter.get().getName());
//                }
//                obj.put("counterName", counterName);
                obj.put("operNumber", amount);
                obj.put("unitPrice", 0);
                obj.put("allPrice", 0);
                obj.put("taxRate", 0);
                obj.put("taxMoney", 0);
                obj.put("taxLastMoney", 0);
                ary.add(obj);

                String rows = ary.toJSONString();

                addDepotHeadAndDetail(beanJson.toJSONString(), rows, request, userInfo);

                importCount++;
            }

            logService.insertLog("匯入配送單",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT).append(importCount).append(BusinessConstants.LOG_DATA_UNIT).toString(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            Long endTime = System.currentTimeMillis();
            logger.info("匯入秏時：{}", endTime - beginTime);
            info.code = 200;
            info.data = "匯入成功";
            // TODO 加入顯示匯入失敗的記錄 及判斷importCount 是否等於匯入數量
            if(importError.size() > 0) {
                StringBuffer sb= new StringBuffer();
                sb.append(", 匯入失敗列數:\n");
                importError.entrySet().stream().forEach(value->{
                    sb.append("Excel文件第"+value.getKey()+"列");
                    sb.append("->");
                    sb.append(value.getValue());
                    sb.append("\n");
                });
                info.data += sb.toString();
            }
            logger.info((String) info.data);
        } catch (BusinessRunTimeException brte) {
            info.code = brte.getCode();
            info.data = brte.getData().get("message");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            info.code = 500;
            info.data = "匯入失敗";
        }

        return info;
    }

    private String getJsonValue(JSONObject json, String key) {
        if(json == null) {
            return "";
        }
        return json.getString(key);
    }

    public static void main(String[] args) throws Exception {
//        String datetimeStr = "10/10/23 17:10:10";
//        String dateStr = "12/4/23";

        String issueDate = "2023/11/6";

        LocalDate date = LocalDate.parse(issueDate, formatterDate);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(time);
//        String operTime = LocalDateTime.parse(date.toString().concat(" ").concat(time), formatterChange).toString(); // 出庫時間
//        System.out.println("operTime>>"+operTime);

        String number = String.format("S%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        System.out.println(">>>"+number);

//        System.out.println(">>>"+LocalDateTime.parse(datetimeStr, formatter).format(formatterChange));
//        System.out.println(LocalDate.parse(dateStr));
//        System.out.println(">>>"+LocalDate.parse(dateStr, formatterDate));
//        System.out.println(">>>"+ LocalDate.parse(dateStr, formatterDate).format(formatterChangeDate));

//        String a = "【】";
    }
}
