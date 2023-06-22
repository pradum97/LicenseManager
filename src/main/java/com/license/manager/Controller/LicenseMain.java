package com.license.manager.Controller;

import com.license.manager.Controller.Auth.Login;
import com.license.manager.CustomDialog;
import com.license.manager.ImageLoader;
import com.license.manager.Main;
import com.license.manager.Model.SerialKeyModel;
import com.license.manager.PropertiesLoader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LicenseMain implements Initializable {

    public Label applicationIdL;
    public Label nameTf;
    public Label emailTf;
    public TableView<SerialKeyModel> tableView;
    public TableColumn<SerialKeyModel, Integer> colSrNo;
    public TableColumn<SerialKeyModel, String> colSerialNum;
    public TableColumn<SerialKeyModel, String> colLicenseType;
    public TableColumn<SerialKeyModel, String> colStartDate;
    public TableColumn<SerialKeyModel, String> colExpireDate;
    public TableColumn<SerialKeyModel, String> colValidity;
    public TableColumn<SerialKeyModel, String> colStatus;
    public TableColumn<SerialKeyModel, String> colAction;
    public Label phoneNumL;
    public TableColumn<SerialKeyModel, String> colQuantity;
    @FXML
    private String applicationId;
    private int companyId, ownerId;
    private CustomDialog customDialog;
    private String ownerName, ownerPhone, ownerEmail;
    private ObservableList<SerialKeyModel> keyList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        Map<String, Object> map = (Map<String, Object>) Main.primaryStage.getUserData();
        applicationId = (String) map.get("application_id");

        ownerName = (String) map.get("name");
        ownerPhone = (String) map.get("phone");
        ownerEmail = (String) map.get("email");

        companyId = (Integer) map.get("company_id");
        ownerId = (Integer) map.get("ownerId");

        setData();

        getSerialKey();
    }

    private void setData() {

        applicationIdL.setText(applicationId);
        nameTf.setText(ownerName.toUpperCase());
        phoneNumL.setText(ownerPhone);
        emailTf.setText(ownerEmail);
    }

    private void getSerialKey() {

        if (null != keyList) {
            keyList.clear();
        }

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("getSerialKey"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("APP_ID", applicationId));
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
                    JSONArray keyArray = new JSONArray(jb.getJSONArray("data"));

                    for (int i = 0; i < keyArray.length(); i++) {
                        JSONObject jo = keyArray.getJSONObject(i);

                        int serialKeyId = jo.getInt("serial_key_id");
                        int isActive = jo.getInt("is_active");
                        int company_id = jo.getInt("company_id");
                        int periodDays = jo.getInt("licence_period_days");

                        String application_id = jo.getString("application_id");
                        String serialKey = jo.getString("serial_key");
                        String licenseType = jo.getString("license_type");

                        String startDate = jo.getString("start_date");
                        String expireDate = jo.getString("expire_date");

                        int quantity = jo.getInt("key_quantity");
                        int quantityCount = jo.getInt("quantity_count");

                        String qty = quantityCount + " / " + quantity;

                        String validity;

                        if (periodDays >= 365) {

                            int year = periodDays / 366;

                            if (year > 1) {

                                validity = year + " - YEARS";
                            } else {
                                validity = year + " - YEAR";
                            }
                        } else {
                            if (periodDays > 1) {
                                validity = periodDays + " - " + "DAYS";
                            } else {
                                validity = periodDays + " - " + "DAY";
                            }
                        }
                        keyList.add(new SerialKeyModel(serialKeyId, isActive, 0, serialKey, licenseType, company_id,
                                application_id, startDate, expireDate, validity, qty));
                    }

                    tableView.setItems(keyList);
                    colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                            tableView.getItems().indexOf(cellData.getValue()) + 1));
                    colLicenseType.setCellValueFactory(new PropertyValueFactory<>("licenseType"));
                    colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
                    colExpireDate.setCellValueFactory(new PropertyValueFactory<>("expireDate"));
                    colValidity.setCellValueFactory(new PropertyValueFactory<>("validity"));
                    colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                    setOptionalCell();

                } else {
                    customDialog.showAlertBox("Failed", message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionalCell() {


        Callback<TableColumn<SerialKeyModel, String>, TableCell<SerialKeyModel, String>>
                cellStatus = (TableColumn<SerialKeyModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    int isActive = tableView.getItems().get(getIndex()).getIsActive();
                    String expireDate = tableView.getItems().get(getIndex()).getExpireDate();

                    Label status = new Label();

                    if (!Objects.equals(expireDate, "0")) {

                        String pattern = "dd-MM-yyyy";
                        SimpleDateFormat sdformat = new SimpleDateFormat(pattern);

                        Date currentDate = null, expiresDate;
                        try {
                            currentDate = sdformat.parse(sdformat.format(new Date()));

                            expiresDate = sdformat.parse(expireDate);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        int checkExpireDate = currentDate.compareTo(expiresDate);
                        if (checkExpireDate > 0) {
                            status.setText("Expired");
                            status.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
                        } else {
                            status.setText("Running...");
                            status.setStyle("-fx-text-fill: green; -fx-font-weight: bold");
                        }
                    } else {

                        if (isActive == 1) {
                            status.setText("Running...");
                        } else {
                            status.setText("Inactive");
                            status.setStyle("-fx-text-fill: #c9c904; -fx-font-weight: bold");
                        }
                    }


                    HBox managebtn = new HBox(status);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(status, new Insets(10, 0, 10, 0));
                    setGraphic(managebtn);
                    setText(null);

                }
            }
        };

        Callback<TableColumn<SerialKeyModel, String>, TableCell<SerialKeyModel, String>>
                cellAction = (TableColumn<SerialKeyModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                   /* ImageView deleteImg = new ImageView();
                    deleteImg.setFitWidth(20);
                    deleteImg.setFitHeight(20);
                    deleteImg.setPreserveRatio(true);
                    deleteImg.setImage(new ImageLoader().load("icon/delete_ic.png"));

                    Button delBn = new Button();
                    delBn.setMinHeight(25);
                    delBn.setStyle("-fx-background-color: red ; -fx-alignment: center; -fx-border-radius: 2 ; " +
                            "-fx-border-color: gray;-fx-cursor: hand");
                    delBn.setGraphic(deleteImg);

                    delBn.managedProperty().bind(delBn.visibleProperty());
                    delBn.setVisible(false);
                    delBn.setOnAction(actionEvent -> {
                        selectTable(getIndex());
                        SerialKeyModel serialKeyModel = tableView.getSelectionModel().getSelectedItem();
                        if (null == serialKeyModel) {
                            return;
                        }
                        Alert alert = new Alert(Alert.AlertType.NONE);
                        alert.setAlertType(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Warning ");
                        alert.setHeaderText("ARE YOU SURE YOU WANT TO DELETE SERIAL KEY ?");
                        alert.setContentText(serialKeyModel.getSerialKey());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(Main.primaryStage);
                        Optional<ButtonType> result = alert.showAndWait();
                        ButtonType button = result.orElse(ButtonType.CANCEL);
                        if (button == ButtonType.OK) {
                            HttpClient httpClient = HttpClients.createDefault();
                            HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("deleteSerialKey"));
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("SERIAL_KEY_ID", String.valueOf(serialKeyModel.getSerialKeyId())));
                            params.add(new BasicNameValuePair("APP_ID", String.valueOf(serialKeyModel.getApplication_id())));
                            params.add(new BasicNameValuePair("COMPANY_ID", String.valueOf(Login.activeUserId)));
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
                                        getSerialKey();
                                        customDialog.showAlertBox("Success", message);
                                    } else {
                                        customDialog.showAlertBox("Failed", message);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            alert.close();
                        }

                    });*/

                    String key = tableView.getItems().get(getIndex()).getSerialKey();

                    Label serialKey = new Label(key);

                    ImageView copyImg = new ImageView();
                    copyImg.setFitWidth(15);
                    copyImg.setFitHeight(15);
                    copyImg.setPreserveRatio(true);
                    copyImg.setImage(new ImageLoader().load("icon/copy_ic.png"));
                    Button copyBn = new Button();
                    copyBn.setStyle("-fx-background-color: #000000 ; -fx-alignment: center; -fx-border-radius: 2 ; " +
                            "-fx-border-color: gray;-fx-cursor: hand");
                    copyBn.setGraphic(copyImg);

                    copyBn.setOnMouseClicked(mouseEvent -> {
                        selectTable(getIndex());
                        SerialKeyModel serialKeyModel = tableView.getSelectionModel().getSelectedItem();
                        if (null == serialKeyModel) {
                            return;
                        }

                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(serialKeyModel.getSerialKey());
                        clipboard.setContent(content);
                        copyBn.setStyle("-fx-background-color: #03961c ; -fx-alignment: center; -fx-border-radius: 2 ; -fx-border-color: gray;-fx-cursor: hand");

                    });

                    HBox managebtn = new HBox(serialKey, copyBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(copyBn, new Insets(10, 20, 10, 20));
                    //   HBox.setMargin(deleteImg, new Insets(10, 10, 10, 0));
                    setGraphic(managebtn);
                    setText(null);
                }
            }
        };
        colStatus.setCellFactory(cellStatus);

        colSerialNum.setCellFactory(cellAction);
    }

    private void selectTable(int index) {
        tableView.getSelectionModel().select(index);
    }

    public void addNewSerialKey(ActionEvent actionEvent) {
        Map<String, Object> map = new HashMap<>();
        map.put("company_id", companyId);
        map.put("application_id", applicationId);
        Main.primaryStage.setUserData(map);
        customDialog.showFxmlDialog2("license/addNewSerialKey.fxml", "Add New Serial Key");
        getSerialKey();
    }
}
