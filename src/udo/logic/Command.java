package udo.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Command {
    public static class Option {
        public String optionName;

        public String strArgument;
        public int intArgument;
        public GregorianCalendar dateTimeArgument;
    }

    public String commandName;
    public Option[] options;
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    
    public String toString() {
        String str = "Command: " + commandName + "\n";
        str += "Options:\n";
        
        for (Option op : options) {
            str += "  " + op.optionName + "\n";
            if (op.dateTimeArgument != null) {
                str += "    " +
                       DATE_FORMAT.format(op.dateTimeArgument.getTime()) + 
                       "\n";
            }
        }
        
        return str;
    }
}
