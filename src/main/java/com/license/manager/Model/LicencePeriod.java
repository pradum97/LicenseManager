package com.license.manager.Model;

import java.util.Objects;

public class LicencePeriod {
    private int id , days;
    private String licenceType;

    public LicencePeriod(int id, int days, String licenceType) {
        this.id = id;
        this.days = days;
        this.licenceType = licenceType;
    }

    public String getLicenceType() {
        return licenceType;
    }

    public void setLicenceType(String licenceType) {
        this.licenceType = licenceType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    @Override
    public String toString() {

        String type = this.getLicenceType();

        if (Objects.equals(type, "TRIAL")){

            return this.getDays()+"-DAYS" ;
        }else {
            int year =  this.getDays()/366;

            if (year >1){
                return year+"-YEARS";
            }else {
                return year+"-YEAR";
            }

        }


    }
}

