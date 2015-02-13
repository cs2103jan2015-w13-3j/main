package udo.logic;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import udo.util.Config;

public class InputParser {
    // Regex string used to match command name (case insensitive)
    private Pattern commandNamePattern =
            Pattern.compile("(?i)(?:\\s)*" +
                            "(add)|(modify)|(delete)|(display)|(search)" +
                            "(?:\\s)*");
    
    // Regex strings and pattern used for matching an option
    private static final String OPTION_FORMATER = "-%s|-%s";
    private Pattern optionsPattern;
    // Map option names to their corresponding types
    private HashMap<String, String> optionTypeMap = new HashMap<>();
    
    // Used to store the option strings and their positions within the command
    private ArrayList<String> extractedOptions = new ArrayList<>();
    private ArrayList<Integer> optionStarts = new ArrayList<>();
    private ArrayList<Integer> optionEnds = new ArrayList<>();
    
    // Pattern used to match date
    String monthNames = "(january|jan|february|feb|march|mar|april|apr|" +
                        "june|july|august|aug|september|sept|october|oct|" +
                        "december|dec)";
    String separator = "[-., \\/]+";
    // Pattern for dd/mm/yy or dd/mm/yyyy
    Pattern dmyPat1 = Pattern.compile("(\\d{1,2})" + separator + "(\\d{1,2})" +
                                      separator + "(\\d{2}|\\d{4})",
                                      Pattern.CASE_INSENSITIVE);
    // Pattern for dd mon, year or dd month, year
    Pattern dmyPat2 = Pattern.compile("(\\d{1,2})" + separator + monthNames +
                                      separator + "(\\d{2}|\\d{4})",
                                      Pattern.CASE_INSENSITIVE);
    // Pattern for monday or next monday etc.
    Pattern daysOfWeekPat = Pattern.compile(
                            "(next)?\\s+" +
                            "(monday|mon|tuesday|tue|wednesday|wed|" +
                            "thursday|thu|friday|fri|saturday|sat|sunday|sun)",
                            Pattern.CASE_INSENSITIVE);
    // Pattern for "in 5 days" 
    Pattern inDaysPat = Pattern.compile("in\\s+day(?:s?)",
                                        Pattern.CASE_INSENSITIVE);
    Pattern daysPat = Pattern.compile("(today|tomorrow)",
                                      Pattern.CASE_INSENSITIVE);

    public InputParser() {
        StringBuilder optionPatternBuilder = new StringBuilder();
        String optionPattern;
        
        for (int i = 0; i < Config.OPTIONS_TABLE.length; i++) {
            String[] option = Config.OPTIONS_TABLE[i];
                    
            optionTypeMap.put(option[Config.OPT_LONG],
                              option[Config.OPT_TYPE]);
            optionTypeMap.put(option[Config.OPT_SHORT],
                              option[Config.OPT_TYPE]);
            
            optionPattern = (String.format(OPTION_FORMATER,
                                           option[Config.OPT_LONG],
                                           option[Config.OPT_LONG],
                                           option[Config.OPT_SHORT],
                                           option[Config.OPT_SHORT]));

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
        Command resultCommand = new Command();
        
        Matcher cmdNameMatcher = commandNamePattern.matcher(command);

        if (cmdNameMatcher.find()) {
            resultCommand.commandName = cmdNameMatcher.group();
        } else {
            System.out.println("No valid command name found");
        }
        
        Matcher optionsMatcher = optionsPattern.matcher(command);

        while (optionsMatcher.find()) {
            extractedOptions.add(removeOptionMarker(optionsMatcher.group()));

            optionStarts.add(optionsMatcher.start());
            optionEnds.add(optionsMatcher.end());
        }
        
        parseAllOptions(command, resultCommand);

        System.out.println(resultCommand);
        return resultCommand;
    }

    private String removeOptionMarker(String group) {
        return group.substring(1);
    }

    private void parseAllOptions(String command, Command resultCommand) {
        resultCommand.options = new Command.Option[extractedOptions.size()];

        for (int i = 0; i < extractedOptions.size(); i++) {
            parseOption(i, command, resultCommand);
        }
    }

    private void parseOption(int i, String command, Command resultCommand) {
        resultCommand.options[i] = new Command.Option();
        Command.Option option = resultCommand.options[i];
        
        option.optionName = extractedOptions.get(i);
        
        String optionArgType = optionTypeMap.get(option.optionName);

        if (optionArgType.equals(Config.TYPE_STR)) {
            option.strArgument = parseStringArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_INT)) {
            option.intArgument = parseIntArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_DATETIME)) {
            option.dateTimeArgument = parseDateTimeArg(i, command);
        } else if (optionArgType.equals(Config.TYPE_TIME)) { 
            option.dateTimeArgument = parseTimeArg(i, command);
        } else {
            System.out.println("Parser error: unknown option type");
            System.exit(1);
        }
    }

    private GregorianCalendar parseTimeArg(int i, String command) {
        String argStr = getArgStr(i, command);
        System.out.println(argStr);
        return null;
    }

    private GregorianCalendar parseDateTimeArg(int i, String command) {
        String argStr = getArgStr(i, command);

        return null;
    }

    private String getArgStr(int i, String command) {
        int argStart = optionEnds.get(i);
        int argEnd = command.length();

        if (i < extractedOptions.size() - 1) {
           argEnd =  optionStarts.get(i + 1);
        }
        
        return command.substring(argStart, argEnd).trim();
    }

    private int parseIntArg(int i, String command) {
        String argStr = getArgStr(i, command);
        System.out.println(argStr);
        return 0;
    }

    private String parseStringArg(int i, String command) {
        String argStr = getArgStr(i, command);
        System.out.println(argStr);
        return null;
    }

    private void clearPreviousOptions() {
        extractedOptions.clear();
        optionStarts.clear();
        optionEnds.clear();
    }

    /**
     * Test driver for this class
     * @param args
     */
    public static void main(String[] args) {
        InputParser inputParser = new InputParser();
        
        inputParser.parseCommand("modify -deadline submit reflection -end 1/3/2015");
        inputParser.parseCommand("add -event go to school -start tomorrow 2pm -end tomorrow 4pm");
    }
}