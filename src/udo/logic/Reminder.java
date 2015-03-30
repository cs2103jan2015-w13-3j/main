package udo.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import udo.gui.Gui;
import udo.storage.Task;

public class Reminder {
    private static final Logger log = Logger
            .getLogger(Reminder.class.getName());

    // Reminder object singleton
    private static Reminder reminderObj;

	private Queue<Task> tasksQueue;
	private Timer timer;

	private Gui gui;

	private class ReminderSchedule extends TimerTask {
	    private Task remindedTask;

	    public ReminderSchedule(Task task) {
	        remindedTask = task;
        }

		@Override
		public void run() {
		    remind(remindedTask);
		}
	}

	private Reminder() {
	    log.info("Reminder initialized");
		tasksQueue = new PriorityQueue<Task>(11, new ReminderComparator());
		timer = new Timer();
	}

	public static Reminder getReminder() {
	    if (reminderObj == null) {
	        reminderObj = new Reminder();
	    }

	    return reminderObj;
	}

	public void updateTasks(List<Task> tasks) {
	    log.info("Tasks update for reminder");;
	    assert(tasks != null);
	    synchronized(tasksQueue) {
	        addTasksToPQ(tasksQueue, tasks);
	    }

	    timer.cancel();
	    schedule();
	}

	private void addTasksToPQ(Queue<Task> tasksQueue, List<Task> tasks) {
	    Calendar currentDate = new GregorianCalendar();
	    
	    for (Task t : tasks) {
	        GregorianCalendar reminder = t.getReminder();
		    System.out.println(t);

	        if (!t.isDone() &&
	            reminder != null && reminder.after(currentDate)) {
	            tasksQueue.add(t);
	        }
	    }
    }

    public void schedule() {
        log.info("Scheduling...");
		Task task;
		Date reminderDate;
		Date currentDate = new Date();

		synchronized (tasksQueue) {
    		while (!tasksQueue.isEmpty()) {
    		    task = tasksQueue.poll();
    		    reminderDate = task.getReminder().getTime();

    		    if (!task.isDone() && reminderDate.after(currentDate)) {
    		        log.info("Scheduling for " + task.getContent());

    		        ReminderSchedule schedule =  new ReminderSchedule(task);
    		        timer = new Timer();
    		        timer.schedule(schedule, reminderDate);

    		        break;
    		    }
    		}
		}
	}

	private void remind(Task task) {
	    log.info("Reminder Alert: " + task.getContent());

	    gui.displayAlert(task);

	    schedule();
	}

	public void setGui(Gui gui) {
	    this.gui = gui;
	}

	public static void main(String[] args) {
		Reminder rmd = getReminder();

		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.SECOND, 60);

		Task t = new Task();
		t.setReminder(cal);
		t.setContent("poke the moon");
		List<Task> tasks = new ArrayList<>();
		tasks.add(t);

		rmd.updateTasks(tasks);
		try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}