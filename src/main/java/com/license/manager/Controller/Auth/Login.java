package com.license.manager.Controller.Auth;

import com.license.manager.CustomDialog;
import com.license.manager.Main;
import com.license.manager.Method.SecurePassword;
import com.license.manager.PropertiesLoader;
import com.license.manager.Model.Users;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Login implements Initializable {
    private static String LOGIN_URL = null;
    public TextField input;
    public TextField passwordTf;
    private CustomDialog customDialog;
    private PropertiesLoader propertiesLoader;
    public static int activeUserId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        propertiesLoader = new PropertiesLoader();
        LOGIN_URL = propertiesLoader.getInsertProp().getProperty("login");
    }

    public void clickLoginButton(ActionEvent actionEvent) throws IOException {
        startLogin();
    }

    private void startLogin() {
        String input = this.input.getText();
        String password = passwordTf.getText();


        if (input.isEmpty()) {
            customDialog.show_popup("Enter Username", this.input);
            return;
        } else if (password.isEmpty()) {
            customDialog.show_popup("Enter Password", passwordTf);
            return;
        }

        login(input, new SecurePassword().secure(password));

    }

    private void login(String input, String password) {

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair("input", input));
        params.add(new BasicNameValuePair("password", password));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);

                JSONObject jb = new JSONObject(content);

                int statusCode = jb.getInt("statusCode");
                String message = jb.getString("message");

                if (statusCode == 200) {

                    JSONObject data = jb.getJSONObject("data");
                    int company_id = data.getInt("company_id");
                    String fullName = data.getString("full_name");
                    String username = data.getString("username");
                    String email = data.getString("email");
                    String phone = data.getString("phone");
                    String gender = data.getString("gender");
                    String registeredDate = data.getString("registered_date");
                    String companyName = data.getString("company_name");

                    activeUserId = company_id;

                    Users users = new Users(company_id, fullName, username, email, phone, gender, companyName, registeredDate);

                    Main.primaryStage.setUserData(users);
                    new Main().changeScene("dashboard.fxml", "DASHBOARD");

                } else if (statusCode == 404) {
                    customDialog.showAlertBox("Failed", message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void signupHere(MouseEvent mouseEvent) {
        new Main().changeScene("auth/signup.fxml", "");
    }
    public void enterPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            startLogin();
        }
    }

    public void forgotPassword(ActionEvent actionEvent) {

        customDialog.showFxmlDialog2("auth/forgotPassword.fxml", "FORGOT PASSWORD");
    }
}
