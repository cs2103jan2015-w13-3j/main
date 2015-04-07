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

//@author A0114906J

/**
 * This is the controller class for Help Manual dialog
 */

public class HelpManual {

    private static final String _TITLE = "Help Manual";
    private static final String _PATH_TO_MANUAL = "view/HelpManual.fxml";
    private static final String _PATH_TO_MANUAL_HTML = "view/HelpManual.html";
    
    private static double _xPos;
    private static double _yPos;
    
    private static Stage _dialogStage;
    
    @FXML
    private WebView _web;

    @FXML
    private void initialize() {
        WebEngine webEngine = _web.getEngine();
        String url = HelpManual.class.getResource(_PATH_TO_MANUAL_HTML)
                                     .toExternalForm();
        webEngine.load(url);
    }

    public void display() {
        Scene scene = setScene(_PATH_TO_MANUAL);

        _dialogStage = new Stage();
        setStage(scene);

        _dialogStage.showAndWait();
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
            _dialogStage.close();
        }
    }

    public void setPosition(double xCoord, double yCoord) {
        _xPos = xCoord;
        _yPos = yCoord;
    }

    private void setStage(Scene scene) {
        _dialogStage.setTitle(_TITLE);
        _dialogStage.initModality(Modality.WINDOW_MODAL);
        _dialogStage.setScene(scene);
        setStageLocation();
    }

    private void setStageLocation() {
        _dialogStage.setX(_xPos);
        _dialogStage.setY(_yPos);
    }

    public HelpManual() {

    }
}
