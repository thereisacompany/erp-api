package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.util.List;

@Data
public class DepotHeadDelivery {

    private String number;
    private String customNumber;
    private String sourceNumber;
    private String customName;
    private String orderDate;
    private String takeDate;
    private String driverName;
    private String carNumber;
    private String memo;
    private List<DeliveryStatus> deliveryStatusList;


}