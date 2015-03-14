package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

public class AddCommand extends Command {
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

        return super.isValid() && isContentNonEmpty() && isStartBeforeEnd() &&
               isDurationValid() && isDeadlineValid();
    }

    @Override
    public boolean execute() {
        //System.out.println("Adding new task...");
        if (!super.execute()) {
            return false;
        }

        Task task = fillAddedTask();

        boolean isSuccessful = storage.add(task);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        } else {
            setStatus(getAddSucessStatus());
            gui.display(storage.query());
        }

        //System.out.println(task);
        updateGUIStatus();
        return isSuccessful;
    }


    private String getAddSucessStatus() {
        String taskContent = Logic.summarizeContent(argStr);

        return String.format(Logic.STATUS_ADDED, taskContent);
    }


    private Task fillAddedTask() {
        Task task = new Task();

        task.setTaskType(getTaskType());

        fillTaskFromCommand(task);

        fillDefaults(task);

        return task;
    }
}
