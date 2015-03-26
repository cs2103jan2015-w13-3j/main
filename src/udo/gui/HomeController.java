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

/**
 * This is the main controller class for the HomePage of the GUI. It controls
 * the data displayed and its styling. It is also the interface between the java
 * objects and its corresponding FXML objects. All user events are handled by
 * this class
 * 
 * @author Sharmine
 *
 */

public class HomeController {
    private static final Logger logger = 
            Logger.getLogger(HomeController.class.getName());
      
    private static final Color COLOR_TABLE_HEADERS = Color.rgb(26, 188, 156);
    private static final Color COLOR_TEXT_WARNING = Color.ORANGE;
    private static final Color COLOR_TEXT_ERROR = Color.RED;
    private static final Color COLOR_TEXT_NORMAL = Color.WHITE;
            
    private static String STYLE_ITALIC = "italic";
    private static String STYLE_STRAIGHT = "straight";

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
    private Gui gui;

    
    public HomeController() {
        logger.setLevel(Level.INFO);
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
        inputBox.setOnKeyPressed(keyHandlers);
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

    @FXML
    private void handleReturnKey(ActionEvent event) {
        String text = retrieveUserInput();

        if (gui.callLogicCommand(text) == true) {
            inputBox.clear();
        }
    }
    
    //TODO to be refactored to OOP
    EventHandler<KeyEvent> keyHandlers = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            switch(code) {
                case TAB:
                    handleTabKey(event);
                    break;
                case UP:
                    handleDirectionKey(event, GuiUtil.KEY_UP);
                    break;
                case DOWN:
                    handleDirectionKey(event, GuiUtil.KEY_DOWN);
                    break;
                default:
                    handleOtherKeys(event, code);
                    break; 
            }          
        }
    };
    
    private void handleTabKey(KeyEvent event) {
        String userInput = retrieveUserInput();
        String completedStr = gui.callAutocomplete(userInput);
        displayInput(completedStr);
        inputBox.end();
        event.consume();
        logger.finer(completedStr);
    }

    private void handleDirectionKey(KeyEvent event, String direction) {
        String command = gui.callCmdHistory(direction);
        displayInput(command);
        inputBox.end();
        event.consume();
        logger.finer(command);
    }
    
    /*
     * Retrieves suggestions for any letter keys
     */
    private void handleOtherKeys(KeyEvent event, KeyCode code) {
        if(code.isLetterKey()) {
            String userInput = retrieveUserInput();
            gui.callSuggestions(userInput);
            //displayStatus();
            event.consume();
            logger.info("Suggestion: ");
        } else {
            return;
        }
    }
    
    //TODO null or empty string
    public void displayStatus(String receivedString) {
        if(receivedString == null) {
            receivedString = GuiUtil.EMPTY_STRING;
        } else if(GuiUtil.isWarning(receivedString)) {
            statusString.setTextFill(COLOR_TEXT_WARNING);
        } else if(GuiUtil.isError(receivedString)) {
            statusString.setTextFill(COLOR_TEXT_ERROR);
        } else {
            statusString.setTextFill(COLOR_TEXT_NORMAL);
        }
        statusString.setText(receivedString);
        logger.finer(receivedString);
    }
    
    private void displayInput(String str) {
        inputBox.setText(str);
    }
    
    private String retrieveUserInput() {
        return inputBox.getText();
    }
    
    /**
     * Called by the main application to give a reference back to itself.
     * 
     * @param gui
     */
    public void setMainApp(Gui gui) {
        this.gui = gui;            
    }
    
    public void setData() {
        refreshTable();
        data = gui.getNewData();
        TaskTable.setItems(data);
        logger.finer(data.toString());
    }
    
    private void refreshTable() {
        TaskTable.getColumns().get(0).setVisible(false);
        TaskTable.getColumns().get(0).setVisible(true);
    }
    
    private class TaskCell extends TableCell<Task, String> {

        public TaskCell() {

        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            this.setText(GuiUtil.EMPTY_STRING);
            formatCellIfNotEmpty(item, this);
            this.setText(item);
        }
        

        private void formatCellIfNotEmpty(String item, TableCell<Task, String> tableCell) {      
            if (!tableCell.isEmpty()) {
                formatCellText(item, tableCell);
            }
        }

        private void formatCellText(String item, TableCell<Task, String> tableCell)
                throws ClassCastException {

            if (GuiUtil.isHeader(item)) {
                setHeaderStyle();
            } else if (GuiUtil.isImportant(item, data)) { 
                setImportantStyle();
            } else {
               setTextStyle();
            }
        }
        
        private void setHeaderStyle() {        
            setTextFill(COLOR_TABLE_HEADERS);
            setAlignment(Pos.CENTER);
            styleItalic();
        }
        
        private void setTextStyle() {
            setTextFill(Color.WHITE);
            setAlignment(Pos.CENTER_LEFT);
            styleStraight();
        }
        
        private void setImportantStyle() {
            setTextFill(Color.RED);
            setAlignment(Pos.CENTER_LEFT);
            styleStraight();
        }

        private void styleStraight() {
            getStyleClass().remove(STYLE_ITALIC);
            getStyleClass().add(STYLE_STRAIGHT);
        }
        
        private void styleItalic() {
            getStyleClass().remove(STYLE_STRAIGHT);
            getStyleClass().add(STYLE_ITALIC);
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
