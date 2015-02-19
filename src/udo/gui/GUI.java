package udo.gui;

import java.io.IOException;
import java.util.ArrayList;

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
    
    /**
     * Constructor
     */
    public GUI() {
        
        /*For testing purposes
        ArrayList<Tasks> testList = new ArrayList<Tasks>();
        display(testList);
         */
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

            // Set person overview into the center of root layout.
            rootLayout.setCenter(homeOverview);
            
            // Give the controller access to the main app.
            HomeController controller = loader.getController();
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
