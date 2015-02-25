package udo.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import udo.gui.GUI;
import udo.storage.Task;

public class HomeController {
    @FXML
    private TableView<Task> TaskTable;
    @FXML
    private TableColumn<Task, String> displayIndexColumn;
    @FXML
    private TableColumn<Task, String> taskNameColumn;
    @FXML
    private TextField inputBox;
    @FXML
    private Label status;
    
    // Reference to the main application.
    private GUI gui;
    private static Label statusString;
    
    public HomeController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        //Initialize the Task table with the two columns.
        displayIndexColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("taskType"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("content"));
        statusString = status;
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {
        
        String text = inputBox.getText();
        inputBox.clear();
        System.out.println(text); //for testing
      
        gui.passUserInput(text);
        
        //To be removed: Logic returns a string
        displayStatus("added succesfully"); 
    }
    
    public static void displayStatus(String testString){
       statusString.setText(testString);
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param gui
     */
    public void setMainApp(GUI gui) {
        this.gui = gui;

        // Add observable list data to the table      
        TaskTable.setItems(gui.getTaskData());  
    }
    
}
