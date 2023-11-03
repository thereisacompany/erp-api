package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotCounter.DepotCounterService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.userBusiness.UserBusinessService;
import com.jsh.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

@RestController
@RequestMapping(value = "/depot")
@Api(tags = {"倉庫管理"})
public class DepotController {
    private Logger logger = LoggerFactory.getLogger(DepotController.class);

    @Resource
    private DepotService depotService;

    @Resource
    private DepotCounterService depotCounterService;

    @Resource
    private UserBusinessService userBusinessService;

    @Resource
    private MaterialService materialService;

    @GetMapping(value = "/counter/getAllList")
    @ApiOperation(value = "儲位列表")
    public BaseResponseInfo getCounterAllList(@RequestParam(value = "depotId", required = false) Long depotId,
                                              HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<DepotCounter> counterList = depotCounterService.getAllList(depotId);
            res.code = 200;
            res.data = counterList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取數據失敗";
        }
        return res;
    }

    @GetMapping(value = "/counter/{id}")
    @ApiOperation(value = "取得指定儲位")
    public String getCounter(@PathVariable("id") Long id, HttpServletRequest request) {
        Object obj = depotCounterService.getCounter(id);
        Map<String, Object> objectMap = new HashMap<>();
        if(obj != null) {
            objectMap.put("info", obj);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 倉庫列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getAllList")
    @ApiOperation(value = "倉庫列表")
    public BaseResponseInfo getAllList(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<Depot> depotList = depotService.getAllList();
            res.code = 200;
            res.data = depotList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取數據失敗";
        }
        return res;
    }

    /**
     * 用户对应倉庫显示
     * @param type
     * @param keyId
     * @param request
     * @return
     */
    @GetMapping(value = "/findUserDepot")
    @ApiOperation(value = "用户对应倉庫显示")
    public JSONArray findUserDepot(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                 HttpServletRequest request) throws Exception{
        JSONArray arr = new JSONArray();
        try {
            //获取权限信息
            String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, keyId);
            List<Depot> dataList = depotService.findUserDepot();
            //开始拼接json数据
            JSONObject outer = new JSONObject();
            outer.put("id", 0);
            outer.put("key", 0);
            outer.put("value", 0);
            outer.put("title", "倉庫列表");
            outer.put("attributes", "倉庫列表");
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (Depot depot : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", depot.getId());
                    item.put("key", depot.getId());
                    item.put("value", depot.getId());
                    item.put("title", depot.getName());
                    item.put("attributes", depot.getName());
                    Boolean flag = ubValue.contains("[" + depot.getId().toString() + "]");
                    if (flag) {
                        item.put("checked", true);
                    }
                    dataArray.add(item);
                }
            }
            outer.put("children", dataArray);
            arr.add(outer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 获取当前用户拥有权限的倉庫列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findDepotByCurrentUser")
    @ApiOperation(value = "获取当前用户拥有权限的倉庫列表")
    public BaseResponseInfo findDepotByCurrentUser(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONArray arr = depotService.findDepotByCurrentUser();
            res.code = 200;
            res.data = arr;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取數據失敗";
        }
        return res;
    }

    /**
     * 更新默认倉庫
     * @param object
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/updateIsDefault")
    @ApiOperation(value = "更新默认倉庫")
    public String updateIsDefault(@RequestBody JSONObject object,
                                       HttpServletRequest request) throws Exception{
        Long depotId = object.getLong("id");
        Map<String, Object> objectMap = new HashMap<>();
        int res = depotService.updateIsDefault(depotId);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 倉庫列表-带库存
     * @param mId
     * @param request
     * @return
     */
    @GetMapping(value = "/getAllListWithStock")
    @ApiOperation(value = "倉庫列表-带库存")
    public BaseResponseInfo getAllList(@RequestParam("mId") Long mId,
                                       HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<Depot> list = depotService.getAllList();
            List<DepotEx> depotList = new ArrayList<DepotEx>();
            for(Depot depot: list) {
                DepotEx de = new DepotEx();
                if(mId!=0) {
                    BigDecimal initStock = materialService.getInitStock(mId, depot.getId());
                    BigDecimal currentStock = materialService.getCurrentStockByMaterialIdAndDepotId(mId, depot.getId());
                    de.setInitStock(initStock);
                    de.setCurrentStock(currentStock);
                    MaterialInitialStock materialInitialStock = materialService.getSafeStock(mId, depot.getId());
                    de.setLowSafeStock(materialInitialStock.getLowSafeStock());
                    de.setHighSafeStock(materialInitialStock.getHighSafeStock());
                } else {
                    de.setInitStock(BigDecimal.ZERO);
                    de.setCurrentStock(BigDecimal.ZERO);
                }
                de.setId(depot.getId());
                de.setName(depot.getName());
                depotList.add(de);
            }
            res.code = 200;
            res.data = depotList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取數據失敗";
        }
        return res;
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = depotService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
