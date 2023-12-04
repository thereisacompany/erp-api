package com.jsh.erp.datasource.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MaterialsListVo {

    private Long id;
    private Long headerId;

    private String materialsList;

    private Long categoryId;

    private BigDecimal materialCount;

    private String materialNumber;

    private String depotList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getMaterialsList() {
        List<String> retList = new ArrayList<>();
        String[] list = materialsList.split(",");
        for(String str : list) {
            String[] data = str.split("[|]");
            double number = 0.0;
            if(data.length > 1) {
                number = Double.parseDouble(data[1]);
            }
//            retList.add(data[0] + "     *" + (int)number);
            retList.add(data[0]);
        }

        return String.join(",", retList);
    }

    public void setMaterialsList(String materialsList) {
        this.materialsList = materialsList;
    }

    public String getDepotList() {
        return depotList;
    }

    public void setDepotList(String depotList) {
        this.depotList = depotList;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(BigDecimal materialCount) {
        this.materialCount = materialCount;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }
}
