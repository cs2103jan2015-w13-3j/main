package udo.gui;

import java.io.IOException;

import java.net.URL;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

//@author A0114906J

/**
 * This class is the entry point for the application JustU. It provides an
 * interface for the user to interact with and displays corresponding feedback.
 * It initiates the Logic object.
 *
 */

public class Gui extends Application {
    private static final Logger logger = Logger.getLogger(Gui.class.getName());

    private static final String _NAME_APP = "JustU";

    private static final int _MAX_WORDS = 5;

    private static final int _OFFSET_DISPLAY = 250;

    private static final int _ARR_INDEX_0 = 0;
    private static final int _ARR_INDEX_1 = 1;
    private static final int _ARR_SIZE = 2;
    
    private static final String _PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String _PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String _PATH_TO_LOGO = "view/logo.png";   
    private static final String _PATH_TO_FONTS =
            "http://fonts.googleapis.com/css?family=Open+Sans:" +
            "300italic,400italic,600italic,700,600,400";
    
    private static List<Task> _displayList;
    private static HomeController _controller;
    private static ObservableList<Task> _taskData;
    private static GuiFormatter _guiFormatter;
    private static HelpManual _helpManual;

    private Stage _primaryStage;
    private BorderPane _rootLayout;
    private Logic _logic;

    public Gui() {
        _logic = Logic.getInstance();
        _logic.setGui(this);
        _guiFormatter = GuiFormatter.getInstance();
        _helpManual = new HelpManual();
        logger.setLevel(Level.INFO);
    }

    @Override
    public void start(Stage _primaryStage) {
        setPrimaryStage(_primaryStage);
        setSecondaryStage();
        logger.fine("Gui Initiation Completed");

        callLogicCommand(Config.CMD_STR_DISPLAY);
    }

    private void setPrimaryStage(Stage primaryStage) {
        this._primaryStage = primaryStage;
        Image icon = new Image(Gui.class.getResourceAsStream(_PATH_TO_LOGO));
        this._primaryStage.getIcons().add(icon);
        this._primaryStage.setTitle(_NAME_APP);
    }

    private void setSecondaryStage() {
        showRootLayout();
        showOverview();
    }

    /**
     * Initializes the root layout.
     */
    private void showRootLayout() {
        try {
            FXMLLoader loader = getLoader(_PATH_TO_ROOTLAYOUT);
            Scene rootScene = getRootScene(loader);
            setSceneAtStage(rootScene);
            logger.finer("Root Layout Initiated");

        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    private Scene getRootScene(FXMLLoader loader) throws IOException {
        _rootLayout = (BorderPane) loader.load();
        Scene scene = new Scene(_rootLayout);
        addFonts(scene);
        return scene;
    }

    private Scene addFonts(Scene scene) {
        scene.getStylesheets().add(_PATH_TO_FONTS);
        return scene;
    }

    private void setSceneAtStage(Scene scene) {
        _primaryStage.setScene(scene);
        _primaryStage.show();
    }

    /**
     * Shows overview inside the root layout.
     */
    private void showOverview() {
        try {
            FXMLLoader loader = getLoader(_PATH_TO_OVERVIEW);
            setOverview(loader);
            getControllerAccess(loader);
            logger.finer("Overview Scene Initiated");

        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    private void setOverview(FXMLLoader loader) throws IOException {
        AnchorPane homeOverview = (AnchorPane) loader.load();
        centerOverview(homeOverview);
    }

    private FXMLLoader getLoader(String path) {
        URL url = Gui.class.getResource(path);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        return loader;
    }

    private void centerOverview(AnchorPane homeOverview) {
        _rootLayout.setCenter(homeOverview);
    }

    private void getControllerAccess(FXMLLoader loader) {
        _controller = loader.getController();
        _controller.setMainApp(this);
    }

    /**
     * Returns the main stage.
     *
     * @return _primaryStage
     */
    @SuppressWarnings("unused")
    private Stage getPrimaryStage() {
        return _primaryStage;
    }

    /**
     * Calls the execution command in Logic
     *
     * @param userInput
     * @return true if userInput is successfully executed
     */
    public boolean callLogicCommand(String userInput) {
        assert(userInput != null);
        return _logic.executeCommand(userInput) == true;
    }

    public String callAutocomplete(String userInput) {
        assert(userInput != null);
        String completedStr = _logic.autocomplete(userInput);
        logger.fine(completedStr);
        return completedStr;
    }

    /**
     * Use the command history in Logic
     *
     * @param direction - up or down 
     * @return the previous or next command depending on the direction
     */
    public String callCmdHistory(String direction) {
        assert(direction != null);

        String command = new String();
        if(direction.equals(GuiUtil.KEY_UP)) {
            command = _logic.getPreviousCmd();
        } else {
            command = _logic.getNextCmd();
        }

        assert(command != GuiUtil.EMPTY_STRING);
        return command;
    }

    /**
     * Gets the a list of suggestions of words
     * 
     * @param userInput
     * @return a list of words 
     */
    public List<String> callSuggestions(String userInput) {
        assert(userInput != null);
        return _logic.getSuggestions(userInput, _MAX_WORDS);
    }

    /**
     * Displays the reminder dialog associated with the given task
     * 
     * @param task
     */
    public void displayAlert(Task task) {
        assert(task != null);
        ReminderDialog reminder = new ReminderDialog(task);
        reminder.appear();
        return;
    }
    
    /**
     * Displays the help manual 
     */
    public void displayManual() {
        double pos[] = getLocation();
        _helpManual.setPosition(pos[0] + _OFFSET_DISPLAY, pos[1]);
        _helpManual.display();
    }

    /**
     * Changes the status information to statusString
     *
     * @param statusString
     */
    public void displayStatus(String statusString) {
        assert(statusString != null);
        logger.info(statusString);

        _controller.displayStatus(statusString);
    }

    /**
     * Displays free slots in the GUI
     *
     * @param list of free slots
     */
    public void displayFreeSlots(List<Task> receivedList) {
        assert(receivedList != null);
        logger.info(receivedList.toString());

        _displayList = receivedList;
        processFreeSlots(_displayList);
        setDataToController();
    }
    
    private void processFreeSlots(List<Task> displayList) {
        _guiFormatter.setData(displayList);
        _taskData = _guiFormatter.getFormattedFreeSlotsData();
        logger.fine(_taskData.toString());
    }
    
    /**
     * Displays receivedList in the GUI
     *
     * @param list of tasks
     */
    public void display(List<Task> receivedList) {
        assert(receivedList != null);
        logger.info(receivedList.toString());

        _displayList = receivedList;
        processReceivedList(_displayList);
        setDataToController();
    }

    private void processReceivedList(List<Task> displayList) {
        _guiFormatter.setData(displayList);
        _taskData = _guiFormatter.getFormattedData();
        logger.fine(_taskData.toString());
    }

    private void setDataToController() {
        if(_controller != null) {
            _controller.setData();
        }
    }

    /**
     * Returns the data as an ObservableList of TaskS
     *
     * @return _taskData
     */
    public ObservableList<Task> getNewData() {
        return _taskData;
    }

    private double[] getLocation() {
        double[] position = new double[_ARR_SIZE];
        position[_ARR_INDEX_0] = _primaryStage.getX();
        position[_ARR_INDEX_1] = _primaryStage.getY();
        return position;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
