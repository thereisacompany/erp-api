package com.jsh.erp.datasource.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MaterialsListVo {

    private Long headerId;

    private String materialsList;

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
            double number = Double.parseDouble(data[1]);
            retList.add(data[0] + "     *" + (int)number);
        }

        return String.join(",", retList);
    }

    public void setMaterialsList(String materialsList) {
        this.materialsList = materialsList;
    }
}
