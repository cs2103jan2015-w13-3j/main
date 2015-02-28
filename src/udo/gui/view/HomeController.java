package udo.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
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
    private TableColumn<Task, String> timeColumn;
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
        initialiseTableColumns(); 
        disableDefaultSort();
        statusString = status;        
    }
    
    /**
     * Initialises the TableView with 3 columns
     */
    private void initialiseTableColumns() {
        //TODO refactor
        displayIndexColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("taskType"));
        
        //TODO decide how to display duration
        timeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("label"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("content"));
        taskNameColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            
            public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TableCell<Task, String>() {
    
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.RED);
                            // Get fancy and change color based on data
                            if(item.contains("coffee")) {
                                this.setTextFill(Color.BLUEVIOLET);
                                this.setFont(Font.font ("Droid Sans", FontWeight.BOLD, 20));
                            }
                            setText(item);
                        }
                    }
                };
            }
        });
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {
        
        String text = inputBox.getText();
        inputBox.clear();
        System.out.println(text); //for testing
      
        gui.passUserInput(text);
        
        //To be removed: Logic returns a string
        //gui.displayStatus("added succesfully"); 
    }
    
    public void displayStatus(String testString){
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
    
    private void disableDefaultSort(){
        displayIndexColumn.setSortable(false);
        taskNameColumn.setSortable(false);
        timeColumn.setSortable(false);
    }
}
