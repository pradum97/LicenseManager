package com.license.manager.Controller;

import com.license.manager.Controller.Auth.Login;
import com.license.manager.CustomDialog;
import com.license.manager.LocalDb.LocalDatabase;
import com.license.manager.Main;
import com.license.manager.Model.LicencePeriod;
import com.license.manager.Model.LicenseType;
import com.license.manager.PropertiesLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AddNewSerialKey implements Initializable {

    public ComboBox<LicencePeriod> licensePeriodCom;
    public ComboBox<LicenseType> licenseTypeCom;
    public TextField applicationIdTf;
    public ComboBox<Integer> licenseQuantityCom;

    private String applicationId;
    private int companyId;
    private CustomDialog customDialog;
    private LocalDatabase localDb;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        localDb = new LocalDatabase();
        Map<String, Object> map = (Map<String, Object>) Main.primaryStage.getUserData();
        applicationId = (String) map.get("application_id");
        companyId = (Integer) map.get("company_id");
        applicationIdTf.setText(applicationId);
        setData();

    }
    private void setData() {
        licenseQuantityCom.setItems(localDb.getLicenseQuantity());
        licenseQuantityCom.getSelectionModel().selectFirst();
        licenseTypeCom.setItems(localDb.getLicenseType());
        licenseTypeCom.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, licenseType, t1) -> {
            int id = t1.getId();

            switch (id){
                case 1 -> {
                    licensePeriodCom.setItems(localDb.getLicensePeriod());
                    licensePeriodCom.getSelectionModel().select(0);
                }
                case 2 -> {
                    licensePeriodCom.setItems(localDb.getLicensePeriodForTrial());
                    licensePeriodCom.getSelectionModel().selectFirst();
                }
            }
        });
       // licenseTypeCom.getSelectionModel().selectFirst();
    }

    public void cancelBn(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }
    public void saveBn(ActionEvent actionEvent) {
        addingStart(actionEvent.getSource());
    }

    public void enterPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){

            addingStart(keyEvent.getSource());
        }
    }

    private void addingStart(Object source) {

        if (licenseTypeCom.getSelectionModel().isEmpty()){
            customDialog.show_popup("Please Select License Type",licenseTypeCom);
            return;
        }else  if (licensePeriodCom.getSelectionModel().isEmpty()){
            customDialog.show_popup("Please Select License Period",licensePeriodCom);
            return;
        }else  if (licenseQuantityCom.getSelectionModel().isEmpty()){
            customDialog.show_popup("Please Select License Quantity",licenseQuantityCom);
            return;
        }

        int licensePeriod = licensePeriodCom.getSelectionModel().getSelectedItem().getDays(); // in days
        int licenseQuantity = licenseQuantityCom.getSelectionModel().getSelectedItem();
        String licenseType = licenseTypeCom.getSelectionModel().getSelectedItem().getType();


        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("generateNewSerialKey"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("application_id", applicationId));
        params.add(new BasicNameValuePair("company_id", String.valueOf(Login.activeUserId)));
        params.add(new BasicNameValuePair("licenseType", String.valueOf(licenseType)));
        params.add(new BasicNameValuePair("licensePeriod", String.valueOf(licensePeriod)));
        params.add(new BasicNameValuePair("licenseQty", String.valueOf(licenseQuantity)));
        try {

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);

                JSONObject jb = new JSONObject(content);

                int statusCode = jb.getInt("statusCode");
                String message = jb.getString("message");

                if (statusCode == 200){

                    customDialog.showAlertBox("Success",message);

                    Stage stage = (Stage) ((Node) source).getScene().getWindow();
                    if (null != stage && stage.isShowing()){
                        Main.primaryStage.setUserData(true);
                        stage.close();
                    }
                }else {
                    customDialog.showAlertBox("Failed",message);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
