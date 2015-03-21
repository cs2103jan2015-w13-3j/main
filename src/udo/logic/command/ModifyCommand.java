package udo.logic.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

public class ModifyCommand extends Command {
    public static final String STATUS_MODIFIED =
            "Task: %s modified sucessfully";
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

        Task.TaskType newTaskType = getTaskType();
        if (task.getTaskType() == newTaskType) {
            fillTaskFromCommand(task, 0);
        } else {
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
                if (clash == null) {
                    setStatus(getModifySucessStatus(task));
                } else {
                    setStatus(getClashWarning(task.getContent(),
                                              clash.getContent()));
                }
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
