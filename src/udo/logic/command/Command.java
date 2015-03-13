package udo.logic.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import udo.logic.InputParser;
import udo.util.Config;

public abstract class Command {
    public static class Option {
        public String strArgument;
        public Integer intArgument;
        public Date dateArgument;
        public Integer timeArgument;
    }

    private Config.CommandName commandName;
    private String argStr;
    private Integer argIndex; 
    private Map<String, Option> options;
    
    private String status;

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    private static final Pattern indexPattern =
            Pattern.compile("^(\\d+)");
    
    public Command() {
        options = new HashMap<>();
    }
    
    public Config.CommandName getCommandName() {
        return commandName;
    }
    
    public String getArgStr() {
        return argStr;
    }
    
    public Integer getArgIndex() {
        return argIndex;
    }
    
    public Option getOption(String optionName) {
        return options.get(optionName);
    }
    
    public boolean hasOption(String optionName) {
        return options.containsKey(optionName);
    }
    
    public String getStatus() {
        return status;
    }

    public void setCommandName(Config.CommandName commandName) {
        this.commandName = commandName;
    }
    
    public boolean setArg(String argStr) {
        return parseArg(argStr);
    }
    
    public void setOption(String optionName, Option option) {
        options.put(optionName, option);
    }
    
    protected void setStatus(String status) {
        this.status = status;
    }
    
    protected boolean parseArg(String argStr) {
        this.argStr = argStr;
        return true;
    }

    /**
     * Parse an argument which consists of an index and some string content
     * @param extractCmdArg
     * @param resultCommand
     * @return true if the argument format is valid or false otherwise
     */
    protected boolean parseIndexContentPair(String extractCmdArg) {
        if (extractCmdArg == null) {
            return false;
        }

        int idxEnd = extractIndex(extractCmdArg);
        if (idxEnd < 0) {
            return false;
        }

        this.argStr = extractCmdArg.substring(idxEnd).trim();
        return true;
    }

    /**
     * Extract the task's index from the command's argument and store
     * it in the argIndex component of resultCommand
     * @param extractCmdArg
     * @param resultCommand
     * @return the end position of the index in the argument string
     *          or -1 if the index cannot be found
     */
    private int extractIndex(String extractCmdArg) {
        Matcher indexMatcher = indexPattern.matcher(extractCmdArg);

        if (indexMatcher.find()) {
            this.argIndex = Integer.parseInt(indexMatcher.group());
            return indexMatcher.end();
        } else {
            setStatus(InputParser.ERR_UNSPECIFIED_INDEX);
            this.argIndex = null;
            return -1;
        }
    }
    
    public String toString() {
        String str = "Command: " + commandName + "\n";
        if (argIndex != null) {
            str += "Index: " + argIndex + "\n";
        }
        if (argStr != null && !argStr.equalsIgnoreCase("")) {
            str += "Argument: " + argStr + "\n";
        }
        str += "Options:\n";
        
        for (Map.Entry<String, Option> opEntry : options.entrySet()) {
            String opName = opEntry.getKey();
            Option op = opEntry.getValue();

            str += "  " + opName + ": ";
            if (op.dateArgument != null) {
                str += DATE_FORMAT.format(op.dateArgument);
            } else if (op.strArgument != null) {
                str += op.strArgument;
            } else if (op.intArgument != null) {
                str += op.intArgument.toString();
            } else if (op.timeArgument != null) {
                str += op.timeArgument / 60 + "h" +
                       op.timeArgument % 60 + "m";
            }
            
            str += "\n";
        }
        
        return str;
    }
}