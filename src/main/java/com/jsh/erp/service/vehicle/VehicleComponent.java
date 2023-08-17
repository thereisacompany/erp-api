package com.jsh.erp.service.vehicle;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.ICommonQuery;
import com.jsh.erp.service.user.UserResource;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.QueryUtils;
import com.jsh.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "vehicle_component")
@UserResource
public class VehicleComponent implements ICommonQuery {

    @Resource
    private VehicleService vehicleService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return vehicleService.getVehicle(id);
    }

    @Override
    public List<?> select(Map<String, String> map) throws Exception {
        return getVehicleList(map);
    }

    private List<?> getVehicleList(Map<String, String> map) {
        String search = map.get(Constants.SEARCH);
        String licensePlateNumber = StringUtil.getInfo(search, "licensePlateNumber");
        String brandModel = StringUtil.getInfo(search, "brandModel");
        String driver = StringUtil.getInfo(search, "driver");

        return vehicleService.select(licensePlateNumber, brandModel, driver, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map) throws Exception {
        String search = map.get(Constants.SEARCH);
        String licensePlateNumber = StringUtil.getInfo(search, "licensePlateNumber");
        String brandModel = StringUtil.getInfo(search, "brandModel");
        String driver = StringUtil.getInfo(search, "driver");
        return vehicleService.countVehicle(licensePlateNumber, brandModel, driver);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception {
        return vehicleService.insertVehicle(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request) throws Exception {
        return vehicleService.updateVehicle(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request) throws Exception {
        return 0;
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request) throws Exception {
        return 0;
    }

    @Override
    public int checkIsNameExist(Long id, String name) throws Exception {
        return vehicleService.checkIsNameExist(id, name);
    }
}
