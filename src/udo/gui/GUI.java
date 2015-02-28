package udo.gui;

import udo.storage.Task;
import udo.gui.view.HomeController;
import udo.logic.Logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    private static ArrayList<Task> currList = new ArrayList<Task>();
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
        populateTest();
        formatElement();
        insertDateHeaderLoop();
        convertToObservable(currList);
        
    }
    
    private void populateTest () {
        currList.add(new Task(Task.TaskType.EVENT, "drink coffee", new GregorianCalendar(), new GregorianCalendar(2015,1,1), 
                0, new GregorianCalendar(), "label", true));
        currList.add(new Task(Task.TaskType.EVENT, "drink more coffee", new GregorianCalendar(), new GregorianCalendar(2015,2,2), 
              0, new GregorianCalendar(), "label", true));
        currList.add(new Task(Task.TaskType.EVENT, "say hello", new GregorianCalendar(), new GregorianCalendar(2015,2,2), 
                0, new GregorianCalendar(), "label", true));
        currList.add(new Task(Task.TaskType.EVENT, "say hello to more people", new GregorianCalendar(), new GregorianCalendar(2015,2,2), 
                0, new GregorianCalendar(), "label", true));
        currList.add(new Task(Task.TaskType.EVENT, "code more", new GregorianCalendar(), new GregorianCalendar(2015,3,3), 
              0, new GregorianCalendar(), "label", true));
    }
    
    private void formatElement() {
        
        for(int i = 0; i < currList.size(); i++) {
            int counter = i + 1;
            Task task = currList.get(i);
            task.setContent( "" + counter + ".  " + task.getContent());
        }
    }
    
    private void insertDateHeaderLoop() {
        
        String prevDayMonthYear = "";
        
        for(int i = 0; i < currList.size(); i++) {
            
            Task task = currList.get(i);
            GregorianCalendar date = task.getEnd();
            String dayMonthYear = extractDate(date);         
            
            if (!dayMonthYear.equals(prevDayMonthYear) || prevDayMonthYear.isEmpty()) {
                insertDateHeader(dayMonthYear, i);
                i++;
            }
            
            prevDayMonthYear = dayMonthYear;
        }
       
    }
    
    /**
     * Add Day-Month-Year Header in the list
     */
    private void insertDateHeader(String date, int i) {
        Task dateHeader = new Task(null, date, new GregorianCalendar(), new GregorianCalendar(), 
                0, new GregorianCalendar(), "", true);
        currList.add(i, dateHeader);        
    }

    private String extractDate(GregorianCalendar date) {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        return "" + year + " " + month + " " + day;
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
     * Maps Arraylist of objects to an ObservableArrayList
     * @param Arraylist<Task>
     */
    public static void convertToObservable(ArrayList<Task> testingInputTaskList) { 
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
