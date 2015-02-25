package udo.logic;

import udo.gui.GUI;
import udo.util.Config;

public class Logic {
    private GUI gui;
    
    private static final String ERR_FORMAT = "Error: %s";
    private static final String ERR_INVALID_CMD_NAME = "Invalid command";
    private static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    
    private static final String STATUS_ADDED = "Task: %s added sucessfully";
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
            // TODO: Inform GUI of error status
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
           // TODO: Inform GUI of error status 
        }
    }

    private void executeDisplayCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDisplaySucessStatus(parsedCommand);
    }

    private String getDisplaySucessStatus(Command parsedCommand) {
        // TODO Auto-generated method stub
        return "";
    }

    private void executeDeleteCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDeleteSucessStatus(parsedCommand);
    }

    private String getDeleteSucessStatus(Command parsedCommand) {
        // TODO Auto-generated method stub
        return "";
    }

    private void executeModifyCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getModifySucessStatus(parsedCommand);
    }

    private String getModifySucessStatus(Command parsedCommand) {
        // TODO Auto-generated method stub
        return "";
    }

    private void executeAddCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getAddSucessStatus(parsedCommand);
    }

    private String getAddSucessStatus(Command parsedCommand) {
        String taskContent = parsedCommand.argStr;

        if (taskContent.length() > MAX_STATUS_LENGTH) {
            taskContent = taskContent.substring(0, MAX_STATUS_LENGTH);
        }

        return String.format(STATUS_ADDED, taskContent);
    }

    /**
     * Check the semantics of a parsed command
     * @param parsedCommand
     * @return the command's correctness
     */
    private boolean isCommandValid(Command parsedCommand) {
        if (parsedCommand == null || parsedCommand.commandName == null ||
            parsedCommand.commandName == null) {
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
        // TODO Auto-generated method stub
        return true;
    }

    private boolean isModifyCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return true;
    }

    private boolean isDeleteCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return true;
    }

    private boolean isAddCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return true;
    }

    private String formatErrorStr(String errorInvalidCmdName) {
        return String.format(ERR_FORMAT, errorInvalidCmdName);
    }
}