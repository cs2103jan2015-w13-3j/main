package udo.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
      
    public static final Color COLOR_TABLE_HEADERS = Color.rgb(26, 188, 156);
    public static final Color COLOR_TEXT_WARNING = Color.ORANGE;
    public static final Color COLOR_TEXT_ERROR = Color.RED;
    public static final Color COLOR_TEXT_NORMAL = Color.WHITE;
    
    public static String STYLE_ITALIC = "italic";
    public static String STYLE_STRAIGHT = "straight";

    private static String COLUMN_FIELD_CONTENT = "content";
    private static String COLUMN_FIELD_LABEL= "label";
    private static Label statusString;
    private static ObservableList<Task> data;
    
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
        LOGGER.setLevel(Level.OFF);
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

        if (GUIUtil.isHeader(item)) {
            setHeaderStyle(tableCell);
        } else if (GUIUtil.isImportant(item, data)) { 
            setImportantStyle(tableCell);
        } else {
           setTextStyle(tableCell);
        }
    }

    @FXML
    private void handleReturnKey(ActionEvent event) {
        String text = retrieveUserInput();

        if (gui.callLogicCommand(text) == true) {
            inputBox.clear();
        }
    }
    
    // Event handler for Tab Key
    EventHandler<KeyEvent> handleTabKey = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.TAB) {
                LOGGER.info("Handling event " + event.getEventType());
                String userInput = retrieveUserInput();
                gui.callAutocomplete(userInput);
                event.consume();
            }
        }
    };
    
    public void displayStatus(String receivedString) {
        if(receivedString == null) {
            receivedString = GUIUtil.EMPTY_STRING;
        } else if(GUIUtil.isWarning(receivedString)) {
            statusString.setTextFill(COLOR_TEXT_WARNING);
        } else if(GUIUtil.isError(receivedString)) {
            statusString.setTextFill(COLOR_TEXT_ERROR);
        } else {
            statusString.setTextFill(COLOR_TEXT_NORMAL);
        }
        statusString.setText(receivedString);
        LOGGER.finer("Status string = " + receivedString);
    }
    
    private String retrieveUserInput() {
        return inputBox.getText();
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
        data = gui.getNewData();
        TaskTable.setItems(data);
        LOGGER.finer("New Data set to Table");
    }
    
    private void refreshTable() {
        TaskTable.getColumns().get(0).setVisible(false);
        TaskTable.getColumns().get(0).setVisible(true);
    }
    
    public static void setHeaderStyle(TableCell<Task, String> cell) {        
        cell.setTextFill(COLOR_TABLE_HEADERS);
        cell.setAlignment(Pos.CENTER);
        cell.getStyleClass().remove(STYLE_STRAIGHT);
        cell.getStyleClass().add(STYLE_ITALIC);
    }
    
    public static void setTextStyle(TableCell<Task, String> cell) {
        cell.setTextFill(Color.WHITE);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getStyleClass().remove(STYLE_ITALIC);
        cell.getStyleClass().add(STYLE_STRAIGHT);
    }
    
    public static void setImportantStyle(TableCell<Task, String> cell) {
        cell.setTextFill(Color.RED);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getStyleClass().remove(STYLE_ITALIC);
        cell.getStyleClass().add(STYLE_STRAIGHT);
    }
    
    private class TaskCell extends TableCell<Task,String> {

        public TaskCell() {
            
        }
          
        @Override 
            protected void updateItem(String item, boolean empty) {            
            super.updateItem(item, empty);  
            this.setText(GUIUtil.EMPTY_STRING);
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
            this.setTextFill(COLOR_TEXT_NORMAL); 
            this.setText(item);
        }
    }
}
