package com.jsh.erp.datasource.vo;

import lombok.Data;

@Data
public class DepotHeadDetail {
    private Long id;
    private String status;
    private String supplier;
    private String assignDate;
    private String assignUser;
    private String licensePlateNumber;
}
