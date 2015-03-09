package udo.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.PriorityQueue;

public class TestPQ {
	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static PriorityQueue taskQueue;
	public enum TASK_TYPE{ EVENT, DEADLINE, TODO};


	public static void main(String[] args) throws IOException{

		PriorityQueue pq = PQ();
		System.out.println("taskQueue "+pq);

	}

	//read from json file
	static String lastPath;
	public static PriorityQueue PQ(){
		taskQueue = new PriorityQueue();
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
