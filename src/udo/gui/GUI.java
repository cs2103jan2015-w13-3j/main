package udo.gui;

import udo.storage.Task;
import udo.gui.view.HomeController;
import udo.logic.Logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUI extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Logic logic;
    private static HomeController controller;

    private static final String NAME_APP = "JustU";
    private static final String PATH_TO_ROOTLAYOUT = "view/RootLayout.fxml";
    private static final String PATH_TO_OVERVIEW = "view/Home.fxml";
    private static final String PATH_TO_FONTS = "http://fonts.googleapis.com/css?family=Open+Sans:" +
                                                "300italic,400italic,600italic,700,600,400";

    private static ObservableList<Task> taskData;
    private static List<Task> originalList;
    private static ArrayList<Task> displayList = new ArrayList<Task>();
    private static ArrayList<Task> testList = new ArrayList<Task>();
    
   
    /**
     * Constructor
     */
    public GUI() {

        logic = new Logic(this);

        // For unit testing purposes
        populateTest();
        display(testList);

    }

    private void populateTest() {
        testList.add(new Task(Task.TaskType.EVENT, "event: drink coffee in jan",
                new GregorianCalendar(2015, 1, 1, 1, 1), new GregorianCalendar(2015, 1, 1, 10, 10), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: drink more coffee in feb",
                new GregorianCalendar(2015, 2, 2, 2, 2), new GregorianCalendar(2015, 2, 2, 10, 10), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.TODO, "todo: say hello",
                new GregorianCalendar(), new GregorianCalendar(), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: look at the moon in feb",
                new GregorianCalendar(2015, 2, 2, 7, 1), new GregorianCalendar(2015, 2, 2), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: code in march",
                new GregorianCalendar(2015, 3, 3, 3, 3), new GregorianCalendar(2015, 3, 3, 10, 10), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.TODO, "todo: say second hello",
                new GregorianCalendar(), new GregorianCalendar(), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: should code more in jan",
                new GregorianCalendar(2015, 1, 1, 2, 1), new GregorianCalendar(2015, 1, 1, 10, 10), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: can't think in feb",
                new GregorianCalendar(2015, 2, 25, 1, 1), new GregorianCalendar(2015, 2, 25, 10, 10), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.TODO, "todo say second hello",
                new GregorianCalendar(), new GregorianCalendar(), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "event: shoot for the moon in jan",
                new GregorianCalendar(2015, 1, 1, 1, 1), new GregorianCalendar(2015, 1, 1, 9, 9), 0,
                new GregorianCalendar(), "time", true));
        //System.out.println("in test list" + testList);
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
            loader.setLocation(GUI.class.getResource(PATH_TO_ROOTLAYOUT));
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
    public void showOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GUI.class.getResource(PATH_TO_OVERVIEW));
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
     * Returns the main stage.
     * 
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void passUserInput(String input) {
        logic.executeCommand(input);
    }

    public void displayStatus(String statusString) {
        if (statusString == null) {
            statusString = "Null value received"; //testing
        }
        System.out.println("In GUI: statusString = " + statusString);
        controller.displayStatus(statusString);
    }

    /**
     * Called by logic component to display information
     * 
     * @param Object that implements List<Task>
     */
    public void display(List<Task> rcvdList) {
        if(rcvdList == null) {
            System.out.println("In GUI: rcvdList is Null");
        }
        
        duplicateList(rcvdList);
        GUIFormatter.formatDisplayList(displayList);
        convertToObservable(displayList);
    }

    private void duplicateList(List<Task> rcvdList) {
        originalList = rcvdList;
        displayList.addAll(originalList);
        // TODO: sort curr list
        // hash currentlist index against originallist index
    }
    
    /**
     * Maps Arraylist of objects to an ObservableArrayList
     * 
     * @param Arraylist<Task>
     */
    private static void convertToObservable(ArrayList<Task> testingInputTaskList) {
        taskData = FXCollections.observableArrayList(testingInputTaskList);
    }

    /**
     * Returns the data as an Observable list of Task
     * 
     * @return taskData
     */
    public ObservableList<Task> getTaskData() {
        return taskData;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
