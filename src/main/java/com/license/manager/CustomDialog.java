package com.license.manager;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class CustomDialog {

    public static Stage stage;
    public static Stage stage2;
    public static Stage stage3;

    public void showFxmlDialog(String fxml_file, String title)  {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml_file)));

            stage = new Stage();
            stage.setTitle(title);
            stage.initOwner(Main.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
            stage.setScene(scene);

            scene.setOnKeyReleased(e -> {

                if (e.getCode() == KeyCode.ESCAPE){
                    if (stage.isShowing()){
                        stage.close();
                    }

                }
            });

            stage.showAndWait();



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showAlertBox(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.initOwner(Main.primaryStage);
        alert.showAndWait();
    }

    public void showFxmlDialog2(String fxml_file, String title)  {

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml_file)));
            stage2 = new Stage();
            stage2.initOwner(Main.primaryStage);
            stage2.initStyle(StageStyle.UTILITY);
            stage2.setTitle(title);
            stage2.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
            stage2.setScene(scene);
            stage2.setResizable(false);

            scene.setOnKeyReleased(e -> {

                if (e.getCode() == KeyCode.ESCAPE){
                    if (stage2.isShowing()){
                        stage2.close();
                    }

                }
            });

            stage2.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showFxmlFullDialog(String fxml_file, String title)  {

        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml_file)));

            stage3 = new Stage();
            stage3.setTitle(title);
            stage3.initOwner(Main.primaryStage);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
            stage3.setScene(scene);

            scene.setOnKeyReleased(e -> {

                if (e.getCode() == KeyCode.ESCAPE){
                    if (stage3.isShowing()){
                        stage3.close();
                    }
                }
            });

            stage3.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContextMenu show_popup(String message, Object textField){

        ContextMenu form_Validator = new ContextMenu();
        form_Validator.setAutoHide(true);
        form_Validator.getItems().add(new MenuItem(message));
        form_Validator.show((Node) textField, Side.RIGHT, 10, 0);

        return form_Validator;

    }
}
