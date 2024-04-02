package com.jsh.erp.datasource.vo;


import java.math.BigDecimal;
import java.util.Date;

public class DepotHeadVo4InDetail {

    private Long headerId;

    private String Number;

    private String barCode;

    private String userName;

    private String MName;

    private String MId;

    private String MNumber;

    private String Model;

    private String standard;

    private BigDecimal UnitPrice;

    private String mUnit;

    private String newRemark;

    private Long SubId;

    private BigDecimal stock;
    private BigDecimal OperNumber;
    
    private BigDecimal ConfirmNumber;

    private BigDecimal AllPrice;

    private BigDecimal taxRate;

    private BigDecimal taxMoney;

    private BigDecimal taxLastMoney;

    private Long organId;

    private String SName;

    private Long SId;

    private Long SCounterId;

    private String SCounter;

    private String DName;

    private Long DId;

    private Long DCounterId;

    private String DCounter;

    private String categoryName;

    private String OperTime;

    private String NewType;

    private String status;

    private Long tenantId;

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMId() {
        return MId;
    }

    public void setMId(String MId) {
        this.MId = MId;
    }

    public String getMName() {
        return MName;
    }

    public void setMName(String MName) {
        this.MName = MName;
    }

    public String getMNumber() {
        return MNumber;
    }

    public void setMNumber(String MNumber) {
        this.MNumber = MNumber;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public BigDecimal getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getmUnit() {
        return mUnit;
    }

    public void setmUnit(String mUnit) {
        this.mUnit = mUnit;
    }

    public String getNewRemark() {
        return newRemark;
    }

    public void setNewRemark(String newRemark) {
        this.newRemark = newRemark;
    }
    public Long getSubId() {
        return SubId;
    }

    public void setSubId(Long subId) {
        SubId = subId;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public BigDecimal getOperNumber() {
        return OperNumber;
    }

    public void setOperNumber(BigDecimal operNumber) {
        OperNumber = operNumber;
    }

    public BigDecimal getConfirmNumber() {
        return ConfirmNumber;
    }

    public void setConfirmNumber(BigDecimal confirmNumber) {
        this.ConfirmNumber = confirmNumber;
    }
    public BigDecimal getAllPrice() {
        return AllPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        AllPrice = allPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxMoney() {
        return taxMoney;
    }

    public void setTaxMoney(BigDecimal taxMoney) {
        this.taxMoney = taxMoney;
    }

    public BigDecimal getTaxLastMoney() {
        return taxLastMoney;
    }

    public void setTaxLastMoney(BigDecimal taxLastMoney) {
        this.taxLastMoney = taxLastMoney;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public Long getSId() {
        return SId;
    }

    public void setSId(Long SId) {
        this.SId = SId;
    }

    public Long getSCounterId() {
        return SCounterId;
    }

    public void setSCounterId(Long SCounterId) {
        this.SCounterId = SCounterId;
    }

    public String getSCounter() {
        return SCounter;
    }

    public void setSCounter(String SCounter) {
        this.SCounter = SCounter;
    }

    public String getDName() {
        return DName;
    }

    public void setDName(String DName) {
        this.DName = DName;
    }

    public Long getDId() {
        return DId;
    }

    public void setDId(Long DId) {
        this.DId = DId;
    }

    public Long getDCounterId() {
        return DCounterId;
    }

    public void setDCounterId(Long DCounterId) {
        this.DCounterId = DCounterId;
    }

    public String getDCounter() {
        return DCounter;
    }

    public void setDCounter(String DCounter) {
        this.DCounter = DCounter;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOperTime() {
        return OperTime;
    }

    public void setOperTime(String operTime) {
        OperTime = operTime;
    }

    public String getNewType() {
        return NewType;
    }

    public void setNewType(String newType) {
        NewType = newType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}