package udo.logic.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.InputParser;
import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;
import udo.util.Config.CommandName;

import com.joestelmach.natty.DateGroup;

//@author A0093587M
public class SearchCommand extends Command {
    private static final String STATUS_SEARCH = "Search results for: %s";
    private static final String STATUS_DISP_FREE = "Displaying free slots";

    private static final Logger log = Logger.getLogger(SearchCommand.class.getName());

    public SearchCommand() {
        super();
        setCommandName(CommandName.SEARCH);
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
               (isContentNonEmpty() || getOption(Config.OPT_FREE) != null);
    }

    @Override
    public boolean execute() {
        if (!super.execute()) {
            return false;
        }

        if (getOption(Config.OPT_FREE) != null) {
            setStatus(STATUS_DISP_FREE);
            updateGuiTasks(storage.findFreeSlots());
            updateGUIStatus();
            return true;
        }

        log.log(Level.FINE, "Searching for " + argStr);

        List<Task> strSearchTasks = storage.search(argStr);

        List<DateGroup> dateGroups = InputParser.dateParser.parse(argStr);
        List<Task> dateSearchTasks = new ArrayList<>();
        GregorianCalendar cal = new GregorianCalendar();

        for (DateGroup group : dateGroups) {
            for (Date date : group.getDates()) {
                cal.setTime(date);
                dateSearchTasks.addAll(storage.query(cal));
            }
        }

        List<Task> result = removeDuplicates(strSearchTasks,
                                             dateSearchTasks);

        setStatus(getSearchStatus());
        updateGuiTasks(result);
        updateGUIStatus();

        log.log(Level.FINER, "Search result: " + result.toString(), result);

        return true;
    }

    private String getSearchStatus() {
        return String.format(STATUS_SEARCH, Logic.summarizeContent(argStr));
    }

    private List<Task> removeDuplicates(List<Task> taskList1,
                                        List<Task> taskList2) {
        List<Task> result = new ArrayList<>();

        HashSet<Task> set = new HashSet<>();
        set.addAll(taskList1);
        set.addAll(taskList2);
        result.addAll(set);

        return result;
    }
}
