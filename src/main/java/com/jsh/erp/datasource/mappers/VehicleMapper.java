package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.Vehicle;
import com.jsh.erp.datasource.entities.VehicleExample;

import java.util.List;

public interface VehicleMapper {

    long countByExample(VehicleExample example);

    List<Vehicle> selectByExample(VehicleExample example);

    Vehicle selectByPrimaryKey(Long id);

    int insertSelective(Vehicle vehicle);

    int updateByPrimaryKeySelective(Vehicle vehicle);

    int isDriverExist(String driver, Long id);
}
