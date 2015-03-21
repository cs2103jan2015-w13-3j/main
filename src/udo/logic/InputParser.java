package udo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import udo.logic.command.AddCommand;
import udo.logic.command.ChdirCommand;
import udo.logic.command.Command;
import udo.logic.command.DeleteCommand;
import udo.logic.command.DisplayCommand;
import udo.logic.command.DoneCommand;
import udo.logic.command.ModifyCommand;
import udo.logic.command.SearchCommand;
import udo.logic.command.UndoCommand;
import udo.util.Config;
import udo.util.Config.CommandName;
import udo.util.Utility;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class InputParser {
    // Regex string used to match command name (case insensitive)
    private static final String GROUP_NAME = "name";
    private Pattern commandNamePattern =
            Pattern.compile("(?i)^(?:\\s)*" +
                            "(?<name>add|modify|delete|display|" +
                            "search|done|chdir|undo|confirm)");
    
    // Regex strings and pattern used for matching an option
    public static final String OPTION_MAKER = "/";
    private static final String OPTION_NO_ARG_FORMATER = "(/%s|/%s)";
    private static final String OPTION_WITH_ARG_FORMATER = "((/%s)|(/%s)\\s+)";
    private static final String DATE_DELIMITER = "\\sor\\s";
    private Pattern optionsPattern;
    // Map option names to their corresponding types
    private Map<String, String> optionTypeMap = new HashMap<>();
    private Map<String, String> shortToLongMap = new HashMap<>();
    
    // Used to store the option strings and their positions within the command
    private ArrayList<String> extractedOptions = new ArrayList<>();
    private ArrayList<Integer> optionStarts = new ArrayList<>();
    private ArrayList<Integer> optionEnds = new ArrayList<>();
    
    // Used to parse date in natural language using Natty library
    public static final Parser dateParser = new Parser();
    
    // Patterns used to parse time
    private static final String GROUP_HOUR = "h";
    private static final String GROUP_MINUTE1 = "m1";
    private static final String GROUP_MINUTE2 = "m2";

    private static final String hourPatStr =
            "(?i)(?<h>\\d{1,2})\\s*([:.,-]|hours?|h)+";
    private static final String minutePatStr =
            "(([:.,-]|hours?|h)+\\s*(?<m1>\\d{1,2}))|" +
            "((?<m2>\\d{1,2})\\s*(minutes?|min|m))";
    private static final Pattern hourPat = Pattern.compile(hourPatStr);
    private static final Pattern minPat = Pattern.compile(minutePatStr);
    
    private static final int MINUTES_IN_HOUR = 60;
    
    private String errorStatus;
    /** Syntax errors messages */
    private static final String ERR_INVALID_TIME_FORMAT =
            "Hours and minutes are not integers";
    private static final String ERR_INVALID_DATE_FORMAT =
            "Date time format is invalid";
    private static final String ERR_INVALID_INT_FORMAT =
           "Argument to an option is not a valid integer"; 
    public static final String ERR_UNSPECIFIED_INDEX =
            "Task's index is not specified";
    
    private static final Logger log = Logger.getLogger(InputParser.class.getName());

    public InputParser() {
        StringBuilder optionPatternBuilder = new StringBuilder();
        String optionPattern;
        
        for (int i = 0; i < Config.OPTIONS_TABLE.length; i++) {
            String[] option = Config.OPTIONS_TABLE[i];
                    
            fillOptionTypeMap(option);
            
            optionPattern = createOptionPattern(option);

            if (i == Config.OPTIONS_TABLE.length - 1) {
                optionPatternBuilder.append(optionPattern);
            } else {
                optionPatternBuilder.append(optionPattern + "|");
            }
        }
        
        optionsPattern = Pattern.compile(optionPatternBuilder.toString());
    }

    /**
     * Create a regex pattern for a single option depending on the option type
     * @param option
     * @return the string representation of the regex pattern
     */
    private String createOptionPattern(String[] option) {
        String optionPattern;
        if (option[Config.OPT_TYPE] != Config.TYPE_NONE) {
            optionPattern = String.format(OPTION_WITH_ARG_FORMATER,
                                          option[Config.OPT_LONG],
                                          option[Config.OPT_SHORT]);

        } else {
            optionPattern = String.format(OPTION_NO_ARG_FORMATER,
                                          option[Config.OPT_LONG],
                                          option[Config.OPT_SHORT]);
        }
        return optionPattern;
    }

    /**
     * Fill pairs of option name and option type into the map data structure
     * Both long option and short option names are considered
     * @param option
     */
    private void fillOptionTypeMap(String[] option) {
        optionTypeMap.put(option[Config.OPT_LONG],
                          option[Config.OPT_TYPE]);
        optionTypeMap.put(option[Config.OPT_SHORT],
                          option[Config.OPT_TYPE]);
        shortToLongMap.put(option[Config.OPT_SHORT],
                           option[Config.OPT_LONG]);
    }

    public Command parseCommand(String command) {
        log.log(Level.FINE, "Parsing: " + command);
        clearPreviousOptions();
        clearErrorStatus();

        if (command == null) {
            return null;
        }
        
        Config.CommandName cmdName = extractCommandName(command);
        Command resultCommand = createCommandFromName(cmdName);

        if (resultCommand == null || errorStatus != null) {
            return resultCommand;
        }
        
        extractOptions(command);
        
        log.log(Level.FINE, "Parsing argument");
        if (!resultCommand.setArg(extractCmdArg(command, resultCommand))) {
            errorStatus = resultCommand.getStatus();
        }
        
        log.log(Level.FINE, "Parsing options");
        parseAllOptions(command, resultCommand);

        return resultCommand;
    }

    /**
     * Create a new Command instance from the given command name
     * @param cmdName
     * @return an instance of a subclass of Command
     *         corresponding to the command name. Returns null if
     *         command name is invalid
     */
    public Command createCommandFromName(CommandName cmdName) {
        switch (cmdName) {
            case ADD:
                return new AddCommand();
            case MODIFY:
                return new ModifyCommand();
            case DELETE:
                return new DeleteCommand();
            case DISPLAY:
                return new DisplayCommand();
            case DONE:
                return new DoneCommand(); 
            case CHDIR:
                return new ChdirCommand();
            case SEARCH:
                return new SearchCommand();
            case UNDO:
                return new UndoCommand();
            default:
                 errorStatus = Logic.ERR_UNSUPPORTED_CMD;
                return null;
        }
    }


    /**
     * Extract the argument part of the command string that is not
     * attached to any option
     * @param command the command string
     * @param cmdEndIndex the end index of the cmd name in the cmd string
     * @return the string containing the command's argumument
     */
    private String extractCmdArg(String command, Command resultCommand) {
        int cmdNameEndIndex = getCmdNameEndIndex(command);
        assert(cmdNameEndIndex >= 0);
        
        int argEndIndex = command.length();

        if (resultCommand.getCommandName() != Config.CommandName.CHDIR &&
            extractedOptions.size() > 0) {
            argEndIndex = optionStarts.get(0);
        }
        
        return command.substring(cmdNameEndIndex, argEndIndex).trim();
    }
    
    /**
     * Return the end index of the command name in the command string
     * @param command
     * @return
     */
    private int getCmdNameEndIndex(String command) {
        Matcher cmdNameMatcher = commandNamePattern.matcher(command);

        if (cmdNameMatcher.find()) {
            return cmdNameMatcher.end(GROUP_NAME);
        } else {
            return 0;
        }
    }

    /**
     * Extract the command name from the command string and store it
     * If there is no command name detected, it is assumed to be
     * the 'add' command
     * @param command
     * @param resultCommand
     * @return the end index of command name in the command string
     */
    private Config.CommandName extractCommandName(String command) {
        Matcher cmdNameMatcher = commandNamePattern.matcher(command);

        if (cmdNameMatcher.find()) {
            return Utility.convertToCommandName(
                                    cmdNameMatcher.group(GROUP_NAME));
        } else {
            return Config.CommandName.ADD;
        }
    }

    /**
     * Extract all options' strings from the command strings and
     * store it in the extractedOptions array and mark its beginning
     * and ending in the string in optionStarts and optionEnds
     * @param command
     */
    private void extractOptions(String command) {
        Matcher optionsMatcher = optionsPattern.matcher(command);

        while (optionsMatcher.find()) {
            String option = removeOptionMarker(optionsMatcher.group());
            String longOpt = shortToLongMap.get(option);
            if (longOpt != null) {
                option = longOpt;
            }
            
            extractedOptions.add(option);

            optionStarts.add(optionsMatcher.start());
            optionEnds.add(optionsMatcher.end());
        }
    }

    private String removeOptionMarker(String group) {
        return group.substring(1).trim();
    }

    /**
     * Parse all arguments of options in extractedOptions and store
     * the result in resultCommand
     * @param command
     * @param resultCommand
     */
    private void parseAllOptions(String command, Command resultCommand) {
        for (int i = 0; i < extractedOptions.size(); i++) {
            parseOption(i, command, resultCommand);
        }
    }

    /**
     * Parse and single option at index i in extractedOptions and store
     * the result in the options component of the Command datastructure
     * @param i
     * @param command
     * @param resultCommand
     */
    private void parseOption(int i, String command, Command resultCommand) {
        Command.Option option = new Command.Option();
        
        String optionName = extractedOptions.get(i);
        String optionArgType = optionTypeMap.get(optionName);

        if (optionArgType.equals(Config.TYPE_STR)) {
            option.strArgument = parseStringArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_INT)) {
            option.intArgument = parseIntArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_DATETIME)) {
            option.dateArgument = parseDateTimeArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_TIME)) { 
            option.timeArgument = parseTimeArg(i, command);
        }
        
        resultCommand.setOption(optionName, option);
    }

    /**
     * Parse the time specified in hours and minutes for the option
     * at index 'i' in extractedOptions
     * @param i
     * @param command
     * @return the integer represented by the argument string
     */
    private Integer parseTimeArg(int i, String command) {
        String argStr = getArgStr(i, command);
        
        Matcher hourMatcher = hourPat.matcher(argStr);
        Matcher minMatcher = minPat.matcher(argStr);
        int h = 0;
        int m = 0;
        
        if (hourMatcher.find()) {
            try {
                h = Integer.parseInt(hourMatcher.group(GROUP_HOUR));
            } catch (NumberFormatException e) {
                errorStatus = ERR_INVALID_TIME_FORMAT;
                return null;
            }
        }
        
        if (minMatcher.find()) {
            try {
                if (minMatcher.group(GROUP_MINUTE1) != null) {
                    m = Integer.parseInt(minMatcher.group(GROUP_MINUTE1));
                } else if (minMatcher.group(GROUP_MINUTE2) != null) {
                    m = Integer.parseInt(minMatcher.group(GROUP_MINUTE2));
                }
            } catch (NumberFormatException e) {
                errorStatus = ERR_INVALID_TIME_FORMAT;
                return null;
            }
        }

        return h * MINUTES_IN_HOUR + m;
    }

    /**
     * Parse the string argument of an option
     * containing date/time data in natural language
     * @param i the index of the option in extractedOptions
     * @param command the full command string
     * @return the Date that the date/time string represents
     */
    private Date[] parseDateTimeArg(int i, String command) {
        String[] dateStrs = getArgStr(i, command).split(DATE_DELIMITER);
        Date[] result = new Date[dateStrs.length];
        
        for (int j = 0; j < dateStrs.length; j++) {
            List<DateGroup> groups = dateParser.parse(dateStrs[j]);
    
            if (groups == null || groups.size() == 0 ||
                groups.get(0).getDates() == null ||
                groups.get(0).getDates().size() == 0) {
                errorStatus = ERR_INVALID_DATE_FORMAT;
                return null;
            }
    
            result[j] = groups.get(0).getDates().get(0);
        }
        
        return result;
    }

    /**
     * Extract the string containing an argument to an option
     * @param i the index of the option in the extractedOptions
     * @param command the full command string
     * @return a string containing the argument to the ith option
     */
    private String getArgStr(int i, String command) {
        int argStart = optionEnds.get(i);
        int argEnd = command.length();

        if (i < extractedOptions.size() - 1) {
           argEnd =  optionStarts.get(i + 1);
        }
        
        return command.substring(argStart, argEnd).trim();
    }

    /**
     * Extract the integer argument to an option
     * @param i the index of the option in the extractedOptions
     * @param command the full command string
     * @return an integer represented by the argument string
     */
    private Integer parseIntArg(int i, String command) {
        String argStr = getArgStr(i, command);
        
        Integer result = null;
        
        try {
            result = Integer.parseInt(argStr);
        } catch (NumberFormatException e) {
            errorStatus = ERR_INVALID_INT_FORMAT;
            return null;
        }

        return result;
    }

    /**
     * Extract the string argument to an option
     * @param i the index of the option in the extractedOptions
     * @param command the full command string
     * @return the argument string or empty string if it is null
     */
    private String parseStringArg(int i, String command) {
        String argStr = getArgStr(i, command);
        
        if (argStr == null) {
            return "";
        }

        return argStr;
    }

    /**
     * Reset all data structure containing data for options
     * to initial state
     */
    private void clearPreviousOptions() {
        extractedOptions.clear();
        optionStarts.clear();
        optionEnds.clear();
    }
    
    public String getErrorStatus() {
        return errorStatus;
    }
    
    private void clearErrorStatus() {
        errorStatus = null;
    }

    /**
     * Test driver for this class
     * @param args
     */
    public static void main(String[] args) {
        InputParser inputParser = new InputParser();
        
        System.out.println(inputParser.parseCommand("modify 10 modify reflection /deadline 1/3/2015 /reminder today"));
        System.out.println(inputParser.parseCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm"));
        System.out.println(inputParser.parseCommand("add AAAI conference /start in 2 days /end tuesday"));
        System.out.println(inputParser.parseCommand("add match midterm /start next friday /end 11/02/15"));
        System.out.println(inputParser.parseCommand("add watch a movie /duration 2 hours 30 minutes"));
        System.out.println(inputParser.parseCommand("submit the report /dl next friday /reminder next thursday"));
        System.out.println(inputParser.parseCommand("undo"));
        System.out.println(inputParser.parseCommand("delete 1"));
        System.out.println(inputParser.parseCommand("display"));
        System.out.println(inputParser.parseCommand("chdir /deadline"));
        System.out.println(inputParser.parseCommand("search school tomorrow"));
        System.out.println(inputParser.parseCommand("done 2"));
        
        /* Testing multiple dates */
        System.out.println(inputParser.parseCommand("add match midterm /start next friday or next thursday /end 11/02/15 or 20/02/2015"));
        System.out.println(inputParser.parseCommand("submit the report /dl tomorrow or the day after tomorrow /reminder next thursday"));
    }
}