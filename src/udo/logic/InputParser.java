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
            Pattern.compile("(?i)(?: \t\r\n)*" +
                            "(add)|(modify)|(delete)|(display)|(search)" +
                            "(?: \t\r\n)*");
    
    // Regex strings and pattern used for matching an option
    private static final String OPTION_FORMATER = "-(%s)|-(%s)";
    private Pattern optionsPattern;
    // Map option names to their corresponding types
    private HashMap<String, String> optionTypeMap = new HashMap<>();
    
    // Used to store the option strings and their positions within the command
    private ArrayList<String> extractedOptions = new ArrayList<>();
    private ArrayList<Integer> optionStarts = new ArrayList<>();
    private ArrayList<Integer> optionEnds = new ArrayList<>();

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
            extractedOptions.add(optionsMatcher.group());
            optionStarts.add(optionsMatcher.start());
            optionEnds.add(optionsMatcher.end());
        }
        
        parseAllOptions(command, resultCommand);

        System.out.println(resultCommand);
        return resultCommand;
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
        
        switch(optionArgType) {
            case Config.TYPE_STR:
                option.strArgument = parseStringArg(i, command);
                break;
            case Config.TYPE_INT:
                option.intArgument = parseIntArg(i, command);
            case Config.TYPE_DATETIME:
                option.dateTimeArgument = parseDateTimeArg(i, command);
            case Config.TYPE_TIME:
                option.dateTimeArgument = parseTimeArg(i, command);
            default:
                System.out.println("Parser error: unknown option type");
                System.exit(1);
        }
    }

    private GregorianCalendar parseTimeArg(int i, String command) {
        return null;
    }

    private GregorianCalendar parseDateTimeArg(int i, String command) {
        return null;
    }

    private int parseIntArg(int i, String command) {
        return 0;
    }

    private String parseStringArg(int i, String command) {
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
        
        inputParser.parseCommand("add -event go to school");
    }
}