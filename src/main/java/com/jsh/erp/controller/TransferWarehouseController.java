package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.DepotHeadVo4Body;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.utils.ErpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;


@RestController
@RequestMapping(value = "/transferDepot")
@Api(tags = {"移倉管理"})
public class TransferWarehouseController {

    private Logger logger = LoggerFactory.getLogger(TransferWarehouseController.class);

    @Resource
    private DepotHeadService depotHeadService;



    /**
     * 新增移倉單
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增移倉單")
    public Object add(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws  Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        depotHeadService.addTransferDepotHead(beanJson, rows, request);
        return result;
    }

    /**
     * 更新移倉單
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新移倉單")
    public Object update(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        depotHeadService.updateTransferDepotHead(beanJson,rows, request);
        return result;
    }

    /**
     * 批量設置狀態-移倉完成
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量設置狀態-移倉完成")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception{
        Map<String, Object> objectMap = new HashMap<>();
        String ids = jsonObject.getString("ids");
        int res = depotHeadService.batchSetStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
