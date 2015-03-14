package udo.logic.command;

import udo.logic.Logic;

public class ConfirmCommand extends Command {
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

        // TODO: call the correct confirm api from storage
        boolean isSuccessful = storage.markDone(storageIndex);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
        }

        return isSuccessful;
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
