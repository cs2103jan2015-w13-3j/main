package udo.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import udo.storage.Task;
import udo.util.Utility;

/**
 * This class is a singleton and it serves as a helper class for the GUI class.
 * It processes the given list of Tasks into a user-friendly format. It first
 * sorts the list, inserts date headers, serial numbers and formats the time
 * displayed.
 * 
 * @author Sharmine
 *
 */

public class GUIFormatter {
    
    public static final String HEADER_TODO = "To-Dos";    
    
    private static final Logger logger = Logger.getLogger(GUIFormatter.class.getName());
    
    private static GUIFormatter guiFormatter;
    private static ArrayList<Task> rawData;
    
    private GUIFormatter() {
        
    }
    
    public static GUIFormatter getInstance() {
        if(guiFormatter == null) {
            guiFormatter = new GUIFormatter();
        }
        return guiFormatter;
    }
    
    public void setData(List<Task> receivedList) {
        rawData = (ArrayList<Task>) receivedList;
    }

    public ObservableList<Task> getFormattedData() {
        formatDisplayList();
        return convertToObservable();        
    }
    
    /**
     * Associate an ArrayList of objects with an ObservableArrayList
     */
    private static ObservableList<Task> convertToObservable() {
        return FXCollections.observableArrayList(rawData);  
    }
    
    private void formatDisplayList() {
        if (rawData == null) {
            return;
        }

        Collections.sort(rawData);
        processIndex();
        formatDateLoop();
        logger.fine(rawData.toString());
    }
    
    private void processIndex() {
        Utility.indexMap.clear();
        formatIndexLoop();
    }
    
    /**
     * Maps the displayIndex to the Task's actual index 
     * and appends serial number
     * 
     * @param rawData
     */
    private void formatIndexLoop() {
        
        for (int i = 0; i < rawData.size(); i++) {
            int displayIndex = i + 1;
            Task task = rawData.get(i);
            mapIndex(displayIndex, task.getIndex());
            appendSerialNumber(task, displayIndex);
            logger.finest(task.getContent());
        }
    }
    
    private void mapIndex(Integer displayIndex, Integer actualIndex) {
        Utility.indexMap.put(displayIndex, actualIndex);
    }
    
    private void appendSerialNumber(Task task, int counter) {    
        task.setContent("" + counter + ".  " + task.getContent());
    }

    /**
     * Formats list into a GUI display format, inserts date headers and time
     */
    private void formatDateLoop() {

        String prevHeader = GUIUtil.EMPTY_STRING;

        for (int i = 0; i < rawData.size(); i++) {
            String header = new String();
            Task task = rawData.get(i);
            formatDisplayTime(task);
            
            header = getHeader(task);       
            i = insertIfNewHeader(prevHeader, i, header);
            prevHeader = header;
            
            logger.finest(header);
        }

    }

    private String getHeader(Task task) {
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

    private int insertIfNewHeader(String prevHeader, int i, 
                                  String header) {
        if (!header.equals(prevHeader) || prevHeader.isEmpty()) {
            insertHeader(header, i);
            i++;
        }
        return i;
    }

    private void insertHeader(String header, int i) {       
        Task newHeader = new Task(null, header, null,
                                  null, null, 0, null,
                                  GUIUtil.EMPTY_STRING, false, false);
        rawData.add(i, newHeader);
    }

    /**
     * Format the start, end, deadline time into a string for display
     * 
     * @param task
     */
    private void formatDisplayTime(Task task) {
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
        
        logger.finest(task.getContent() + " " + task.getLabel());
    }

    private void setDisplayTimeDeadLine(Task task) {
        task.setLabel(GUIUtil.guiTimeFormat(task.getDeadline()));
    }

    private void setDisplayTimeTodo(Task task) {
        task.setLabel(GUIUtil.EMPTY_STRING);
    }

    private void setDisplayTimeEvent(Task task) {
        String start = GUIUtil.guiTimeFormat(task.getStart());
        String end = getEnd(task);
        task.setLabel(start + " - " + end);
    }
    
    private String getEnd(Task task) {
        GregorianCalendar start = task.getStart();
        GregorianCalendar end = task.getEnd();
        if(Utility.isSameDate(start, end)) {
            return GUIUtil.guiTimeFormat(end);
        } else {
            return GUIUtil.guiEndDateFormat(end);
        }        
    }
}
