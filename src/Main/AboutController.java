package Main;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AboutController {
    @FXML
    void onCloseAbout() {
        IntroController.aboutStage.hide();
    }

    @FXML
    void openLog() {
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                Desktop.getDesktop().open(new File("log.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
