package udo.logic.command;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

//@author A0093587M
public class ConfirmCommand extends Command {
    private static final String STATUS_CONFIRM = "Task %s confirmed";
    private static final String STATUS_NOT_BLOCKED =
            "selected task is not an unconfirmed task";

    public ConfirmCommand() {
        setCommandName(CommandName.CONFIRM);
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

        Task confirmedTask = storage.query(storageIndex);

        boolean isSuccessful = storage.confirm(storageIndex);
        if (!isSuccessful) {
            setStatus(Logic.formatErrorStr(STATUS_NOT_BLOCKED));
        } else {
            setStatus(getConfirmStatus(confirmedTask));
            updateGuiTasks();
            updateReminder();
        }

        updateGUIStatus();
        return isSuccessful;
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }

    private String getConfirmStatus(Task task) {
        return String.format(STATUS_CONFIRM, task.getContent());
    }
}
