package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.vo.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author ji-sheng-hua 752*718*920
 */
@RestController
@RequestMapping(value = "/depotHead")
@Api(tags = {"单据管理"})
public class DepotHeadController {
    private Logger logger = LoggerFactory.getLogger(DepotHeadController.class);

    @Resource
    private DepotHeadService depotHeadService;

    @Resource
    private DepotService depotService;

    @Resource
    private RedisService redisService;

    @Resource
    private UserService userService;

    @Value(value="${file.path}")
    private String filePath;

    @Value(value="${spring.servlet.multipart.max-file-size}")
    private Long maxFileSize;

    @Value(value="${spring.servlet.multipart.max-request-size}")
    private Long maxRequestSize;

    /**
     * 批量设置状态-审核或者反审核
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-审核或者反审核")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception{
        Map<String, Object> objectMap = new HashMap<>();
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        int res = depotHeadService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 入庫出庫明细接口
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param number
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInOutDetail")
    @ApiOperation(value = "入庫出庫明细接口")
    public BaseResponseInfo findInOutDetail(@RequestParam("currentPage") Integer currentPage,
                                        @RequestParam("pageSize") Integer pageSize,
                                        @RequestParam(value = "organId", required = false) Integer oId,
                                        @RequestParam("number") String number,
                                        @RequestParam("materialParam") String materialParam,
                                        @RequestParam(value = "depotId", required = false) Long depotId,
                                        @RequestParam("beginTime") String beginTime,
                                        @RequestParam("endTime") String endTime,
                                        @RequestParam(value = "roleType", required = false) String roleType,
                                        @RequestParam("type") String type,
                                        @RequestParam("remark") String remark,
                                        HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            List<DepotHeadVo4InDetail> resList = new ArrayList<DepotHeadVo4InDetail>();
            String [] creatorArray = depotHeadService.getCreatorArray(roleType);
            String subType = "出庫".equals(type)? "销售" : "";
            String [] organArray = depotHeadService.getOrganArray(subType, "");
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4InDetail> list = depotHeadService.findInOutDetail(beginTime, endTime, type, creatorArray, organArray,
                    StringUtil.toNull(materialParam), depotList, oId, StringUtil.toNull(number), remark, (currentPage-1)*pageSize, pageSize);
            int total = depotHeadService.findInOutDetailCount(beginTime, endTime, type, creatorArray, organArray,
                    StringUtil.toNull(materialParam), depotList, oId, StringUtil.toNull(number), remark);
            map.put("total", total);
            //存放数据json数组
            if (null != list) {
                for (DepotHeadVo4InDetail dhd : list) {
                    resList.add(dhd);
                }
            }
            map.put("rows", resList);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 入庫出庫统计接口
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInOutMaterialCount")
    @ApiOperation(value = "入庫出庫统计接口")
    public BaseResponseInfo findInOutMaterialCount(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(value = "organId", required = false) Integer oId,
                                         @RequestParam("materialParam") String materialParam,
                                         @RequestParam(value = "depotId", required = false) Long depotId,
                                         @RequestParam("beginTime") String beginTime,
                                         @RequestParam("endTime") String endTime,
                                         @RequestParam("type") String type,
                                         @RequestParam(value = "roleType", required = false) String roleType,
                                         HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4InOutMCount> list = depotHeadService.findInOutMaterialCount(beginTime, endTime, type, StringUtil.toNull(materialParam),
                    depotList, oId, roleType, (currentPage-1)*pageSize, pageSize);
            int total = depotHeadService.findInOutMaterialCountTotal(beginTime, endTime, type, StringUtil.toNull(materialParam),
                    depotList, oId, roleType);
            map.put("total", total);
            map.put("rows", list);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 移倉明細統計
     * @param currentPage
     * @param pageSize
     * @param number
     * @param materialParam
     * @param depotIdF  移出倉庫
     * @param depotId  移入倉庫
     * @param beginTime
     * @param endTime
     * @param subType
     * @param request
     * @return
     */
    @GetMapping(value = "/findAllocationDetail")
    @ApiOperation(value = "移倉明細統計")
    public BaseResponseInfo findallocationDetail(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("number") String number,
                                                 @RequestParam("materialParam") String materialParam,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam(value = "depotIdF", required = false) Long depotIdF,
                                                 @RequestParam("beginTime") String beginTime,
                                                 @RequestParam("endTime") String endTime,
                                                 @RequestParam("subType") String subType,
                                                 @RequestParam(value = "roleType", required = false) String roleType,
                                                 @RequestParam("remark") String remark,
                                                 HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            List<Long> depotFList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            if(depotIdF != null) {
                depotFList.add(depotIdF);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotFList.add(object.getLong("id"));
                }
            }
            String [] creatorArray = depotHeadService.getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4InDetail> list = depotHeadService.findAllocationDetail(beginTime, endTime, subType, StringUtil.toNull(number),
                    creatorArray, StringUtil.toNull(materialParam), depotList, depotFList, remark, (currentPage-1)*pageSize, pageSize);
            int total = depotHeadService.findAllocationDetailCount(beginTime, endTime, subType, StringUtil.toNull(number),
                    creatorArray, StringUtil.toNull(materialParam), depotList, depotFList, remark);
            map.put("rows", list);
            map.put("total", total);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 对账单接口
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param organId
     * @param supplierType
     * @param request
     * @return
     */
    @GetMapping(value = "/getStatementAccount")
    @ApiOperation(value = "对账单接口")
    public BaseResponseInfo getStatementAccount(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("beginTime") String beginTime,
                                                 @RequestParam("endTime") String endTime,
                                                 @RequestParam(value = "organId", required = false) Integer organId,
                                                 @RequestParam("supplierType") String supplierType,
                                                 HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String type = "";
            String subType = "";
            String typeBack = "";
            String subTypeBack = "";
            if (("供应商").equals(supplierType)) {
                type = "入庫";
                subType = "采购";
                typeBack = "出庫";
                subTypeBack = "采购退货";
            } else if (("客户").equals(supplierType)) {
                type = "出庫";
                subType = "销售";
                typeBack = "入庫";
                subTypeBack = "销售退货";
            }
            String [] organArray = depotHeadService.getOrganArray(subType, "");
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4StatementAccount> list = depotHeadService.getStatementAccount(beginTime, endTime, organId, organArray,
                    supplierType, type, subType,typeBack, subTypeBack, (currentPage-1)*pageSize, pageSize);
            int total = depotHeadService.getStatementAccountCount(beginTime, endTime, organId, organArray,
                    supplierType, type, subType,typeBack, subTypeBack);
            for(DepotHeadVo4StatementAccount item: list) {
                BigDecimal preNeed = item.getBeginNeed().add(item.getPreDebtMoney()).subtract(item.getPreReturnDebtMoney()).subtract(item.getPreBackMoney());
                item.setPreNeed(preNeed);
                BigDecimal realDebtMoney = item.getDebtMoney().subtract(item.getReturnDebtMoney());
                item.setDebtMoney(realDebtMoney);
                BigDecimal allNeedGet = preNeed.add(realDebtMoney).subtract(item.getBackMoney());
                item.setAllNeed(allNeedGet);
            }
            map.put("rows", list);
            map.put("total", total);
            List<DepotHeadVo4StatementAccount> totalPayList = depotHeadService.getStatementAccountTotalPay(beginTime, endTime, organId, organArray, supplierType, type, subType, typeBack, subTypeBack);
            if(totalPayList.size()>0) {
                DepotHeadVo4StatementAccount totalPayItem = totalPayList.get(0);
                BigDecimal firstMoney = BigDecimal.ZERO;
                BigDecimal lastMoney = BigDecimal.ZERO;
                if(totalPayItem!=null) {
                    firstMoney = totalPayItem.getBeginNeed().add(totalPayItem.getPreDebtMoney()).subtract(totalPayItem.getPreReturnDebtMoney()).subtract(totalPayItem.getPreBackMoney());
                    lastMoney = firstMoney.add(totalPayItem.getDebtMoney()).subtract(totalPayItem.getReturnDebtMoney()).subtract(totalPayItem.getBackMoney());
                }
                map.put("firstMoney", firstMoney); //期初
                map.put("lastMoney", lastMoney);  //期末
            }
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据编号查询单据信息
     * @param number
     * @param request
     * @return
     */
    @GetMapping(value = "/getDetailByNumber")
    @ApiOperation(value = "根据编号查询单据信息")
    public BaseResponseInfo getDetailByNumber(@RequestParam("number") String number,
                                         HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        DepotHeadVo4List dhl = new DepotHeadVo4List();
        try {
            String[] numbers = new String[] {number};
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);
            if(list.size() >= 1) {
                dhl = list.get(0);
                if(dhl.getType().equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
                    if (dhl.getRemark() != null && !dhl.getRemark().isEmpty()) {
                        JSONObject json = JSONObject.parseObject(dhl.getRemark());
                        dhl.setInstall(json.getString("install"));
                        dhl.setRecycle(json.getString("recycle"));
                        dhl.setRemark(json.getString("memo"));
                        if(dhl.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) { // 門市取貨派送
                            JSONObject store = json.getJSONObject("store");
                            dhl.setStoreMan(store.getString("man"));
                            dhl.setStoreName(store.getString("name"));
                            dhl.setStoreAddress(store.getString("address"));
                            dhl.setStorePhone(store.getString("phone"));
                        }
                    } else {
                        dhl.setInstall("");
                        dhl.setRecycle("");
                        if(dhl.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) { // 門市取貨派送
                            dhl.setStoreMan("");
                            dhl.setStoreName("");
                            dhl.setStoreAddress("");
                            dhl.setStorePhone("");
                        }
                    }
                }
            }
            res.code = 200;
            res.data = dhl;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据原单号查询关联的单据列表
     * @param number
     * @param request
     * @return
     */
    @GetMapping(value = "/getBillListByLinkNumber")
    @ApiOperation(value = "根据原单号查询关联的单据列表")
    public BaseResponseInfo getBillListByLinkNumber(@RequestParam("number") String number,
                                              HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        DepotHead dh = new DepotHead();
        try {
            List<DepotHead> list = depotHeadService.getBillListByLinkNumber(number);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 新增單據主表及單據子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addDepotHeadAndDetail")
    @ApiOperation(value = "新增單據主表及單據子表信息")
    public Object addDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws  Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        depotHeadService.addDepotHeadAndDetail(beanJson, rows, request, null);
        return result;
    }

    /**
     * 更新單據主表及單據子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateDepotHeadAndDetail")
    @ApiOperation(value = "更新單據主表及單據子表信息")
    public Object updateDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        depotHeadService.updateDepotHeadAndDetail(beanJson,rows, request);
        return result;
    }

    /**
     * 统计今日采购额、昨日采购额、本月采购额、今年采购额|销售额|零售额
     * @param request
     * @return
     */
    @GetMapping(value = "/getBuyAndSaleStatistics")
    @ApiOperation(value = "统计今日采购额、昨日采购额、本月采购额、今年采购额|销售额|零售额")
    public BaseResponseInfo getBuyAndSaleStatistics(@RequestParam(value = "roleType", required = false) String roleType,
                                                    HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String today = Tools.getNow() + BusinessConstants.DAY_FIRST_TIME;
            String monthFirstDay = Tools.firstDayOfMonth(Tools.getCurrentMonth()) + BusinessConstants.DAY_FIRST_TIME;
            String yesterdayBegin = Tools.getYesterday() + BusinessConstants.DAY_FIRST_TIME;
            String yesterdayEnd = Tools.getYesterday() + BusinessConstants.DAY_LAST_TIME;
            String yearBegin = Tools.getYearBegin() + BusinessConstants.DAY_FIRST_TIME;
            String yearEnd = Tools.getYearEnd() + BusinessConstants.DAY_LAST_TIME;
            Map<String, Object> map = depotHeadService.getBuyAndSaleStatistics(today, monthFirstDay,
                    yesterdayBegin, yesterdayEnd, yearBegin, yearEnd, roleType, request);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据当前用户获取操作员数组，用于控制当前用户的数据权限，限制可以看到的单据范围
     * 注意：该接口提供给部分插件使用，勿删
     * @param request
     * @return
     */
    @GetMapping(value = "/getCreatorByCurrentUser")
    @ApiOperation(value = "根据当前用户获取操作员数组")
    public BaseResponseInfo getCreatorByRoleType(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String creator = "";
            String roleType = redisService.getObjectFromSessionByKey(request,"roleType").toString();
            if(StringUtil.isNotEmpty(roleType)) {
                creator = depotHeadService.getCreatorByRoleType(roleType);
            }
            res.code = 200;
            res.data = creator;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 查询存在欠款的单据
     * @param search
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/debtList")
    @ApiOperation(value = "查询存在欠款的单据")
    public String debtList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                           HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        String organIdStr = StringUtil.getInfo(search, "organId");
        Long organId = Long.parseLong(organIdStr);
        String materialParam = StringUtil.getInfo(search, "materialParam");
        String number = StringUtil.getInfo(search, "number");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        String roleType = StringUtil.getInfo(search, "roleType");
        String status = StringUtil.getInfo(search, "status");
        List<DepotHeadVo4List> list = depotHeadService.debtList(organId, materialParam, number, beginTime, endTime, type, subType, roleType, status);
        if (list != null) {
            objectMap.put("rows", list);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            objectMap.put("rows", new ArrayList<>());
            return returnJson(objectMap, "查找不到数据", ErpInfo.OK.code);
        }
    }

    @GetMapping(value = "/export")
    @ApiOperation(value = "匯出(家電、冷氣)確認書")
    public void export(@ApiParam(value = "配送單單號") @RequestParam(value = "number") String number,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            String[] numbers = new String[]{number};
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);
            DepotHeadVo4List dhl = list.get(0);
            List<Long> idList = new ArrayList<>();
            List<Long> pickupList = new ArrayList<>();
            if (dhl.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP)
                    || dhl.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) {
                pickupList.add(dhl.getId());
            } else {
                idList.add(dhl.getId());
            }

            List<File> files = new ArrayList<>();

            if(!idList.isEmpty()) {
                Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
                        depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
                findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
                    MaterialsListVo vo = materialMap.getValue();
                    File file = ExcelUtils.exportHAConfirm(dhl, vo);
                    files.add(file);
                });
            }
            if(!pickupList.isEmpty()) {
                Map<String, MaterialPickupsListVo> pickupListMap =
                        depotHeadService.findMaterialsPickupListMapByHeaderIdList(pickupList);
                pickupListMap.entrySet().stream().forEach(materialMap->{
                    MaterialPickupsListVo vo = materialMap.getValue();
                    MaterialsListVo mVo = new MaterialsListVo();
                    mVo.setId(vo.getId());
                    mVo.setHeaderId(vo.getHeaderId());
                    mVo.setMaterialsList(vo.getName());
                    mVo.setMaterialCount(vo.getAmount());
                    File file = ExcelUtils.exportHAConfirm(dhl, mVo);
                    files.add(file);
                });
            }

//            Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
//                    depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
//            if(findMaterialsListMapByHeaderIdList.size()==1) {
//                File file = ExcelUtils.exportHAConfirm(dhl, null);
//                files.add(file);
//            } else {
//                findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
//                    File file = ExcelUtils.exportHAConfirm(dhl, materialMap.getValue());
//                    files.add(file);
//                });
//
//            }
            if(!files.isEmpty()) {
//                if(files.size() == 1) {
//                    File file = files.get(0);
//                    ExportExecUtil.showExec(file, file.getName(), response);
//                } else {
                    ExportExecUtil.showExecs(files, response);
//                }

                files.stream().forEach(File::delete);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/exportPicking")
    @ApiOperation(value = "匯出撿貨單")
    public void exportPicking(@ApiParam(value = "配送單單號") @RequestParam(value = "numbers") String[] numbers,
                               @ApiParam(value = "細單單號") @RequestParam(value = "subIds") Long[] subIds,
                               HttpServletRequest request, HttpServletResponse response) {
        if(numbers.length > 20) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_EXPORT_PICKING_MAX_CODE,
                    ExceptionConstants.DEPOT_HEAD_EXPORT_PICKING_MAX_MSG);
        }

        try {
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);

            File outputFile;
            List<File> files = new ArrayList<>();
            for (DepotHeadVo4List depotHeadVo4List : list) {
                List<Long> idList = new ArrayList<>();

                if (depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP)
                        || depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_EXPORT_PICKING_TYPE_ERROR_CODE,
                            ExceptionConstants.DEPOT_HEAD_EXPORT_PICKING_TYPE_ERROR_MSG);
                } else {
                    idList.add(depotHeadVo4List.getId());
                }

                if(!idList.isEmpty()) {
                    Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
                            depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
                        MaterialsListVo vo = materialMap.getValue();
                        if(Arrays.stream(subIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            depotHeadVo4List.setDepotList(vo.getDepotList());
                            depotHeadVo4List.setMaterialNumber(vo.getMaterialNumber());
                            depotHeadVo4List.setMaterialsList(vo.getMaterialsModel()+"@"+vo.getMaterialsStandard());
                            depotHeadVo4List.setMaterialCount(vo.getMaterialCount());
                        }
                    });
                }
            }

            if(list.size() > 0) {
                User userInfo = userService.getCurrentUser();
                String name = "Server";
                if(userInfo!=null) {
                    name = userInfo.getUsername();
                }
                File file = ExcelUtils.exportPicking(list, name);
                ExportExecUtil.showExec(file, file.getName(), response);
                file.delete();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping(value = "/export/list")
    @ApiOperation(value = "批次匯出確認書")
    public void exportList(@ApiParam(value = "配送單單號") @RequestParam(value = "numbers") String[] numbers,
                           @ApiParam(value = "細單單號") @RequestParam(value = "subIds") Long[] subIds,
                           HttpServletRequest request, HttpServletResponse response) {
        try {
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);

            List<File> files = new ArrayList<>();
            for (DepotHeadVo4List depotHeadVo4List : list) {
                List<Long> idList = new ArrayList<>();
                List<Long> pickupList = new ArrayList<>();
                if (depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP)
                        || depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) {
                    pickupList.add(depotHeadVo4List.getId());
                } else {
                    idList.add(depotHeadVo4List.getId());
                }

                if(!idList.isEmpty()) {
                    Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
                            depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
                        MaterialsListVo vo = materialMap.getValue();
                        if(Arrays.stream(subIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
                            files.add(file);
                        }
                    });
                }
                if(!pickupList.isEmpty()) {
                    Map<String, MaterialPickupsListVo> pickupListMap =
                            depotHeadService.findMaterialsPickupListMapByHeaderIdList(pickupList);
                    pickupListMap.entrySet().stream().forEach(materialMap->{
                        MaterialPickupsListVo vo = materialMap.getValue();
                        if(Arrays.stream(subIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            MaterialsListVo mVo = new MaterialsListVo();
                            mVo.setId(vo.getId());
                            mVo.setHeaderId(vo.getHeaderId());
                            mVo.setMaterialsList(vo.getName());
                            mVo.setMaterialCount(vo.getAmount());
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, mVo);
                            files.add(file);
                        }
                    });
                }

//                Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
//                        depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
//                if(findMaterialsListMapByHeaderIdList.size()==1) {
//                    File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, null);
//                    files.add(file);
//                } else {
//                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
//                        MaterialsListVo vo = materialMap.getValue();
//                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
//                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
//                            files.add(file);
//                        }
//                    });
//                }
            }
            if(!files.isEmpty()) {
                ExportExecUtil.showExecs(files, response);

                files.stream().forEach(File::delete);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/print/list")
    @ApiOperation(value = "批次列印確認書")
    public void printList(@ApiParam(value = "配送單單號") @RequestParam(value = "numbers") String[] numbers,
                           @ApiParam(value = "細單單號") @RequestParam(value = "subIds") Long[] sudIds,
                           HttpServletRequest request, HttpServletResponse response) {
        try {
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);

            List<File> files = new ArrayList<>();
            for (DepotHeadVo4List depotHeadVo4List : list) {
                List<Long> idList = new ArrayList<>();
                List<Long> pickupList = new ArrayList<>();
                if (depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP)
                        || depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) {
                    pickupList.add(depotHeadVo4List.getId());
                } else {
                    idList.add(depotHeadVo4List.getId());
                }

                if(!idList.isEmpty()) {
                    Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
                            depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
                        MaterialsListVo vo = materialMap.getValue();
                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
                            files.add(file);
                        }
                    });
                }
                if(!pickupList.isEmpty()) {
                    Map<String, MaterialPickupsListVo> pickupListMap =
                            depotHeadService.findMaterialsPickupListMapByHeaderIdList(pickupList);
                    pickupListMap.entrySet().stream().forEach(materialMap->{
                        MaterialPickupsListVo vo = materialMap.getValue();
                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            MaterialsListVo mVo = new MaterialsListVo();
                            mVo.setId(vo.getId());
                            mVo.setHeaderId(vo.getHeaderId());
                            mVo.setMaterialsList(vo.getName());
                            mVo.setMaterialCount(vo.getAmount());
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, mVo);
                            files.add(file);
                        }
                    });
                }

//                Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
//                        depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
//                if(findMaterialsListMapByHeaderIdList.size()==1) {
//                    File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, null);
//                    files.add(file);
//                } else {
//                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
//                        MaterialsListVo vo = materialMap.getValue();
//                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
//                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
//                            files.add(file);
//                        }
//                    });
//                }
            }
            if(!files.isEmpty()) {
                depotHeadService.setPrintData(sudIds);
                File file = files.get(0);
                ExportExecUtil.showExec(file, file.getName(), response);
//                ExportExecUtil.showExecs(files, response);

                files.stream().forEach(File::delete);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格匯入配送單")
    public BaseResponseInfo importExcel(MultipartFile file,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = depotHeadService.importExcel(file, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @GetMapping(value = "/getDeliveryData")
    @ApiOperation(value = "取得配送單狀態")
    public BaseResponseInfo getDeliveryData(@RequestParam("number") String number, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        DepotHeadDelivery dhd = new DepotHeadDelivery();
        try {
            String[] numbers = new String[] {number};

            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);
            if(list.size() >= 1) {
                DepotHeadVo4List dhl = list.get(0);
                if(dhl.getType().equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
                    dhd = depotHeadService.getDeliveryDetail(dhl);
                }
            }

            res.code = 200;
            res.data = dhd;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取資料失敗";
        }
        return res;
    }

    @GetMapping(value = "/getDeliveryStatus")
    @ApiOperation(value = "取得配送狀態")
    public BaseResponseInfo getDeliveryStatus(@RequestParam("headerId") Long headerId, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();

        try {
            res.code = 200;
            res.data = depotHeadService.getDeliveryStatus(headerId);
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取資料失敗";
        }

        return res;
    }

    @GetMapping(value = "/getDeliveryAgreedData")
    @ApiOperation(value = "取得約配記錄")
    public BaseResponseInfo getDeliveryAgreedData(@RequestParam("headerId") Long headerId, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();

        try {
            res.code = 200;
            res.data = depotHeadService.getAgreedData(headerId);
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取資料失敗";
        }

        return res;
    }

    @GetMapping(value = "/getDeliveryReport")
    @ApiOperation(value = "取得司機回報")
    public BaseResponseInfo getDeliveryReport(@RequestParam("headerId") Long headerId, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();

        try {
            res.code = 200;
            res.data = depotHeadService.getDeliveryReport(headerId);
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取資料失敗";
        }

        return res;
    }

    @PutMapping(value = "/feedBackReport/{id}")
    @ApiOperation(value = "客服回覆")
    public Object feedBackReport(@PathVariable("id") Long id, @RequestBody DepotReportVo4Body body, HttpServletRequest request) throws Exception {
        JSONObject result = ExceptionConstants.standardSuccess();
        depotHeadService.feedBackReport(id, body.getFeedback(), request);
        return result;
    }

    @PutMapping(value = "/delivery/assign")
    @ApiOperation(value = "司機派發")
    public Object deliveryAssign(@RequestBody DepotDetailVo4Body body,
                                 HttpServletRequest request) throws Exception {
        JSONObject result = ExceptionConstants.standardSuccess();
        depotHeadService.assignDelivery(body.getHeaderId(), body.getDriverId(), body.getAssignDate(), body.getAssignUser(), request);
        return result;
    }

    @PutMapping(value = "/delivery/unAssign/{headerId}")
    @ApiOperation(value = "重新指派(司機取消)")
    public Object deliveryUnAssign(@PathVariable("headerId") Long headerId, HttpServletRequest request) throws Exception {
        JSONObject result = ExceptionConstants.standardSuccess();
        depotHeadService.unAssignDelivery(headerId, request);
        return result;
    }

    @GetMapping(value = "/driver/findByAll")
    @ApiOperation(value = "司機配送統計")
    public BaseResponseInfo findByAllDriver(@ApiParam(value = "司機名稱(帶入空或全部，查詢全部)", required = true)
                                                @RequestParam("driverName") String driverName,
                                            @ApiParam(value = "車牌號號(帶入空或全部，查詢全部)", required = true)
                                            @RequestParam("licensePlateNumber") String licensePlateNumber,
                                            @RequestParam(value = "beginDateTime", required = false) String beginDateTime,
                                            @RequestParam(value = "endDateTime", required = false) String endDateTime,
                                            @ApiParam(value = "關鍵字")
                                                @RequestParam(required = false, value = "keyword") String keyword,
                                            @RequestParam("currentPage") Integer currentPage,
                                            @RequestParam("pageSize") Integer pageSize,
                                            HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> objectMap = new HashMap<>();
        try {
            if(currentPage < 1) {
                currentPage = 1;
            }
            if(pageSize < 1) {
                pageSize = null;
            }
            Integer offset = 0;
            if(pageSize != null) {
                offset = (currentPage - 1) * pageSize;
            }

            res.code = 200;
            objectMap.put("total", depotHeadService.countDriverDelivery(driverName, licensePlateNumber, beginDateTime, endDateTime, keyword));
            objectMap.put("rows", depotHeadService.getDriverDelivery(driverName, licensePlateNumber, beginDateTime, endDateTime, keyword, offset, pageSize, request));
            res.data = objectMap;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取資料失敗";
        }

        return res;
    }

    @GetMapping("/driver/findByStatus")
    @ApiOperation(value = "")
    public BaseResponseInfo findByStatus(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize,
                                         HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> objectMap = new HashMap<>();

        return res;
    }

    @GetMapping(value = "/print/list-path")
    @ApiOperation(value = "批次列印確認書")
    public BaseResponseInfo printListPath(@ApiParam(value = "配送單單號") @RequestParam(value = "numbers") String[] numbers,
                                      @ApiParam(value = "細單單號") @RequestParam(value = "subIds") Long[] sudIds,
                                      HttpServletRequest request, HttpServletResponse response) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<DepotHeadVo4List> list = depotHeadService.getDetailByNumber(numbers);

            List<File> files = new ArrayList<>();
            for (DepotHeadVo4List depotHeadVo4List : list) {
                List<Long> idList = new ArrayList<>();
                List<Long> pickupList = new ArrayList<>();
                if (depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP)
                        || depotHeadVo4List.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1)) {
                    pickupList.add(depotHeadVo4List.getId());
                } else {
                    idList.add(depotHeadVo4List.getId());
                }

                if(!idList.isEmpty()) {
                    Map<String, MaterialsListVo> findMaterialsListMapByHeaderIdList =
                            depotHeadService.findMaterialsListMapByHeaderIdList(idList, Boolean.TRUE);
                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
                        MaterialsListVo vo = materialMap.getValue();
                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
                            files.add(file);
                        }
                    });
                }
                if(!pickupList.isEmpty()) {
                    Map<String, MaterialPickupsListVo> pickupListMap =
                            depotHeadService.findMaterialsPickupListMapByHeaderIdList(pickupList);
                    pickupListMap.entrySet().stream().forEach(materialMap->{
                        MaterialPickupsListVo vo = materialMap.getValue();
                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
                            MaterialsListVo mVo = new MaterialsListVo();
                            mVo.setId(vo.getId());
                            mVo.setHeaderId(vo.getHeaderId());
                            mVo.setMaterialsList(vo.getName());
                            mVo.setMaterialCount(vo.getAmount());
                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, mVo);
                            files.add(file);
                        }
                    });
                }

//                if(findMaterialsListMapByHeaderIdList.size()==1) {
//                    File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, null);
//                    files.add(file);
//                } else {
//                    findMaterialsListMapByHeaderIdList.entrySet().stream().forEach(materialMap->{
//                        MaterialsListVo vo = materialMap.getValue();
//                        if(Arrays.stream(sudIds).filter(subId->subId==vo.getId()).findFirst().isPresent()) {
//                            File file = ExcelUtils.exportHAConfirm(depotHeadVo4List, vo);
//                            files.add(file);
//                        }
//                    });
//                }
            }
            if(!files.isEmpty()) {
                depotHeadService.setPrintData(sudIds);

                List<String> fileNames = new ArrayList<>();
                for(File file: files) {
                    if(file != null) {
                        CustomMultipartFile multipartFile = new CustomMultipartFile(file);
                        fileNames.add(uploadLocal(multipartFile, "excel", ""));
                    }
                }

                res.code = 200;
                res.data = fileNames;

                files.stream().forEach(File::delete);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private String uploadLocal(MultipartFile mf,String bizPath,String name){
        try {
            String ctxPath = filePath;
            String fileName = null;
            File file = new File(ctxPath + File.separator + bizPath + File.separator );
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String orgName = mf.getOriginalFilename();// 获取文件名
            orgName = FileUtils.getFileName(orgName);
            if(orgName.indexOf(".")!=-1){
                if(StringUtil.isNotEmpty(name)) {
                    fileName = name.substring(0, name.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
                } else {
                    fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
                }
            }else{
                fileName = orgName+ "_" + System.currentTimeMillis();
            }
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String dbpath = null;
            if(StringUtil.isNotEmpty(bizPath)){
                dbpath = bizPath + File.separator + fileName;
            }else{
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }
}
