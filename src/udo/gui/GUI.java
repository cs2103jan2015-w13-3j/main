package udo.gui;

import udo.storage.Task;
import udo.util.Utility;
import udo.gui.view.HomeController;
import udo.logic.Logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

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
    private HomeController controller;

    private static final String NAME_APP = "JustU";
    private static ObservableList<Task> taskData;
    private static ArrayList<Task> currList = new ArrayList<Task>();
    private static ArrayList<Task> originalList = new ArrayList<Task>();
    private static ArrayList<Task> testList = new ArrayList<Task>();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy");

    /**
     * Constructor
     */
    public GUI() {

        logic = new Logic(this);

        // For testing purposes
        populateTest();
        display(testList);

    }

    private void populateTest() {
        testList.add(new Task(Task.TaskType.EVENT, "drink coffee",
                new GregorianCalendar(), new GregorianCalendar(2015, 1, 1), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "drink more coffee",
                new GregorianCalendar(), new GregorianCalendar(2015, 2, 2), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "say hello",
                new GregorianCalendar(), new GregorianCalendar(2015, 2, 2), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "say css is dumb",
                new GregorianCalendar(), new GregorianCalendar(2015, 2, 2), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "code more",
                new GregorianCalendar(), new GregorianCalendar(2015, 3, 3), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "code more",
                new GregorianCalendar(), new GregorianCalendar(2015, 4, 3), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "drink coffee",
                new GregorianCalendar(), new GregorianCalendar(2015, 1, 1), 0,
                new GregorianCalendar(), "time", true));
        testList.add(new Task(Task.TaskType.EVENT, "drink more coffee",
                new GregorianCalendar(), new GregorianCalendar(2015, 2, 2), 0,
                new GregorianCalendar(), "time", true));
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
            scene.getStylesheets().add(
                    "http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700,600,400");
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

    /**
     * Called by logic component to display information
     * 
     * @param rcvdList
     */
    public void display(ArrayList<Task> rcvdList) {
        copyBackupList(rcvdList);
        formatElementNumberings();
        insertDateHeaderLoop();
        convertToObservable(currList);
    }

    private void copyBackupList(ArrayList<Task> rcvdList) {
        originalList = rcvdList;
        currList.addAll(originalList);
        // sort curr list
        // hash currentlist index against originallist index
    }

    private void formatElementNumberings() {

        for (int i = 0; i < currList.size(); i++) {
            int counter = i + 1;
            Task task = currList.get(i);
            task.setContent("" + counter + ".  " + task.getContent());
        }
    }

    private void insertDateHeaderLoop() {

        String prevDayMonthYear = "";

        for (int i = 0; i < currList.size(); i++) {

            Task task = currList.get(i);
            String dayMonthYear = formatDateGUI(task.getEnd());
            // String date = Utility.calendarToString(task.getEnd());

            if (!dayMonthYear.equals(prevDayMonthYear)
                    || prevDayMonthYear.isEmpty()) {
                insertDateHeader(currList, dayMonthYear, i);
                i++;
            }

            prevDayMonthYear = dayMonthYear;
        }

    }

    private String formatDateGUI(GregorianCalendar calendar) {
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Add Day-Month-Year Header
     */
    private void insertDateHeader(ArrayList<Task> currList, String date, int i) {
        Task dateHeader = new Task(null, date, new GregorianCalendar(),
                new GregorianCalendar(), 0, new GregorianCalendar(), "", true);
        currList.add(i, dateHeader);
    }

    /*
     * private String extractDate(GregorianCalendar date) { int year =
     * date.get(Calendar.YEAR); int month = date.get(Calendar.MONTH); int day =
     * date.get(Calendar.DAY_OF_MONTH); return "" + year + " " + month + " " +
     * day; }
     */

    /**
     * Maps Arraylist of objects to an ObservableArrayList
     * 
     * @param Arraylist
     *            <Task>
     */
    private static void convertToObservable(ArrayList<Task> testingInputTaskList) {
        taskData = FXCollections.observableArrayList(testingInputTaskList);
    }

    public void displayStatus(String statusString) {
        System.out.println("In GUI: " + statusString);
        controller.displayStatus(statusString);
    }

    /**
     * Returns the data as an observable list of Task
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
