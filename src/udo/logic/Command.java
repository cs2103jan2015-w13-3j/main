package udo.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Command {
    public static class Option {
        public String strArgument;
        public Integer intArgument;
        public Date dateArgument;
        public Integer timeArgument;
    }

    public String commandName;
    public String commandArg;
    public Map<String, Option> options;

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    
    public Command() {
        options = new HashMap<>();
    }
    
    public String toString() {
        String str = "Command: " + commandName + "\n";
        if (!commandArg.equalsIgnoreCase("")) {
            str += "Argument: " + commandArg + "\n";
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
