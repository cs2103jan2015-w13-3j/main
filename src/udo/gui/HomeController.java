package udo.gui;

import java.util.List;
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

//@author A0114906J

/**
 * This is the main controller class for the HomePage of the GUI. It controls
 * the data displayed and its styling. It is also the interface between the java
 * objects and its corresponding FXML objects. All user events are also handled
 * by this class
 *
 */

public class HomeController {
    private static final Logger logger = Logger.getLogger(HomeController.class
                                               .getName());

    private static String _STYLE_ITALIC = "italic";
    private static String _STYLE_STRAIGHT = "straight";

    private static String _HOTKEY_DISPLAY = "display";
    private static String _HOTKEY_DONE = "display /done";

    private static String _COLUMN_FIELD_CONTENT = "content";
    private static String _COLUMN_FIELD_LABEL = "label";
    private static Label _statusString;
    private static ObservableList<Task> _data;
    private static CustomTextField _customTextField;

    @FXML
    private TableView<Task> _TaskTable;
    @FXML
    private TableColumn<Task, String> _taskNameColumn;
    @FXML
    private TableColumn<Task, String> _timeColumn;
    @FXML
    private TextField _inputBox;
    @FXML
    private Label _status;

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
        _taskNameColumn.setSortable(false);
        _timeColumn.setSortable(false);
    }

    private void configureTextField() {
        setFocusInputBox();
        _customTextField = new CustomTextField(_inputBox, this);
        _customTextField.bindKeys(keyHandlers);
    }

    @FXML
    private void configureStatus() {
        _statusString = _status;
    }

    private void setFocusInputBox() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _inputBox.requestFocus();
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
        PropertyValueFactory<Task, String> labelFactory = 
                new PropertyValueFactory<Task, String>(_COLUMN_FIELD_LABEL);
        
        _timeColumn.setCellValueFactory(labelFactory);

        // create 'label' column callback
        Callback<TableColumn<Task, String>, 
                 TableCell<Task, String>> labelCallBack = 
                 new Callback<TableColumn<Task, String>, 
                              TableCell<Task, String>>() {
            
            @Override
            public TableCell<Task, String> call(TableColumn<Task, 
                                                            String> param) {
                return new TimeCell();
            }
        };
        _timeColumn.setCellFactory(labelCallBack);
    }

    private void initialiseTaskNameColumn() {
        PropertyValueFactory<Task, String> contentFactory = 
                new PropertyValueFactory<Task, String>(_COLUMN_FIELD_CONTENT);
        
        _taskNameColumn.setCellValueFactory(contentFactory);

        // create 'content' column callback
        Callback<TableColumn<Task, String>, 
                             TableCell<Task, String>> contentCallBack = 
                             new Callback<TableColumn<Task, String>, 
                             TableCell<Task, String>>() {
            
            @Override
            public TableCell<Task, String> call(TableColumn<Task, 
                                                            String> param) {
                return new TaskCell();
            }
        };
        _taskNameColumn.setCellFactory(contentCallBack);
    }

    EventHandler<KeyEvent> keyHandlers = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            switch (code) {
                case TAB:
                    _customTextField.handleTabKey(event);
                    break;
                case ENTER:
                    _customTextField.handleReturnKey();
                    break;
                case UP:
                    _customTextField
                        .handleDirectionKey(event, GuiUtil.KEY_UP);
                    break;
                case DOWN:
                    _customTextField
                        .handleDirectionKey(event, GuiUtil.KEY_DOWN);
                    break;
                case F1:
                    handleF1Key();
                    break;
                case F2:
                    handleF2Key();
                    break;
                case F3:
                    handleF3Key();
                    break;
                default:
                    handleOtherKeys(event, code);
                    break;
            }
        }
    };

    public String getAutocompleted(String userInput) {
        assert (userInput != null);
        return gui.callAutocomplete(userInput);
    }

    public boolean getCommand(String userInput) {
        assert (userInput != null);
        return gui.callLogicCommand(userInput) == true;
    }

    public String getCmdHistory(String direction) {
        assert (direction != null);
        return gui.callCmdHistory(direction);
    }

    public void displayStatus(String receivedString) {
        assert (receivedString != null);

        if (receivedString == null) {
            receivedString = GuiUtil.EMPTY_STRING;
        } else if (GuiUtil.isWarning(receivedString)) {
            _statusString.setTextFill(GuiUtil.COLOUR_TEXT_WARNING);
        } else if (GuiUtil.isError(receivedString)) {
            _statusString.setTextFill(GuiUtil.COLOUR_TEXT_ERROR);
        } else {
            _statusString.setTextFill(GuiUtil.COLOUR_TEXT_NORMAL);
        }
        _statusString.setText(receivedString);
        logger.finer(receivedString);
    }

    private void handleF1Key() {
        gui.displayManual();
    }

    private void handleF2Key() {
        gui.callLogicCommand(_HOTKEY_DISPLAY);
    }

    private void handleF3Key() {
        gui.callLogicCommand(_HOTKEY_DONE);
    }

    /*
     * Retrieves suggestions for any letter keys
     */
    public void handleOtherKeys(KeyEvent event, KeyCode code) {
        String userInput = new String();

        if (code.isLetterKey()) {
            userInput = _customTextField.getText() + retrieveLetter(code);
        } else if (code.equals(KeyCode.BACK_SPACE)) {
            userInput = _customTextField.getText();
        } else {
            return;
        }

        String suggestedWords = getSuggestedWords(userInput);
        displayStatus(suggestedWords);

        logger.finer(suggestedWords.toString());
    }

    private String getSuggestedWords(String userInput) {
        List<String> suggestedListOfWords = gui.callSuggestions(userInput);
        String suggestedWords = GuiUtil
                .concatListToString(suggestedListOfWords);
        return suggestedWords;
    }

    private String retrieveLetter(KeyCode code) {
        return code.getName().toLowerCase();
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
        _data = gui.getNewData();
        _TaskTable.setItems(_data);
        logger.finer(_data.toString());
    }

    private void refreshTable() {
        _TaskTable.getColumns().get(0).setVisible(false);
        _TaskTable.getColumns().get(0).setVisible(true);
    }

    /**
     * Private nested class for cells under the task content column
     */
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

        private void formatCellIfNotEmpty(String item, 
                                          TableCell<Task, String> tableCell) {
            if (!tableCell.isEmpty()) {
                formatCellText(item, tableCell);
            }
        }

        private void formatCellText(String item,
                                    TableCell<Task, String> tableCell) 
                                    throws ClassCastException {

            if (GuiUtil.isHeader(item)) {
                setHeaderStyle();
            } else if (GuiUtil.isImportant(item, _data)) {
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
            getStyleClass().remove(_STYLE_ITALIC);
            getStyleClass().add(_STYLE_STRAIGHT);
        }

        private void styleItalic() {
            getStyleClass().remove(_STYLE_STRAIGHT);
            getStyleClass().add(_STYLE_ITALIC);
        }

    }

    /**
     * Private nested class for cells under the date/time column
     */
    private class TimeCell extends TableCell<Task, String> {

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
