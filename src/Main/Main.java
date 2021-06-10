package Main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Optional;

public class Main extends Application {
    static Stage introStage;
    private double xOffset = 0;
    private double yOffset = 0;
    static TrayIcon trayIcon;
    static boolean firstTime = true;
    SystemTray tray;

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Lands Notify",
                    "Ok, I'm Here",
                    TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    private void hide(final Stage stage) {
        Platform.runLater(() -> {
            stage.hide();
            showProgramIsMinimizedMsg();
        });
    }

    private void addAppToTray(final Stage stage) {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            java.awt.Image image = Toolkit.getDefaultToolkit().getImage(System.class.getResource("/images/icon.png"));
            stage.setOnCloseRequest(t -> hide(stage));
            final ActionListener closeListener = e -> {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("إغلاق البرنامج");
                    alert.setHeaderText(null);
                    alert.setContentText("هل تريد إغلاق البرنامج نهائياً ؟");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK)
                        System.exit(0);
                });
            };

            ActionListener showListener = e -> Platform.runLater(stage::show);
            ActionListener hideListener = e -> Platform.runLater(stage::hide);
            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Show");
            MenuItem hideItem = new MenuItem("Hide");
            MenuItem exitItem = new MenuItem("Exit");

            showItem.addActionListener(showListener);
            hideItem.addActionListener(hideListener);
            exitItem.addActionListener(closeListener);

            popup.add(showItem);
            popup.add(hideItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon = new TrayIcon(image.getScaledInstance(16, -1, java.awt.Image.SCALE_SMOOTH), "Lands Notify", popup);
            trayIcon.addActionListener(showListener);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        addAppToTray(primaryStage);
        firstTime = true;
        Platform.setImplicitExit(false);
        Parent root = FXMLLoader.load(getClass().getResource("intro_screen.fxml"));
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        primaryStage.setTitle("Lands Notify");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root, 512, 512);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.getIcons().add(new Image("/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        introStage = primaryStage;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        event -> hide(primaryStage)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
