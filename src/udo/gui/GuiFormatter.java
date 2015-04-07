package udo.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import udo.storage.Task;
import udo.util.Utility;

//@author A0114906J

/**
 * This class is a singleton and it serves as a helper class for the GUI class.
 * It processes the given list of Tasks into a user-friendly format. It first
 * sorts the list, inserts date headers, serial numbers and formats the time
 * displayed.
 *
 */

public class GuiFormatter {

    public static final String HEADER_TODO = "To-Dos";

    private static final Logger logger = 
            Logger.getLogger(GuiFormatter.class.getName());
    
    private static final String _SEPARATOR_DASH = " - ";
    private static final String _SEPARATOR_QUESTION_MARK = "? ";
    private static final String _SEPARATOR_SPACE = " ";
    private static final String _SEPARATOR_DOT = ".  ";
    
    private static GuiFormatter _guiFormatter;
    private static ArrayList<Task> _rawData;

    private GuiFormatter() {

    }
    
    public static GuiFormatter getInstance() {
        if(_guiFormatter == null) {
            _guiFormatter = new GuiFormatter();
        }
        return _guiFormatter;
    }
    
    public void setData(List<Task> receivedList) {
        _rawData = (ArrayList<Task>) receivedList;
    }

    public ObservableList<Task> getFormattedData() {
        formatDisplayList();
        return convertToObservable();        
    }
    
    /**
     * Associate an ArrayList of objects with an ObservableArrayList
     */
    private static ObservableList<Task> convertToObservable() {
        return FXCollections.observableArrayList(_rawData);  
    }
    
    private void formatDisplayList() {
        if (_rawData == null) {
            return;
        }

        Collections.sort(_rawData);
        processData();
        formatDateLoop();
        logger.fine(_rawData.toString());
    }
    
    private void processData() {
        Utility.indexMap.clear();
        formatDataLoop();
    }
    
    /**
     * Maps the displayIndex to the Task's actual index 
     * and appends important information like serial number
     * and '?' marks for unconfirmed tasks
     * 
     * @param _rawData
     */
    private void formatDataLoop() {
        
        for (int i = 0; i < _rawData.size(); i++) {
            int displayIndex = i + 1;
            Task task = _rawData.get(i);
            
            mapIndex(displayIndex, task.getIndex());
            appendInformation(displayIndex, task);
            
            logger.finest(task.getContent());
        }
    }

    private void appendInformation(int displayIndex, Task task) {
        appendUnconfirmedMarks(task);
        appendSerialNumber(task, displayIndex);
    }
    
    private void mapIndex(Integer displayIndex, Integer actualIndex) {
        Utility.indexMap.put(displayIndex, actualIndex);
    }
    
    private void appendSerialNumber(Task task, int counter) {    
        task.setContent(GuiUtil.EMPTY_STRING + counter + 
                        _SEPARATOR_DOT + task.getContent());
    }

    private void appendUnconfirmedMarks(Task task) {
        if(GuiUtil.isUnconfirmed(task)) {
            task.setContent(_SEPARATOR_QUESTION_MARK +
                            task.getContent());
        }
    }

    /**
     * Formats list into a GUI display format, inserts date headers 
     * and formats time
     */
    private void formatDateLoop() {

        String prevHeader = GuiUtil.EMPTY_STRING;

        for (int i = 0; i < _rawData.size(); i++) {
            String header = new String();
            Task task = _rawData.get(i);
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
                return GuiUtil.guiDateFormat(task.getStart());
            case DEADLINE :
                return GuiUtil.guiDateFormat(task.getDeadline());
            default :
                return GuiUtil.EMPTY_STRING;
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
                                  GuiUtil.EMPTY_STRING, false, false);
        _rawData.add(i, newHeader);
    }

    /**
     * Formats the start, end, deadline time into a string for display 
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
        
        logger.finest(task.getContent() + _SEPARATOR_SPACE + 
                      task.getLabel());
    }

    private void setDisplayTimeDeadLine(Task task) {
        task.setLabel(GuiUtil.guiTimeFormat(task.getDeadline()));
    }

    private void setDisplayTimeTodo(Task task) {
        task.setLabel(GuiUtil.EMPTY_STRING);
    }

    private void setDisplayTimeEvent(Task task) {
        String start = GuiUtil.guiTimeFormat(task.getStart());
        String end = GuiUtil.getEnd(task);
        task.setLabel(start + _SEPARATOR_DASH + end);
    }
}
