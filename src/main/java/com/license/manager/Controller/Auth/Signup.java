package com.license.manager.Controller.Auth;

import com.license.manager.CustomDialog;
import com.license.manager.Main;
import com.license.manager.Method.SecurePassword;
import com.license.manager.PropertiesLoader;
import com.license.manager.LocalDb.LocalDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Signup implements Initializable {
    private static String INSERT_URL = null;
    public Button submitBn;
    public TextField fullNameTf;
    public TextField usernameTf;
    public TextField phoneTf;
    public TextField emailTf;
    public TextField companyNameTf;
    public ComboBox<String> genderCom;
    public TextField passwordTf;
    private CustomDialog customDialog;
    private LocalDatabase localDatabase;
    private PropertiesLoader propertiesLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        localDatabase = new LocalDatabase();
        genderCom.setItems(localDatabase.getGender());
        propertiesLoader = new PropertiesLoader();
        INSERT_URL = propertiesLoader.getInsertProp().getProperty("registeredCompany");

    }

    public void submitBn(ActionEvent actionEvent) throws NoSuchAlgorithmException {

        registrationStart(actionEvent.getSource());
    }
    public void enterPress(KeyEvent keyEvent) throws NoSuchAlgorithmException {
        if (keyEvent.getCode() == KeyCode.ENTER){
            registrationStart(keyEvent.getSource());
        }
    }
    private void registrationStart(Object source) throws NoSuchAlgorithmException {

        String name = fullNameTf.getText();
        String userName = usernameTf.getText();
        String phone = phoneTf.getText();
        String email = emailTf.getText();
        String companyName = companyNameTf.getText();
        String password = passwordTf.getText();
        int passwordSize = password.length();

        if (name.isEmpty()){
            customDialog.show_popup("ENTER FULL NAME",fullNameTf);
            return;
        }else if (userName.isEmpty()){
            customDialog.show_popup("ENTER USERNAME",usernameTf);
            return;
        }else if (genderCom.getSelectionModel().isEmpty()){
            customDialog.show_popup("SELECT GENDER",genderCom);
            return;
        }else if (phone.isEmpty()){
            customDialog.show_popup("ENTER PHONE NUMBER",phoneTf);
            return;
        }else if (phone.length() < 10){
            customDialog.show_popup("ENTER 10-DIGIT PHONE NUMBER",phoneTf);
            return;
        }else if (email.isEmpty()){
            customDialog.show_popup("ENTER VALID EMAIl",emailTf);
            return;
        }else if (companyName.isEmpty()){
            customDialog.show_popup("ENTER COMPANY NAME",companyNameTf);
            return;
        }else if (password.isEmpty()){
            customDialog.show_popup("ENTER PASSWORD",passwordTf);
            return;
        }else if (passwordSize < 5){
            customDialog.show_popup("ENTER PASSWORD MORE THAN 5",passwordTf);
            return;
        }

        String gender = genderCom.getSelectionModel().getSelectedItem();
        registered(name , userName , phone , email , companyName , new SecurePassword().secure(password), gender , source);

    }

    private void registered(String name, String userName, String phone, String email, String companyName, String password, String gender , Object source) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(INSERT_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("full_name", name));
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("gender", gender));
        params.add(new BasicNameValuePair("company_name", companyName));
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
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
                    customDialog.showAlertBox("","Successfully Created");
                   LoginHere(null);

                }else {
                    customDialog.showAlertBox("Failed",message);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void LoginHere(MouseEvent mouseEvent) {
        new Main().changeScene("auth/login.fxml","");
    }
}
