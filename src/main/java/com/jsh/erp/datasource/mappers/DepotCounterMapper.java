package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotCounter;

import java.util.List;

public interface DepotCounterMapper {

    List<DepotCounter> selectAll();

    DepotCounter selectByPrimaryKey(Long id);


}
