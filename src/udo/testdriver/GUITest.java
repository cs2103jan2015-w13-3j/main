package udo.testdriver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import junit.framework.TestCase;
import udo.gui.GUI;
import udo.storage.Task;
import udo.storage.Task.TaskType;

/**
 * This Class only tests the order of tasks, displayed Time and the 
 * appropriate date headers after the list is formatted. It is unable 
 * to check for display properties like colours and styles
 * 
 *  @author Sharmine
 *  
 */

public class GUITest extends TestCase {
    
    public static final String EMPTY_STRING = "";
    public static final String HEADER_TODO = "To-Dos";

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy");
    public static SimpleDateFormat endDateFormat = 
            new SimpleDateFormat("dd/MM HH:mm");
    
    private static ArrayList<Task> inputList;
    private static ArrayList<Task> expectedArr; 
    private static ObservableList<Task> expectedList;
    private static GUI gui;
    
    private void initialise() {
        initVariables();
        removeExistingTasks();
    }

    private void initVariables() {
        gui = new GUI();
        inputList = new ArrayList<Task>();
        expectedArr = new ArrayList<Task>(); 
        expectedList = FXCollections.observableList(expectedArr);
    }
    
    //Code reuse from Thien
    private void removeExistingTasks() {
        try {
            (new RandomAccessFile("task.json", "rws")).setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Test Todo task and Todo header
    public void test1() throws Exception {
        
        initialise();
        
        Task todoHeader = new Task(null, HEADER_TODO, null,
                                   null, null, 0, null,
                                   EMPTY_STRING, false, false);
        
        Task task1 = new Task(TaskType.TODO, "Watch Harry Potter", null, 
                              null, null, 0, null, "label",
                              false, false);
        
        Task taskAfter1 = new Task(TaskType.TODO, "1.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);

        inputList.add(task1);
        expectedList.add(todoHeader);
        expectedList.add(taskAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());        
    }
    
    //Events to be placed before Todo
    public void test2() throws Exception {
        initialise();
        
        Task todoHeader = new Task(null, HEADER_TODO, null,
                                   null, null, 0, null,
                                   EMPTY_STRING, false, false);
        
        Task firstDayHeader = new Task(null, "Fri, 20 Mar 2015", null,
                                    null, null, 0, null,
                                    EMPTY_STRING, false, false);
        
        Task todo1 = new Task(TaskType.TODO, "Watch Harry Potter", null, 
                              null, null, 0, null, "label",
                              false, false);

        Task event2 = new Task(TaskType.EVENT, "meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 12, 0), 
                              new GregorianCalendar(2015, 02, 20, 13, 30),
                              0, null, EMPTY_STRING, false, false);
        
        Task event3 = new Task(TaskType.EVENT, "second meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 14, 0), 
                              new GregorianCalendar(2015, 02, 20, 16, 0),
                              0, null, EMPTY_STRING, false, false);
        
        Task todoAfter1 = new Task(TaskType.TODO, "3.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task eventAfter2 = new Task(TaskType.EVENT, "1.  meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 12, 0), 
                                   new GregorianCalendar(2015, 02, 20, 13, 30),
                                   0, null, EMPTY_STRING, false, false);
        
        Task eventAfter3 = new Task(TaskType.EVENT, "2.  second meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 14, 0), 
                                   new GregorianCalendar(2015, 02, 20, 16, 0),
                                   0, null, EMPTY_STRING, false, false);

        
        inputList.add(todo1);
        inputList.add(event2);
        inputList.add(event3);
        expectedList.add(firstDayHeader);
        expectedList.add(eventAfter2);
        expectedList.add(eventAfter3);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    //Separate headers for different dates
    public void test3() throws Exception {
        initialise();
        
        Task todoHeader = new Task(null, HEADER_TODO, null,
                                   null, null, 0, null,
                                   EMPTY_STRING, false, false);

        Task firstDayHeader = new Task(null, "Fri, 20 Mar 2015", null,
                                       null, null, 0, null,
                                       EMPTY_STRING, false, false);
        
        Task secondDayHeader = new Task(null, "Sat, 21 Mar 2015", null,
                                        null, null, 0, null,
                                        EMPTY_STRING, false, false);
        
        Task todo1 = new Task(TaskType.TODO, "Watch Harry Potter", null, 
                              null, null, 0, null, "label",
                              false, false);

        Task event2 = new Task(TaskType.EVENT, "meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 12, 0), 
                              new GregorianCalendar(2015, 02, 20, 13, 30),
                              0, null, EMPTY_STRING, false, false);
        
        Task event3 = new Task(TaskType.EVENT, "second meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 14, 0), 
                              new GregorianCalendar(2015, 02, 20, 16, 0),
                              0, null, EMPTY_STRING, false, false);
        
        Task event4 = new Task(TaskType.EVENT, "conference", null, 
                               new GregorianCalendar(2015, 02, 21, 11, 30), 
                               new GregorianCalendar(2015, 02, 21, 12, 0),
                               0, null, EMPTY_STRING, false, false);
        
        Task todoAfter1 = new Task(TaskType.TODO, "4.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task eventAfter2 = new Task(TaskType.EVENT, "1.  meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 12, 0), 
                                   new GregorianCalendar(2015, 02, 20, 13, 30),
                                   0, null, EMPTY_STRING, false, false);
        
        Task eventAfter3 = new Task(TaskType.EVENT, "2.  second meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 14, 0), 
                                   new GregorianCalendar(2015, 02, 20, 16, 0),
                                   0, null, EMPTY_STRING, false, false);
        
        Task eventAfter4 = new Task(TaskType.EVENT, "3.  conference", null, 
                                    new GregorianCalendar(2015, 02, 21, 11, 30), 
                                    new GregorianCalendar(2015, 02, 21, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        inputList.add(todo1);
        inputList.add(event2);
        inputList.add(event3);
        inputList.add(event4);
        expectedList.add(firstDayHeader);
        expectedList.add(eventAfter2);
        expectedList.add(eventAfter3);
        expectedList.add(secondDayHeader);
        expectedList.add(eventAfter4);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    public void test4() throws Exception {
        initialise();
        
        Task todoHeader = new Task(null, HEADER_TODO, null,
                                   null, null, 0, null,
                                   EMPTY_STRING, false, false);

        Task firstDayHeader = new Task(null, "Fri, 20 Mar 2015", null,
                                       null, null, 0, null,
                                       EMPTY_STRING, false, false);
        
        Task secondDayHeader = new Task(null, "Sat, 21 Mar 2015", null,
                                        null, null, 0, null,
                                        EMPTY_STRING, false, false);
        
        Task todo1 = new Task(TaskType.TODO, "Watch Harry Potter", null, 
                              null, null, 0, null, "label",
                              false, false);

        Task event2 = new Task(TaskType.EVENT, "meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 12, 0), 
                              new GregorianCalendar(2015, 02, 20, 13, 30),
                              0, null, EMPTY_STRING, false, false);
        
        Task event3 = new Task(TaskType.EVENT, "second meeting", null, 
                              new GregorianCalendar(2015, 02, 20, 14, 0), 
                              new GregorianCalendar(2015, 02, 20, 16, 0),
                              0, null, EMPTY_STRING, false, false);
        
        Task event4 = new Task(TaskType.EVENT, "conference", null, 
                               new GregorianCalendar(2015, 02, 21, 11, 30), 
                               new GregorianCalendar(2015, 02, 21, 12, 0),
                               0, null, EMPTY_STRING, false, false);
        
        Task deadline5 = new Task(TaskType.DEADLINE, "hand in work",  
                                  new GregorianCalendar(2015, 02, 20, 17, 0),
                                  null, null, 0, null, EMPTY_STRING, false, 
                                  false);
        
        Task todoAfter1 = new Task(TaskType.TODO, "5.  Watch Harry Potter",
                                   null, null, null, 0, null, EMPTY_STRING, 
                                   false, false);
     
        Task eventAfter2 = new Task(TaskType.EVENT, "2.  meeting", null, 
                                    new GregorianCalendar(2015, 02, 20, 12, 0), 
                                    new GregorianCalendar(2015, 02, 20, 13, 30),
                                    0, null, EMPTY_STRING, false, false);
        
        Task eventAfter3 = new Task(TaskType.EVENT, "3.  second meeting", null, 
                                    new GregorianCalendar(2015, 02, 20, 14, 0), 
                                    new GregorianCalendar(2015, 02, 20, 16, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        Task eventAfter4 = new Task(TaskType.EVENT, "4.  conference", null, 
                                    new GregorianCalendar(2015, 02, 21, 11, 30), 
                                    new GregorianCalendar(2015, 02, 21, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        Task deadlineAfter5 = new Task(TaskType.DEADLINE, "1.  hand in work",  
                                       new GregorianCalendar(2015, 02, 20, 17, 0),
                                       null, null, 0, null, EMPTY_STRING, false, 
                                       false);
        
        inputList.add(todo1);
        inputList.add(event2);
        inputList.add(event3);
        inputList.add(event4);
        inputList.add(deadline5);
        expectedList.add(firstDayHeader);
        expectedList.add(deadlineAfter5);
        expectedList.add(eventAfter2);
        expectedList.add(eventAfter3);
        expectedList.add(secondDayHeader);
        expectedList.add(eventAfter4);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    } 
}
