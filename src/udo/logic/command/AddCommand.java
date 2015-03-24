package udo.logic.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;

import udo.logic.Logic;
import udo.storage.Task;
import udo.storage.Task.TaskType;
import udo.util.Config;
import udo.util.Config.CommandName;

public class AddCommand extends Command {
    public static final String STATUS_ADDED = "Task: %s added sucessfully";

    private static final Logger log = Logger.getLogger(AddCommand.class.getName());

    public AddCommand() {
        super();
        setCommandName(CommandName.ADD);
    }

    @Override
    public boolean isValid() {
        if (argStr == null) {
            setStatus(Logic.ERR_INVALID_CMD_ARG);
            return false;
        }

        return super.isValid() && isContentNonEmpty() && isDurationValid();
    }

    @Override
    public boolean execute() {
        log.log(Level.INFO, "Adding new task...");
        if (!super.execute()) {
            return false;
        }

        boolean isSuccessful = true;

        List<Task> tasks = fillAddedTask();

        isSuccessful = isTaskValid(tasks);

        if (isSuccessful) {
            Task clash = findClashedTask(tasks, storage.query());

            if (tasks.size() == 1) {
                isSuccessful = storage.add(tasks.get(0));
            } else {
                isSuccessful = storage.add(tasks);
            }

            if (!isSuccessful) {
                setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            } else {
                if (clash == null) {
                    setStatus(getAddSucessStatus());
                } else {
                    setStatus(getClashWarning(getArgStr(),
                                              clash.getContent()));
                }
                gui.display(storage.query());
            }
        }


        log.log(Level.FINER, tasks.toString(), tasks);
        log.log(Level.INFO, "Done adding task!");

        updateGUIStatus();
        return isSuccessful;
    }

    /**
     * @param isSuccessful
     * @param tasks
     * @return
     */
    private boolean isTaskValid(List<Task> tasks) {
        for (Task task : tasks) {
            if (!isTaskDatesValid(task)) {
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
    protected Task findClashedTask(List<Task> tasks, List<Task> existingTasks) {
        assert(tasks != null);
        assert(existingTasks != null);

        Task clash = null;

        for (Task task : tasks) {
            clash = findClashedTask(task, existingTasks);

            if (clash != null) {
                return clash;
            }
        }

        return null;
    }


    private String getAddSucessStatus() {
        String taskContent = Logic.summarizeContent(argStr);

        return String.format(STATUS_ADDED, taskContent);
    }


    /**
     * Fill a task or a test of tasks (in case the command consists
     * of multiple unconfirmed deadline or event dates)
     * @return a list of filled tasks
     */
    private List<Task> fillAddedTask() {
        TaskType taskType = getTaskType();
        List<Task> tasks = new ArrayList<>();

        int numOfTasks = getNumberOfTasks(taskType);

        for (int i = 0; i < numOfTasks; i++) {
            Task task = new Task();
            task.setTaskType(taskType);

            fillTaskFromCommand(task, i);

            fillDefaults(task);

            tasks.add(task);
        }

        return tasks;
    }

    private int getNumberOfTasks(TaskType taskType) {
        switch (taskType) {
            case DEADLINE:
                Date[] deadlines = getOption(Config.OPT_DEADLINE).dateArgument;
                return deadlines.length;
            case EVENT:
                int result = 0;

                Option startsOpt = getOption(Config.OPT_START);
                if (startsOpt != null) {
                    result = startsOpt.dateArgument.length;
                }

                Option endsOpt = getOption(Config.OPT_END);
                if (endsOpt != null) {
                    result = Math.max(result, endsOpt.dateArgument.length);
                }

                return result;
            case TODO:
                return 1;
        }

        return 0;
    }
}
