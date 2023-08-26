package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.Vehicle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VehicleMapperEx {

    List<Vehicle> selectByConditionVehicle(
            @Param("license") String license,
            @Param("brand") String brand,
            @Param("driver") String driver,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByVehicle(
            @Param("license") String license,
            @Param("brand") String brand,
            @Param("driver") String driver);
}
