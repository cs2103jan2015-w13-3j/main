package udo.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import udo.gui.ReminderDialog;
import udo.storage.JsonProcessor;
import udo.storage.ReminderComparator;
import udo.storage.Task;

public class Reminder extends TimerTask {
	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static PriorityQueue<Task> taskQueue;

	public static void main(String[] args) throws IOException{
		PQ();
		Timer timer = new Timer();
		Reminder rm = new Reminder();
		if (!taskQueue.isEmpty()) {
			System.out.println("SCHEDULE SCHEDULE "+taskQueue.peek());
			
			timer.schedule(rm, 0, 10);		
		}
		else
			timer.cancel();
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
	
	}
	@Override
	public void run() {		
		GregorianCalendar current = new GregorianCalendar();
		if (!taskQueue.isEmpty()&&taskQueue.peek().getReminder().compareTo(current)<=0) {
			remindList.add(taskQueue.poll());
			System.out.println("UPDATED UUUUU "+remindList);
			ReminderDialog rd = new ReminderDialog(remindList.get(remindList.size()-1));
		}
	}
}
