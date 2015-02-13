package udo.logic;

import java.util.GregorianCalendar;

public class Command {
    public static class Option {
        public String optionName;

        public String strArgument;
        public int intArgument;
        public GregorianCalendar dateTimeArgument;
    }

    public String commandName;
    public Option[] options;
    
    public String toString() {
        String str = "Command: " + commandName + "\n";
        str += "Options:\n";
        
        for (Option op : options) {
            str += "  " + op.optionName + "\n";
        }
        
        return str;
    }
}
