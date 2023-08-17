package com.jsh.erp.service.vehicle;

import com.jsh.erp.datasource.entities.Vehicle;
import com.jsh.erp.datasource.entities.VehicleExample;
import com.jsh.erp.datasource.mappers.VehicleMapper;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VehicleService {

    private Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Resource
    private VehicleMapper vehicleMapper;

    @Resource
    private LogService logService;


    public List<Vehicle> getVehicle() {
        VehicleExample example = new VehicleExample();

        List<Vehicle> list = null;
        try{
            list = vehicleMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }

        return list;
    }
}
