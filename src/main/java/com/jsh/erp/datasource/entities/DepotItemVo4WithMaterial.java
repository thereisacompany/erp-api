package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class DepotItemVo4WithMaterial {

    private Long MId;

    private String MName;

    private Long organId;

    private String organName;

    private String MNumber;

    private String MModel;

    private String MaterialUnit;

    private String MColor;

    private String MStandard;

    private String MMfrs;

    private String MOtherField1;

    private String MOtherField2;

    private String MOtherField3;

    private String enableSerialNumber;

    private String enableBatchNumber;

    private String categoryName;

    private String DepotName;

    private String counterName;
    private String AnotherDepotName;

    private Long UnitId;

    private String unitName;

    private Integer ratio;

    private String otherUnit;

    private BigDecimal presetPriceOne;

    private String priceStrategy;

    private BigDecimal purchaseDecimal;

    private String barCode;

    private Long depotId;

    public Long getMId() {
        return MId;
    }

    public void setMId(Long MId) {
        this.MId = MId;
    }

    public String getMName() {
        return MName;
    }

    public void setMName(String MName) {
        this.MName = MName;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getOrganId() {
        return organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getMNumber() {
        return MNumber;
    }

    public void setMNumber(String MNumber) {
        this.MNumber = MNumber;
    }

    public String getMModel() {
        return MModel;
    }

    public void setMModel(String MModel) {
        this.MModel = MModel;
    }

    public String getMaterialUnit() {
        return MaterialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        MaterialUnit = materialUnit;
    }

    public String getMColor() {
        return MColor;
    }

    public void setMColor(String MColor) {
        this.MColor = MColor;
    }

    public String getMStandard() {
        return MStandard;
    }

    public void setMStandard(String MStandard) {
        this.MStandard = MStandard;
    }

    public String getMMfrs() {
        return MMfrs;
    }

    public void setMMfrs(String MMfrs) {
        this.MMfrs = MMfrs;
    }

    public String getMOtherField1() {
        return MOtherField1;
    }

    public void setMOtherField1(String MOtherField1) {
        this.MOtherField1 = MOtherField1;
    }

    public String getMOtherField2() {
        return MOtherField2;
    }

    public void setMOtherField2(String MOtherField2) {
        this.MOtherField2 = MOtherField2;
    }

    public String getMOtherField3() {
        return MOtherField3;
    }

    public void setMOtherField3(String MOtherField3) {
        this.MOtherField3 = MOtherField3;
    }

    public String getEnableSerialNumber() {
        return enableSerialNumber;
    }

    public void setEnableSerialNumber(String enableSerialNumber) {
        this.enableSerialNumber = enableSerialNumber;
    }

    public String getEnableBatchNumber() {
        return enableBatchNumber;
    }

    public void setEnableBatchNumber(String enableBatchNumber) {
        this.enableBatchNumber = enableBatchNumber;
    }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDepotName() {
        return DepotName;
    }

    public void setDepotName(String depotName) {
        DepotName = depotName;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getAnotherDepotName() {
        return AnotherDepotName;
    }

    public void setAnotherDepotName(String anotherDepotName) {
        AnotherDepotName = anotherDepotName;
    }

    public Long getUnitId() {
        return UnitId;
    }

    public void setUnitId(Long unitId) {
        UnitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }

    public String getOtherUnit() {
        return otherUnit;
    }

    public void setOtherUnit(String otherUnit) {
        this.otherUnit = otherUnit;
    }

    public BigDecimal getPresetPriceOne() {
        return presetPriceOne;
    }

    public void setPresetPriceOne(BigDecimal presetPriceOne) {
        this.presetPriceOne = presetPriceOne;
    }

    public String getPriceStrategy() {
        return priceStrategy;
    }

    public void setPriceStrategy(String priceStrategy) {
        this.priceStrategy = priceStrategy;
    }

    public BigDecimal getPurchaseDecimal() {
        return purchaseDecimal;
    }

    public void setPurchaseDecimal(BigDecimal purchaseDecimal) {
        this.purchaseDecimal = purchaseDecimal;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }
}