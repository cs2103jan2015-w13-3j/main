package udo.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import udo.storage.Task;

public class GUIFormatter {
    
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    private static final String DISPLAY_TIME_TODO = "-";
    public static final String EMPTY_STRING = "";
    
    public static void formatDisplayList(ArrayList<Task> displayList) {
        if (displayList == null) {
            return;
        }
        
        Collections.sort(displayList);        
        formatSerialNumberings(displayList);
        formatElementLoop(displayList);
    }

    private static void formatSerialNumberings(ArrayList<Task> displayList) {

        for (int i = 0; i < displayList.size(); i++) {
            int counter = i + 1;
            Task task = displayList.get(i);
            task.setContent("" + counter + ".  " + task.getContent());
        }
    }
    
    /**
     * Formats list into a GUI display format
     * Inserts date headers and time
     */
    private static void formatElementLoop(ArrayList<Task> displayList) {

        String prevDayMonthYear = EMPTY_STRING;

        for (int i = 0; i < displayList.size(); i++) {

            Task task = displayList.get(i);
            formatDisplayTime(task);
            String dayMonthYear = formatDateGUI(task.getEnd());            
            i = insertIfNewDate(displayList, prevDayMonthYear, i, dayMonthYear);
            prevDayMonthYear = dayMonthYear;
        }

    }
    
    /**
     * Format the start, end time into display string
     * @param task
     */
    private static void formatDisplayTime(Task task) {
        assert(task!=null);
        
        Task.TaskType taskType = task.getTaskType();
        if(taskType == null) {
            return ;
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
                break;
        }        
    }
    
    private static void formatDisplayTimeTodo(Task task) {
        task.setLabel(DISPLAY_TIME_TODO);       
    }

    private static void formatDisplayTimeEvent(Task task) {
        String start = formatTimeGUI(task.getStart());
        String end = formatTimeGUI(task.getEnd());
        task.setLabel(start + " - " + end);
    }

    private static void formatDisplayTimeDeadLine(Task task) {       
        task.setLabel(formatTimeGUI(task.getDeadline()));
    }

    private static int insertIfNewDate(ArrayList<Task> displayList, String prevDate, int i, String date) {
        if (!date.equals(prevDate) || prevDate.isEmpty()) {
            insertDateHeader(displayList, date, i);
            
            i++;
        }
        return i;
    }

    private static void insertDateHeader(ArrayList<Task> displayList, String date, int i) {
        Task dateHeader = new Task(null, date, new GregorianCalendar(),
                new GregorianCalendar(), 0, new GregorianCalendar(), EMPTY_STRING, true);
        displayList.add(i, dateHeader);
    }

    private static String formatTimeGUI(GregorianCalendar calendar) {
        if(calendar == null) { 
            return EMPTY_STRING;
        }         
        return timeFormat.format(calendar.getTime());
    }

    private static String formatDateGUI(GregorianCalendar calendar) {
        if(calendar == null) {
            return EMPTY_STRING;
        }
        return dateFormat.format(calendar.getTime());
    }    
}
