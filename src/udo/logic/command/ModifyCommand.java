package udo.logic.command;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.Logic;
import udo.storage.Task;
import udo.storage.Task.TaskType;
import udo.util.Config;
import udo.util.Config.CommandName;

//@author A0093587M
public class ModifyCommand extends Command {
    public static final String STATUS_MODIFIED =
            "Task: %s modified successfully";
    private static final Logger log = Logger.getLogger(ModifyCommand.class.getName());

    public ModifyCommand() {
        super();
        setCommandName(CommandName.MODIFY);
    }

    @Override
    public boolean isValid() {
        if (argIndex == null) {
            setStatus(Logic.formatErrorStr(Logic.ERR_UNSPECIFIED_INDEX));
            return false;
        }

        return super.isValid() && isIndexValid() && isDurationValid();
    }

    @Override
    public boolean execute() {
        log.log(Level.INFO, "Modifying task...");
        if (!super.execute()) {
            return false;
        }

        assert(argIndex != null);

        Integer storageIndex = getStorageIndex(argIndex);
        assert(storageIndex != null);

        Task task = storage.query(storageIndex);

        TaskType newTaskType = getTaskType();

        if (task.getTaskType() == newTaskType ||
            isOnlyDurationModified(task.getTaskType(), newTaskType)) {
            fillTaskFromCommand(task, 0);
            fixStartEnd(task);
        } else {
            resetDates(task);
            task.setTaskType(newTaskType);

            fillTaskFromCommand(task, 0);
            fillDefaults(task);
        }

        log.log(Level.INFO, "Modified task: ");
        log.log(Level.INFO, task.toString(), task);

        boolean isSuccessful = false;

        if (isTaskDatesValid(task)) {
            Task clash = findClashedTask(task, storage.query());
            isSuccessful = storage.modify(storageIndex, task);

            if (!isSuccessful) {
                setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            } else {
                if (clash == null || clash.getIndex() == storageIndex) {
                    setStatus(getModifySucessStatus(task));
                } else {
                    setStatus(getClashWarning(task.getContent(),
                                              clash.getContent()));
                }

                updateGuiTasks();
                updateReminder();
            }
        }

        updateGUIStatus();
        return isSuccessful;
    }

    /**
     * Reset a task's datetime data when the task's type is modified
     * @param task
     */
    private void resetDates(Task task) {
        task.setDeadline(null);
        task.setStart(null);
        task.setEnd(null);
        task.setDuration(null);
        task.setReminder(null);
    }

    private boolean isOnlyDurationModified(TaskType taskType,
                                           TaskType newTaskType) {
        return taskType == TaskType.EVENT &&
               newTaskType == TaskType.TODO &&
               getOption(Config.OPT_DUR) != null;
    }

    /**
     * If the start or end time of an event is not specified by the information
     * given to the modify command, this method will attempt to fix it using
     * the task's duration given in the command or in the existing task
     * @param task
     */
    private void fixStartEnd(Task task) {
        if (task.getTaskType() == TaskType.EVENT) {
            Integer duration = task.getDuration();

            if (duration != null) {
                if (getOption(Config.OPT_END) == null) {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(task.getStart().getTime());
                    cal.add(Calendar.MINUTE, duration);

                    task.setEnd(cal);
                } else if (getOption(Config.OPT_START) == null) {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(task.getEnd().getTime());
                    cal.add(-Calendar.MINUTE, duration);

                    task.setStart(cal);
                }
            }
        }
    }

    private String getModifySucessStatus(Task task) {
        return String.format(STATUS_MODIFIED,
                             Logic.summarizeContent(task.getContent()));
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
