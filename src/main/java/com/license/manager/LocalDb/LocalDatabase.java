package com.license.manager.LocalDb;

import com.license.manager.Model.LicencePeriod;
import com.license.manager.Model.LicenseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LocalDatabase {

    public ObservableList<String> applicationType(){
        return  FXCollections.observableArrayList("DESKTOP","ANDROID");
    }

    public ObservableList<String> getGender(){

        return FXCollections.observableArrayList("MALE","FEMALE","OTHER");
    }

    public ObservableList<Integer> getLicenseQuantity(){

        return FXCollections.observableArrayList(1);
    }

    public ObservableList<LicenseType> getLicenseType(){
        ObservableList<LicenseType> typeList = FXCollections.observableArrayList();
        typeList.add(new LicenseType(1,"SUBSCRIPTION LICENSE"));
        typeList.add(new LicenseType(2,"TRIAL LICENSE"));
        return typeList;
    }

    public ObservableList<LicencePeriod> getLicensePeriod(){
        ObservableList<LicencePeriod> list = FXCollections.observableArrayList();
        // m = month
        // d = days
        // y = year
        String type = "SUBSCRIPTION";
        int y = 366;
        list.add(new LicencePeriod(1,y,type));
        list.add(new LicencePeriod(2,(y*2),type));

        return list;
    }

    public ObservableList<LicencePeriod> getLicensePeriodForTrial(){
        ObservableList<LicencePeriod> list = FXCollections.observableArrayList();

        String type = "TRIAL";

        // m = month
        // d = days
        list.add(new LicencePeriod(1,15,type));
        list.add(new LicencePeriod(2,30,type));
        return list;
    }
}
