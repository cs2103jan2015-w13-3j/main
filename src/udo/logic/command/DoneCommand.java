package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

public class DoneCommand extends Command {
    private static final String STATUS_DONE = "Task %s is done";
    
    public DoneCommand() {
        super();
        setCommandName(CommandName.DONE);
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    public boolean execute() {
        if (!super.execute()) {
            return false;
        }

        assert(argIndex != null);
        Integer storageIndex = getStorageIndex(argIndex);
        assert(storageIndex != null);
        
        Task doneTask = storage.query(storageIndex);

        boolean isSuccessful = storage.markDone(storageIndex);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        } else {
            gui.displayStatus(getDoneStatus(doneTask));
            gui.display(storage.query());
        }

        updateGUIStatus();
        return isSuccessful;
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
    
    private String getDoneStatus(Task task) {
        return String.format(STATUS_DONE, task.getContent());
    }
}
