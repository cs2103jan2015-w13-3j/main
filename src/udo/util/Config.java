package udo.util;

public class Config {
    /** Constants for command names **/
    public static final String CMD_STR_ADD = "add";
    public static final String CMD_STR_MODIFY = "modify";
    public static final String CMD_STR_DELETE = "delete";
    public static final String CMD_STR_DISPLAY = "display";
    public static final String CMD_STR_SEARCH = "search";
    
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

    public static final String[] OPT_DUR = {"duration", "d", TYPE_TIME};
    public static final String[] OPT_VENUE = {"venue", "v", TYPE_STR};
    public static final String[] OPT_LABEL = {"label", "l", TYPE_STR};
    public static final String[] OPT_PRIO = {"important", "im", TYPE_NONE};
    public static final String[] OPT_EVENT = {"event", "ev", TYPE_STR};
    public static final String[] OPT_DEADLINE = {"deadline", "dl", TYPE_STR};
    public static final String[] OPT_TODO = {"todo", "td", TYPE_STR};
    public static final String[] OPT_START = {"start", "s", TYPE_DATETIME};
    public static final String[] OPT_END = {"end", "e", TYPE_DATETIME};
    public static final String[] OPT_INDEX = {"index", "i", TYPE_INT};
    
    /**
     * New options can be added to the following table following the same
     * format as existing entry for it to work with the input parser
     **/
    public static final String OPTIONS_TABLE[][] =
        { OPT_DUR, OPT_VENUE, OPT_LABEL, OPT_PRIO, OPT_EVENT,
          OPT_DEADLINE, OPT_TODO, OPT_START, OPT_END, OPT_INDEX };
}
