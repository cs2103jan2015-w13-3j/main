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
import javafx.stage.Modality;
import javafx.stage.Stage;
import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

/**
 * This class is the entry point for the application JustU. It provides an
 * interface for the user to interact with and displays corresponding feedback.
 * It initiates the Logic object.
 *
 * @author Sharmine
 *
 */

public class Gui extends Application {
    private static final Logger logger = Logger.getLogger(Gui.class.getName());

    private static final String NAME_APP = "JustU";
    
    private static final int MAX_WORDS = 5;
    
    private static final int OFFSET_DISPLAY = 250;
    
    private static final String PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String PATH_TO_LOGO = "view/logo.png";
    private static final String PATH_TO_FONTS =
            "http://fonts.googleapis.com/css?family=Open+Sans:" +
            "300italic,400italic,600italic,700,600,400";

    private static List<Task> displayList;
    private static HomeController controller;
    private static ObservableList<Task> taskData;
    private static GuiFormatter guiFormatter;

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Logic logic;

    /**
     * Constructor
     */
    public Gui() {
        logic = Logic.getInstance();
        logic.setGui(this);
        guiFormatter = GuiFormatter.getInstance();
        logger.setLevel(Level.INFO);
    }

    @Override
    public void start(Stage primaryStage) {
        setPrimaryStage(primaryStage);
        setSecondaryStage();
        logger.fine("Gui Initiation Completed");

        callLogicCommand(Config.CMD_STR_DISPLAY);
    }

    private void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Image icon = new Image(Gui.class.getResourceAsStream(PATH_TO_LOGO));
        this.primaryStage.getIcons().add(icon);
        this.primaryStage.setTitle(NAME_APP);
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
            FXMLLoader loader = getLoader(PATH_TO_ROOTLAYOUT);
            Scene rootScene = getRootScene(loader);
            setSceneAtStage(rootScene);
            logger.finer("Root Layout Initiated");

        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    private Scene getRootScene(FXMLLoader loader) throws IOException {
        rootLayout = (BorderPane) loader.load();
        Scene scene = new Scene(rootLayout);
        addFonts(scene);
        return scene;
    }

    private Scene addFonts(Scene scene) {
        scene.getStylesheets().add(PATH_TO_FONTS);
        return scene;
    }

    private void setSceneAtStage(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows overview inside the root layout.
     */
    private void showOverview() {
        try {
            FXMLLoader loader = getLoader(PATH_TO_OVERVIEW);
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
        rootLayout.setCenter(homeOverview);
    }

    private void getControllerAccess(FXMLLoader loader) {
        controller = loader.getController();
        controller.setMainApp(this);
    }

    /**
     * Returns the main stage.
     *
     * @return primaryStage
     */
    @SuppressWarnings("unused")
    private Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Calls the execution command in Logic
     *
     * @param userInput
     * @return true if userInput is successfully executed
     */
    public boolean callLogicCommand(String userInput) {
        return logic.executeCommand(userInput) == true;
    }

    public String callAutocomplete(String userInput) {
        String completedStr = logic.autocomplete(userInput);
        logger.fine(completedStr);
        return completedStr;
    }

    /**
     * Use the command history in Logic
     *
     * @param direction
     * @return the previous or next command depending on the direction
     */
    public String callCmdHistory(String direction) {
        assert(direction != null);

        String command = new String();
        if(direction.equals(GuiUtil.KEY_UP)) {
            command = logic.getPreviousCmd();
        } else {
            command = logic.getNextCmd();
        }

        assert(command != GuiUtil.EMPTY_STRING);
        return command;
    }

    public List<String> callSuggestions(String userInput) {
        return logic.getSuggestions(userInput, MAX_WORDS);
    }

    public void displayAlert(Task task) {
        assert(task != null);
        ReminderDialog reminder = new ReminderDialog(task);
        reminder.appear();
        return;
    }
    
    public void displayManual() {
        HelpManual helpManual = new HelpManual();
        double pos[] = getLocation();
        helpManual.setPosition(pos[0] + OFFSET_DISPLAY, pos[1]);
        helpManual.display();
    }
    
    /**
     * Called by Logic component to change the status information
     *
     * @param statusString
     */
    public void displayStatus(String statusString) {
        assert(statusString != null);
        logger.info(statusString);

        controller.displayStatus(statusString);
    }

    /**
     * Called by Logic component to display information
     *
     * @param Object that implements List<Task>
     */
    public void display(List<Task> receivedList) {
        assert(receivedList != null);
        logger.info(receivedList.toString());

        displayList = receivedList;
        processReceivedList(displayList);
        setDataToController();
    }

    private void processReceivedList(List<Task> displayList) {
        guiFormatter.setData(displayList);
        taskData = guiFormatter.getFormattedData();
        logger.fine(taskData.toString());
    }

    private void setDataToController() {
        if(controller != null) {
            controller.setData();
        }
    }

    /**
     * Returns the data as an ObservableList of Task
     *
     * @return taskData
     */
    public ObservableList<Task> getNewData() {
        return taskData;
    }
    
    private double[] getLocation() {
        double[] position = new double[2];
        position[0] = primaryStage.getX();
        position[1] = primaryStage.getY();
        return position;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
