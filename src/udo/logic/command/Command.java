package udo.logic.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import udo.gui.Gui;
import udo.logic.Autocompleter;
import udo.logic.InputParser;
import udo.logic.Logic;
import udo.logic.Reminder;
import udo.storage.Storage;
import udo.storage.Task;
import udo.storage.Task.TaskType;
import udo.util.Config;
import udo.util.Utility;

public abstract class Command {
    public static class Option {
        public String strArgument;
        public Integer intArgument;
        public Date[] dateArgument;
        public Integer timeArgument;
    }

    public static final String ERR_INVALID_RANGE = "invalid index range";
    public static final String INDEX_RANGE_MARKER = "-";

    protected static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
    protected static final Pattern indexPattern =
            Pattern.compile("^(\\d+)");

    private static final String WARNING_CLASH =
            "task %s clashes with task %s";

    protected Config.CommandName commandName;
    protected String argStr;
    protected Integer argIndex;
    protected Map<String, Option> options;

    protected String status;

    protected Gui gui;
    protected Storage storage;
    protected Autocompleter autocompleter;
    protected Reminder reminder;

    private static final Logger log = Logger.getLogger(Command.class.getName());

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

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void setAutocompleter(Autocompleter completer) {
        this.autocompleter = completer;
    }

    protected boolean parseArg(String argStr) {
        this.argStr = argStr;
        return true;
    }

    /**
     * Check if this command has valid data values
     * To be overrided by subclasses
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (commandName == null) {
            setStatus(Logic.formatErrorStr(Logic.ERR_INVALID_CMD_NAME));
            log.log(Level.FINE, getStatus());
            return false;
        }

        return true;
    }

    /**
     * Execute the current command. GUI and storage must be
     * set fore this method to be called without error
     * @return true if valid, false otherwise
     */
    public boolean execute() {
        assert(gui != null);
        assert(storage != null);

        if (!isValid()) {
            log.log(Level.FINE, getStatus());
            updateGUIStatus();
            return false;
        }

        return true;
    }

    /******************************************************/
    /** Helper methods to retrieve various external data **/
    /******************************************************/

    public Option getOption(String[] option) {
        return getOption(option[Config.OPT_LONG]);
    }

    /**
     * Get the lower-level task's index in storage from the task's index
     * as displayed by gui
     * @param argIndex the displayed index
     * @return the storage index or null if it's not found
     */
    public Integer getStorageIndex(Integer argIndex) {
        return Utility.getStorageIndex(argIndex);
    }

    /**
     * Map an array of displayed indices to storage indices
     * @return
     */
    protected List<Integer> mapIndexArray(Integer[] indices) {
        List<Integer> storageIndices = new ArrayList<>();

        for (int i : indices) {
            Integer index = getStorageIndex(i);
            if (index == null) {
                setStatus(Logic.formatErrorStr(Logic.ERR_INVALID_INDEX));
                return null;
            }

            storageIndices.add(index);
        }
        return storageIndices;
    }

    /**
     * Guess and return a task type from a command
     * @param task
     * @param options
     */
    protected Task.TaskType getTaskType() {
        if (hasOption(Config.OPT_DEADLINE[Config.OPT_LONG]) ||
            hasOption(Config.OPT_DEADLINE[Config.OPT_SHORT])) {
            return Task.TaskType.DEADLINE;
        } else if (hasOption(Config.OPT_START[Config.OPT_LONG]) ||
                   hasOption(Config.OPT_START[Config.OPT_SHORT]) ||
                   hasOption(Config.OPT_END[Config.OPT_LONG]) ||
                   hasOption(Config.OPT_END[Config.OPT_SHORT])) {
            return Task.TaskType.EVENT;
        } else {
            return Task.TaskType.TODO;
        }
    }

    /*********************************************************
     * Helper methods for error checking of command sematics *
     * ******************************************************/

    /**
     * Check if a given duration in the command is positive
     * @return
     */
    protected boolean isDurationValid() {
        Option duration = getOption(Config.OPT_DUR);

        if (duration != null) {
            if (duration.timeArgument <= 0) {
                setStatus(Logic.formatErrorStr(Logic.ERR_NON_POSITIVE_DUR));
                return false;
            }
        }

        return true;
    }

    /**
     * Check if dates given in various command's options are valid
     * @param task
     * @return
     */
    protected boolean isTaskDatesValid(Task task) {
        return isStartBeforeEnd(task) && isDeadlineValid(task);
    }


    /**
     * Check if the start date is before the end date
     * @param task
     * @return
     */
    protected boolean isStartBeforeEnd(Task task) {
        if (task.getTaskType() != TaskType.EVENT) {
            return true;
        }

        GregorianCalendar start = task.getStart();
        GregorianCalendar end = task.getEnd();

        if (start != null && end != null) {
            if (start.compareTo(end) >= 0) {
                setStatus(Logic.formatErrorStr(Logic.ERR_NON_POSITIVE_DUR));

                log.fine("Start: " + Utility.calendarToString(task.getStart()));
                log.fine("End: " + Utility.calendarToString(task.getEnd()));
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the content give to the command's argument is non-empty
     * @return
     */
    protected boolean isContentNonEmpty() {
        if (argStr == null || argStr.trim().equals("")) {
            status = Logic.formatErrorStr(Logic.ERR_EMPTY_CONTENT);
            return false;
        }

        return true;
    }

    /**
     * Check if the deadline has not already passed
     * @param task
     * @return
     */
    protected boolean isDeadlineValid(Task task) {
        if (task.getTaskType() != TaskType.DEADLINE) {
            return true;
        }

        GregorianCalendar deadline = task.getDeadline();

        if (deadline != null) {
            if (deadline.getTime().compareTo(new Date()) < 0) {
                setStatus(Logic.formatErrorStr(Logic.ERR_LATE_DEADLINE));
                return false;
            }
        }

        return true;
    }

    /**
     * Find an event that clashed with an input event
     * @param task
     * @return a clashed with the input event argument or null if the input
     *         Task is not an event, or there is not clashed event
     */
    protected Task findClashedTask(Task task, List<Task> existingTasks) {
        assert(task != null);
        assert(existingTasks != null);

        if (task.getTaskType() != TaskType.EVENT) {
            return null;
        }

        for (Task t : existingTasks) {
            if (t.getTaskType() == TaskType.EVENT) {
                if (!(task.getStart().compareTo(t.getEnd()) >= 0 ||
                      task.getEnd().compareTo(t.getStart()) <= 0)) {
                    return t;
                }
            }
        }

        return null;
    }

    /**
     * Check if the specified index corresponds to a valid storage index
     * @param parsedCommand
     */
    protected boolean isIndexValid() {
        if (getStorageIndex(argIndex) == null) {
            setStatus(Logic.formatErrorStr(Logic.ERR_INVALID_INDEX));
            return false;
        }

        return true;
    }

    /***************************************************************
     * Helper methods for filling in fields in Task data structure *
     * *************************************************************/

    /**
     * Extract data from the parsed command and fill in the task
     * data structure with retrieved information
     * @param parsedCommand
     * @param task
     */
    protected void fillTaskFromCommand(Task task, int dateIndex) {
        fillContent(task);
        fillDeadline(task, dateIndex);
        fillStartDate(task, dateIndex);
        fillEndDate(task, dateIndex);
        fillDuration(task);
        fillReminder(task);
        fillLabel(task);
        fillPriority(task);
    }

    private void fillContent(Task task) {
        if (argStr != null && !argStr.trim().equals("")) {
            task.setContent(argStr);
        }
    }

    /**
     * Fill in the missing fields in a Task datastructure with default values
     * @param task
     */
    protected void fillDefaults(Task task) {
        assert(task != null);

        fillStartEndDefaults(task);

        fillReminderDefault(task);
    }

    /**
     * Fill in the default reminder for the deadline or event task
     * Note that an event task must have a valid start date
     * @param task
     */
    private void fillReminderDefault(Task task) {
        assert(task != null);

        Task.TaskType taskType = task.getTaskType();

        if (task.getReminder() == null) {
            if (taskType == Task.TaskType.DEADLINE) {
                assert(task.getDeadline() != null);

                GregorianCalendar reminder = new GregorianCalendar();
                reminder.setTime(task.getDeadline().getTime());
                reminder.add(GregorianCalendar.DAY_OF_MONTH, -1);

                task.setReminder(reminder);
            } else if (taskType == Task.TaskType.EVENT) {
                assert(task.getStart() != null);

                GregorianCalendar reminder = new GregorianCalendar();
                reminder.setTime(task.getStart().getTime());
                reminder.add(GregorianCalendar.DAY_OF_MONTH, -1);

                task.setReminder(reminder);
            }
        }
    }

    /**
     * Fill in the start or end of an event if one of the fields is missing
     * @param task
     * @param taskType
     */
    private void fillStartEndDefaults(Task task) {
        assert(task != null);

        if (task.getTaskType() == Task.TaskType.EVENT) {
            if (task.getEnd() == null) {
                GregorianCalendar end = new GregorianCalendar();
                end.setTime(task.getStart().getTime());

                if (task.getDuration() != null) {
                    end.add(GregorianCalendar.MINUTE, task.getDuration());
                } else {
                    Utility.setToEndOfDay(end);
                }

                task.setEnd(end);
            } else if (task.getStart() == null) {
                GregorianCalendar start = new GregorianCalendar();
                start.setTime(task.getEnd().getTime());

                if (task.getDuration() != null) {
                    start.add(GregorianCalendar.MINUTE, -task.getDuration());
                } else {
                    Utility.setToStartOfDay(start);
                }

                task.setStart(start);
            }
        }
    }

    private void fillPriority(Task task) {
        Option priority = getOption(Config.OPT_PRIO);

        if (priority != null) {
            task.setPriority(true);
        } else {
            task.setPriority(false);
        }
    }

    private void fillLabel(Task task) {
        Option label = getOption(Config.OPT_LABEL);

        if (label != null) {
            task.setLabel(label.strArgument);
        }
    }

    private void fillReminder(Task task) {
        Option reminder = getOption(Config.OPT_REMINDER);

        if (reminder != null) {
            GregorianCalendar reminderCalendar = new GregorianCalendar();
            reminderCalendar.setTime(reminder.dateArgument[0]);
            task.setReminder(reminderCalendar);
        }
    }

    private void fillDuration(Task task) {
        Option duration = getOption(Config.OPT_DUR);

        if (duration != null) {
            task.setDuration(duration.timeArgument);
        }
    }

    private void fillEndDate(Task task, int dateIndex) {
        Option end = getOption(Config.OPT_END);

        if (end != null && dateIndex < end.dateArgument.length) {
            GregorianCalendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(end.dateArgument[dateIndex]);
            task.setEnd(endCalendar);
        }
    }

    private void fillStartDate(Task task, int dateIndex) {
        Option start = getOption(Config.OPT_START);

        if (start != null && dateIndex < start.dateArgument.length) {
            GregorianCalendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(start.dateArgument[dateIndex]);
            task.setStart(startCalendar);
        }
    }

    private void fillDeadline(Task task, int dateIndex) {
        Option deadline = getOption(Config.OPT_DEADLINE);

        if (deadline != null && dateIndex < deadline.dateArgument.length) {
            GregorianCalendar deadlineCalendar = new GregorianCalendar();
            deadlineCalendar.setTime(deadline.dateArgument[dateIndex]);
            task.setDeadline(deadlineCalendar);
        }
    }

    protected String getClashWarning(String task, String clashedTask) {
        return Logic.formatWarningStr(String.format(WARNING_CLASH,
                                                    task, clashedTask));
    }

    public void updateGUIStatus() {
        gui.displayStatus(getStatus());
    }

    public void updateReminder() {
        reminder.updateTasks(storage.query());
    }

    /***********************************************
     * Helper methods for command specific parsing *
     ***********************************************/

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

    /**
     * Parse multiple indices or index range from command argument string
     * @param arg
     * @return
     */
    protected Integer[] parseIndices(String arg) {
        Set<Integer> indices = new HashSet<>();
        String[] indicesStr = arg.split("\\s*(\\s|,)\\s*");

        for (String s : indicesStr) {
            if (s.contains(INDEX_RANGE_MARKER)) {
                if (!getIndexRange(s, indices)) {
                    return null;
                }
            } else {
                try {
                    indices.add(Integer.parseInt(s));
                } catch (NumberFormatException e){
                    setStatus(InputParser.ERR_INVALID_INT_FORMAT);
                    log.log(Level.FINE, getStatus());
                    return null;
                }
            }
        }

        return indices.toArray(new Integer[indices.size()]);
    }

    /**
     * Add all indices specified by the range syntax 'from-to' to the set indices
     * @param s the range string
     * @param indices
     * @return true if the range is valid or false otherwise
     */
    protected boolean getIndexRange(String s, Set<Integer> indices) {
        String[] range = s.split(INDEX_RANGE_MARKER);

        if (range.length != 2) {
            setStatus(ERR_INVALID_RANGE);
            return false;
        }

        try {
            int from = Integer.parseInt(range[0]);
            int to = Integer.parseInt(range[1]);

            for (int i = from; i <= to; i++) {
                indices.add(i);
            }
        } catch (NumberFormatException e) {
            setStatus(InputParser.ERR_INVALID_INT_FORMAT);
            log.log(Level.FINE, getStatus());
            return false;
        }

        return true;
    }

    @Override
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
            if (op.dateArgument != null && op.dateArgument.length > 0) {
                for (int i = 0; i < op.dateArgument.length - 1; i++) {
                    str += DATE_FORMAT.format(op.dateArgument[i]) + " or ";
                }
                str += DATE_FORMAT.format(
                            op.dateArgument[op.dateArgument.length - 1]);
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
