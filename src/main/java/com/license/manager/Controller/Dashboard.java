package com.license.manager.Controller;

import com.license.manager.Controller.Auth.Login;
import com.license.manager.CustomDialog;
import com.license.manager.ImageLoader;
import com.license.manager.Main;
import com.license.manager.Model.AllApplication;
import com.license.manager.Model.Users;
import com.license.manager.PropertiesLoader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
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
import java.util.*;

public class Dashboard implements Initializable {
    public Label usernameL;
    public TableView<AllApplication> tableView;
    public TableColumn<AllApplication, Integer> colSrNo;
    public TableColumn<AllApplication,String> colAppId;
    public TableColumn<AllApplication,String> colAppName;
    public TableColumn<AllApplication,String> colStatus;
    public TableColumn<AllApplication,String> colRegisteredDate;
    public TableColumn<AllApplication,String> colAppType;
    public TableColumn<AllApplication,String> colAction;
    private  Users users;
  private CustomDialog customDialog;
private ObservableList<AllApplication> appList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        users =(Users) Main.primaryStage.getUserData();
        if (null == users){
            return;
        }
        Main.primaryStage.setUserData(null);
        usernameL.setText(users.getUserName());
        getAllApplication();
    }
    public void logout(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning ");
        alert.setHeaderText("ARE YOU SURE YOU WANT TO LOGOUT ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            Login.activeUserId = 0;
            new Main().changeScene("auth/login.fxml","LOGIN HERE");
        } else {
            alert.close();
        }

    }
    public void addNewApps(ActionEvent actionEvent) {

        customDialog.showFxmlDialog2("addNewApps.fxml","ADD NEW APPLICATION");

        if (null == Main.primaryStage.getUserData()){
            return;
        }

        boolean isSuccessfullyAdded = false;
        try {
            isSuccessfullyAdded = (Boolean) Main.primaryStage.getUserData();
        } catch (ClassCastException e) {
            return;
        }

        if (isSuccessfullyAdded){
           getAllApplication();
            Main.primaryStage.setUserData(null);
        }else {
            System.out.println("fail");
        }
    }
    private void getAllApplication() {
        if (null != appList){
            appList.clear();
        }

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("getAllApps"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("COMPANY_ID",String.valueOf(Login.activeUserId)));
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
                    JSONArray appArray = new JSONArray(jb.getJSONArray("data"));

                    for (int i = 0; i < appArray.length(); i++) {
                        JSONObject jo = appArray.getJSONObject(i);

                        int id = jo.getInt("id");
                        String appId = jo.getString("application_id");
                        String appName = jo.getString("application_name");
                        String appType = jo.getString("application_type");
                        String status = null;

                        switch (jo.getInt("status")){
                            case 0 -> status = "INACTIVE" ;
                            case 1 -> status = "ACTIVE" ;
                        }

                        int connectedApp = jo.getInt("connected_application");
                        int maxConnectedApp = jo.getInt("max_connected_application") ;
                        int ownerId = jo.getInt("owner_id") ;

                        String date  = jo.getString("registered_date");
                        String ownerName  = jo.getString("name");
                        String ownerPhone  = jo.getString("phone");
                        String ownerEmail  = jo.getString("email");

                        appList.add(new AllApplication(id,appId,appName , appType , status , connectedApp , maxConnectedApp ,
                                date,ownerId , ownerName,ownerPhone,ownerEmail));
                    }

                    tableView.setItems(appList);
                    colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                            tableView.getItems().indexOf(cellData.getValue()) + 1));
                    colAppName.setCellValueFactory(new PropertyValueFactory<>("applicationName"));
                    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
                    colRegisteredDate.setCellValueFactory(new PropertyValueFactory<>("registeredDate"));
                    colAppType.setCellValueFactory(new PropertyValueFactory<>("applicationTYpe"));

                    setOptionalCell();

                }else {
                    customDialog.showAlertBox("Failed",message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setOptionalCell(){

        Callback<TableColumn<AllApplication, String>, TableCell<AllApplication, String>>
                cellviewLicense = (TableColumn<AllApplication, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Label space = new Label("space");
                    Button editBn = new Button();
                    space.setVisible(false);

                    ImageLoader imageLoader = new ImageLoader();

                    Button viewLicense = new Button("LICENSE");
                    Button backupFile = new Button("VIEW BACKUP");

                    double width = 125;

                    viewLicense.setMinWidth(width);
                    backupFile.setMinWidth(width);

                    ImageView iconLicense = new ImageView();
                    iconLicense.setFitHeight(18);
                    iconLicense.setFitWidth(18);
                    iconLicense.setImage(imageLoader.load("icon/key_ic.png"));

                    ImageView iconBackup = new ImageView();
                    iconBackup.setFitHeight(18);
                    iconBackup.setFitWidth(18);
                    iconBackup.setImage(imageLoader.load("icon/backup_ic.png"));

                    viewLicense.setGraphic(iconLicense);
                    backupFile.setGraphic(iconBackup);

                    //EDIT BUTTON
                    ImageView editImg = new ImageView();
                    editImg.setFitHeight(18);
                    editImg.setFitWidth(18);
                    editImg.setImage(imageLoader.load("icon/edit_ic.png"));
                    editBn.setGraphic(editImg);
                    editBn.setStyle("-fx-background-color: blue ; -fx-cursor: hand");
                    viewLicense.setStyle("-fx-cursor: hand;-fx-graphic-text-gap: 10; -fx-background-color: #6666ff;-fx-background-radius: 3 ; -fx-text-fill: white");
                    backupFile.setStyle("-fx-cursor: hand; -fx-background-color: #ff5050;-fx-background-radius: 3 ; -fx-text-fill: white");

                    viewLicense.setOnAction(actionEvent ->{
                        selectTable(getIndex());
                        AllApplication allApplication = tableView.getSelectionModel().getSelectedItem();
                        if (null == allApplication){
                            return;
                        }
                        Map<String , Object> map = new HashMap<>();
                        map.put("company_id",Login.activeUserId);
                        map.put("name",allApplication.getOwnerName());
                        map.put("phone",allApplication.getOwnerPhone());
                        map.put("email",allApplication.getOwnerEmail());
                        map.put("ownerId",allApplication.getOwnerId());
                        map.put("application_id",allApplication.getApplicationId());
                        Main.primaryStage.setUserData(map);

                        customDialog.showFxmlFullDialog("license/licenseMain.fxml","");

                    });
                    backupFile.setOnAction(actionEvent ->{
                        selectTable(getIndex());
                        AllApplication allApplication = tableView.getSelectionModel().getSelectedItem();
                        if (null == allApplication){
                            return;
                        }

                        String appId = allApplication.getApplicationId();
                        Main.primaryStage.setUserData(appId);
                        customDialog.showFxmlFullDialog("viewBackup.fxml","APPLICATION ID : "+appId);
                    });

                    editBn.setOnAction(actionEvent -> {
                        selectTable(getIndex());
                        AllApplication allApplication = tableView.getSelectionModel().getSelectedItem();
                        if (null == allApplication){
                            return;
                        }
                        Main.primaryStage.setUserData(allApplication);
                       customDialog.showFxmlDialog2("update/updateApplication.fxml","UPDATE APPLICATION DETAILS");
                       getAllApplication();
                    });

                    HBox managebtn = new HBox(editBn,viewLicense , backupFile);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(viewLicense, new Insets(10, 0, 10, 20));
                    HBox.setMargin(backupFile, new Insets(0, 15, 0, 15));
                    setGraphic(managebtn);
                    setText(null);
                }
            }

        };

        Callback<TableColumn<AllApplication, String>, TableCell<AllApplication, String>>
                cellAppId = (TableColumn<AllApplication, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    String key = tableView.getItems().get(getIndex()).getApplicationId();

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
                        AllApplication AllApplication = tableView.getSelectionModel().getSelectedItem();
                        if (null == AllApplication) {
                            return;
                        }

                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(AllApplication.getApplicationId());
                        clipboard.setContent(content);
                        copyBn.setStyle("-fx-background-color: #03961c ; -fx-alignment: center; -fx-border-radius: 2 ; -fx-border-color: gray;-fx-cursor: hand");

                    });

                    HBox managebtn = new HBox(serialKey , copyBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(copyBn, new Insets(10, 20, 10, 20));
                    //   HBox.setMargin(deleteImg, new Insets(10, 10, 10, 0));
                    setGraphic(managebtn);
                    setText(null);
                }
            }
        };

        colAction.setCellFactory(cellviewLicense);
        colAppId.setCellFactory(cellAppId);
        customColumn(colAppName);
    }

    private void selectTable(int index){
        tableView.getSelectionModel().select(index);
    }

    private void customColumn(TableColumn<AllApplication, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<AllApplication, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("-fx-font-size: 14");
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER ;");
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(columnName.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }
}
