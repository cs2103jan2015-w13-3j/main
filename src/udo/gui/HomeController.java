package udo.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import udo.storage.Task;

public class HomeController {

    private static String COLUMN_FIELD_CONTENT = "content";
    private static String COLUMN_FIELD_LABEL= "label";
    private static Label statusString;
    
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

    // Reference to the Main Application.
    private GUI gui;

    
    public HomeController() {
        
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        configureSettings();
        initialiseTableColumns();        
    }
    
    private void configureSettings() {
        disableDefaults();
        configureTextField();
        configureStatus();      
    }
    
    /**
     * Disables the sorting function of TableView
     */
    private void disableDefaults() {
        taskNameColumn.setSortable(false);
        timeColumn.setSortable(false);       
    }

    private void configureTextField() {
        setFocusInputBox();
        inputBox.setOnKeyPressed(handleTabKey);
    }

    @FXML
    private void configureStatus() {
        statusString = status;
    }

    private void setFocusInputBox() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                inputBox.requestFocus();
            }
        });
    }

    /**
     * Initializes the TableView with 2 columns
     */
    private void initialiseTableColumns() {
        initialiseTaskNameColumn();
        initialiseTimeColumn();

    }

    private void initialiseTimeColumn() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>(
                COLUMN_FIELD_LABEL));
        timeColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TimeCell();

            }
       });
    }
    
    private void initialiseTaskNameColumn() {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>(COLUMN_FIELD_CONTENT));
        taskNameColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            @Override public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TaskCell();

            }
       });
    }
    
    private void formatCellIfNotEmpty(String item, TableCell<Task, String> tableCell) {
       
        if (!tableCell.isEmpty()) {
            formatCellText(item, tableCell);
        }
    }
    
    private void formatCellText(String item, TableCell<Task, String> tableCell)
            throws ClassCastException {

        if (isHeader(item)) {
            GUIFormatter.setHeaderStyle(tableCell);
        } else if (isImportant(item)) { //for later milestones
            GUIFormatter.setImportantStyle(tableCell);
        } else {
           GUIFormatter.setTextStyle(tableCell);
        }
    }
  
    private boolean isHeader(String str) {
        return (isValidDate(str) || str.equals(GUIFormatter.HEADER_TODO));
    }
    
    private boolean isValidDate(String dateString) {       
        try {
            GUIFormatter.dateFormat.parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    //TODO define how to find out important task
    private boolean isImportant(String str) {
        return false;
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {

        String text = inputBox.getText();

        if (gui.callLogicCommand(text) == true) {
            inputBox.clear();
        }

    }
    
    // Event handler for Tab Key
    EventHandler<KeyEvent> handleTabKey = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.TAB) {
                System.out.println("Handling event " + event.getEventType()); 
                event.consume();
            }
        }
    };
    
    public void displayStatus(String receivedString) {
        if(receivedString == null) {
            receivedString = GUIFormatter.EMPTY_STRING;
        }
        statusString.setText(receivedString);
    }

    /**
     * Called by the main application to give a reference back to itself.
     * 
     * @param gui
     */
    public void setMainApp(GUI gui) {
        this.gui = gui;            
    }
    
    public void setData() {
        refreshTable();
        TaskTable.setItems(gui.getNewData());  
    }
    
    private void refreshTable() {
        TaskTable.getColumns().get(0).setVisible(false);
        TaskTable.getColumns().get(0).setVisible(true);
    }
    
    private class TaskCell extends TableCell<Task,String> {

        public TaskCell() {
            
        }
          
        @Override protected void updateItem(String item, boolean empty) {            
            super.updateItem(item, empty);  
            this.setText(GUIFormatter.EMPTY_STRING);
            formatCellIfNotEmpty(item, this);
            this.setText(item);
        }

    }
    
    private class TimeCell extends TableCell<Task,String> {

        public TimeCell() {
            
        }
          
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            this.setTextFill(Color.WHITE); 
            this.setText(item);
        }
    }

}
