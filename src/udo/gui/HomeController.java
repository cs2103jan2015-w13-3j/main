package udo.gui;

import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;
import udo.storage.Task;
import udo.storage.Task.TaskType;

/**
 * This is the main controller class for the HomePage of the GUI. It controls
 * the data displayed and its styling. It is also the interface between the java
 * objects and its corresponding FXML objects. All user events are also handled by
 * this class
 * 
 * @author Sharmine
 *
 */

public class HomeController {
    private static final Logger logger = 
            Logger.getLogger(HomeController.class.getName());
            
    private static String STYLE_ITALIC = "italic";
    private static String STYLE_STRAIGHT = "straight";
    
    private static String HOTKEY_DISPLAY = "display";
    private static String HOTKEY_DONE = "display /done";
    
    private static String COLUMN_FIELD_CONTENT = "content";
    private static String COLUMN_FIELD_LABEL= "label";
    private static Label statusString;
    private static ObservableList<Task> data;
    private static CustomTextField customTextField;
    
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
        customTextField = new CustomTextField(inputBox, this);
        customTextField.bindKeys(keyHandlers);
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
    
    EventHandler<KeyEvent> keyHandlers = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            switch(code) {
                case TAB :
                    customTextField.handleTabKey(event);
                    break;
                case ENTER :
                    customTextField.handleReturnKey();
                    break;
                case UP :
                    customTextField.handleDirectionKey(event, GuiUtil.KEY_UP);
                    break;
                case DOWN :
                    customTextField.handleDirectionKey(event, GuiUtil.KEY_DOWN);
                    break;
                case F1 :
                    handleF1Key();
                    break;
                case F2:
                    handleF2Key();
                    break;
                case F3:
                    handleF3Key();
                    break;
                default:
                    customTextField.handleOtherKeys(event, code);
                    break; 
            }          
        }
    };
    
    //TODO String or list
    public String getAutocompleted(String userInput) {
        return gui.callAutocomplete(userInput);
    }
    
    public String getSuggestions(String userInput) {
        gui.callSuggestions(userInput);
        return null;
    }
    
    public boolean getCommand(String userInput) {
        return gui.callLogicCommand(userInput) == true;
    }
    
    public String getCmdHistory(String direction) {
        return gui.callCmdHistory(direction);
    }
    
    //TODO null or empty string
    public void displayStatus(String receivedString) {
        if(receivedString == null) {
            receivedString = GuiUtil.EMPTY_STRING;
        } else if(GuiUtil.isWarning(receivedString)) {
            statusString.setTextFill(GuiUtil.COLOUR_TEXT_WARNING);
        } else if(GuiUtil.isError(receivedString)) {
            statusString.setTextFill(GuiUtil.COLOUR_TEXT_ERROR);
        } else {
            statusString.setTextFill(GuiUtil.COLOUR_TEXT_NORMAL);
        }
        statusString.setText(receivedString);
        logger.finer(receivedString);
    }
    
    private void handleF1Key() {
        gui.callLogicCommand(HOTKEY_DISPLAY);
    }
    
    private void handleF2Key() {
        gui.callLogicCommand(HOTKEY_DONE);
    }
    //TODO be removed
    private void handleF3Key() {
        Task currDayDeadline5pm = new Task(TaskType.DEADLINE, "hand in work",
                new GregorianCalendar(2015, 02, 20, 17, 0), null, null, 0,
                null, GuiUtil.EMPTY_STRING, false, false);
        gui.displayAlert(currDayDeadline5pm);
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
            setTextFill(GuiUtil.COLOUR_TABLE_HEADERS);
            setAlignment(Pos.CENTER);
            styleItalic();
        }
        
        private void setTextStyle() {
            setTextFill(GuiUtil.COLOUR_TEXT_NORMAL);
            setAlignment(Pos.CENTER_LEFT);
            styleStraight();
        }
        
        private void setImportantStyle() {
            setTextFill(GuiUtil.COLOUR_TEXT_ERROR);
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
            this.setTextFill(GuiUtil.COLOUR_TEXT_NORMAL); 
            this.setText(item);
        }
    }
}
