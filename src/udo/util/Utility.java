package udo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.HashMap;

import udo.storage.Task;

public class Utility {
	private static final SimpleDateFormat fmt =
	        new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static HashMap<Integer, Integer> indexMap = new HashMap<>();
	
    public static Config.CommandName convertToCommandName(String cmdName) {
        cmdName = cmdName.toLowerCase();
        
        if (cmdName.equals(Config.CMD_STR_ADD)) {
            return Config.CommandName.ADD;
        } else if (cmdName.equals(Config.CMD_STR_DELETE)) {
            return Config.CommandName.DELETE;
        } else if (cmdName.equals(Config.CMD_STR_MODIFY)) {
            return Config.CommandName.MODIFY;
        } else if (cmdName.equals(Config.CMD_STR_DISPLAY)) {
            return Config.CommandName.DISPLAY;
        } else if (cmdName.equals(Config.CMD_STR_DONE)){
            return Config.CommandName.DONE;
        } else if (cmdName.equals(Config.CMD_STR_CHDIR)) {
            return Config.CommandName.CHDIR;
        } else if (cmdName.equals(Config.CMD_STR_SEARCH)) {
            return Config.CommandName.SEARCH;
        } else if (cmdName.equals(Config.CMD_STR_UNDO)) {
            return Config.CommandName.UNDO;
        } else if (cmdName.equals(Config.CMD_STR_CONFIRM)) {
            return Config.CommandName.CONFIRM;
        } else {
            return null;
        }
    }

    public static void setToStartOfDay(GregorianCalendar start) {
        start.set(GregorianCalendar.HOUR_OF_DAY, 0);
        start.set(GregorianCalendar.MINUTE, 0);
    }

    /**
     * Set a Gregorian calendar to end of the day i.e 23:59pm
     * @param end
     */
    public static void setToEndOfDay(GregorianCalendar end) {
        end.set(GregorianCalendar.HOUR_OF_DAY, 23);
        end.set(GregorianCalendar.MINUTE, 59);
    }

    /**
     * Convert a Gregorian calendar object to string representation
     * @param calendar
     * @return the string representation or empty string if calendar is null
     */
	public static String calendarToString(GregorianCalendar calendar){
	    if (calendar == null) {
	        return "";
	    }
		return fmt.format(calendar.getTime());
	}
	
	/**
	 * Convert a formatted date string into a calendar object
	 * @param dateStr the formatted string representation of a date
	 * @return a new calendar or null if dateStr is null or
	 *         the dateStr's format is invalid
	 */
	public static GregorianCalendar stringToCalendar(String dateStr) {
	    try {
	        if (dateStr == null) {
	            return null;
	        }
	        GregorianCalendar calendar = new GregorianCalendar();
	        calendar.setTime(fmt.parse(dateStr));
            return calendar;
        } catch (ParseException e) {
            System.err.println("Invalid date format");
            return null;
        }
	}
	
	
	public static ArrayList<Task> deepCopy(List<Task> firstCopy) {
	    ArrayList<Task> copy = new ArrayList<>();
	    for(Task element : firstCopy) {
	        copy.add(element.copy());
	    }
	    return copy;
	}
	
	public static boolean isSameDate(GregorianCalendar cal1, GregorianCalendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}
