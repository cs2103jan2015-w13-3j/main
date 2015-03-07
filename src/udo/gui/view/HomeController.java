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
import javafx.application.Platform;
import javafx.event.ActionEvent;

import udo.gui.GUI;
import udo.gui.GUIFormatter;
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

    // Reference to the Main Application.
    private GUI gui;
    public static final Color COLOR_TABLE_HEADERS = Color.rgb(26, 188, 156);
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
        disableDefaults();
        setFocusInputBox();
        setVariables();
    }

    @FXML
    private void setVariables() {
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

    private void disableDefaults() {
        disableDefaultSort();
        //disableMouse();
    }

    private void disableMouse() {
        TaskTable.setMouseTransparent(true);
    }

    private void disableDefaultSort() {
        taskNameColumn.setSortable(false);
        timeColumn.setSortable(false);
    }

    /**
     * Initialises the TableView with 2 columns
     */
    private void initialiseTableColumns() {
        initialiseTaskNameColumn();
        initialiseTimeColumn();

    }

    // TODO decide how to display time in timecolumn
    private void initialiseTimeColumn() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>(
                "label"));
        timeColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TableCell<Task, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setTextFill(Color.WHITE); 
                        this.setText(item);
                    }

                };
            }
       });
    }
    
    private void initialiseTaskNameColumn() {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("content"));
        taskNameColumn.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
            public TableCell<Task, String> call(TableColumn<Task, String> param) {
                return new TableCell<Task, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        formatCCellIfNotEmpty(item, this);                        
                    }

                };
            }
       });
    }
    
    private void formatCCellIfNotEmpty(String item, TableCell<Task, String> tableCell) {
        if (!tableCell.isEmpty()) {
            formatCellText(item, tableCell);
            tableCell.setText(item);
        }
    }
    
    private void formatCellText(String item, TableCell<Task, String> tableCell)
            throws ClassCastException {

        if (isValidDate(item)) {
            tableCell.setTextFill(COLOR_TABLE_HEADERS);
            tableCell.setAlignment(Pos.CENTER);
            tableCell.getStyleClass().add("italic");

        } else if (item.contains("coffee")) {
            tableCell.setTextFill(Color.HOTPINK);

        } else if (item.contains("more")) {
            tableCell.setTextFill(Color.RED);

        } else {
            tableCell.setTextFill(Color.WHITE);
        }
    }

    private boolean isValidDate(String dateString) {
        try {
            GUIFormatter.dateFormat.parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void handleReturnKey(ActionEvent event) {

        String text = inputBox.getText();
        inputBox.clear();

        gui.passUserInput(text);

    }

    public void displayStatus(String rcvdString) {
        if(rcvdString == null) {
            System.out.println("In Controller: rcvdString is Null");
            rcvdString = GUIFormatter.EMPTY_STRING;
        }
        statusString.setText(rcvdString);
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
