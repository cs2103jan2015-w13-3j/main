package udo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

public class GUI extends Application {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
    
    private static final String NAME_APP = "JustU";
    private static final String PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String PATH_TO_FONTS =
            "http://fonts.googleapis.com/css?family=Open+Sans:" +
            "300italic,400italic,600italic,700,600,400";
    
    private static ArrayList<Task> displayList = new ArrayList<Task>();
    private static HomeController controller;
    private static ObservableList<Task> taskData;
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private Logic logic;
    
    /**
     * Constructor
     */
    public GUI() {
        logic = Logic.getInstance();
        logic.setGUI(this);
        LOGGER.setLevel(Level.ALL);
    }

    @Override
    public void start(Stage primaryStage) {
        setPrimaryStage(primaryStage);
        showRootLayout();
        showOverview();       
        LOGGER.fine("GUI Initiation Completed");
        
        callLogicCommand(Config.CMD_STR_DISPLAY);
    }

    private void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(NAME_APP);
    }

    /**
     * Initializes the root layout.
     */
    private void showRootLayout() {
        try {
            FXMLLoader loader = getLoader(GUI.class.getResource(PATH_TO_ROOTLAYOUT));
            rootLayout = (BorderPane) loader.load();

            Scene scene = setRootScene(rootLayout);
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
            LOGGER.finer("Root Layout Initiated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene setRootScene(BorderPane pane) {
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(PATH_TO_FONTS);
        return scene;
    }

    /**
     * Shows overview inside the root layout.
     */
    private void showOverview() {
        try {
            URL url = GUI.class.getResource(PATH_TO_OVERVIEW);
            FXMLLoader loader = getLoader(url);
            AnchorPane homeOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(homeOverview);

            // Get Controller Access 
            controller = loader.getController();
            controller.setMainApp(this);
            
            LOGGER.finer("Overview Scene Initiated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader getLoader(URL url) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        return loader;
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
        LOGGER.fine("Calls Logic Command");
        return logic.executeCommand(userInput) == true; 
    }
    
    public boolean callAutocomplete(String userInput) {
        LOGGER.fine("Calls Logic Autocomplete");
        String str = logic.autocomplete(userInput);
        System.out.println(str);
        return false;
    }
    
    /**
     * Called by Logic component to change the status information
     * 
     * @param statusString
     */
    public void displayStatus(String statusString) {
        assert(statusString != null);        
        LOGGER.info("Status string = " + statusString);
        
        controller.displayStatus(statusString);
    }

    /**
     * Called by Logic component to display information
     * 
     * @param Object that implements List<Task>
     */
    public void display(List<Task> receivedList) {
        assert(receivedList != null);
        LOGGER.info("Data received " + receivedList);
        
        displayList = (ArrayList<Task>) receivedList;
        processReceivedList(displayList);        
        setDataToController();
    }

    private void processReceivedList(List<Task> receivedList) {
        GUIFormatter.formatDisplayList(displayList);
        convertToObservable(displayList);
        LOGGER.info("In GUI: Data to be displayed " + taskData);
    }

    private void setDataToController() {
        if(controller != null) {
            controller.setData();
        }
    }
    
    /**
     * Associate an ArrayList of objects with an ObservableArrayList
     * 
     * @param Arraylist<Task>
     */
    private static void convertToObservable(ArrayList<Task> displayList) {
        taskData = FXCollections.observableArrayList(displayList);  
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
