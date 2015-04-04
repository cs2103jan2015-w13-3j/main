package udo.gui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is the controller class for Help Manual dialog
 * 
 * @author Sharmine
 *
 */
public class HelpManual {

    @FXML
    private WebView web;

    private static Stage dialogStage;

    private static final String TITLE = "Help Manual";
    private static final String PATH_TO_MANUAL = "view/HelpManual.fxml";
    private static double xPos;
    private static double yPos;

    @FXML
    private void initialize() {
        //TODO asset(path != null);
        WebEngine webEngine = web.getEngine();
        String url = HelpManual.class.getResource("view/HelpManual.html")
                .toExternalForm();
        webEngine.load(url);

    }

    public void display() {
        Scene scene = setScene(PATH_TO_MANUAL);

        dialogStage = new Stage();
        setStage(scene);

        dialogStage.showAndWait();
    }

    private Scene setScene(String path) {
        AnchorPane page = new AnchorPane();
        
        try {
            page = (AnchorPane) getPane(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new Scene(page);
    }

    private AnchorPane getPane(String path) throws IOException {
        FXMLLoader loader = getLoader(path);
        return (AnchorPane) loader.load();
    }

    private FXMLLoader getLoader(String path) {
        URL url = HelpManual.class.getResource(path);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        return loader;
    }

    @FXML
    private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            dialogStage.close();
        }
    }

    public void setPosition(double xCoord, double yCoord) {
        xPos = xCoord;
        yPos = yCoord;
    }

    private void setStage(Scene scene) {
        dialogStage.setTitle(TITLE);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(scene);
        setStageLocation();
    }

    private void setStageLocation() {
        dialogStage.setX(xPos);
        dialogStage.setY(yPos);
    }

    public HelpManual() {

    }
}
