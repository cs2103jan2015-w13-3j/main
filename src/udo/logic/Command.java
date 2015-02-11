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
}
