package com.jsh.erp.service.transferwarehouse;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.DepotHead;
import com.jsh.erp.datasource.entities.DepotHeadExample;
import com.jsh.erp.datasource.entities.DepotItem;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.datasource.mappers.DepotItemMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransferWarehouseService {

    private Logger logger = LoggerFactory.getLogger(TransferWarehouseService.class);

    @Resource
    private DepotHeadService depotHeadService;

    @Resource
    private DepotItemService depotItemService;

    @Resource
    private DepotHeadMapper depotHeadMapper;

    @Resource
    private DepotItemMapper depotItemMapper;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    /**
     * 新增移倉單
     *
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addTransferDepotHead(String beanJson, String rows, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        if (depotHead.getType() == null || depotHead.getType().isEmpty()) {
            depotHead.setType(BusinessConstants.DEPOTHEAD_TYPE_OUT);
        }
        String subType = depotHead.getSubType();
        if (subType == null || subType.isEmpty()) {
            depotHead.setSubType(BusinessConstants.SUB_TYPE_TRANSFER);
        }

        //判断用户是否已经登录过，登录过不再处理
        User userInfo = userService.getCurrentUser();
        depotHead.setCreator(userInfo == null ? null : userInfo.getId());
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (StringUtil.isEmpty(depotHead.getStatus())) {
            depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING);
        }
        depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        depotHead.setPayType(depotHead.getPayType() == null ? "現付" : depotHead.getPayType());

        try {
            depotHeadMapper.insertSelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        //根据单据编号查询单据id
        DepotHeadExample dhExample = new DepotHeadExample();
        dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
        if (list != null) {
            Long headId = list.get(0).getId();
            /**入庫和出庫处理单据子表信息*/
            depotItemService.saveTransferDetails(rows, headId, "add", request);
        }
        logService.insertLog("單據(移倉)",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 更新移倉單
     *
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateTransferDepotHead(String beanJson, String rows, HttpServletRequest request) throws Exception {
        /**更新单据主表信息*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);

        try {
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        /**入庫和出庫处理单据子表信息*/
        depotItemService.saveTransferDetails(rows, depotHead.getId(), "update", request);
        logService.insertLog("單據(移倉)",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotItemIDs) throws Exception {
        int result = 0;

        // header_id, item_ids
        Map<Long, List<Long>> dhIdMap = new HashMap<>();

//        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotItemIDs);
        for(Long id: ids) {
            DepotItem depotItem = depotItemService.getDepotItem(id);

            Long headerId = depotItem.getHeaderId();
            DepotHead depotHead = depotHeadService.getDepotHead(headerId);

            List<Long> headerIdList;
            if (dhIdMap.containsKey(headerId)) {
                headerIdList = dhIdMap.get(headerId);
            } else {
                headerIdList = new ArrayList<>();
            }

            if(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED.equals(status)) {
                if(BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING.equals(depotHead.getStatus())) {
                    headerIdList.add(id);
//                    dhIds.add(depotHead.getId());
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_MSG));
                }

                // 更新item的確認數量
                depotItem.setConfirmNumber(depotItem.getOperNumber());
                depotItemMapper.updateByPrimaryKeySelective(depotItem);

                // TODO 更新當前移入倉的庫存
                if(depotItem.getAnotherDepotId()!=null){
                    depotItemService.updateCurrentStockFun(headerId, depotItem.getMaterialId(), depotItem.getAnotherDepotId(), null);
                }
            }
        }

        dhIdMap.forEach((headerKey, itemList)->{
            try {
                // TODO 取得在移倉主單內含多少張細單
                int totalItemSize = depotItemService.getListByHeaderId(headerKey).size();

                // TODO 有多少細單已經移倉完成
                int moveItemSize = 0;
                DepotHead depotHead = depotHeadService.getDepotHead(headerKey);
                String remark = depotHead.getRemark();
                if(remark != null) {
                    JSONObject json = JSONObject.parseObject(remark);
                    if(json.containsKey("move")) {
                        moveItemSize = json.getString("move").split(",").length;
                    }
                }
                if(totalItemSize == (moveItemSize+itemList.size())) {
                    depotHead.setStatus(status);
                    
                }

            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        });

//        if(dhIds.size()>=ids.size()) {
//            // TODO 需判斷item屬於那些header
//            DepotHead depotHead = new DepotHead();
//            depotHead.setStatus(status);
//
//            String myRemark = depotHead.getRemark();
//            try {
//                JSONObject remarkJson = JSONObject.parseObject(myRemark);
//                String move = "";
//                if (remarkJson.containsKey("move")) {
//                    move = remarkJson.getString("move").concat(",").concat(depotItemIDs.trim());
//                    remarkJson.put("move", move);
//                } else {
//                    remarkJson.put("move", depotItemIDs.trim());
//                }
//                depotHead.setRemark(remarkJson.toJSONString());
//            }catch(Exception e) {
//                System.out.println("["+myRemark + "], 非json格式");
//                JSONObject json = new JSONObject();
        // TODO 需將舊有的資料取出，一併存入
//                json.put("move", depotItemIDs.trim());
//                depotHead.setRemark(json.toJSONString());
//            }
//            DepotHeadExample example = new DepotHeadExample();
//            example.createCriteria().andIdIn(dhIds);
//            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
//        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int confirmSingleStatus(Long id, Integer amount, HttpServletRequest request) throws Exception {
        int result = 0;
        Long dhId;
        DepotItem depotItem = depotItemService.getDepotItem(id);
        Long headerId = depotItem.getHeaderId();

        DepotHead depotHead = depotHeadService.getDepotHead(headerId);
        if (BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING.equals(depotHead.getStatus())) {
            dhId = depotHead.getId();
        } else {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_MSG));
        }
        updateTransferDepotHeadStock(id, amount);

        if (dhId > 0) {
            List<DepotItem> list = depotItemService.getListByHeaderId(headerId);

            DepotHead updateDepotHead = new DepotHead();

            // 所有移倉細單的 confirm_number 是否都不為null
            AtomicInteger count = new AtomicInteger(0);
            list.stream().forEach(item->{
                if (item.getConfirmNumber() != null) {
                    count.addAndGet(1);
                }
            });
            if(list.size() == count.get()) {
                updateDepotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
            }

            // 需將舊有的資料取出，一併存入
            List<Long> dhIds = new ArrayList<>();
            dhIds.add(dhId);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(updateDepotHead, example);
        }
        logService.insertLog("單據(確認移倉)",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return result;
    }

    /**
     * 移倉完成後，更新數量
     *
     * @param id
     * @param amount 確認數量
     * @throws Exception
     */
    private void updateTransferDepotHeadStock(Long id, Integer amount) throws Exception {
        //更新當前庫存
        DepotItem depotItem = depotItemService.getDepotItem(id);

        // 判斷確認數量是否有帶值，若與原移倉數量不相同，需額外調整
        if (amount != null) {
            if (depotItem.getOperNumber().longValue() != amount) {
                // TODO 2023-11-30 是否要判斷確認的數量，是否有超過商品的庫存
                BigDecimal decimalAmount = new BigDecimal(amount);
                depotItem.setOperNumber(decimalAmount);
                depotItem.setBasicNumber(decimalAmount);
                depotItem.setConfirmNumber(decimalAmount);
                String transferChange = "移倉數量: %s, 確認數量: %s";
                String remark = depotItem.getRemark();
                if (remark == null) {
                    remark = String.format(transferChange, depotItem.getOperNumber().toString(), amount);
                } else {
                    remark = remark.concat(",").concat(String.format(transferChange, depotItem.getOperNumber(), amount));
                }
                depotItem.setRemark(remark);

                depotItemMapper.updateByPrimaryKeySelective(depotItem);

                depotItemService.updateCurrentStockFun(depotItem.getHeaderId(), depotItem.getMaterialId(), depotItem.getDepotId(), null);
            }
        } else {
            depotItem.setConfirmNumber(depotItem.getOperNumber());
        }
        if (depotItem.getAnotherDepotId() != null) {
            depotItemService.updateCurrentStockFun(depotItem.getHeaderId(), depotItem.getMaterialId(), depotItem.getAnotherDepotId(), null);
        }
    }
}
