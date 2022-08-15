package com.license.manager.Controller.Auth;

import com.license.manager.CustomDialog;
import com.license.manager.Method.SecurePassword;
import com.license.manager.PropertiesLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class ForgotPassword implements Initializable {
    public VBox emailContainer;
    public TextField emailTf;
    public VBox passwordContainer;
    public TextField newPasswordTf;
    public TextField confirmPasswordTf;
    public TextField otpTf;
    public HBox otpContainer;
    int otp = 0;
    private CustomDialog customDialog;
    private PropertiesLoader propertiesLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        propertiesLoader = new PropertiesLoader();

        passwordContainer.managedProperty().bind(passwordContainer.visibleProperty());
        passwordContainer.setVisible(false);
        otpContainer.setDisable(true);
    }

    public void bnSendOtp(ActionEvent actionEvent) {
        String email = emailTf.getText();

        if (email.isEmpty()){
            customDialog.show_popup("Enter Valid Email",emailTf);
            return;
        }
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(propertiesLoader.getInsertProp().getProperty("forgotPassword"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("type", "auth"));
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
                    otp = jb.getInt("otp");
                    otpContainer.setDisable(false);
                    customDialog.showAlertBox("Success",message);

                }else if (statusCode == 404){

                    customDialog.showAlertBox("Failed",message);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void submitBn(ActionEvent actionEvent) {

        updatePassword(actionEvent.getSource());

    }

    private void updatePassword(Object source) {

        String pass = newPasswordTf.getText();
        String confirmPass = confirmPasswordTf.getText();
        String email = emailTf.getText();

        if (pass.isEmpty()){
            customDialog.show_popup("Enter New Password",newPasswordTf);
            return;
        }else if (confirmPass.isEmpty()){
            customDialog.show_popup("Enter Confirm Password",confirmPasswordTf);
            return;
        } else if (!Objects.equals(pass , confirmPass)) {
            customDialog.show_popup("The password and confirmation password do not match.",confirmPasswordTf);
            return;
        }

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(propertiesLoader.getInsertProp().getProperty("forgotPassword"));
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("password", new SecurePassword().secure(confirmPass)));
        params.add(new BasicNameValuePair("type", "password"));
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

                    Stage stage = (Stage) ((Node)source).getScene().getWindow();

                    if (null != stage && stage.isShowing()){
                        stage.close();
                    }

                }else if (statusCode == 404){

                    customDialog.showAlertBox("Failed",message);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelBn(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void passEnterPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER){
            updatePassword(keyEvent.getSource());
        }
    }
    public void enterPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER){
            bnSendOtp(null);
        }
    }

    public void otpVerifyBn(ActionEvent actionEvent) {
        String inputOtp = otpTf.getText();

        if (inputOtp.isEmpty()){
            customDialog.show_popup("Enter 6 digit OTP",otpTf);
            return;
        }
        int otpD;
        try {
             otpD = Integer.parseInt(inputOtp);
        } catch (NumberFormatException e) {
            customDialog.show_popup("Please Enter Valid Otp",otpTf);
            return;
        }
        if (Objects.equals(otp , otpD)){
            otp = 0;
            passwordContainer.setVisible(true);
            emailContainer.managedProperty().bind(emailContainer.visibleProperty());
            emailContainer.setVisible(false);
        }else {
            customDialog.showAlertBox("Verification Failed","Wrong Otp. Please try again!");
            otpTf.setText("");
        }
    }
}
