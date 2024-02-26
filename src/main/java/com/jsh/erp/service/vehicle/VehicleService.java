package com.jsh.erp.service.vehicle;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.VehicleMapper;
import com.jsh.erp.datasource.mappers.VehicleMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.supplier.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {

    private Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Resource
    private VehicleMapper vehicleMapper;

    @Resource
    private VehicleMapperEx vehicleMapperEx;

    @Resource
    private SupplierService supplierService;

    @Resource
    private LogService logService;

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertVehicle(JSONObject obj, HttpServletRequest request) {
        Vehicle vehicle = JSONObject.parseObject(obj.toJSONString(), Vehicle.class);

        // 檢查此駕駛是否已綁定過車輛
        if(vehicle.getDriver() != null) {
            if(vehicleMapper.isDriverExist(vehicle.getDriver(), null) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_CODE,
                        ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_MSG);
            }

            try {
                if (supplierService.findById(Long.parseLong(vehicle.getDriver())).isEmpty()) {
                    throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                            ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
                }
            } catch (Exception e) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                        ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
            }
        }

        int result=0;
        try{
            result=vehicleMapper.insertSelective(vehicle);
            logService.insertLog("車輛", new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                    .append(vehicle.getLicensePlateNumber()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateVehicle(JSONObject obj, HttpServletRequest request)throws Exception {
        Vehicle vehicle = JSONObject.parseObject(obj.toJSONString(), Vehicle.class);

        // 檢查此駕駛是否已綁定過車輛(並排除是設定在自已身上的
        if(vehicle.getDriver() != null && !vehicle.getDriver().isEmpty()) {
            if(vehicleMapper.isDriverExist(vehicle.getDriver(), vehicle.getId()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_CODE,
                        ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_MSG);
            }
            try {
                if (supplierService.findById(Long.parseLong(vehicle.getDriver())).isEmpty()) {
                    throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                            ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
                }
            } catch (Exception e) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                        ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
            }
        }

        int result=0;
        try{
            result = vehicleMapper.updateByPrimaryKeySelective(vehicle);
            logService.insertLog("車輛", new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                    .append(vehicle.getLicensePlateNumber()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        VehicleExample example = new VehicleExample();
        example.createCriteria().andLicensePlateNumberNotEqualTo(name);
        List<Vehicle> list =null;
        try{
            list=  vehicleMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    public Vehicle getVehicle(long id)throws Exception {
        Vehicle result=null;
        try{
            result = vehicleMapper.selectByPrimaryKey(id);

            Supplier supplier = supplierService.getSupplier(Long.parseLong(result.getDriver()));
            if(supplier.getSupplier()!=null) {
                result.setDriverName(supplier.getSupplier());
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<Vehicle> getVehicle() {
        VehicleExample example = new VehicleExample();

        List<Vehicle> list = null;
        try{
            list = vehicleMapper.selectByExample(example);
            list.stream().forEach(vehicle -> {
                try {
                    Supplier supplier = supplierService.getSupplier(Long.parseLong(vehicle.getDriver()));
                    if(supplier.getSupplier()!=null) {
                        vehicle.setDriverName(supplier.getSupplier());
                    }
                } catch (Exception e) {
                    vehicle.setDriverName("");
//                    throw new RuntimeException(e);
                }
            });
        }catch(Exception e){
            JshException.readFail(logger, e);
        }

        return list;
    }

    public List<Vehicle> getLicenseNumber() {
        List<Vehicle> list = null;
        try {
            list = vehicleMapper.selectVehicleLicenseNumber();

        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Vehicle> select(String license, String brand, String driver, int offset, int rows) {
        List<Vehicle> resList= new ArrayList<>();

        try {
            List<Vehicle> list = vehicleMapperEx.selectByConditionVehicle(license, brand, driver, offset, rows);
            list.stream().forEach(vehicle -> {
                try {
                    Supplier supplier = supplierService.getSupplier(Long.parseLong(vehicle.getDriver()));
                    if(supplier.getSupplier()!=null) {
                        vehicle.setDriverName(supplier.getSupplier());
                    }
                } catch (Exception e) {
                    vehicle.setDriverName("");
//                    throw new RuntimeException(e);
                }
                resList.add(vehicle);
            });
        }catch(Exception e){
            JshException.readFail(logger, e);
        }

        return resList;
    }

    public Long countVehicle(String license, String brand, String driver) throws Exception{
        Long result=null;
        try{
            result = vehicleMapperEx.countsByVehicle(license, brand, driver);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateVehicleDriver(Long id, String driver, HttpServletRequest request) {
        Vehicle vehicle = vehicleMapper.selectByPrimaryKey(id);
        if(vehicle == null) {
            throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_NO_EXIST_CODE,
                    String.format(ExceptionConstants.VEHICLE_NO_EXIST_MSG));
        }
        String title ="車輛綁定";
        if(driver != null) { // 綁定
            if(vehicleMapper.isDriverExist(driver, id) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_CODE,
                        String.format(ExceptionConstants.VEHICLE_HAD_DRIVER_FAILED_MSG));
            }
            try {
                if (supplierService.findById(Long.parseLong(vehicle.getDriver())).isEmpty()) {
                    throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                            ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
                }
            } catch (Exception e) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_CODE,
                        ExceptionConstants.VEHICLE_DRIVER_NO_EXIST_MSG);
            }
            vehicle.setDriver(driver);
        } else {
            title = "車輛解除綁定";
            vehicle.setDriver("");
        }

        try{
            vehicleMapper.updateByPrimaryKeySelective(vehicle);
            logService.insertLog(title, new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                    .append(vehicle.getId()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
    }

}
