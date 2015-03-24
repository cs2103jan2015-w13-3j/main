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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

public class GUI extends Application {
    private static final Logger logger = Logger.getLogger(GUI.class.getName());
    
    private static final String NAME_APP = "JustU";
    private static final String PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String PATH_TO_FONTS =
            "http://fonts.googleapis.com/css?family=Open+Sans:" +
            "300italic,400italic,600italic,700,600,400";
    
    private static List<Task> displayList;
    private static HomeController controller;
    private static ObservableList<Task> taskData;
    private static GUIFormatter guiFormatter;
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private Logic logic;
    
    /**
     * Constructor
     */
    public GUI() {
        logic = Logic.getInstance();
        logic.setGUI(this);
        guiFormatter = GUIFormatter.getInstance();
        logger.setLevel(Level.INFO);
    }

    @Override
    public void start(Stage primaryStage) {
        setPrimaryStage(primaryStage);
        setSecondaryStage();       
        logger.fine("GUI Initiation Completed");
        
        callLogicCommand(Config.CMD_STR_DISPLAY);
    }

    private void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
            URL url = GUI.class.getResource(PATH_TO_ROOTLAYOUT);
            FXMLLoader loader = getLoader(url);
            Scene rootScene = setRootScene(loader);         
            showSceneAtStage(rootScene);
            logger.finer("Root Layout Initiated");
            
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    private Scene setRootScene(FXMLLoader loader) throws IOException {
        rootLayout = (BorderPane) loader.load();    
        Scene scene = castPaneToScene(rootLayout);
        return scene;
    }
    
    private Scene castPaneToScene(BorderPane pane) {
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(PATH_TO_FONTS);
        return scene;
    }

    private void showSceneAtStage(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows overview inside the root layout.
     */
    private void showOverview() {
        try {
            URL url = GUI.class.getResource(PATH_TO_OVERVIEW);
            FXMLLoader loader = getLoader(url);
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

    private FXMLLoader getLoader(URL url) {
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
    
    public boolean callAutocomplete(String userInput) {
        String str = logic.autocomplete(userInput);
        logger.info(str);
        return false;
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
        logger.info(taskData.toString());
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

    public static void main(String[] args) {
        launch(args);
    }

}
