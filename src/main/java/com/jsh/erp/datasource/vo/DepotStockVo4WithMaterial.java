package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepotStockVo4WithMaterial {


    private String depotName;

    private String counterName;

    private BigDecimal stock;

}