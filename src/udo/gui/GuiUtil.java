package udo.gui;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import udo.storage.Task;
import udo.util.Utility;

//@author A0114906J

/**
 * This class is a Utility class catered for GUI package
 * mainly.
 *
 */

public class GuiUtil {

    public static final String EMPTY_STRING = "";

    public static final String SPACER_STRING = " | ";

    public static final String PREFIX_WARNING = "Warning";
    public static final String PREFIX_ERROR = "Error";

    public static final String KEY_UP = "UP";
    public static final String KEY_DOWN = "DOWN";

    public static final String START_OF_DAY = "00:00";
    public static final String END_OF_DAY = "23:59";
    
    public static final Color COLOUR_TABLE_HEADERS = Color.rgb(26, 188, 156);
    public static final Color COLOUR_TEXT_WARNING = Color.ORANGE;
    public static final Color COLOUR_TEXT_ERROR = Color.RED;
    public static final Color COLOUR_TEXT_NORMAL = Color.WHITE;
    
    public static final String COLOUR_BACKGROUND = "-fx-background-color: #2b3339; ";
    public static final String COLOUR_WHITE = "-fx-fill: white; ";
    public static final String COLOUR_GREEN = "-fx-fill: #1abc9c; ";

    public static final String STYLE_SIZE = "-fx-font-size: 12.5px; ";
    public static final String STYLE_FONT = "-fx-font-family: 'Open Sans', sans-serif; ";

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy");
    public static SimpleDateFormat DateTimeFormat =
            new SimpleDateFormat("dd/MM HH:mm");

    public static boolean isHeader(String str) {
        if(str == null) {
            return false;
        }
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
        return task.isPriority() == true;
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
            String index = title.substring(0, title.indexOf("."));
            return Integer.parseInt(index);
        } catch(Exception e) {
            return -1;
        }
    }

    public static boolean isUnconfirmed(Task task) {
        return task.getGroupId() != null && task.getGroupId() > 0;
    }

    public static boolean isWarning(String receivedString) {
       return receivedString.length() > 7 && 
               receivedString.substring(0, 7).equals(PREFIX_WARNING);
    }

    public static boolean isError(String receivedString) {
        return receivedString.length() > 5 &&
                receivedString.substring(0, 5).equals(PREFIX_ERROR);
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
     * @return date in EEE, dd/MM HH:mm format
     */
    public static String guiDateTimeFormat(GregorianCalendar calendar) {
        if (calendar == null) {
            return EMPTY_STRING;
        }
        return DateTimeFormat.format(calendar.getTime());
    }

    public static String concatListToString(List<String> suggestedList) {
        String suggestedWords = new String();
        for(int i =0; i < suggestedList.size(); i++) {
            String word = suggestedList.get(i);
            suggestedWords = addWord(suggestedWords, word);
            suggestedWords = trimIfLastIndex(suggestedList, suggestedWords, i);
        }
        return suggestedWords;

    }

    private static String trimIfLastIndex(List<String> suggestedList,
            String suggestedWords, int i) {
        if(isLastIndex(suggestedList, i)) {
            suggestedWords = trimSuggestions(suggestedWords);
        }
        return suggestedWords;
    }

    private static boolean isLastIndex(List<String> suggestedList, int i) {
        return i == suggestedList.size() - 1;
    }

    private static String addWord(String suggestedWords, String word) {
        suggestedWords = suggestedWords + word + SPACER_STRING;
        return suggestedWords;
    }

    private static String trimSuggestions(String suggestedWords) {
        int index = suggestedWords.lastIndexOf(SPACER_STRING);
        return suggestedWords.substring(0, index);
    }
    
    public static boolean isStartOfDay(GregorianCalendar cal1) {
        return guiTimeFormat(cal1).equals(START_OF_DAY);
    }

    public static boolean isEndOfDay(GregorianCalendar cal1) {
        return guiTimeFormat(cal1).equals(END_OF_DAY);
    }
}
