package udo.storage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

/** File to test reading from and writing to between ArrayList and JSON
 */
public class JsonProcessor{

	public static void main(String args[]) {
		// generate JSON String in Java
		//Gson gson = new Gson();

		//  JSONArray jsonArray = new JSONArray();
		/*Task task1 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(1995, 10, 20, 0, 1) , new GregorianCalendar(1995, 10, 20, 11, 30), 
				5, new GregorianCalendar(1995, 10, 20, 23, 59), "boring",  false);
		Task task2 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(1995, 10, 20) , new GregorianCalendar(1995, 10, 20), 
				5, new GregorianCalendar(1995, 10, 20), "boring",  false);
		Task task3 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(1995, 10, 20) , new GregorianCalendar(1995, 10, 20), 
				10, new GregorianCalendar(1995, 10, 20), "boring",  false);
		//String jtask1 = gson.toJson(task1);
		ArrayList<Task> myArr = new ArrayList<Task>();
		myArr.add(task1);
		myArr.add(task2);
		myArr.add(task2);
		myArr.add(task3);*/


		// generate JSON String in Java
		//writeJson("E:/Subject/CS2103T/project1/main/src/udo/storage/test2.json", myArr);

		// let's read
		//readJson("E:/Subject/CS2103T/project1/main/src/udo/storage/test2.json");
	}
	/*
	 * Java Method to read JSON From File
	 */
	public static ArrayList<Task> readJson(String file) throws Exception {
		JSONParser parser = new JSONParser();
		ArrayList<Task> rtList = new ArrayList<Task>();
		try {
			System.out.println("Reading JSON file from Java program");
			FileReader fileReader = new FileReader(file);
			JSONArray json = (JSONArray) parser.parse(fileReader);
			for (int i=0; i<json.size(); i++) {
				Gson gson = new Gson();
				Task temp = gson.fromJson(json.get(i).toString(), Task.class);
				if (temp.getIndex()==i) {
					rtList.add(temp);
				}
				else throw new Exception ("Index at "+i+" is invalid");
			}

			/*rtList.remove(1);
			rtList.get(0).setDuration(1);
			writeJson("E:/Subject/CS2103T/project1/main/src/udo/storage/testUpdated.json", rtList);*/

		} catch (Exception ex) {
			//	ex.printStackTrace();
			throw ex;
		}
		return rtList;
	}

	/* Java Method to write JSON String to file
	 */
	public static void writeJson(String path, ArrayList<Task> myArr) {
		ArrayList<String> gsonArray = new ArrayList<String>();
		for (int i=0; i<myArr.size(); i++) {
			Gson gson = new Gson();
			gsonArray.add(gson.toJson(myArr.get(i)));
		}


		try {
			System.out.println("Writting JSON into file ...");
			System.out.println(gsonArray);
			FileWriter jsonFileWriter = new FileWriter(path);
			jsonFileWriter.write(gsonArray.toString());
			jsonFileWriter.flush();
			jsonFileWriter.close();
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
