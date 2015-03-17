package udo.logic.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import udo.logic.Logic;
import udo.storage.Task;
import udo.storage.Task.TaskType;
import udo.util.Config;
import udo.util.Config.CommandName;

public class AddCommand extends Command {
    public static final String STATUS_ADDED = "Task: %s added sucessfully";

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
        System.out.println("Adding new task...");
        if (!super.execute()) {
            return false;
        }

        boolean isSuccessful = true;

        List<Task> tasks = fillAddedTask();

        for (Task task : tasks) {
            if (!isStartBeforeEnd(task) || !isDeadlineValid(task)) {
                isSuccessful = false;
                break;
            }
        }

        if (isSuccessful) {
            if (tasks.size() == 1) {
                isSuccessful = storage.add(tasks.get(0));
            } else {
                isSuccessful = storage.add(tasks);
            }

            if(!isSuccessful) {
                setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            } else {
                setStatus(getAddSucessStatus());
                gui.display(storage.query());
            }
        }


        System.out.println(tasks);
        System.out.println("Done adding task!");
        updateGUIStatus();
        return isSuccessful;
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

            fillTaskFromCommand(task, 0);

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
