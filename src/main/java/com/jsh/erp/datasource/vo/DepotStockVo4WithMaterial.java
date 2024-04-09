package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepotStockVo4WithMaterial {

    private String depotName;
    private String anotherDepotName;

    private String counterName;
    private String anotherCounterName;

    private BigDecimal stock = new BigDecimal(0);
    private BigDecimal stock2 = new BigDecimal(0);

    private Integer status;

}