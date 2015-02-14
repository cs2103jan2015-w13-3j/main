package udo.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Command {
    public static class Option {
        public String optionName;

        public String strArgument;
        public Integer intArgument;
        public Date dateArgument;
        public Integer timeArgument;
    }

    public String commandName;
    public Option[] options;
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    
    public String toString() {
        String str = "Command: " + commandName + "\n";
        str += "Options:\n";
        
        for (Option op : options) {
            str += "  " + op.optionName + ": ";
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
