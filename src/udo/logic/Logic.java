package udo.logic;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import udo.gui.GUI;
import udo.storage.Storage;
import udo.storage.Task;
import udo.util.Config;
import udo.util.Utility;

public class Logic {
    private GUI gui;
    private Storage storage;
    
    private static final String ERR_FORMAT = "Error: %s";
    private static final String ERR_INVALID_CMD_NAME = "Invalid command";
    private static final String ERR_UNSUPPORTED_CMD = "Unsupported command";
    private static final String ERR_INVALID_CMD_ARG =
            "Invalid command's argument";
    private static final String ERR_UNSPECIFIED_INDEX =
            "A valid task's index is required";
    private static final String ERR_LATE_DEADLINE =
            "Deadline has already passed";
    private static final String ERR_NON_POSITIVE_DUR =
            "Task's duration must be positive";
    
    private static final String STATUS_ADDED = "Task: %s added sucessfully";
    private static final String STATUS_DELETED =
            "Task: %d deleted sucessfully";
    private static final String STATUS_MODIFIED =
            "Task: %d modified sucessfully";
    private static final Integer MAX_STATUS_LENGTH = 40;
    
    private InputParser parser;
    
    private static String status;
    
    public Logic(GUI gui) {
        this.gui = gui;
        parser = new InputParser();
        storage = new Storage();
        /* TODO:
         * Initialize and start up passive thread for reminder
         */
    }
    
    /******************************
     * Code for the active logics *
     ******************************/
    
    /**
     * Execute the command given in the command string 
     * @param command the command string
     */
    public boolean executeCommand(String command) {
        Command parsedCommand = parser.parseCommand(command);
        if (parser.getErrorStatus() != null) {
            // Syntax error
            status = parser.getErrorStatus();
            gui.displayStatus(status);
            return false;
        }
        
        if (isCommandValid(parsedCommand)) {
            switch (parsedCommand.commandName) {
                case ADD:
                    executeAddCommand(parsedCommand);
                    break;
                case MODIFY:
                    executeModifyCommand(parsedCommand);
                    break;
                case DELETE:
                    executeDeleteCommand(parsedCommand);
                    break;
                case DISPLAY:
                    executeDisplayCommand(parsedCommand);
                    break;
                default:
                    status = ERR_UNSUPPORTED_CMD;
                    return false;
            }
            
            gui.displayStatus(status);
            return true;
        } else {
            gui.displayStatus(status);
            return false;
        }
    }

    private void executeDisplayCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDisplaySucessStatus(parsedCommand);
    }

    private String getDisplaySucessStatus(Command parsedCommand) {
        return "";
    }

    private void executeDeleteCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getDeleteSucessStatus(parsedCommand);
        // TODO retrieve and display all tasks
    }

    private String getDeleteSucessStatus(Command parsedCommand) {
        return String.format(STATUS_DELETED, parsedCommand.argIndex);
    }

    private void executeModifyCommand(Command parsedCommand) {
        // TODO fill in default values
        // TODO fill in data structure and call storage apis
        status = getModifySucessStatus(parsedCommand);
        // TODO retrieve and display all tasks
    }

    private String getModifySucessStatus(Command parsedCommand) {
        // TODO Auto-generated method stub
        return String.format(STATUS_MODIFIED, parsedCommand.argIndex);
    }

    private void executeAddCommand(Command parsedCommand) {
        Task task = fillAddedTask(parsedCommand);
        storage.add(task);
        status = getAddSucessStatus(parsedCommand);
        System.out.println(task);
        // TODO retrieve and display all tasks
    }

    private Task fillAddedTask(Command parsedCommand) {
        Task task = new Task();

        task.setTaskType(getTaskType(parsedCommand));
        task.setContent(parsedCommand.argStr);
        
        fillDeadline(task, parsedCommand);
        fillStartDate(task, parsedCommand);
        fillEndDate(task, parsedCommand);
        fillDuration(task, parsedCommand);
        fillReminder(task, parsedCommand);
        fillLabel(task, parsedCommand);
        fillPriority(task, parsedCommand);
        
        fillDefaults(task);

        return task;
    }
    
    /**
     * Fill in the missing fields in a Task datastructure with default values
     * @param task
     */
    private void fillDefaults(Task task) {
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

    private void fillPriority(Task task, Command cmd) {
        Command.Option priority = getOption(cmd, Config.OPT_PRIO);

        if (priority != null) {
            task.setPriority(true);
        } else {
            task.setPriority(false);
        }
    }

    private void fillLabel(Task task, Command cmd) {
        Command.Option label = getOption(cmd, Config.OPT_LABEL);

        if (label != null) {
            task.setLabel(label.strArgument);
        }
    }

    private void fillReminder(Task task, Command cmd) {
        Command.Option reminder = getOption(cmd, Config.OPT_START);

        if (reminder != null) {
            GregorianCalendar reminderCalendar = new GregorianCalendar();
            reminderCalendar.setTime(reminder.dateArgument);
            task.setReminder(reminderCalendar);
        }
    }

    private void fillDuration(Task task, Command cmd) {
        Command.Option duration = getOption(cmd, Config.OPT_DUR);
        
        if (duration != null) {
            task.setDuration(duration.timeArgument);
        }
    }

    private void fillEndDate(Task task, Command cmd) {
        Command.Option end = getOption(cmd, Config.OPT_START);

        if (end != null) {
            GregorianCalendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(end.dateArgument);
            task.setEnd(endCalendar);
        }
    }

    private void fillStartDate(Task task, Command cmd) {
        Command.Option start = getOption(cmd, Config.OPT_START);

        if (start != null) {
            GregorianCalendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(start.dateArgument);
            task.setStart(startCalendar);
        }
    }

    private void fillDeadline(Task task, Command cmd) {
        Command.Option deadline = getOption(cmd, Config.OPT_DEADLINE);

        if (deadline != null) {
            GregorianCalendar deadlineCalendar = new GregorianCalendar();
            deadlineCalendar.setTime(deadline.dateArgument);
            task.setDeadline(deadlineCalendar);
        }
    }
    
    private Command.Option getOption(Command cmd, String[] option) {
        return cmd.options.get(option[Config.OPT_LONG]);
    }

    /**
     * Guess and return a task type from a command
     * @param task
     * @param options
     */
    private Task.TaskType getTaskType(Command parsedCommand) {
        Map<String, Command.Option> options = parsedCommand.options;

        if (options.containsKey(Config.OPT_DEADLINE[Config.OPT_LONG]) ||
            options.containsKey(Config.OPT_DEADLINE[Config.OPT_SHORT])) {
            return Task.TaskType.DEADLINE;
        } else if (options.containsKey(Config.OPT_START[Config.OPT_LONG]) ||
                   options.containsKey(Config.OPT_START[Config.OPT_SHORT]) ||
                   options.containsKey(Config.OPT_END[Config.OPT_LONG]) ||
                   options.containsKey(Config.OPT_END[Config.OPT_SHORT])) {
            return Task.TaskType.EVENT;
        } else {
            return Task.TaskType.TODO;
        }
    }

    private String getAddSucessStatus(Command parsedCommand) {
        String taskContent = parsedCommand.argStr;

        if (taskContent.length() > MAX_STATUS_LENGTH) {
            taskContent = taskContent.substring(0, MAX_STATUS_LENGTH);
            taskContent += "...";
        }

        return String.format(STATUS_ADDED, taskContent);
    }

    /**
     * Check the semantics of a parsed command
     * @param parsedCommand
     * @return the command's correctness
     */
    private boolean isCommandValid(Command parsedCommand) {
        if (parsedCommand == null || parsedCommand.commandName == null) {
            status = formatErrorStr(ERR_INVALID_CMD_NAME);
            return false;
        }
        
        Config.CommandName cmdName = parsedCommand.commandName;
        switch (cmdName) {
            case ADD:
                return isAddCmdValid(parsedCommand); 
            case DELETE:
                return isDeleteCmdValid(parsedCommand);
            case MODIFY:
                return isModifyCmdValid(parsedCommand);
            case DISPLAY:
                return isDisplayCmdValid(parsedCommand);
            default:
                status = formatErrorStr(ERR_UNSUPPORTED_CMD);
                return false;
        }
    }

    private boolean isDisplayCmdValid(Command parsedCommand) {
        return true;
    }

    private boolean isModifyCmdValid(Command parsedCommand) {
        if (parsedCommand.argIndex == null) {
            status = ERR_UNSPECIFIED_INDEX;
            return false;
        }
        return true;
    }

    private boolean isDeleteCmdValid(Command parsedCommand) {
        if (parsedCommand.argIndex == null) {
            status = ERR_UNSPECIFIED_INDEX;
            return false;
        }
        return true;
    }

    private boolean isAddCmdValid(Command parsedCommand) {
        if (parsedCommand.argStr == null) {
            status = ERR_INVALID_CMD_ARG;
            return false;
        }
        
        return isStartBeforeEnd(parsedCommand) &&
               isDurationValid(parsedCommand) &&
               isDeadlineValid(parsedCommand);
    }

    private boolean isDeadlineValid(Command cmd) {
        Command.Option deadline = getOption(cmd, Config.OPT_DEADLINE);
        if (deadline != null) {
            if (deadline.dateArgument.compareTo(new Date()) < 0) {
                status = ERR_LATE_DEADLINE;
                return false;
            }
        }

        return true;
    }

    private boolean isDurationValid(Command cmd) {
        Command.Option duration = getOption(cmd, Config.OPT_DUR);
        if (duration != null) {
            if (duration.timeArgument <= 0) {
                status = ERR_NON_POSITIVE_DUR;
                return false;
            }
        }
        
        return true;
    }

    private boolean isStartBeforeEnd(Command cmd) {
        Command.Option start = getOption(cmd, Config.OPT_DEADLINE);
        Command.Option end = getOption(cmd, Config.OPT_END);

        if (start != null && end != null) {
            if (start.dateArgument.compareTo(end.dateArgument) >= 0) {
                status = ERR_NON_POSITIVE_DUR;
                return false;
            }
        }

        return true;
    }

    private String formatErrorStr(String errorInvalidCmdName) {
        return String.format(ERR_FORMAT, errorInvalidCmdName);
    }
    
    public static void main(String[] argv) {
        Logic logic = new Logic(new GUI());
        logic.executeCommand("go to school /deadline tomorrow");
        logic.executeCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm");
        logic.executeCommand("add AAAI conference /start in 2 days /end tuesday");
        logic.executeCommand("add match midterm /start next friday /end 11/02/15");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("submit the report /dl next friday /reminder next thursday");
    }
}