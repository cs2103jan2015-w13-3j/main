package udo.logic;

import udo.gui.GUI;
import udo.util.Config;

public class Logic {
    private GUI gui;
    
    private static final String ERR_FORMAT = "Error: %s";
    private static final String ERR_INVALID_CMD_NAME = "Invalid command";
    private static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    private static final String ERR_INVALID_CMD_ARG =
            "Invalid command's argument";
    private static final String ERR_UNSPECIFIED_INDEX =
            "A valid task's index is required";
    
    private static final String STATUS_ADDED = "Task: %s added sucessfully";
    private static final String STATUS_DELETED =
            "Task: %d deleted sucessfully";
    private static final String STATUS_MODIFIED =
            "Task: %d modified sucessfully";
    private static final Integer MAX_STATUS_LENGTH = 40;
    
    private InputParser parser;
    
    private static String status;
    
    public Logic(GUI gui) {
        this.gui = gui;
        parser = new InputParser();
        /* TODO:
         * Initialize Storage
         * Initialize and start up passive thread for reminder
         */
    }
    
    /******************************
     * Code for the active logics *
     ******************************/
    
    /**
     * Execute the command given in the command string 
     * @param command the command string
     */
    public void executeCommand(String command) {
        Command parsedCommand = parser.parseCommand(command);
        if (parser.getErrorStatus() != null) {
            // Syntax error
            status = parser.getErrorStatus();
            gui.displayStatus(status);
        }
        
        if (isCommandValid(parsedCommand)) {
            switch (parsedCommand.commandName) {
                case ADD:
                    executeAddCommand(parsedCommand);
                    break;
                case MODIFY:
                    executeModifyCommand(parsedCommand);
                    break;
                case DELETE:
                    executeDeleteCommand(parsedCommand);
                    break;
                case DISPLAY:
                    executeDisplayCommand(parsedCommand);
                    break;
                default:
                    status = ERR_UNSUPPORTED_CMD;
            }
            
            gui.displayStatus(status);
        } else {
            gui.displayStatus(status);
        }
    }

    private void executeDisplayCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDisplaySucessStatus(parsedCommand);
    }

    private String getDisplaySucessStatus(Command parsedCommand) {
        return "";
    }

    private void executeDeleteCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDeleteSucessStatus(parsedCommand);
        // TODO retrieve and display all tasks
    }

    private String getDeleteSucessStatus(Command parsedCommand) {
        return String.format(STATUS_DELETED, parsedCommand.argIndex);
    }

    private void executeModifyCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getModifySucessStatus(parsedCommand);
        // TODO retrieve and display all tasks
    }

    private String getModifySucessStatus(Command parsedCommand) {
        // TODO Auto-generated method stub
        return String.format(STATUS_MODIFIED, parsedCommand.argIndex);
    }

    private void executeAddCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getAddSucessStatus(parsedCommand);
        // TODO retrieve and display all tasks
    }

    private String getAddSucessStatus(Command parsedCommand) {
        String taskContent = parsedCommand.argStr;

        if (taskContent.length() > MAX_STATUS_LENGTH) {
            taskContent = taskContent.substring(0, MAX_STATUS_LENGTH);
            taskContent += "...";
        }

        return String.format(STATUS_ADDED, taskContent);
    }

    /**
     * Check the semantics of a parsed command
     * @param parsedCommand
     * @return the command's correctness
     */
    private boolean isCommandValid(Command parsedCommand) {
        if (parsedCommand == null || parsedCommand.commandName == null) {
            status = formatErrorStr(ERR_INVALID_CMD_NAME);
            return false;
        }
        
        Config.CommandName cmdName = parsedCommand.commandName;
        switch (cmdName) {
            case ADD:
                return isAddCmdValid(parsedCommand); 
            case DELETE:
                return isDeleteCmdValid(parsedCommand);
            case MODIFY:
                return isModifyCmdValid(parsedCommand);
            case DISPLAY:
                return isDisplayCmdValid(parsedCommand);
            default:
                status = formatErrorStr(ERR_UNSUPPORTED_CMD);
                return false;
        }
    }

    private boolean isDisplayCmdValid(Command parsedCommand) {
        return true;
    }

    private boolean isModifyCmdValid(Command parsedCommand) {
        if (parsedCommand.argIndex == null) {
            status = ERR_UNSPECIFIED_INDEX;
            return false;
        }
        return true;
    }

    private boolean isDeleteCmdValid(Command parsedCommand) {
        if (parsedCommand.argIndex == null) {
            status = ERR_UNSPECIFIED_INDEX;
            return false;
        }
        return true;
    }

    private boolean isAddCmdValid(Command parsedCommand) {
        if (parsedCommand.argStr == null) {
            status = ERR_INVALID_CMD_ARG;
            return false;
        }

        return true;
    }

    private String formatErrorStr(String errorInvalidCmdName) {
        return String.format(ERR_FORMAT, errorInvalidCmdName);
    }
}