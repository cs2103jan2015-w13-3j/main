package udo.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import udo.storage.Task;
import udo.gui.view.HomeController;
import udo.logic.Logic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUI extends Application{
    
    private static final String NAME_APP = "JustU";
    private static ObservableList<Task> taskData;
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private Logic logic;
    private HomeController controller;
    
    /**
     * Constructor
     */
    public GUI() {
        
        logic = new Logic(this);
        
        //For testing purposes
        ArrayList<Task> testList = new ArrayList<Task>();
        testList.add(new Task("event", "drink coffee", new GregorianCalendar(), new GregorianCalendar(), 
                  0, new GregorianCalendar(), "label", true));
        testList.add(new Task("event", "drink more coffee", new GregorianCalendar(), new GregorianCalendar(), 
                0, new GregorianCalendar(), "label", true));
        testList.add(new Task("event", "code more", new GregorianCalendar(), new GregorianCalendar(), 
                0, new GregorianCalendar(), "label", true));
        
        displayContent(testList);
        
    }

    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(NAME_APP);

        initRootLayout();

        showOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GUI.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows overview inside the root layout.
     */
    public void showOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GUI.class.getResource("view/Home.fxml"));
            AnchorPane homeOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(homeOverview);
            
            // Give the controller access to the main application.
            controller = loader.getController();
            controller.setMainApp(this);
         
                       
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param Arraylist<Task>
     */
    public static void displayContent(ArrayList<Task> testingInputTaskList) { 
        taskData = FXCollections.observableArrayList(testingInputTaskList);
    }
    
    public void passUserInput(String input) {
        logic.executeCommand(input);
    }
    
    public void displayStatus(String statusString) {
        System.out.println(statusString);
        controller.displayStatus(statusString);
    }
    
    /**
     * Returns the data as an observable list of Task 
     * @return taskData
     */
    public ObservableList<Task> getTaskData() {
        return taskData;
    }
       
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
