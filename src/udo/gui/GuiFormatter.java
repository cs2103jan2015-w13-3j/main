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
    
    private static final String _MESSAGE_FREE_DAY = "Free for the entire day";
    private static final String _MESSAGE_FREE_BEFORE = "Free before %s";
    private static final String _MESSAGE_FREE_AFTER = "Free after %s";
    private static final String _MESSAGE_FREE_BETWEEN = "Free from %s to %s";

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

    /**
     * Formats a list of given list of tasks
     */
    public ObservableList<Task> getFormattedData() {
        formatDisplayList();
        return convertToObservable();        
    }
    
    /**
     * Formats a list of given list of free slots
     */
    public ObservableList<Task> getFormattedFreeSlotsData() {
        formatFreeSlotsList();
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
        insertHeaderLoop();
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
     * or '?' marks for unconfirmed tasks
     * 
     * @param _rawData
     */
    private void formatDataLoop() {
        
        for (int i = 0; i < _rawData.size(); i++) {
            int displayIndex = i + 1;
            Task task = _rawData.get(i);
            assert(task != null);
            
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
     */
    private void insertHeaderLoop() {

        String prevHeader = GuiUtil.EMPTY_STRING;

        for (int i = 0; i < _rawData.size(); i++) {
            String header = new String();
            Task task = _rawData.get(i);
            
            header = getHeader(task);       
            i = insertIfNewHeader(prevHeader, i, header);
            prevHeader = header;
            
            logger.finest(header);
        }
    }
    
    /**
     * Returns the corresponding date header for that task
     * 
     * @param task
     * @return a date in a string format
     */
    private String getHeader(Task task) {
        assert(task != null);
        
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
     * Formats the task's time into a readable format
     */
    private void formatDateLoop() {
    
        for (int i = 0; i < _rawData.size(); i++) {
            Task task = _rawData.get(i);
            formatDisplayTime(task);
        }
    }

    /**
     * Formats the start, end, deadline time into a string for display 
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
        
        logger.finest(task.getContent() + _SEPARATOR_SPACE + 
                      task.getLabel());
    }

    
    private void formatFreeSlotsList() {
        if (_rawData == null) {
            return;
        }
        
        insertHeaderLoop();
        formatFreeSlotLoop();

        logger.fine(_rawData.toString());   
    }
    
    private void formatFreeSlotLoop() {
        for(int i = 0; i < _rawData.size(); i++) {
            Task task = _rawData.get(i);
            logger.info(task.toString());
            setFreeSlotTitle(task);
        }               
    }

    private void setFreeSlotTitle(Task task) {
        assert(task != null);
        
        GregorianCalendar start = task.getStart();
        GregorianCalendar end = task.getEnd();
        String title = task.getContent();
        
        if(GuiUtil.isHeader(title)) {
            return;
        }

        if(GuiUtil.isStartOfDay(start) && GuiUtil.isEndOfDay(end)) {
            setFreeEntireDay(task);
        } else if (GuiUtil.isEndOfDay(end)) {
            setFreeAfter(task, start);
        } else if (GuiUtil.isStartOfDay(start)){
            setFreeBefore(task, end);
        } else {
            setFreeBetween(task, start, end); 
        }   
        removeDateField(task);
    }
    
    private void setFreeEntireDay(Task task) {
        task.setContent(String.format(_MESSAGE_FREE_DAY));
    }
    
    private void setFreeBefore(Task task, GregorianCalendar end) {
        String time = GuiUtil.guiTimeFormat(end);
        task.setContent(String.format(_MESSAGE_FREE_BEFORE, time));
    }
    
    private void setFreeAfter(Task task, GregorianCalendar start) {
        String time = GuiUtil.guiTimeFormat(start);
        task.setContent(String.format(_MESSAGE_FREE_AFTER, time));
    }
    
    private void setFreeBetween(Task task, GregorianCalendar start,
                                GregorianCalendar end) {
        String timeStart = GuiUtil.guiTimeFormat(start);
        String timeEnd = GuiUtil.guiTimeFormat(end);

        task.setContent(String.format(_MESSAGE_FREE_BETWEEN, timeStart, 
                                      timeEnd));
    }
    
    private void removeDateField(Task task) {
        task.setLabel(GuiUtil.EMPTY_STRING);
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
