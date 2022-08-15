package com.license.manager.Model;

public class SerialKeyModel {
    private int serialKeyId , isActive , isExpires ;
    private String serialKey,licenseType;
    private int company_id ;
    private  String application_id;
    private String startDate , expireDate;
    private String validity , quantity;

    public SerialKeyModel(int serialKeyId, int isActive, int isExpires,
                          String serialKey, String licenseType, int company_id, String application_id, String startDate, String expireDate, String validity, String quantity) {
        this.serialKeyId = serialKeyId;
        this.isActive = isActive;
        this.isExpires = isExpires;
        this.serialKey = serialKey;
        this.licenseType = licenseType;
        this.company_id = company_id;
        this.application_id = application_id;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.validity = validity;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getSerialKeyId() {
        return serialKeyId;
    }

    public void setSerialKeyId(int serialKeyId) {
        this.serialKeyId = serialKeyId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsExpires() {
        return isExpires;
    }

    public void setIsExpires(int isExpires) {
        this.isExpires = isExpires;
    }

    public String getSerialKey() {
        return serialKey;
    }

    public void setSerialKey(String serialKey) {
        this.serialKey = serialKey;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
