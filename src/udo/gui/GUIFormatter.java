package udo.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import udo.storage.Task;
import udo.util.Utility;

public class GUIFormatter {
    
    public static final String EMPTY_STRING = "";
    public static final String HEADER_TODO = "To-Dos";
    public static final Color COLOR_TABLE_HEADERS = Color.rgb(26, 188, 156);
    
    public static SimpleDateFormat dateFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
   
    public static String STYLE_ITALIC = "italic";
    public static String STYLE_STRAIGHT = "straight";
    
    public static void formatDisplayList(ArrayList<Task> displayList) {
        if (displayList == null) {
            return;
        }

        Collections.sort(displayList);
        formatIndex(displayList);
        formatDateLoop(displayList);
        //System.out.println("Formatted List = " + displayList);
    }
    
    private static void formatIndex(ArrayList<Task> displayList) {
        Utility.indexMap.clear();
        formatIndexLoop(displayList);
    }
    
    /**
     * Maps the displayIndex to the Task's actual index
     * @param displayList
     */
    private static void formatIndexLoop(ArrayList<Task> displayList) {
        
        for (int i = 0; i < displayList.size(); i++) {
            int displayIndex = i + 1;
            Task task = displayList.get(i);
            mapIndex(displayIndex, task.getIndex());
            appendSerialNumber(task, displayIndex);
        }
    }
    
    private static void mapIndex(Integer displayIndex, Integer actualIndex) {
        Utility.indexMap.put(displayIndex, actualIndex);
    }
    
    private static void appendSerialNumber(Task task, int counter) {    
        task.setContent("" + counter + ".  " + task.getContent());
    }

    /**
     * Formats list into a GUI display format, inserts date headers and time
     */
    private static void formatDateLoop(ArrayList<Task> displayList) {

        String prevHeader = EMPTY_STRING;

        for (int i = 0; i < displayList.size(); i++) {
            String header = new String();
            Task task = displayList.get(i);
            formatDisplayTime(task);
            
            header = getHeader(header, task);       
            i = insertIfNewHeader(displayList, prevHeader, i, header);
            prevHeader = header;
        }

    }

    private static String getHeader(String header, Task task) {
        switch (task.getTaskType()) {
            case TODO :
                return HEADER_TODO;              
            case EVENT :
                return getDateGUI(task.getStart());
            case DEADLINE :
                return getDateGUI(task.getDeadline());
            default :
                return EMPTY_STRING;
        }
    }

    private static int insertIfNewHeader(ArrayList<Task> displayList,
                                         String prevHeader, int i, 
                                         String header) {
        if (!header.equals(prevHeader) || prevHeader.isEmpty()) {
            insertHeader(displayList, header, i);
    
            i++;
        }
        return i;
    }

    private static void insertHeader(ArrayList<Task> displayList,
                                         String header, int i) {
        
        Task newHeader = new Task(null, header, null,
                null, null, 0,
                null, EMPTY_STRING, false, false);
        displayList.add(i, newHeader);
    }

    /**
     * Format the start, end time into display string
     * 
     * @param task
     */
    private static void formatDisplayTime(Task task) {
        assert (task != null);

        Task.TaskType taskType = task.getTaskType();
        if (taskType == null) {
            return;
        }

        switch (taskType) {
            case DEADLINE:
                formatDisplayTimeDeadLine(task);
                break;
            case EVENT:
                formatDisplayTimeEvent(task);
                break;
            case TODO:
                formatDisplayTimeTodo(task);
        }
    }

    private static void formatDisplayTimeDeadLine(Task task) {
        task.setLabel(getTimeGUI(task.getDeadline()));
    }

    private static void formatDisplayTimeEvent(Task task) {
        String start = getTimeGUI(task.getStart());
        String end = getTimeGUI(task.getEnd());
        task.setLabel(start + " - " + end);
    }

    private static void formatDisplayTimeTodo(Task task) {
        task.setLabel(EMPTY_STRING);
    }

    private static String getTimeGUI(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return timeFormat.format(calendar.getTime());
    }

    private static String getDateGUI(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return dateFormat.format(calendar.getTime());
    }
    
    public static void setHeaderStyle(TableCell<Task, String> cell) {        
        cell.setTextFill(GUIFormatter.COLOR_TABLE_HEADERS);
        cell.setAlignment(Pos.CENTER);
        cell.getStyleClass().remove(STYLE_STRAIGHT);
        cell.getStyleClass().add(STYLE_ITALIC);
    }
    
    public static void setTextStyle(TableCell<Task, String> cell) {
        cell.setTextFill(Color.WHITE);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getStyleClass().remove(STYLE_ITALIC);
        cell.getStyleClass().add(STYLE_STRAIGHT);
    }
    
    public static void setImportantStyle(TableCell<Task, String> cell) {
        cell.setTextFill(Color.RED);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getStyleClass().remove(STYLE_ITALIC);
        cell.getStyleClass().add(STYLE_STRAIGHT);
    }
}
