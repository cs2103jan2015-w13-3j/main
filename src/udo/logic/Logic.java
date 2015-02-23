package udo.logic;

public class Logic {
    private static final String ERROR_FORMAT = "Error: %s";
    private static final String ERROR_INVALID_CMD_NAME =
            "Invalid command name";
    
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
            parsedCommand.commandName.trim().equalsIgnoreCase("")) {
            status = formatErrorStr(ERROR_INVALID_CMD_NAME);
            return false;
        }
        return true;
    }

    private String formatErrorStr(String errorInvalidCmdName) {
        return String.format(ERROR_FORMAT, errorInvalidCmdName);
    }
}
