package udo.logic.command;

import udo.logic.Logic;
import udo.util.Config.CommandName;

public class UndoCommand extends Command {
    public UndoCommand() {
        super();
        setCommandName(CommandName.UNDO);
    }

    @Override
    public boolean execute() {
        if (!super.execute()) {
            return false;
        }

        boolean isSuccessful = storage.undo();
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        }
        return isSuccessful;
    }
    
    @Override
    public boolean isValid() {
        return super.isValid();
    }

}