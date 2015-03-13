package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

public class DeleteCommand extends Command {
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

        if (!storage.delete(index)) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            return false;
        }

        gui.display(storage.query());
        status = getDeleteSucessStatus(deletedTask);
        
        return true;
    }

    private String getDeleteSucessStatus(Task task) {
        return String.format(Logic.STATUS_DELETED,
                             Logic.summarizeContent(task.getContent()));
    }


    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
