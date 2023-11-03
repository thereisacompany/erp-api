package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotCounter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepotCounterMapper {

    List<DepotCounter> selectAll(@Param("depotId") Long depotId);

    DepotCounter selectByPrimaryKey(Long id);


}
