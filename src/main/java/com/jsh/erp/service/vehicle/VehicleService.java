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
import com.jsh.erp.utils.ExcelUtils;
import com.jsh.erp.utils.StringUtil;
import jxl.Sheet;
import jxl.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if(vehicle.getLicensePlateNumber() != null && !vehicle.getLicensePlateNumber().isEmpty()) {
            Vehicle existVehiclePlateNumber = vehicleMapper.selectByLicensePlateNumber(vehicle.getLicensePlateNumber());
            if(existVehiclePlateNumber != null) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_LICENSE_PLATE_NUMBER_EXIST_CODE,
                        ExceptionConstants.VEHICLE_LICENSE_PLATE_NUMBER_EXIST_MSG);
            }
        }

        // 檢查此駕駛是否已綁定過車輛
        if(vehicle.getDriver() != null && !vehicle.getDriver().isEmpty()) {
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

        // 檢查此駕駛是否已綁定過車輛(並排除是設定在自已身上的)
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

        if(vehicle.getLicensePlateNumber() != null && !vehicle.getLicensePlateNumber().isEmpty()) {
            if(vehicleMapper.isDriverLicensePlateNumberExist(vehicle.getLicensePlateNumber(), vehicle.getId()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.VEHICLE_LICENSE_PLATE_NUMBER_EXIST_CODE,
                        ExceptionConstants.VEHICLE_LICENSE_PLATE_NUMBER_EXIST_MSG);
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

    public String importExcel(MultipartFile file, HttpServletRequest request) throws Exception{
        String msg = "";
        Workbook workbook = Workbook.getWorkbook(file.getInputStream());
        Sheet src = workbook.getSheet(0);
        List<Vehicle> vList = new ArrayList<>();
        Map<String, String> importError = new HashMap<>();
        for (int i = 1; i < src.getRows(); i++) {
            String licensePlateNumber = ExcelUtils.getContent(src, i, 0);

            if(StringUtil.isNotEmpty(licensePlateNumber)) {
                Vehicle existVehiclePlateNumber = vehicleMapper.selectByLicensePlateNumber(licensePlateNumber);
                if(existVehiclePlateNumber != null) {
                    importError.put(""+i, ExceptionConstants.VEHICLE_LICENSE_PLATE_NUMBER_EXIST_MSG);
                    continue;
                }
                String brand = ExcelUtils.getContent(src, i, 1);
                if(StringUtil.isEmpty(brand)) {
                    importError.put(""+i, "品牌型號未填寫");
                    continue;
                }

                Vehicle v = new Vehicle();
                v.setLicensePlateNumber(licensePlateNumber);
                v.setBrandModel(brand);
                v.setColor(ExcelUtils.getContent(src, i, 2));
                String mileage = ExcelUtils.getContent(src, i, 3);
                if(StringUtil.isNotEmpty(mileage)) {
                    v.setMileage(Integer.valueOf(mileage));
                }
                v.setEngineNumber(ExcelUtils.getContent(src, i, 4));
                v.setManufacture(ExcelUtils.getContent(src, i, 5));
                v.setTestDate(ExcelUtils.getContent(src, i, 6));
                v.setInsuranceDate(ExcelUtils.getContent(src, i, 7));
                v.setInsuranceDateEnd(ExcelUtils.getContent(src, i, 8));
                v.setTakeOver(ExcelUtils.getContent(src, i, 9));
                v.setLoanDue(ExcelUtils.getContent(src, i, 10));
                v.setContractExpired(ExcelUtils.getContent(src, i, 11));
                v.setRenewalDate(ExcelUtils.getContent(src, i, 12));
                v.setLicenseValid(ExcelUtils.getContent(src, i, 13));
                v.setCargoInsuranceDue(ExcelUtils.getContent(src, i, 14));
                String emissions = ExcelUtils.getContent(src, i, 15);
                if(StringUtil.isNotEmpty(emissions)) {
                    v.setEmissions(Integer.valueOf(emissions));
                }
                String price = ExcelUtils.getContent(src, i, 16);
                if(StringUtil.isNotEmpty(price)) {
                    v.setPrice(Integer.valueOf(price));
                }

                String status = ExcelUtils.getContent(src, i, 17);
                if(StringUtil.isNotEmpty(status)) {
                    String[] tmp = status.split("-");
                    v.setStatus(Integer.valueOf(tmp[0]));
                }

                String ownerShip = ExcelUtils.getContent(src, i, 18);
                if(StringUtil.isNotEmpty(ownerShip)) {
                    String[] ship = ownerShip.split("-");
                    v.setOwnership(Integer.valueOf(ship[0]));
                }
                v.setDriver("");

                vList.add(v);
            } else {
                importError.put(""+i, "車牌號碼未填寫");
            }
        }
        if(importError.isEmpty()) {
            vList.forEach(vData-> vehicleMapper.insertSelective(vData));
        } else {
            StringBuffer sb= new StringBuffer();
            sb.append("請檢查文件資料，匯入失敗列數:\n");
            importError.entrySet().stream().forEach(value->{
                sb.append("Excel文件第"+value.getKey()+"列");
                sb.append("->");
                sb.append(value.getValue());
                sb.append("\n");
            });
            msg = sb.toString();
        }
        return msg;
    }

}
