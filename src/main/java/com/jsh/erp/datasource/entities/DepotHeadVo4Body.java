package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class DepotHeadVo4Body {

    private Long id;

    private String info;

    private String rows;

    private String categoryName;

    private BigDecimal preTotalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPreTotalPrice() {
        return preTotalPrice;
    }

    public void setPreTotalPrice(BigDecimal preTotalPrice) {
        this.preTotalPrice = preTotalPrice;
    }
}