package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Supplier {
    private Long id;

    private String supplier;

    private String supplierall;

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

    private String sort;

    private Long tenantId;

    private String deleteFlag;

    private String loginName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier == null ? null : supplier.trim();
    }

    public String getSupplierall() {
        return supplierall;
    }

    public void setSupplierall(String supplierall) { this.supplierall = supplierall == null ? null : supplierall.trim(); }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) { this.taxid = taxid == null ? null : taxid.trim(); }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum == null ? null : phoneNum.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Byte getIsystem() {
        return isystem;
    }

    public void setIsystem(Byte isystem) {
        this.isystem = isystem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getAdvanceIn() {
        return advanceIn;
    }

    public void setAdvanceIn(BigDecimal advanceIn) {
        this.advanceIn = advanceIn;
    }

    public BigDecimal getBeginNeedGet() {
        return beginNeedGet;
    }

    public void setBeginNeedGet(BigDecimal beginNeedGet) {
        this.beginNeedGet = beginNeedGet;
    }

    public BigDecimal getBeginNeedPay() {
        return beginNeedPay;
    }

    public void setBeginNeedPay(BigDecimal beginNeedPay) {
        this.beginNeedPay = beginNeedPay;
    }

    public BigDecimal getAllNeedGet() {
        return allNeedGet;
    }

    public void setAllNeedGet(BigDecimal allNeedGet) {
        this.allNeedGet = allNeedGet;
    }

    public BigDecimal getAllNeedPay() {
        return allNeedPay;
    }

    public void setAllNeedPay(BigDecimal allNeedPay) {
        this.allNeedPay = allNeedPay;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmergencyPhone() { return emergencyPhone; }

    public void setEmergencyPhone(String phone) {this.emergencyPhone = phone == null ? null : phone.trim(); }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getLicensePlate() { return licensePlate; }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate == null ? null : licensePlate.trim();
    }

    public String getTaxNum() {
        return taxNum;
    }

    public void setTaxNum(String taxNum) {
        this.taxNum = taxNum == null ? null : taxNum.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber == null ? null : accountNumber.trim();
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag == null ? null : deleteFlag.trim();
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}