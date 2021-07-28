package Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class IntroController implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;
    static Stage aboutStage = new Stage();

    @FXML
    void onClose() {
        if (showConfirmationDialog())
            System.exit(0);
    }

    @FXML
    void onInfo() {
        aboutStage.show();
    }

    @FXML
    void onMinimize() {
        if (Main.firstTime) {
            Main.trayIcon.displayMessage("Lands Notify",
                    "Ok, I'm Here",
                    TrayIcon.MessageType.INFO);
            Main.firstTime = false;
        }
        Main.introStage.hide();
    }

    boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("إغلاق البرنامج");
        alert.setHeaderText(null);
        alert.setContentText("هل تريد إغلاق البرنامج نهائياً ؟");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBChangeNotification dcn = new DBChangeNotification();
        try {
            dcn.run();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Fail to connect to Oracle Database,\n" + ex.getMessage());
            System.exit(0);
        }
        try {
            aboutStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("about_screen.fxml"));
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                aboutStage.setX(event.getScreenX() - xOffset);
                aboutStage.setY(event.getScreenY() - yOffset);
            });
            aboutStage.setTitle("About Lands Notify");
            aboutStage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root, 600, 250);
            aboutStage.getIcons().add(new Image("/images/icon.png"));
            aboutStage.setScene(scene);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
