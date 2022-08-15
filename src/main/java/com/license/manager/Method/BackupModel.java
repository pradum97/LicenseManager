package com.license.manager.Method;

public class BackupModel {

    private int backupId;
    private String path , backupDate , applicationId;

    public BackupModel(int backupId, String path, String backupDate, String applicationId) {
        this.backupId = backupId;
        this.path = path;
        this.backupDate = backupDate;
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getBackupId() {
        return backupId;
    }

    public void setBackupId(int backupId) {
        this.backupId = backupId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(String backupDate) {
        this.backupDate = backupDate;
    }
}
