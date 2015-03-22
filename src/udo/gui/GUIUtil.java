package udo.gui;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javafx.collections.ObservableList;
import udo.storage.Task;
import udo.util.Utility;

public class GUIUtil {
    
    public static final String EMPTY_STRING = "";
    
    public static final String PREFIX_WARNING = "Warning";
    public static final String PREFIX_ERROR = "Error";
    
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy");
    public static SimpleDateFormat endDateFormat = 
            new SimpleDateFormat("dd/MM HH:mm");
    
    public static boolean isHeader(String str) {
        return (isValidDate(str) || str.equals(GUIFormatter.HEADER_TODO));
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
    
    public static boolean isWarning(String receivedString) {
       return receivedString.substring(0, 7).equals(PREFIX_WARNING);
    }
    
    public static boolean isError(String receivedString) {
        return receivedString.substring(0, 5).equals(PREFIX_ERROR);
    }
    
    public static String guiTimeFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return timeFormat.format(calendar.getTime());
    }
    
    public static String guiDateFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return dateFormat.format(calendar.getTime());
    }

    public static String guiEndDateFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return endDateFormat.format(calendar.getTime());
    }
}
