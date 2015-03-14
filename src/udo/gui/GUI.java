package udo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import udo.util.Utility;

public class GUI extends Application {

    private static final String NAME_APP = "JustU";
    private static final String PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String PATH_TO_FONTS = "http://fonts.googleapis.com/css?family=Open+Sans:" +
                                                "300italic,400italic,600italic,700,600,400";
    
    private static List<Task> originalList;
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
        // For unit testing purposes      
        //display();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(NAME_APP);

        showRootLayout();

        showOverview();
        
    }

    /**
     * Initializes the root layout.
     */
    private void showRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = getLoader(GUI.class.getResource(PATH_TO_ROOTLAYOUT));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(PATH_TO_FONTS);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows overview inside the root layout.
     */
    private void showOverview() {
        try {
            // Load person overview. 
            FXMLLoader loader = getLoader(GUI.class.getResource(PATH_TO_OVERVIEW));
            AnchorPane homeOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(homeOverview);

            // Give the controller access to the main application.
            controller = loader.getController();
            controller.setMainApp(this);
            callLogicCommand(Config.CMD_STR_DISPLAY);
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
    private Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Calls the execution command 
     *     
     * @param userInput
     * @return true if userInput is successfully executed
     */
    public boolean callLogicCommand(String userInput) {
        return logic.executeCommand(userInput) == true ; 
    }
    
    /**
     * Called by logic component to change the status information
     * 
     * @param statusString
     */
    public void displayStatus(String statusString) {
        assert(statusString != null);
        
        System.out.println("In GUI: statusString = " + statusString);
        controller.displayStatus(statusString);
    }

    /**
     * Called by logic component to display information
     * 
     * @param Object that implements List<Task>
     */
    public void display(List<Task> rcvdList) {
        assert(rcvdList != null);
        
        System.out.println("In GUI: received list is " + rcvdList);
        duplicateList(rcvdList);
        GUIFormatter.formatDisplayList(displayList);
        convertToObservable(displayList);
        controller.setData();
    }

    private void duplicateList(List<Task> rcvdList) {
        originalList = rcvdList;
        displayList = Utility.deepCopy(originalList);
    }
    
    /**
     * Maps Arraylist of objects to an ObservableArrayList
     * 
     * @param Arraylist<Task>
     */
    private static void convertToObservable(ArrayList<Task> displayList) {
        taskData = FXCollections.observableArrayList(displayList);  
        System.out.println("taskData " + taskData);
    }

    /**
     * Returns the data as an Observable list of Task
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
