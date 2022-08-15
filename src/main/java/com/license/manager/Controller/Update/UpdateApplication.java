package com.license.manager.Controller.Update;

import com.license.manager.Controller.Auth.Login;
import com.license.manager.CustomDialog;
import com.license.manager.LocalDb.LocalDatabase;
import com.license.manager.Main;
import com.license.manager.Model.AllApplication;
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
import java.util.ResourceBundle;

public class UpdateApplication implements Initializable {
    public TextField applicationNameTf;
    public TextField fullNameTf;
    public TextField phoneTf;
    public TextField emailTf;
    private CustomDialog customDialog;
    private AllApplication application;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        application = (AllApplication) Main.primaryStage.getUserData();

        if (null == application){
            return;
        }
        setData();
    }
    private void setData() {
        applicationNameTf.setText(application.getApplicationName());
        fullNameTf.setText(application.getOwnerName());
        phoneTf.setText(application.getOwnerPhone());
        emailTf.setText(application.getOwnerEmail());
    }

    public void cancelBn(ActionEvent actionEvent) {
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void enterPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER){
            updateDetails(keyEvent.getSource());
        }
    }

    public void updateBn(ActionEvent actionEvent) {
        updateDetails(actionEvent.getSource());
    }

    private void updateDetails(Object source) {

        String applicationName = applicationNameTf.getText();
        String name = fullNameTf.getText();
        String phone = (phoneTf.getText()).replaceAll("[^0-9.]", "");
        String email = emailTf.getText();


        if (applicationName.isEmpty()){
            customDialog.show_popup("Please Enter Application Name ",applicationNameTf);
            return;
        } else if (name.isEmpty()){
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


        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("UPDATE_APPLICATION"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("ID", String.valueOf(application.getId())));
        params.add(new BasicNameValuePair("OWNER_ID", String.valueOf(application.getOwnerId())));
        params.add(new BasicNameValuePair("APP_NAME", applicationName));
        params.add(new BasicNameValuePair("name",name ));
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("COMPANY_ID",String.valueOf( Login.activeUserId)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);
                System.out.println(content);
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
