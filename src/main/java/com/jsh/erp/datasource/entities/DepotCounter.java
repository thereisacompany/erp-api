package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepotCounter {

    private Long id;

    private Long depotId;

    private String name;

    private BigDecimal space;

    private Integer sort;

    private String remark;

    private Long principal;

    private Boolean enabled;

    private Boolean isDefault;
}
