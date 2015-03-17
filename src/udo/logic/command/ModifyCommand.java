package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

public class ModifyCommand extends Command {
    public static final String STATUS_MODIFIED =
            "Task: %s modified sucessfully";

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
        System.out.println("Modifying task...");
        if (!super.execute()) {
            return false;
        }

        assert(argIndex != null);

        Integer storageIndex = getStorageIndex(argIndex);
        assert(storageIndex != null);

        Task task = storage.query(storageIndex);

        Task.TaskType newTaskType = getTaskType();
        if (task.getTaskType() == newTaskType) {
            fillTaskFromCommand(task, 0);
        } else {
            task.setTaskType(newTaskType);
            fillTaskFromCommand(task, 0);
            fillDefaults(task);
        }
        
        System.out.println("Modified task: ");
        System.out.println(task);

        boolean isSuccessful = false;

        if (isTaskDatesValid(task)) {
            isSuccessful = storage.modify(storageIndex, task);

            if (!isSuccessful) {
                setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            } else {
                status = getModifySucessStatus(task);
                gui.display(storage.query());
            }
        }
        
        updateGUIStatus();
        return isSuccessful;
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
