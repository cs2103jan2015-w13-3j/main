package udo.logic.command;

import udo.logic.Logic;
import udo.util.Config.CommandName;

//@author A0093587M
public class ChdirCommand extends Command {
    private static final String STATUS_FILE_PATH =
            "Tasks file is at: %s";

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

        if (argStr == null || argStr.equals("")) {
            assert(storage.getPath() != null);
            setStatus(String.format(STATUS_FILE_PATH, storage.getPath()));

            updateGUIStatus();
            return true;
        }

        boolean isSuccessful = storage.chDir(argStr);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        }

        updateGUIStatus();
        return isSuccessful;
    }
}
