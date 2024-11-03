package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Supplier {
    private Long id;

    private String supplier;

    private String supplierall;
    private Long customId;

    private String taxid;

    private String contacts;

    private String phoneNum;

    private String email;

    private String description;

    private Byte isystem;

    private String type;

    private Boolean enabled;

    private BigDecimal advanceIn;

    private BigDecimal beginNeedGet;

    private BigDecimal beginNeedPay;

    private BigDecimal allNeedGet;

    private BigDecimal allNeedPay;

    private String fax;

    private String telephone;

    private String emergencyPhone;

    private String address;

    private String licensePlate;

    private String taxNum;

    private String bankName;

    private String accountNumber;

    private BigDecimal taxRate;

    private String groupInsuranceStart;
    private String groupInsuranceEnd;
    private String laborHealthInsuranceStart;
    private String laborHealthInsuranceEnd;
    private String onboarding;
    private String resign;
    private String idNumber;
    private String birthday;
    private String license;
    private Integer sex;
    private String sort;

    private Long tenantId;

    private String deleteFlag;

    private String loginName;

}