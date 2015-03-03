package udo.storage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

/**
 * Java Program to show how to work with JSON in Java. 
 * In this tutorial, we will learn creating
 * a JSON file, writing data into it and then reading from JSON file.
 *
 * @author Javin Paul
 */
public class TestJson2{

	public static void main(String args[]) {
		// generate JSON String in Java
		//Gson gson = new Gson();

		//  JSONArray jsonArray = new JSONArray();
		Task task1 = new Task("task1", "meeting", new GregorianCalendar(1995, 10, 20, 0, 1) , new GregorianCalendar(1995, 10, 20, 11, 30), 
				5, new GregorianCalendar(1995, 10, 20, 23, 59), "boring",  false);
		Task task2 = new Task("task2", "meeting", new GregorianCalendar(1995, 10, 20) , new GregorianCalendar(1995, 10, 20), 
				5, new GregorianCalendar(1995, 10, 20), "boring",  false);
		Task task3 = new Task("task3", "meeting", new GregorianCalendar(1995, 10, 20) , new GregorianCalendar(1995, 10, 20), 
				5, new GregorianCalendar(1995, 10, 20), "boring",  false);
		//String jtask1 = gson.toJson(task1);
		ArrayList<Task> myArr = new ArrayList<Task>();
		myArr.add(task1);
		myArr.add(task2);
		myArr.add(task2);
		myArr.add(task3);


		// generate JSON String in Java
		writeJson("E:/Subject/CS2103T/project/src/udo/storage/test2.json", myArr);

		// let's read
		readJson("E:/Subject/CS2103T/project/src/udo/storage/test2.json");
	}
	/*
	 * Java Method to read JSON From File
	 */
	public static void readJson(String file) {
		JSONParser parser = new JSONParser();

		try {
			System.out.println("Reading JSON file from Java program");
			FileReader fileReader = new FileReader(file);
			JSONArray json = (JSONArray) parser.parse(fileReader);
			ArrayList<Task> rtList = new ArrayList<Task>();
			for (int i =0; i<json.size(); i++) {
				Gson gson = new Gson();
				TaskOut taskTest = gson .fromJson((json.get(i)).toString(), TaskOut.class);
				rtList.add(revertTask(taskTest));
			}


			rtList.remove(1);
			rtList.get(0).setDuration(1);
			writeJson("E:/Subject/CS2103T/project/src/udo/storage/testUpdated.json", rtList);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public static JSONObject getJSONObject(Task task) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("taskType", task.getTaskType());
		obj.put("content", task.getContent());
		obj.put("start", task.getStart());
		obj.put("end", task.getEnd());
		obj.put("reminder", task.getReminder());
		obj.put("label", task.getLabel());
		obj.put("priority", task.isPriority());
		obj.put("done", task.isDone());
		return obj;
	}
	/*
	 * Java Method to write JSON String to file
	 */
	public static void writeJson(String path, ArrayList<Task> myArr) {
		ArrayList<String> jsonArray = new ArrayList<String>();
		for (int i=0; i<myArr.size(); i++) {
			Gson gson = new Gson();
			jsonArray.add(gson.toJson(formatTask(myArr.get(i))));
		}


		try {
			System.out.println("Writting JSON into file ...");
			System.out.println(jsonArray);
			FileWriter jsonFileWriter = new FileWriter(path);
			jsonFileWriter.write(jsonArray.toString());
			jsonFileWriter.flush();
			jsonFileWriter.close();
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Format Task and Date

	public static TaskOut formatTask(Task task) {
		String startFor = formatDate(task.getStart());
		String endFor = formatDate(task.getEnd());
		String reminderFor = formatDate(task.getReminder());
		return new TaskOut(task.getTaskType(), task.getContent(), startFor, endFor, 
				task.getDuration(),  reminderFor, task.getLabel(),  task.isPriority());
	}
	public static String formatDate(GregorianCalendar date){
		GregorianCalendar dateFormatted = new GregorianCalendar(date.get(Calendar.YEAR),
				date.get(Calendar.MONTH)-1, date.get(Calendar.DAY_OF_MONTH), 
				date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
		fmt.setCalendar(dateFormatted);
		String dateFormatted1 = fmt.format(dateFormatted.getTime());
		return dateFormatted1;
	}
	//Revert Task and Date
	public static Task revertTask (TaskOut task) {
		GregorianCalendar start = new GregorianCalendar(Integer.valueOf(task.getStartOut().substring(6, 10)),
				Integer.valueOf(task.getStartOut().substring(3, 5)), Integer.valueOf(task.getStartOut().substring(0, 2)),
				Integer.valueOf(task.getStartOut().substring(14, 16)),Integer.valueOf(task.getStartOut().substring(17, 19)));	
		GregorianCalendar end = new GregorianCalendar(Integer.valueOf(task.getEndOut().substring(6, 10)),
				Integer.valueOf(task.getEndOut().substring(3, 5)), Integer.valueOf(task.getEndOut().substring(0, 2)),
				Integer.valueOf(task.getEndOut().substring(14, 16)),Integer.valueOf(task.getEndOut().substring(17, 19)));
		GregorianCalendar reminder = new GregorianCalendar(Integer.valueOf(task.getReminderOut().substring(6, 10)),
				Integer.valueOf(task.getReminderOut().substring(3, 5)), Integer.valueOf(task.getReminderOut().substring(0, 2)),
				Integer.valueOf(task.getReminderOut().substring(14, 16)),Integer.valueOf(task.getReminderOut().substring(17, 19)));
		return new Task(task.getTaskType(), task.getContent(), start, end, 
				task.getDuration(),  reminder, task.getLabel(),  task.isPriority());
	}

}
