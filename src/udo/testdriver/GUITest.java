package udo.testdriver;

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
    
    private static Task todoHeader;
    private static Task todo1;
    private static Task currDayHeader;
    private static Task currDayEvent12pm;
    private static Task currDayEvent2pm;
    private static Task currDayDeadline5pm;
    private static Task nextDayHeader;
    private static Task nextDayEvent11pm;
    private static Task prevDayHeader;
    private static Task prevDayEvent11pm;

    private void initialise() {
        initVariables();
        initTestTasks();
        removeExistingTasks();
    }

    private void initVariables() {
        gui = new GUI();
        inputList = new ArrayList<Task>();
        expectedArr = new ArrayList<Task>(); 
        expectedList = FXCollections.observableList(expectedArr);
    }
    
    private void initTestTasks() {
        todoHeader = new Task(null, HEADER_TODO, null,
                              null, null, 0, null,
                              EMPTY_STRING, false, false);
        
        todo1 = new Task(TaskType.TODO, "Watch Harry Potter",
                         null, null, null, 0, null, "label", 
                         false, false);

        currDayHeader = new Task(null, "Fri, 20 Mar 2015", null,
                                 null, null, 0, null,
                                 EMPTY_STRING, false, false);
        
        currDayEvent12pm = new Task(TaskType.EVENT, "meeting", null, 
                                    new GregorianCalendar(2015, 02, 20, 12, 0), 
                                    new GregorianCalendar(2015, 02, 20, 13, 30),
                                    0, null, EMPTY_STRING, false, false);
        
        currDayEvent2pm = new Task(TaskType.EVENT, "second meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 14, 0), 
                                   new GregorianCalendar(2015, 02, 20, 16, 0),
                                   0, null, EMPTY_STRING, false, false);
        
        currDayDeadline5pm = new Task(TaskType.DEADLINE, "hand in work",  
                                      new GregorianCalendar(2015, 02, 20, 17, 0),
                                      null, null, 0, null, EMPTY_STRING, false, 
                                      false);
        
        nextDayHeader = new Task(null, "Sat, 21 Mar 2015", null,
                                 null, null, 0, null,
                                 EMPTY_STRING, false, false);
        
        nextDayEvent11pm = new Task(TaskType.EVENT, "conference", null, 
                                    new GregorianCalendar(2015, 02, 21, 11, 30), 
                                    new GregorianCalendar(2015, 02, 21, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        prevDayHeader = new Task(null, "Thu, 19 Mar 2015", null,
                                 null, null, 0, null,
                                 EMPTY_STRING, false, false);
        
        prevDayEvent11pm = new Task(TaskType.EVENT, "old macs", null, 
                                    new GregorianCalendar(2015, 02, 19, 11, 30), 
                                    new GregorianCalendar(2015, 02, 19, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
    }
    
    /**
     * Code reuse from Thien
     */
    private void removeExistingTasks() {
        try {
            (new RandomAccessFile("task.json", "rws")).setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Insertion of new headers: Todo header
    public void test1() throws Exception {
        
        initialise();

        Task todoAfter1 = new Task(TaskType.TODO, "1.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);

        inputList.add(todo1);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());        
    }
    
    //Later-added events to be placed before Todo
    public void test2() throws Exception {
        initialise();
        
        Task todoAfter = new Task(TaskType.TODO, "2.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task currDayEvent12pmAfter = 
                new Task(TaskType.EVENT, "1.  meeting",
                         null, new GregorianCalendar(2015, 02, 20, 12, 0),
                         new GregorianCalendar(2015, 02, 20, 13, 30), 0, null,
                         EMPTY_STRING, false, false);

        inputList.add(todo1);
        inputList.add(currDayEvent12pm);
        
        expectedList.add(currDayHeader);
        expectedList.add(currDayEvent12pmAfter);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    //Multiple events to be sorted by time
    public void test3() throws Exception {
        
        initialise();
        
        Task todoAfter = new Task(TaskType.TODO, "3.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task currDayEvent12pmAfter = 
                new Task(TaskType.EVENT, "1.  meeting",
                         null, new GregorianCalendar(2015, 02, 20, 12, 0),
                         new GregorianCalendar(2015, 02, 20, 13, 30), 0, null,
                         EMPTY_STRING, false, false);

        Task currDayEvent2pmAfter = 
                new Task(TaskType.EVENT, "2.  second meeting", null, 
                         new GregorianCalendar(2015, 02, 20, 14, 0), 
                         new GregorianCalendar(2015, 02, 20, 16, 0), 0,
                         null, EMPTY_STRING, false, false);

        
        inputList.add(todo1);
        inputList.add(currDayEvent2pm);
        inputList.add(currDayEvent12pm);
        
        expectedList.add(currDayHeader);
        expectedList.add(currDayEvent12pmAfter);
        expectedList.add(currDayEvent2pmAfter);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    /*Insertion of an event with a later date 
    separate headers for different dates*/
    public void test4() throws Exception {
        initialise();
                        
        Task todoAfter1 = new Task(TaskType.TODO, "4.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task currDayEvent12pmAfter = new Task(TaskType.EVENT, "1.  meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 12, 0), 
                                   new GregorianCalendar(2015, 02, 20, 13, 30),
                                   0, null, EMPTY_STRING, false, false);
        
        Task currDayEvent2pmAfter = new Task(TaskType.EVENT, "2.  second meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 14, 0), 
                                   new GregorianCalendar(2015, 02, 20, 16, 0),
                                   0, null, EMPTY_STRING, false, false);
        
        Task nextDayEvent11pmAfter = new Task(TaskType.EVENT, "3.  conference", null, 
                                    new GregorianCalendar(2015, 02, 21, 11, 30), 
                                    new GregorianCalendar(2015, 02, 21, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        inputList.add(todo1);
        inputList.add(currDayEvent12pm);
        inputList.add(currDayEvent2pm);
        inputList.add(nextDayEvent11pm);
        
        expectedList.add(currDayHeader);
        expectedList.add(currDayEvent12pmAfter);
        expectedList.add(currDayEvent2pmAfter);
        expectedList.add(nextDayHeader);
        expectedList.add(nextDayEvent11pmAfter);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    /*Insertion of an event with an earlier date 
    separate headers to be inserted for different dates*/
    public void test5() throws Exception {
        initialise();

        Task todoAfter1 = new Task(TaskType.TODO, "4.  Watch Harry Potter", null, 
                                   null, null, 0, null, EMPTY_STRING,
                                   false, false);
     
        Task currDayEvent12pmAfter = new Task(TaskType.EVENT, "2.  meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 12, 0), 
                                   new GregorianCalendar(2015, 02, 20, 13, 30),
                                   0, null, EMPTY_STRING, false, false);
        
        Task currDayEvent2pmAfter = new Task(TaskType.EVENT, "3.  second meeting", null, 
                                   new GregorianCalendar(2015, 02, 20, 14, 0), 
                                   new GregorianCalendar(2015, 02, 20, 16, 0),
                                   0, null, EMPTY_STRING, false, false);
        
        Task prevDayEvent11pmAfter = new Task(TaskType.EVENT, "1.  old macs", null, 
                                    new GregorianCalendar(2015, 02, 19, 11, 30), 
                                    new GregorianCalendar(2015, 02, 19, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        inputList.add(todo1);
        inputList.add(currDayEvent12pm);
        inputList.add(currDayEvent2pm);
        inputList.add(prevDayEvent11pm);
        
        expectedList.add(prevDayHeader);
        expectedList.add(prevDayEvent11pmAfter);
        expectedList.add(currDayHeader);
        expectedList.add(currDayEvent12pmAfter);
        expectedList.add(currDayEvent2pmAfter);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    }
    
    //Insertion of deadline on the same day with a later time
    public void test6() throws Exception {
        initialise();
        
        Task todoAfter1 = new Task(TaskType.TODO, "5.  Watch Harry Potter",
                                   null, null, null, 0, null, EMPTY_STRING, 
                                   false, false);
     
        Task currDayEvent12pmAfter = new Task(TaskType.EVENT, "2.  meeting", null, 
                                    new GregorianCalendar(2015, 02, 20, 12, 0), 
                                    new GregorianCalendar(2015, 02, 20, 13, 30),
                                    0, null, EMPTY_STRING, false, false);
        
        Task currDayEvent2pmAfter = new Task(TaskType.EVENT, "3.  second meeting", null, 
                                    new GregorianCalendar(2015, 02, 20, 14, 0), 
                                    new GregorianCalendar(2015, 02, 20, 16, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        Task nextDayEvent11pmAfter = new Task(TaskType.EVENT, "4.  conference", null, 
                                    new GregorianCalendar(2015, 02, 21, 11, 30), 
                                    new GregorianCalendar(2015, 02, 21, 12, 0),
                                    0, null, EMPTY_STRING, false, false);
        
        Task currDayDeadline5pmAfter = new Task(TaskType.DEADLINE, "1.  hand in work",  
                                       new GregorianCalendar(2015, 02, 20, 17, 0),
                                       null, null, 0, null, EMPTY_STRING, false, 
                                       false);
        
        inputList.add(todo1);
        inputList.add(currDayEvent12pm);
        inputList.add(currDayEvent2pm);
        inputList.add(nextDayEvent11pm);
        inputList.add(currDayDeadline5pm);
        
        expectedList.add(currDayHeader);
        expectedList.add(currDayDeadline5pmAfter);
        expectedList.add(currDayEvent12pmAfter);
        expectedList.add(currDayEvent2pmAfter);
        expectedList.add(nextDayHeader);
        expectedList.add(nextDayEvent11pmAfter);
        expectedList.add(todoHeader);
        expectedList.add(todoAfter1);
      
        gui.display(inputList);

        assertEquals(expectedList.toString(), gui.getNewData().toString());
    } 
}
