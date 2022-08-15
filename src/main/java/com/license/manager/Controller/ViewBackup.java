package com.license.manager.Controller;

import com.license.manager.CustomDialog;
import com.license.manager.ImageLoader;
import com.license.manager.Main;
import com.license.manager.Method.BackupModel;
import com.license.manager.PropertiesLoader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewBackup implements Initializable {

    public TableView<BackupModel> tableView;
    public TableColumn <BackupModel, Integer>colSrNo;
    public TableColumn<BackupModel, String> colFileName;
    public TableColumn<BackupModel, String> colBackDate;
    public TableColumn<BackupModel, String> colAction;
    private CustomDialog customDialog;
    private String applicationId ;

    ObservableList<BackupModel> backupModels = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        applicationId = (String) Main.primaryStage.getUserData();
        customDialog = new CustomDialog();

        getApplicationId();
    }

    private void getApplicationId() {

        if (null != backupModels){
            backupModels.clear();
        }
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(new PropertiesLoader().getInsertProp().getProperty("viewBackup"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("application_id",applicationId));
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

                        int backupId = jo.getInt("backup_id");
                        String path = jo.getString("path");
                        String backupDate = jo.getString("backup_date");
                        String applicationId = jo.getString("application_id");


                        backupModels.add(new BackupModel(backupId,path,backupDate , applicationId));
                    }

                    tableView.setItems(backupModels);
                    colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                            tableView.getItems().indexOf(cellData.getValue()) + 1));
                    colFileName.setCellValueFactory(new PropertyValueFactory<>("path"));
                    colBackDate.setCellValueFactory(new PropertyValueFactory<>("backupDate"));

                    setOptionalCell();

                }else {
                    customDialog.showAlertBox("Failed",message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionalCell() {

        Callback<TableColumn<BackupModel, String>, TableCell<BackupModel, String>>
                cellAppId = (TableColumn<BackupModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    ImageView copyImg = new ImageView();
                    copyImg.setFitWidth(20);
                    copyImg.setFitHeight(20);
                    copyImg.setPreserveRatio(true);
                    copyImg.setImage(new ImageLoader().load("icon/download_ic.png"));
                    Button downloadBn = new Button();
                    downloadBn.setStyle("-fx-background-color: #000000 ; -fx-alignment: center; -fx-border-radius: 2 ; " +
                            "-fx-border-color: gray;-fx-cursor: hand");
                    downloadBn.setGraphic(copyImg);

                    downloadBn.setOnMouseClicked(mouseEvent -> {
                        selectTable(getIndex());
                        BackupModel bm = tableView.getSelectionModel().getSelectedItem();
                        if (null == bm) {
                            return;
                        }
                        String root = new PropertiesLoader().getInsertProp().getProperty("backupFolderRoot");
                        String fullPath = root+bm.getPath();

                        try {
                         openBrowser(fullPath);
                        } catch (Exception e) {}

                    });

                    HBox managebtn = new HBox(downloadBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(downloadBn, new Insets(10, 20, 10, 20));
                    setGraphic(managebtn);
                    setText(null);
                }
            }
        };
        colAction.setCellFactory(cellAppId);
    }

    private void openBrowser(String fullPath) {

        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(fullPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectTable(int index){
        tableView.getSelectionModel().select(index);
    }
}
