package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

public class DeleteCommand extends Command {
    public static final String STATUS_DELETED = "Task: %s deleted sucessfully";

    public DeleteCommand() {
        super();
        setCommandName(Config.CommandName.DELETE);
    }
    
    @Override
    public boolean isValid() {
        if (argIndex == null) {
            setStatus(Logic.formatErrorStr(Logic.ERR_UNSPECIFIED_INDEX));
            return false;
        }
        return super.isValid() && isIndexValid();
    }

    @Override
    public boolean execute() {
        if (!super.execute()) {
            return false;
        }

        assert(argIndex != null);
        Integer index = getStorageIndex(argIndex);
        assert(index != null);
        
        Task deletedTask = storage.query(index);

        boolean isSuccessful = storage.delete(index);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        } else {
            gui.display(storage.query());
            setStatus(getDeleteSucessStatus(deletedTask));
        }
        
        updateGUIStatus();
        return isSuccessful;
    }

    private String getDeleteSucessStatus(Task task) {
        return String.format(STATUS_DELETED,
                             Logic.summarizeContent(task.getContent()));
    }


    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
