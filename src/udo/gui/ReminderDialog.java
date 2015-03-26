package udo.gui;

import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;
import udo.storage.Task;

public class ReminderDialog {
    
    private static final String TITLE = "Reminders";
    private static final String CONTENT = "Tasks Due";

    private static final double POS_X = 980;
    private static final double POS_Y = 10;
    private static final double POS_WIDTH = 800;
    private static final double POS_HEIGHT = 100;
    
    private Alert alert;
    
    public ReminderDialog(ArrayList<Task> list) {
        alert = new Alert(AlertType.INFORMATION);
        removeDefaults();
        setInformation(list);
        setStyles();
        setPosition();
        setButton();
    }

    private void setStyles() {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b3339;");
        //Text text1=new Text("This is ");
        //text1.setStyle("-fx-fill: white;");
        //dialogPane.setContent(text1);
        //dialogPane.setHeight(POS_HEIGHT);
        //dialogPane.setWidth(POS_WIDTH);
    }

    private void setInformation(ArrayList<Task> list) {
        alert.setTitle(TITLE);
        alert.setContentText(CONTENT);
    }
    
    private void removeDefaults(){
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
    }
    
    private void setPosition(){
        alert.setX(POS_X);
        alert.setY(POS_Y);
        //alert.setWidth(POS_WIDTH);
        //alert.setHeight(POS_HEIGHT);
    }
    
    private void setButton() {
        ButtonType openButton = new ButtonType("Open", ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().addAll(openButton);
    }
    
    public void appear(){
        alert.showAndWait();
    }
}
