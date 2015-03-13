package udo.logic.command;

import udo.logic.Logic;
import udo.util.Config.CommandName;

public class DoneCommand extends Command {
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

        boolean isSuccessful = storage.markDone(storageIndex);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        }

        return false;
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
