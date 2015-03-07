package udo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import udo.util.Config;
import udo.util.Utility;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class InputParser {
    // Regex string used to match command name (case insensitive)
    private static final String GROUP_NAME = "name";
    private Pattern commandNamePattern =
            Pattern.compile("(?i)^(?:\\s)*" +
                            "(?<name>add|modify|delete|display|" +
                            "search|done|chdir|undo)");
    
    // Regex used to parse command's argument
    private static final Pattern indexPattern =
            Pattern.compile("^(\\d+)");

    // Regex strings and pattern used for matching an option
    private static final String OPTION_NO_ARG_FORMATER = "(/%s|/%s)";
    private static final String OPTION_WITH_ARG_FORMATER = "((/%s)|(/%s)\\s+)";
    private Pattern optionsPattern;
    // Map option names to their corresponding types
    private Map<String, String> optionTypeMap = new HashMap<>();
    private Map<String, String> shortToLongMap = new HashMap<>();
    
    // Used to store the option strings and their positions within the command
    private ArrayList<String> extractedOptions = new ArrayList<>();
    private ArrayList<Integer> optionStarts = new ArrayList<>();
    private ArrayList<Integer> optionEnds = new ArrayList<>();
    
    // Used to parse date in natural language using Natty library
    private static final Parser dateParser = new Parser();
    
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
    //private static final String ERR_INVALID_CMD_NAME = "Invalid command name";
    private static final String ERR_INVALID_TIME_FORMAT =
            "Hours and minutes are not integers";
    private static final String ERR_INVALID_DATE_FORMAT =
            "Date time format is invalid";
    private static final String ERR_INVALID_INT_FORMAT =
           "Argument to an option is not a valid integer"; 
    private static final String ERR_UNSPECIFIED_INDEX =
            "Task's index is not specified";

    public InputParser() {
        StringBuilder optionPatternBuilder = new StringBuilder();
        String optionPattern;
        
        for (int i = 0; i < Config.OPTIONS_TABLE.length; i++) {
            String[] option = Config.OPTIONS_TABLE[i];
                    
            optionTypeMap.put(option[Config.OPT_LONG],
                              option[Config.OPT_TYPE]);
            optionTypeMap.put(option[Config.OPT_SHORT],
                              option[Config.OPT_TYPE]);
            shortToLongMap.put(option[Config.OPT_SHORT],
                               option[Config.OPT_LONG]);
            
            if (option[Config.OPT_TYPE] != Config.TYPE_NONE) {
                optionPattern = String.format(OPTION_WITH_ARG_FORMATER,
                                              option[Config.OPT_LONG],
                                              option[Config.OPT_SHORT]);

            } else {
                optionPattern = String.format(OPTION_NO_ARG_FORMATER,
                                              option[Config.OPT_LONG],
                                              option[Config.OPT_SHORT]);
            }

            if (i == Config.OPTIONS_TABLE.length - 1) {
                optionPatternBuilder.append(optionPattern);
            } else {
                optionPatternBuilder.append(optionPattern + "|");
            }
        }
        
        optionsPattern = Pattern.compile(optionPatternBuilder.toString());
    }

    public Command parseCommand(String command) {
        clearPreviousOptions();
        clearErrorStatus();

        if (command == null) {
            return null;
        }
        
        Command resultCommand = new Command();

        int cmdEndIndex = extractCommandName(command, resultCommand);
        if (resultCommand.commandName == null || errorStatus != null) {
            return resultCommand;
        }
        
        extractOptions(command);
        
        parseCommandArg(extractCmdArg(command, cmdEndIndex, resultCommand),
                        resultCommand);
        
        parseAllOptions(command, resultCommand);

        //System.out.println(resultCommand);
        return resultCommand;
    }

    /**
     * Parse different components inside a argument string of the command
     * and store the result in argStr and argIndex of a Command datastructure
     * @param extractCmdArg
     * @param resultCommand
     */
    private void parseCommandArg(String extractCmdArg, Command resultCommand) {
        if (extractCmdArg == null || resultCommand == null) {
            return;
        }
        
        switch (resultCommand.commandName) {
            case MODIFY:
            case DELETE:
            case DONE:
                int idxEnd = extractIndex(extractCmdArg, resultCommand);
                resultCommand.argStr = extractCmdArg.substring(idxEnd).trim();
                break;
            default:
                resultCommand.argStr = extractCmdArg;
        }
    }

    /**
     * Extract the task's index from the command's argument and store
     * it in the argIndex component of resultCommand
     * @param extractCmdArg
     * @param resultCommand
     * @return the end position of the index in the argument string
     */
    private int extractIndex(String extractCmdArg, Command resultCommand) {
        Matcher indexMatcher = indexPattern.matcher(extractCmdArg);

        if (indexMatcher.find()) {
            resultCommand.argIndex = Integer.parseInt(indexMatcher.group());
            return indexMatcher.end();
        } else {
            errorStatus = ERR_UNSPECIFIED_INDEX;
            resultCommand.argIndex = null;
            return 0;
        }
    }

    /**
     * Extract the argument part of the command string that is not
     * attached to any option
     * @param command the command string
     * @param cmdEndIndex the end index of the cmd name in the cmd string
     * @return the string containing the command's argumument
     */
    private String extractCmdArg(String command, int cmdNameEndIndex,
                                 Command resultCommand) {
        if (cmdNameEndIndex < 0) {
            return null;
        }
        
        int argEndIndex = command.length();
        if (resultCommand.commandName != Config.CommandName.CHDIR &&
            extractedOptions.size() > 0) {
            argEndIndex = optionStarts.get(0);
        }
        
        return command.substring(cmdNameEndIndex, argEndIndex).trim();
    }

    /**
     * Extract the command name from the command string and store it
     * in the resultCommand data-structure. If there is no command name
     * detected, it is assumed to be the add command
     * @param command
     * @param resultCommand
     * @return the end index of command name in the command string
     */
    private int extractCommandName(String command, Command resultCommand) {
        Matcher cmdNameMatcher = commandNamePattern.matcher(command);

        if (cmdNameMatcher.find()) {
            resultCommand.commandName = Utility.convertToCommandName(
                                            cmdNameMatcher.group(GROUP_NAME));
            return cmdNameMatcher.end(GROUP_NAME);
        } else {
            resultCommand.commandName = Config.CommandName.ADD;
            return 0;
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
        
        resultCommand.options.put(optionName, option);
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
    private Date parseDateTimeArg(int i, String command) {
        String argStr = getArgStr(i, command);
        
        List<DateGroup> groups = dateParser.parse(argStr);
        if (groups == null || groups.size() == 0 ||
            groups.get(0).getDates() == null ||
            groups.get(0).getDates().size() == 0) {
            errorStatus = ERR_INVALID_DATE_FORMAT;
            return null;
        }

        return groups.get(0).getDates().get(0);
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
    }
}