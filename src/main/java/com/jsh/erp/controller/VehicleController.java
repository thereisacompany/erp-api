package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.Vehicle;
import com.jsh.erp.datasource.vo.VehicleLicenseNumberListVo;
import com.jsh.erp.service.vehicle.VehicleService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/vehicle")
@Api(tags = {"車輛管理"})
public class VehicleController {

    private Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Resource
    private VehicleService vehicleService;

    @GetMapping(value = "/getList")
    @ApiOperation(value = "取得車輛列表")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "ok", response = Vehicle.class, responseContainer = "List")})
    public BaseResponseInfo getVehicleList(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            List<Vehicle> dataList = vehicleService.getVehicle();
            if (null != dataList) {
                data.put("vehicleList", dataList);
            }
            res.code = 200;
            res.data = data;

        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取失敗";
        }
        return res;
    }

    @GetMapping(value = "/licenseNumber/list")
    @ApiOperation(value = "車牌列表(下拉選項)")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "ok", response = VehicleLicenseNumberListVo.class, responseContainer = "List")})
    public BaseResponseInfo getLicenseNumber(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            List<VehicleLicenseNumberListVo> vList = new ArrayList<>();
            List<Vehicle> dataList = vehicleService.getLicenseNumber();
            if (null != dataList) {
                dataList.stream().forEach(vehicle -> {
                    VehicleLicenseNumberListVo vo = new VehicleLicenseNumberListVo();
                    vo.setLicensePlateNumber(vehicle.getLicensePlateNumber());
                    if(vehicle.getDriver() != null && !vehicle.getDriver().isEmpty()) {
                        vo.setIsBind(1);
                    } else {
                        vo.setIsBind(0);
                    }
                    vList.add(vo);
                });
                data.put("licenseNumberList", vList);
            } else {
                data.put("licenseNumberList", vList);
            }
            res.code = 200;
            res.data = data;

        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "獲取失敗";
        }
        return res;
    }

    @PutMapping(value = "/driver/bind/{id}")
    @ApiOperation(value = "綁定車輛駕駛")
    public Object bindDriver(@PathVariable Long id, @RequestParam(value = "driver") String driver, HttpServletRequest request) {
        JSONObject result = ExceptionConstants.standardSuccess();
        vehicleService.updateVehicleDriver(id, driver, request);
        return result;
    }

    @PutMapping(value = "/driver/unbind/{id}")
    @ApiOperation(value = "解除綁定車輛駕駛")
    public Object unbindDriver(@PathVariable Long id, HttpServletRequest request) {
        JSONObject result = ExceptionConstants.standardSuccess();
        vehicleService.updateVehicleDriver(id, null, request);
        return result;
    }

}
