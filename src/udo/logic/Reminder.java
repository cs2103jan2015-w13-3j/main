package udo.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import udo.gui.ReminderDialog;
import udo.logic.ReminderComparator;
import udo.storage.Storage;
import udo.storage.Task;

public class Reminder  {
	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static PriorityQueue<Task> taskQueue;

	public static void main(String[] args) throws IOException{
		Reminder rmd = new Reminder();
	}

	//read from json file
	public static PriorityQueue<Task> PQ(){
		taskQueue = new PriorityQueue<Task>(10, new ReminderComparator());
		ArrayList<Task> taskList = new ArrayList<Task>();
		Storage st = new Storage();
		try {
			taskList = st.query();

			for (int i =0; i<taskList.size(); i++) {
				if (!taskList.get(i).isDone()) {
					taskQueue.add(taskList.get(i));
				}
			}
			System.out.println("taskQueue ORI"+taskQueue);
			GregorianCalendar current = new GregorianCalendar();
			while (!taskQueue.isEmpty()&&(taskQueue.peek().getReminder().compareTo(current)<=0)) {
				remindList.add(taskQueue.poll());
			}
			System.out.println("Reminding!!!!!!! "+remindList);
		} catch (Exception ex) {
		}	
		return taskQueue;
	}

	public static ArrayList<Task> remindList = new ArrayList<Task>();
	public Reminder() {
		System.out.println("I AM RUNNINNGGGGGG");
		schedule();
	}

	public void schedule() {
		PQ();
		Timer timer = new Timer();

		Remind rm =  new Remind();
		if (!taskQueue.isEmpty()) {
			System.out.println("SCHEDULE SCHEDULE "+taskQueue.peek());

			timer.schedule(rm, nextRM());		
		}
	}

	public Date nextRM() {
		return taskQueue.peek().getReminder().getTime();
	}
	public class Remind extends TimerTask {
		@Override
		public void run() {		
			GregorianCalendar current = new GregorianCalendar();
			if (!taskQueue.isEmpty()&&taskQueue.peek().getReminder().compareTo(current)<=0) {
				remindList.add(taskQueue.poll());
				System.out.println("UPDATED UUUUU "+remindList);	
				schedule();
			}
		}
	}

}
