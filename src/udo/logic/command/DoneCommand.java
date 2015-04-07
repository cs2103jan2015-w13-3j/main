package udo.logic.command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config.CommandName;

//@author A0093587M
public class DoneCommand extends Command {
    private static final String STATUS_DONE = "Task %s is done";
    private static final String STATUS_ALL_DONE = "All tasks done";

    private Integer[] indices;

    private static final Logger log = Logger.
            getLogger(DoneCommand.class.getName());

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

        assert(indices != null && indices.length > 0);

        Task doneTask = null;
        if (indices.length == 1) {
            doneTask = storage.query(getStorageIndex(indices[0]));
        }

        boolean isSuccessful = markTasksDone();

        if (isSuccessful) {
            setStatus(getDoneStatus(doneTask));
            updateGuiTasks();
            updateReminder();
        }

        updateGUIStatus();
        return isSuccessful;
    }

    @Override
    protected boolean parseArg(String arg) {
        indices = parseIndices(arg);

        if (indices == null) {
            return false;
        }

        return true;
    }

    /**
     * Delete all tasks with index in the array indices
     * @param isSuccessful
     * @return
     */
    private boolean markTasksDone() {
        List<Integer> storageIndices = mapIndexArray(indices);

        if (storageIndices == null) {
            return false;
        }

        log.log(Level.INFO, "Done task " + storageIndices.toString(),
                storageIndices);

        if (!storage.markDone(storageIndices)) {
            setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
            return false;
        }

        return true;
    }

    private String getDoneStatus(Task task) {
        if (task != null) {
            return String.format(STATUS_DONE,
                                 Logic.summarizeContent(task.getContent()));
        } else {
            return STATUS_ALL_DONE;
        }
    }
}
