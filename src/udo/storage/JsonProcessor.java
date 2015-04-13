
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
//@author A0112115A
public class JsonProcessor{

	public static void main(String args[]) {

	}
	
	/**
	 * Java Method to read JSON From File
	 */
	public static ArrayList<Task> readJson(String file) throws Exception {
		JSONParser parser = new JSONParser();
		ArrayList<Task> tasksList = new ArrayList<Task>();
		try {
			int countError = 0;
			System.out.println("Reading JSON file from Java program");
			FileReader fileReader = new FileReader(file);
			JSONArray json = (JSONArray) parser.parse(fileReader);
			for (int i=0; i<json.size(); i++) {
				Gson gson = new Gson();
				Task temp = gson.fromJson(json.get(i).toString(), Task.class);
				if (temp.getIndex()==i) {
					tasksList.add(temp);
				}
				else {
					temp.setIndex(i);
					tasksList.add(temp);
					countError++;
				}
				if (countError>0)
						writeJson(file, tasksList);
			}

		} catch (Exception ex) {
			throw ex;
		}
		return tasksList;
	}

	/** Java Method to write JSON String to file
	 */
	public static boolean writeJson(String path, ArrayList<Task> toWriteArr) {
		ArrayList<String> gsonArray = new ArrayList<String>();
		for (int i=0; i<toWriteArr.size(); i++) {
			Gson gson = new Gson();
			gsonArray.add(gson.toJson(toWriteArr.get(i)));
		}


		try {
			System.out.println("Writting JSON into file ...");
			System.out.println(gsonArray);
			FileWriter jsonFileWriter = new FileWriter(path);
			jsonFileWriter.write(gsonArray.toString());
			jsonFileWriter.flush();
			jsonFileWriter.close();
			System.out.println("Done");
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
