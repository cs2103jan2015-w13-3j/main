package udo.util;

public class Config {
    /** Procedure for adding a new command:
     * Add a new command name string here
     * Add the corresponding entry in the CommandName enum here
     * Add the command name into the regex pattern in InputParser
     * Add a new branch for string to enum conversion in Utility class
     * Implement a subclass of Command with an 'execute' method and
     * Add a branch to createCommandFromName method in InputParser
     * 
     * Procedure for adding a new option:
     * Add a new string array of 3 entries here:
     *      [option long name, option short name, option arg type]
     * Add the string array name into the OPTIONS_TABLE array
     * Add the option's strings to res/dict.txt for autocomplete
     * -> see existing options as examples
     */

    /** Constants for command names **/
    public static final String CMD_STR_ADD = "add";
    public static final String CMD_STR_MODIFY = "modify";
    public static final String CMD_STR_DELETE = "delete";
    public static final String CMD_STR_DISPLAY = "display";
    public static final String CMD_STR_SEARCH = "search";
    public static final String CMD_STR_DONE = "done";
    public static final String CMD_STR_CHDIR = "chdir";
    public static final String CMD_STR_UNDO = "undo";
    public static final String CMD_STR_CONFIRM = "confirm";
    
    public static final String CMD_STR_DELETE_SHORT = "dd";

    public static enum CommandName {
        ADD, MODIFY, DELETE, DISPLAY, SEARCH, DONE, CHDIR, UNDO, CONFIRM
    }
    
    /** Constants for command options **/
    public static final int OPT_LONG = 0;
    public static final int OPT_SHORT = 1;
    public static final int OPT_TYPE = 2;
    
    // Types that arguments to an option can take
    public static final String TYPE_STR = "str_t";
    public static final String TYPE_DATETIME = "datetime_t";
    public static final String TYPE_TIME = "time_t";
    public static final String TYPE_INT = "int_t";
    public static final String TYPE_NONE = "notype_t";

    public static final String[] OPT_DUR = {"duration", "du", TYPE_TIME};
    public static final String[] OPT_LABEL = {"label", "l", TYPE_STR};
    public static final String[] OPT_PRIO = {"important", "im", TYPE_NONE};
    public static final String[] OPT_DEADLINE = {"deadline", "dl", TYPE_DATETIME};
    public static final String[] OPT_START = {"start", "s", TYPE_DATETIME};
    public static final String[] OPT_END = {"end", "e", TYPE_DATETIME};
    public static final String[] OPT_REMINDER = {"reminder", "r", TYPE_DATETIME};
    public static final String[] OPT_FREE = {"free", "f", TYPE_NONE};
    public static final String[] OPT_DONE = {"done", "do", TYPE_NONE};
    
    /**
     * New options can be added to the following table following the same
     * format as existing entry for it to work with the input parser
     **/
    public static final String OPTIONS_TABLE[][] =
        { OPT_DUR, OPT_LABEL, OPT_PRIO, OPT_DEADLINE,
          OPT_START, OPT_END, OPT_REMINDER, OPT_FREE, OPT_DONE };
}
