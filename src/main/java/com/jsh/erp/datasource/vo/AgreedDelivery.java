package com.jsh.erp.datasource.vo;

import lombok.Data;

@Data
public class AgreedDelivery {
    private Long detailId;
    private String datetime;
    private String name;
    private int isDefault;

}
