package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.jsh.erp.datasource.entities.Vehicle;
import com.jsh.erp.service.vehicle.VehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public JSONArray getVehicleList(HttpServletRequest request) {
        JSONArray dataArray = new JSONArray();

        List<Vehicle> list = vehicleService.getVehicle();

        return dataArray;
    }
}
