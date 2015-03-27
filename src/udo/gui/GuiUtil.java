package udo.gui;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javafx.collections.ObservableList;
import udo.storage.Task;
import udo.util.Utility;

/**
 * This class is a Utility class catered for GUI package
 * mainly.
 * 
 * @author Sharmine
 *
 */
public class GuiUtil {
    
    public static final String EMPTY_STRING = "";
    
    public static final String PREFIX_WARNING = "Warning";
    public static final String PREFIX_ERROR = "Error";
    
    public static final String KEY_UP = "UP";
    public static final String KEY_DOWN = "DOWN";
    
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy");
    public static SimpleDateFormat DateTimeFormat = 
            new SimpleDateFormat("dd/MM HH:mm");
    
    public static boolean isHeader(String str) {
        return (isValidDate(str) || str.equals(GuiFormatter.HEADER_TODO));
    }
    
    public static boolean isValidDate(String dateString) {       
        try {
           dateFormat.parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isImportant(String title, ObservableList<Task> data) {
        Task task = lookUpTask(title, data);
        return task.getPriority() == true;
    }

    private static Task lookUpTask(String title, ObservableList<Task> data) {
        int specifiedIndex = extractIndex(title);
        for(Task element: data) {
            int currIndex = extractIndex(element.getContent());
            if(currIndex == specifiedIndex) {
                return element;
            }
        }
        return null;
    }
    
    /**
     * Extracts the serial number of the task
     * Returns -1 if serial number is not present
     * 
     * @param title
     * @return serial number or -1
     * @throws NumberFormatException
     */
    private static int extractIndex(String title) throws NumberFormatException {
        try {
            return Integer.parseInt(title.substring(0,1));
        } catch(Exception e) {
            return -1;
        }        
    }
    
    public static boolean isUnconfirmed(String title, ObservableList<Task> data) {
        Task task = lookUpTask(title, data);
        return task.getGroupId() > 0;
    }

    public static boolean isWarning(String receivedString) {
       return receivedString.substring(0, 7).equals(PREFIX_WARNING);
    }
    
    public static boolean isError(String receivedString) {
        return receivedString.substring(0, 5).equals(PREFIX_ERROR);
    }
    
    /**
     * Checks whether the start and end time is on the same day and
     * returns different formats of end time accordingly
     * 
     * @param task
     * @return endDateTime in HH:mm format if start and end is on the
     *                     same date or else in dd/MM HH:mm format 
     */
    public static String getEnd(Task task) {
        GregorianCalendar start = task.getStart();
        GregorianCalendar end = task.getEnd();
        if(Utility.isSameDate(start, end)) {
            return GuiUtil.guiTimeFormat(end);
        } else {
            return GuiUtil.guiDateTimeFormat(end);
        }        
    }
    
    /**
     * Formats a calendar object into a specified time format
     * 
     * @param calendar
     * @return time in HH:mm format
     */
    public static String guiTimeFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return timeFormat.format(calendar.getTime());
    }
    
    /**
     * Formats a calendar object into a specified date format
     * 
     * @param calendar
     * @return date in EEE, dd MMM yyyy format
     */
    public static String guiDateFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return dateFormat.format(calendar.getTime());
    }
    
    /**
     * Formats a calendar object into a specified date and time format
     * 
     * @param calendar
     * @return date in EEE, dd MMM yyyy format
     */
    public static String guiDateTimeFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return DateTimeFormat.format(calendar.getTime());
    }
}
