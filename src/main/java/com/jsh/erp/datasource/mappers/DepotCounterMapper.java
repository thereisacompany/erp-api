package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotCounter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepotCounterMapper {

    List<DepotCounter> selectByConditionDepot(
            @Param("name") String name,
            @Param("depotId") Long depotId,
            @Param("remark") String remark,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByCounter(
            @Param("name") String name,
            @Param("depotId") Long depotId,
            @Param("remark") String remark);

    DepotCounter selectByPrimaryKey(Long id);

    int insertSelective(DepotCounter record);

    int updateByPrimaryKeySelective(DepotCounter record);
}
