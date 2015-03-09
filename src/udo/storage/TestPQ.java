package udo.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import udo.storage.Task.TaskType;

public class TestPQ {
	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static PriorityQueue<Task> taskQueue;

	public static void main(String[] args) throws IOException{

		PriorityQueue<Task> pq = PQ();
		taskQueue.add(new Task(TaskType.DEADLINE, "fighting", new GregorianCalendar(2010,01,03), 
				new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 5,new GregorianCalendar(2010,01,03),
				"personal", false, false));
		taskQueue.add(new Task(TaskType.DEADLINE, "jogging", new GregorianCalendar(2010,01,03), 
				new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 10,new GregorianCalendar(2010,01,02),
				"personal", false, false));
		taskQueue.add(new Task(TaskType.DEADLINE, "sleeping", new GregorianCalendar(2010,01,03), 
				new GregorianCalendar(2011,01,02),new GregorianCalendar(2010,01,03), 15,new GregorianCalendar(2010,01,05),
				"personal", false, false));
		System.out.println("taskQueue "+pq);

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
				taskQueue.add(taskList.get(i));
			}
		} catch (Exception ex) {
			PQ();
		}
		return taskQueue;

	}
}
