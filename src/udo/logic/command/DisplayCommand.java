package udo.logic.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import udo.storage.Task;
import udo.util.Config;
import udo.util.Config.CommandName;

//@author A0093587M
public class DisplayCommand extends Command {
    private static final String STATUS_DISP_ALL = "Displaying all tasks";
    private static final String STATUS_DISP_IMP =
            "Displaying important tasks";
    private static final String STATUS_DISP_DONE = "Displaying done tasks";
    private static final String STATUS_DISP_OVERDUE =
            "Displaying overdue tasks";

    public DisplayCommand() {
        super();
        setCommandName(CommandName.DISPLAY);
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

        if (getOption(Config.OPT_PRIO) != null) {
            setStatus(STATUS_DISP_IMP);
            updateGuiTasks(storage.query(true));
        } else if (getOption(Config.OPT_DONE) != null) {
            setStatus(STATUS_DISP_DONE);
            updateGuiTasks(storage.getDone());
        } else if (getOption(Config.OPT_OVERDUE) != null) {
            setStatus(STATUS_DISP_OVERDUE);
            updateGuiTasks(getOverdueTasks());
        } else {
            setStatus(STATUS_DISP_ALL);
            updateGuiTasks();
        }

        updateGUIStatus();
        return true;
    }

    private List<Task> getOverdueTasks() {
        List<Task> tasks = storage.query();
        List<Task> result = new ArrayList<>();

        Calendar now = new GregorianCalendar();

        for (Task t : tasks) {
            if (t.getTaskType() == Task.TaskType.DEADLINE) {
                Calendar deadline = t.getDeadline();

                if (deadline != null && deadline.before(now)) {
                    result.add(t);
                }
            }
        }

        return result;
    }
}
