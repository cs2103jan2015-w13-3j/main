package udo.logic;

import udo.util.Config;

public class Logic {
    private static final String ERR_FORMAT = "Error: %s";
    private static final String ERR_INVALID_CMD_NAME = "Invalid command";
    private static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    
    private InputParser parser;
    
    private static String status;
    
    public Logic() {
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
        }
        
        if (isCommandValid(parsedCommand)) {
        } else {
           // TODO: Inform GUI of error status 
        }
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
        return false;
    }

    private boolean isModifyCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isDeleteCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isAddCmdValid(Command parsedCommand) {
        // TODO Auto-generated method stub
        return false;
    }

    private String formatErrorStr(String errorInvalidCmdName) {
        return String.format(ERR_FORMAT, errorInvalidCmdName);
    }
}