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

		} catch (Exception ex) {
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
