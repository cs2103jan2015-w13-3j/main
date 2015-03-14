package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

public class ModifyCommand extends Command {
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

        return super.isValid() && isIndexValid() &&
               isStartBeforeEnd() &&
               isDurationValid() &&
               isDeadlineValid();
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
            fillTaskFromCommand(task);
        } else {
            task.setTaskType(newTaskType);
            fillTaskFromCommand(task);
            fillDefaults(task);
        }
        
        System.out.println("Modified task: ");
        System.out.println(task);

        boolean isSuccessful = storage.modify(storageIndex, task);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        } else {
            status = getModifySucessStatus(task);
            gui.display(storage.query());
        }
        
        updateGUIStatus();
        return isSuccessful;
    }

    private String getModifySucessStatus(Task task) {
        return String.format(Logic.STATUS_MODIFIED,
                             Logic.summarizeContent(task.getContent()));
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
