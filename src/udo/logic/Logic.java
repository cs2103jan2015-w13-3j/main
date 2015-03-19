package udo.logic;

import udo.gui.GUI;
import udo.logic.command.Command;
import udo.storage.Storage;

public class Logic {
    private GUI gui;
    private static Logic logicObj = null;
    private Storage storage;

    private static final String ERR_FORMAT = "Error: %s";
    public static final String ERR_INVALID_CMD_NAME = "Invalid command";
    public static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    public static final String ERR_INVALID_CMD_ARG =
            "Invalid command's argument";
    public static final String ERR_UNSPECIFIED_INDEX =
            "A valid task's index is required";
    public static final String ERR_LATE_DEADLINE =
            "Deadline has already passed";
    public static final String ERR_NON_POSITIVE_DUR =
            "Task's duration must be positive";
    public static final String ERR_STORAGE =
            "Tasks' storage input/output error";
    public static final String ERR_INVALID_INDEX =
            "Specified task's index is not valid";
    public static final String ERR_EMPTY_CONTENT =
            "task's content cannot be empty";
    
    public static final String WARN_FORMAT = "Warning: %s";

    public static final Integer MAX_STATUS_LENGTH = 40;

    private InputParser parser;

    private static String status;

    private Logic() {
        parser = new InputParser();
        storage = new Storage();
        /* TODO:
         * Initialize and start up passive thread for reminder
         */
    }
    
    public static Logic getInstance() {
        if (logicObj == null) {
            logicObj = new Logic();
        }
        
        return logicObj;
    }
    
    public void setGUI(GUI guiObj) {
        gui = guiObj;
    }

    /**
     * Execute the command given in the command string
     * @param command the command string
     */
    public boolean executeCommand(String command) {
        Command parsedCommand = parser.parseCommand(command);
        if (parser.getErrorStatus() != null) {
            status = formatErrorStr(parser.getErrorStatus());
            gui.displayStatus(status);
            return false;
        }
        
        parsedCommand.setGUI(gui);
        parsedCommand.setStorage(storage);

        return parsedCommand.execute();
    }

    /**
     * @param parsedCommand
     * @return
     */
    public static String summarizeContent(String taskContent) {
        if (taskContent == null) {
            return "";
        }

        if (taskContent.length() > MAX_STATUS_LENGTH) {
            taskContent = taskContent.substring(0, MAX_STATUS_LENGTH);
            taskContent += "...";
        }
        return taskContent;
    }

    public static String formatErrorStr(String error) {
        return String.format(ERR_FORMAT, error);
    }
    
    public static String formatWarningStr(String warning) {
        return String.format(WARN_FORMAT, warning);
    }

    public static void main(String[] argv) {
        Logic logic = new Logic();
        logic.executeCommand("go to school /deadline tomorrow");
        logic.executeCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm");
        logic.executeCommand("add AAAI conference /start in 2 days /end tuesday");
        logic.executeCommand("add match midterm /start next friday /end 11/02/15");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("submit the report /dl next friday /reminder next thursday");
    }
}