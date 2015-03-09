package udo.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import udo.storage.Task.TaskType;

public class Reminder extends TimerTask {
	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static PriorityQueue<Task> taskQueue;

	public static void main(String[] args) throws IOException{

		PriorityQueue<Task> pq = PQ();
		System.out.println("taskQueue "+pq);
		remind();
		System.out.println("HERE "+remindList);
	}

	//read from json file
	static String lastPath;
	public static PriorityQueue<Task> PQ(){
		taskQueue = new PriorityQueue<Task>(10, new ReminderComparator());
		ArrayList<Task> taskList = new ArrayList<Task>();
		try {
			System.out.println("Reading JSON file from setting");
			FileReader fr = new FileReader("setting.txt");
			BufferedReader br = new BufferedReader(fr);
			lastPath = br.readLine();
			br.close();
			taskList = JsonProcessor.readJson(lastPath);
			taskList.add(new Task(TaskType.DEADLINE, "fighting", new GregorianCalendar(2010,01,03), 
					new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 5,new GregorianCalendar(2010,01,03),
					"personal", false, false));
			taskList.add(new Task(TaskType.DEADLINE, "jogging", new GregorianCalendar(2010,01,03), 
					new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 10,new GregorianCalendar(2010,01,02),
					"personal", false, false));
			taskList.add(new Task(TaskType.DEADLINE, "sleeping", new GregorianCalendar(2010,01,03), 
					new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 15,new GregorianCalendar(2010,01,05),
					"personal", false, false));
			taskList.add(new Task(TaskType.DEADLINE, "crying", new GregorianCalendar(2010,01,03), 
					new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 15,new GregorianCalendar(2010,01,05),
					"personal", false, true));

			for (int i =0; i<taskList.size(); i++) {
				if (!taskList.get(i).isDone())
					taskQueue.add(taskList.get(i));
			}
		} catch (Exception ex) {
			PQ();
		}
		return taskQueue;
	}
	public static ArrayList<Task> remind() {
		Timer timer = new Timer();
		if (!taskQueue.isEmpty()) {
		timer.schedule(new Reminder(), taskQueue.peek().getReminder().getTime());
		}
		return remindList;
	}
	public static ArrayList<Task> remindList = new ArrayList<Task>();
	public Reminder() {
		run();
	}
	@Override
	public void run() {
		remindList.add(taskQueue.poll());
	}
}
