package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialPickupsListVo {

    private Long headerId;
    private String name;
    private BigDecimal amount;
}
