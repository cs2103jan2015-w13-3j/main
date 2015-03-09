package udo.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import udo.storage.Task;
import udo.util.Utility;

public class GUIFormatter {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    public static final String EMPTY_STRING = "";
    public static final String HEADER_TODO = "To-Dos";
    
    public static void formatDisplayList(ArrayList<Task> displayList) {
        if (displayList == null) {
            return;
        }

        Collections.sort(displayList);
        mapIndex(displayList);
        formatEntryLoop(displayList);
        System.out.println("Formatted List = " + displayList);
    }

    private static void mapIndex(ArrayList<Task> displayList) {
        
        for (int i = 0; i < displayList.size(); i++) {
            int displayIndex = i + 1;
            Task task = displayList.get(i);
            Utility.indexMap.put(displayIndex, task.getIndex());
            addSerialNumber(task, displayIndex);
        }
    }

    private static void addSerialNumber(Task task, int counter) {    
        task.setContent("" + counter + ".  " + task.getContent());
    }

    /**
     * Formats list into a GUI display format, inserts date headers and time
     */
    private static void formatEntryLoop(ArrayList<Task> displayList) {

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
                return formatDateGUI(task.getStart());
            case DEADLINE :
                return formatDateGUI(task.getDeadline());
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
                break;
        }
    }

    private static void formatDisplayTimeTodo(Task task) {
        task.setLabel(EMPTY_STRING);
    }

    private static void formatDisplayTimeEvent(Task task) {
        String start = formatTimeGUI(task.getStart());
        String end = formatTimeGUI(task.getEnd());
        task.setLabel(start + " - " + end);
    }

    private static void formatDisplayTimeDeadLine(Task task) {
        task.setLabel(formatTimeGUI(task.getDeadline()));
    }

    private static String formatTimeGUI(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return timeFormat.format(calendar.getTime());
    }

    private static String formatDateGUI(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return dateFormat.format(calendar.getTime());
    }
}
