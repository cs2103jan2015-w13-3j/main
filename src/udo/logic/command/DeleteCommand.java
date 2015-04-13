package udo.logic.command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

//@author A0093587M
public class DeleteCommand extends Command {
    private static final String STATUS_DELETED =
            "Task: %s deleted successfully";
    private static final String STATUS_ALL_DELETED = "All tasks deleted";

    private Integer[] indices;

    private static final Logger log = Logger.
            getLogger(DeleteCommand.class.getName());

    public DeleteCommand() {
        super();
        setCommandName(Config.CommandName.DELETE);
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

        assert(indices != null && indices.length > 0);

        Task deletedTask = null;
        if (indices.length == 1) {
            deletedTask = storage.query(getStorageIndex(indices[0]));
        }

        boolean isSuccessful = deleteTasks();

        if (isSuccessful) {
            setStatus(getDeleteSucessStatus(deletedTask));
            updateGuiTasks();
            updateReminder();
        }

        updateGUIStatus();
        return isSuccessful;
    }

    /**
     * Delete all tasks with index in the array indices
     * @param isSuccessful
     * @return
     */
    private boolean deleteTasks() {
        List<Integer> storageIndices = mapIndexArray(indices);

        if (storageIndices == null) {
            return false;
        }

        log.log(Level.INFO, "Deleting task " + storageIndices.toString(),
                storageIndices);

        if (!storage.delete(storageIndices)) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            return false;
        }

        return true;
    }

    private String getDeleteSucessStatus(Task task) {
        if (task != null) {
            return String.format(STATUS_DELETED,
                                 Logic.summarizeContent(task.getContent()));
        } else {
            return STATUS_ALL_DELETED;
        }
    }

    @Override
    protected boolean parseArg(String arg) {
        indices = parseIndices(arg);

        if (indices == null) {
            return false;
        }

        return true;
    }
}
