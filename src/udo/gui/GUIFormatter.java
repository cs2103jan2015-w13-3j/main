package udo.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import udo.storage.Task;
import udo.util.Utility;

/**
 * This class serves as a helper class for the GUI class. It processes the given
 * list of Tasks into a user-friendly format. It first sorts the list,
 * inserts date headers, serial numbers and formats the time displayed.
 * 
 * @author Sharmine
 *
 */
public class GUIFormatter {
    
    public static final String HEADER_TODO = "To-Dos";    
    
    public static void formatDisplayList(ArrayList<Task> displayList) {
        if (displayList == null) {
            return;
        }

        Collections.sort(displayList);
        processIndex(displayList);
        formatDateLoop(displayList);
        //System.out.println("Formatted List = " + displayList);
    }
    
    private static void processIndex(ArrayList<Task> displayList) {
        Utility.indexMap.clear();
        formatIndexLoop(displayList);
    }
    
    /**
     * Maps the displayIndex to the Task's actual index 
     * and appends serial number
     * 
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

        String prevHeader = GUIUtil.EMPTY_STRING;

        for (int i = 0; i < displayList.size(); i++) {
            String header = new String();
            Task task = displayList.get(i);
            formatDisplayTime(task);
            
            header = getHeader(task);       
            i = insertIfNewHeader(displayList, prevHeader, i, header);
            prevHeader = header;
        }

    }

    private static String getHeader(Task task) {
        switch (task.getTaskType()) {
            case TODO :
                return HEADER_TODO;              
            case EVENT :
                return GUIUtil.guiDateFormat(task.getStart());
            case DEADLINE :
                return GUIUtil.guiDateFormat(task.getDeadline());
            default :
                return GUIUtil.EMPTY_STRING;
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
                                  null, null, 0, null,
                                  GUIUtil.EMPTY_STRING, false, false);
        displayList.add(i, newHeader);
    }

    /**
     * Format the start, end, deadline time into a string for display
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
                setDisplayTimeDeadLine(task);
                break;
            case EVENT:
                setDisplayTimeEvent(task);
                break;
            case TODO:
                setDisplayTimeTodo(task);
        }
    }

    private static void setDisplayTimeDeadLine(Task task) {
        task.setLabel(GUIUtil.guiTimeFormat(task.getDeadline()));
    }

    private static void setDisplayTimeTodo(Task task) {
        task.setLabel(GUIUtil.EMPTY_STRING);
    }

    private static void setDisplayTimeEvent(Task task) {
        String start = GUIUtil.guiTimeFormat(task.getStart());
        String end = getEnd(task);
        task.setLabel(start + " - " + end);
    }
    
    private static String getEnd(Task task) {
        GregorianCalendar start = task.getStart();
        GregorianCalendar end = task.getEnd();
        if(Utility.isSameDate(start, end)) {
            return GUIUtil.guiTimeFormat(end);
        } else {
            return GUIUtil.guiEndDateFormat(end);
        }        
    }
}
