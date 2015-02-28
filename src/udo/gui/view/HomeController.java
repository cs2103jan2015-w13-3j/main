package udo.gui.view;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import udo.gui.GUI;
import udo.storage.Task;

public class HomeController {
    @FXML
    private TableView<Task> TaskTable;
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
     * Initialises the TableView with 2 columns
     */
    private void initialiseTableColumns() {

        initialiseTaskNameColumn();
        initialiseTimeColumn();
        
    }
    
    //TODO decide how to display time in timecolumn
    private void initialiseTimeColumn() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("label"));
    }

    private void initialiseTaskNameColumn() {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("content"));
        taskNameColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            
            public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TableCell<Task, String>() {
    
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            formatCellText(item, this);
                            setText(item);
                        }
                    }

                };
            }
        });
    }
    
    //TODO decide how to check for 'date' format
    private void formatCellText(String item, TableCell<Task,String> cell) throws ClassCastException {

        if (item.contains("2015")) {
            cell.setTextFill(Color.WHITE);
            cell.setAlignment(Pos.CENTER);
            cell.setUnderline(true);
        }

        if (item.contains("coffee")) {
            cell.setTextFill(Color.BLUEVIOLET);

        }
      
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {
        
        String text = inputBox.getText();
        inputBox.clear();
        System.out.println(text); //for testing
      
        gui.passUserInput(text);
        
        //To be removed: Logic returns a string
        //gui.displayStatus("added successfully"); 
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
        taskNameColumn.setSortable(false);
        timeColumn.setSortable(false);
    }
    
    
}
