package udo.util;

import java.util.GregorianCalendar;

public class Utility {
    public static Config.CommandName convertToCommandName(String cmdName) {
        cmdName = cmdName.toLowerCase();
        
        if (cmdName.equals(Config.CMD_STR_ADD)) {
            return Config.CommandName.ADD;
        } else if (cmdName.equals(Config.CMD_STR_DELETE)) {
            return Config.CommandName.DELETE;
        } else if (cmdName.equals(Config.CMD_STR_MODIFY)) {
            return Config.CommandName.MODIFY;
        } else if (cmdName.equals(Config.CMD_STR_DISPLAY)) {
            return Config.CommandName.DISPLAY;
        } else if (cmdName.equals(Config.CMD_STR_DONE)){
            return Config.CommandName.DONE;
        } else if (cmdName.equals(Config.CMD_STR_CHDIR)) {
            return Config.CommandName.CHDIR;
        } else {
            return null;
        }
    }

    public static void setToStartOfDay(GregorianCalendar start) {
        start.set(GregorianCalendar.HOUR_OF_DAY, 0);
        start.set(GregorianCalendar.MINUTE, 0);
    }

    /**
     * Set a Gregorian calendar to end of the day i.e 23:59pm
     * @param end
     */
    public static void setToEndOfDay(GregorianCalendar end) {
        end.set(GregorianCalendar.HOUR_OF_DAY, 23);
        end.set(GregorianCalendar.MINUTE, 59);
    }

}
