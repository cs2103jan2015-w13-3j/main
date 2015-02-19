package udo.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import udo.storage.Tasks;
import udo.gui.view.HomeController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUI extends Application{
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private static ObservableList<Tasks> taskData;
    private static HomeController controller;
    
    /**
     * Constructor
     */
    public GUI() {
        
        //For testing purposes
        ArrayList<Tasks> testList = new ArrayList<Tasks>();
        testList.add(new Tasks("event", "drink coffee", new GregorianCalendar(), new GregorianCalendar(), 
                  new GregorianCalendar(), "label", true));
        testList.add(new Tasks("event", "drink more coffee", new GregorianCalendar(), new GregorianCalendar(), 
                new GregorianCalendar(), "label", true));
        testList.add(new Tasks("event", "code more", new GregorianCalendar(), new GregorianCalendar(), 
                new GregorianCalendar(), "label", true));
        
        display(testList);
        
    }

    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("JustU");

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
     * @param Arraylist<Tasks>
     */
    public static void display(ArrayList<Tasks> testingInputTaskList) { 
        taskData = FXCollections.observableArrayList(testingInputTaskList);
    }
    
    public static void displayStatus(String newStatus) {
        HomeController.displayStatus(newStatus);
    }
    
    /**
     * Returns the data as an observable list of Task 
     * @return
     */
    public ObservableList<Tasks> getTaskData() {
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
