package udo.logic;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.gui.Gui;
import udo.logic.command.Command;
import udo.storage.Storage;
import udo.testdriver.GuiStub;

public class Logic {
    private Gui gui;
    private static Logic logicObj = null;
    private Storage storage;

    private static final String ERR_FORMAT = "Error: %s";
    public static final String ERR_INVALID_CMD_NAME = "Invalid command name";
    public static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    public static final String ERR_INVALID_CMD_ARG =
            "invalid command's argument";
    public static final String ERR_UNSPECIFIED_INDEX =
            "a valid task's index is required";
    public static final String ERR_LATE_DEADLINE =
            "deadline has already passed";
    public static final String ERR_NON_POSITIVE_DUR =
            "task's duration must be positive";
    public static final String ERR_STORAGE =
            "tasks' storage input/output error";
    public static final String ERR_INVALID_INDEX =
            "specified task's index is not valid";
    public static final String ERR_EMPTY_CONTENT =
            "task's content cannot be empty";

    public static final String WARN_FORMAT = "Warning: %s";

    public static final Integer MAX_STATUS_LENGTH = 40;

    private InputParser parser;
    private Autocompleter autocompleter;

    private static String status;

    private static final Logger log = Logger.getLogger(Logic.class.getName());

    private Logic() {
        parser = new InputParser();

        storage = new Storage();

        autocompleter = new Autocompleter();
        autocompleter.addTaskContentToTree(storage.query());

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

    public void setGui(Gui guiObj) {
        gui = guiObj;
    }

    /**
     * Execute the command given in the command string
     * @param command the command string
     */
    public boolean executeCommand(String command) {
        log.log(Level.FINE, "Receive command: " + command);

        Command parsedCommand = parser.parseCommand(command);

        if (parser.getErrorStatus() != null) {
            log.log(Level.FINE,
                    "Command syntax error: " + parser.getErrorStatus());

            status = formatErrorStr(parser.getErrorStatus());
            gui.displayStatus(status);
            return false;
        }

        log.log(Level.FINER, parsedCommand.toString(), parsedCommand);

        parsedCommand.setGui(gui);
        parsedCommand.setStorage(storage);
        parsedCommand.setAutocompleter(autocompleter);

        return parsedCommand.execute();
    }

    /******************************
     * Interface for autocomplete *
     ******************************/

    /**
     * Refer to same method in Autocompleter
     */
    public List<String> getSuggestions(String text) {
        return autocompleter.getSuggestions(text);
    }

    /**
     * Refer to same method in Autocompleter
     */
    public List<String> getSuggestions(String text, Integer maxWords) {
        return autocompleter.getSuggestions(text, maxWords);
    }

    /**
     * Refer to same method in Autocompleter
     */
    public String autocomplete(String text) {
        return autocompleter.autocomplete(text);
    }

    /**
     * Refer to same method in Autocompleter
     */
    public String getPreviousCmd() {
        return autocompleter.getPreviousCmd();
    }

    /**
     * Refer to same method in Autocompleter
     */
    public String getNextCmd() {
        return autocompleter.getNextCmd();
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
        logic.setGui(new GuiStub());
        logic.executeCommand("go to school /deadline tomorrow");
        logic.executeCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm");
        logic.executeCommand("add AAAI conference /start in 2 days /end tuesday");
        logic.executeCommand("add match midterm /start next friday /end 11/02/15");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("submit the report /dl next friday /reminder next thursday");
    }
}
