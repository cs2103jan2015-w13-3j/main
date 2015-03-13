package udo.logic.command;

import udo.logic.Logic;
import udo.util.Config.CommandName;

public class ChdirCommand extends Command {
    public ChdirCommand() {
        super();
        setCommandName(CommandName.CHDIR);
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

        assert(argStr != null);

        boolean isSuccessful = storage.chDir(argStr);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        }

        updateGUIStatus();
        return isSuccessful;
    }
}
