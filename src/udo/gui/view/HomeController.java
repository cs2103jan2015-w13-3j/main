package udo.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import udo.gui.GUI;
import udo.storage.Tasks;

public class HomeController {
    @FXML
    private TableView<Tasks> TaskTable;
    @FXML
    private TableColumn<Tasks, String> displayIndexColumn;
    @FXML
    private TableColumn<Tasks, String> taskNameColumn;
    @FXML
    private TextField inputBox;
    
    // Reference to the main application.
    private GUI gui;
    
    public HomeController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        //Initialize the Task table with the two columns.
        displayIndexColumn.setCellValueFactory(new PropertyValueFactory<Tasks, String>("taskType"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Tasks, String>("content"));
        
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {
        // to pass string to Logic Class
        String text = inputBox.getText();
        System.out.println(text);
        inputBox.clear();
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
