package com.license.manager.Controller;

import com.license.manager.Controller.Auth.Login;
import com.license.manager.CustomDialog;
import com.license.manager.LocalDb.LocalDatabase;
import com.license.manager.Main;
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
import java.util.Random;
import java.util.ResourceBundle;

public class AddNewApps implements Initializable {
    public TextField applicationIdTf;
    public TextField applicationNameTf;
    public ComboBox<String> applicationTypeC;
    public TextField fullNameTf;
    public TextField phoneTf;
    public TextField emailTf;
    private CustomDialog customDialog;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        applicationTypeC.setItems(new LocalDatabase().applicationType());
        applicationIdTf.setText(getApplicationId());
    }
    public void cancelBn(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void saveBn(ActionEvent e) {

        addApps(e.getSource());
    }

    private String getApplicationId() {
        Random r = new Random();
        String s1 =  String.valueOf((char)(r.nextInt(26)+'A'));
        String s2 =  String.valueOf((char)(r.nextInt(26)+'A'));
        String s3 =  String.valueOf((char)(r.nextInt(26)+'A'));
        return  s1+s2+s3+System.currentTimeMillis();
    }

    public void enterPress(KeyEvent e) {

        if (e.getCode() == KeyCode.ENTER){
            addApps(e.getSource());
        }
    }

    private void addApps(Object source) {

        String applicationId = applicationIdTf.getText();
        String applicationName = applicationNameTf.getText();
        String name = fullNameTf.getText();
        String phone = (phoneTf.getText()).replaceAll("[^0-9.]", "");
        String email = emailTf.getText();


        if (applicationId.isEmpty()){
            return;
        }else if (applicationName.isEmpty()){
            customDialog.show_popup("Please Enter Application Name ",applicationNameTf);
            return;
        }
        else if (applicationTypeC.getSelectionModel().isEmpty()){
            customDialog.show_popup("Please Select Application Type",applicationTypeC);
            return;
        }else if (name.isEmpty()){
            customDialog.show_popup("Please Enter Full Name",fullNameTf);
            return;
        }
        else if (phone.isEmpty()){
            customDialog.show_popup("Please Enter 10-Digit Phone Number In Number Format",phoneTf);
            return;
        }else if (phone.length() != 10 ){
            customDialog.show_popup("Please Enter 10-Digit Phone Number (Without Country Code)",phoneTf);
            return;
        }else if (email.isEmpty()){
            customDialog.show_popup("Please Enter Valid Email",emailTf);
            return;
        }

        String applicationType = applicationTypeC.getSelectionModel().getSelectedItem();


        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("addNewApplication"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("APP_ID", applicationId));
        params.add(new BasicNameValuePair("APP_NAME", applicationName));
        params.add(new BasicNameValuePair("APP_TYPE", applicationType));
        params.add(new BasicNameValuePair("COMPANY_ID", String.valueOf(Login.activeUserId)));
        params.add(new BasicNameValuePair("name",name ));
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("email", email));
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
            e.printStackTrace();
        }

    }
}
