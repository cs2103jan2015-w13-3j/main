package udo.logic.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.logic.InputParser;
import udo.logic.Logic;
import udo.storage.Task;
import udo.storage.Task.TaskType;
import udo.util.Config;
import udo.util.Config.CommandName;
import udo.util.Utility;

import com.joestelmach.natty.DateGroup;

//@author A0093587M
public class SearchCommand extends Command {
    private static final String STATUS_SEARCH = "Search results for: %s";
    private static final String STATUS_DISP_FREE =
            "Search results for free slots";

    private static final Logger log = Logger.
            getLogger(SearchCommand.class.getName());

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
        log.log(Level.FINE, "Searching for " + argStr);

        if (!super.execute()) {
            return false;
        }

        if (getOption(Config.OPT_FREE) != null) {
            return searchFreeSlots();
        }

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

    /**
     * Search and display free slots given start, end period and duration
     * @return
     */
    private boolean searchFreeSlots() {
        log.fine("Searching for free slots...");

        GregorianCalendar start = getDateOpt(Config.OPT_START);
        GregorianCalendar end = getDateOpt(Config.OPT_END);
        Integer duration = null;

        if (getOption(Config.OPT_DUR) != null) {
            duration = getOption(Config.OPT_DUR).timeArgument;
        }

        if (!isStartBeforeEnd(start, end)) {
            updateGUIStatus();
            return false;
        }
        if (duration != null && duration <= 0) {
            setStatus(Logic.ERR_NON_POSITIVE_DUR);
            updateGUIStatus();
            return false;
        }

        List<Task> results = new ArrayList<>();
        List<Task> freeSlots = storage.findFreeSlots();
        List<Task> allTasks = storage.query();

        addFirstSlot(results, start, end, allTasks, duration);

        generateViewableSlots(results, freeSlots, start, end, duration);
        logic.updateGuiFreeSlots(results);

        addLastSlot(results, start, end, allTasks, duration);

        System.out.println(results);

        setStatus(STATUS_DISP_FREE);
        updateGUIStatus();
        return true;
    }

    /**
     * Shrink the initial free time slots obtained from lower level storage
     * to fit into the start, end and duration option by finding the
     * overlapping period between the slot and (start to end) and check with
     * duration. If the shrunken slot extend over more than 1 day,
     * it's divided into 2 separate slots
     * @param results
     * @param freeSlots
     * @param start
     * @param end
     * @param duration
     */
    private void generateViewableSlots(List<Task> results,
                                       List<Task> freeSlots,
                                       GregorianCalendar start,
                                       GregorianCalendar end, Integer duration) {
        for (Task slot : freeSlots) {
            GregorianCalendar slotStart = slot.getStart();
            GregorianCalendar slotEnd = slot.getEnd();

            if (start != null && slotStart.before(start)) {
                slotStart = start;
            }
            if (end != null && slotEnd.after(end)) {
                slotEnd = end;
            }

            if (slotStart.before(slotEnd) &&
                (duration == null ||
                 Utility.findDiffMinutes(slotStart, slotEnd) > duration)) {
                if (onSameDay(slotStart, slotEnd)) {
                    Task t = new Task();
                    setFreeFromTo(t, slotStart, slotEnd);

                    results.add(t);
                } else {
                    Task t1 = new Task();
                    setFreeAfter(t1, slotStart);

                    Task t2 = new Task();
                    setFreeBefore(t2, slotEnd);

                    results.add(t1);
                    results.add(t2);
                }
            }
        }
    }

    /**
     * Set a free slot as a free before [date/time] slot
     * @param task the free slot
     * @param date free-before date
     */
    private void setFreeBefore(Task task, GregorianCalendar date) {
        task.setTaskType(TaskType.EVENT);

        GregorianCalendar cal = new GregorianCalendar();
        Utility.setToStartOfDay(cal);

        task.setStart(cal);
        task.setEnd(date);
    }

    /**
     * Set a free slot as a free after [date/time] slot
     * @param task the free slot
     * @param date free-before date
     */
    private void setFreeAfter(Task task, GregorianCalendar date) {
        task.setTaskType(TaskType.EVENT);

        GregorianCalendar cal = new GregorianCalendar();
        Utility.setToEndOfDay(cal);

        task.setStart(date);
        task.setEnd(cal);
    }

    private void setFreeFromTo(Task task, GregorianCalendar from,
                               GregorianCalendar to) {
        task.setTaskType(TaskType.EVENT);

        task.setStart(from);
        task.setEnd(to);
    }

    /**
     * @param cal1
     * @param cal2
     * @return
     */
    private boolean onSameDay(GregorianCalendar cal1, GregorianCalendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isStartBeforeEnd(GregorianCalendar start,
                                     GregorianCalendar end) {
        if (start != null && end != null) {
            if (start.after(end)) {
                setStatus(Logic.ERR_NON_POSITIVE_DUR);
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param results
     * @param end
     * @param allTasks
     */
    private void addLastSlot(List<Task> results,
                             GregorianCalendar start, GregorianCalendar end,
                             List<Task> allTasks, Integer duration) {
        assert(allTasks != null);

        Task t = new Task();

        GregorianCalendar last = findLastEvent(allTasks);

        if (last == null) {
           if (end != null) {
               setFreeBefore(t, end);
           }
        } else {
            if (end == null) {
                if (start == null || last.after(start)) {
                    setFreeAfter(t, last);
                }
            } else {
                if (last.before(end) &&
                    (duration == null ||
                     Utility.findDiffMinutes(last, end) > duration)) {
                    setFreeAfter(t, last);
                }
            }
        }

        if (t.getStart() != null || t.getEnd() != null) {
            results.add(t);
        }
    }

    /**
     * Prepend the first free slot to the results which is before the earliest
     * event date unless the specified start time to the earliest event task
     * is less than the specified duration. If
     * @param results
     * @param start
     * @param allTasks
     */
    private void addFirstSlot(List<Task> results,
                              GregorianCalendar start, GregorianCalendar end,
                              List<Task> allTasks, Integer duration) {
        assert(allTasks != null);

        Task t = new Task();
        t.setTaskType(TaskType.EVENT);

        GregorianCalendar first = findFirstEvent(allTasks);

        if (first == null) {
           if (start != null) {
               setFreeAfter(t, start);
           }
        } else {
            if (start == null) {
                if (end == null || first.before(end)) {
                    setFreeBefore(t, first);
                }
            } else {
                if (first.after(start) &&
                    (duration == null ||
                     Utility.findDiffMinutes(start, first) > duration)) {
                    setFreeBefore(t, first);
                }
            }
        }

        if (t.getStart() != null || t.getEnd() != null) {
            results.add(t);
        }
    }

    private GregorianCalendar findFirstEvent(List<Task> allTasks) {
        GregorianCalendar first = null;

        for (Task t : allTasks) {
            if (t.getTaskType() == TaskType.EVENT) {
                if (first == null || t.getStart().before(first)) {
                    first = t.getStart();
                }
            }
        }

        return first;
    }

    private GregorianCalendar findLastEvent(List<Task> allTasks) {
        GregorianCalendar last = null;

        for (Task t : allTasks) {
            if (t.getTaskType() == TaskType.EVENT) {
                if (last == null || t.getEnd().after(last)) {
                    last = t.getEnd();
                }
            }
        }

        return last;
    }

    private GregorianCalendar getDateOpt(String[] option) {
        Option opt = getOption(option);

        if (opt != null) {
            Date[] date = opt.dateArgument;

            if (date != null && date.length > 0) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(date[0]);

                return cal;
            }
        }

        return null;
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
