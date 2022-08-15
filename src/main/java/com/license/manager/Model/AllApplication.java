package com.license.manager.Model;

public class AllApplication {
    private int id;
    private String applicationId , applicationName , applicationTYpe , status ;
    private int connectedApplication , maxConnectedApplication ;
    private String registeredDate;
    private int ownerId;
    private String ownerName , ownerPhone , ownerEmail;


    public AllApplication(int id, String applicationId, String applicationName, String applicationTYpe, String status, int connectedApplication,
                          int maxConnectedApplication, String registeredDate, int ownerId, String ownerName, String ownerPhone, String ownerEmail) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationTYpe = applicationTYpe;
        this.status = status;
        this.connectedApplication = connectedApplication;
        this.maxConnectedApplication = maxConnectedApplication;
        this.registeredDate = registeredDate;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationTYpe() {
        return applicationTYpe;
    }

    public void setApplicationTYpe(String applicationTYpe) {
        this.applicationTYpe = applicationTYpe;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getConnectedApplication() {
        return connectedApplication;
    }

    public void setConnectedApplication(int connectedApplication) {
        this.connectedApplication = connectedApplication;
    }

    public int getMaxConnectedApplication() {
        return maxConnectedApplication;
    }

    public void setMaxConnectedApplication(int maxConnectedApplication) {
        this.maxConnectedApplication = maxConnectedApplication;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
