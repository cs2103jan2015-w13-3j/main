package udo.testdriver;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Test;

import udo.storage.JsonProcessor;
import udo.storage.Task;
import udo.storage.Task.TaskType;
/**
 * This class performs unit testing on methods in JsonProcessor class
 * @author Tue
 */
public class JsonProcessorTest {

	public static final String storageFile = "res/tests/storage/tasks.json";
	public static final String expectedFile = "res/tests/storage/expected.txt";
	public static final String inputFile = "res/tests/storage/input.txt";
	private static ArrayList<Task> taskList = new ArrayList<Task>();

	public void clearFile(String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("");
			fw.close();
			bw.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	/**code reuse
	 * @author Thien
	 */
	public String readFile(String filename) {
		String result = null;

		try {
			result = new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Test
	public void writeJsonTest() {
		clearFile(storageFile);
		taskList.add(new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false));
		taskList.add(new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false));
		taskList.add(new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false));
		JsonProcessor.writeJson(storageFile,taskList);
		String expected = readFile(expectedFile);
		assertEquals(expected.trim(), readFile(storageFile).trim());		//general case
	}

	@Test
	public void readJsonTest() {
		ArrayList<Task> testList;
		try {
			testList = JsonProcessor.readJson(inputFile);
		} catch (Exception e) {
			testList = new ArrayList<Task>();
			e.printStackTrace();
		}
		taskList.clear();
		taskList.add(new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false));
		taskList.add(new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false));
		taskList.add(new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false));

		for (int i = 0; i <taskList.size(); i++){
			taskList.get(i).setIndex(i);
			taskList.get(i).setGroupId(0);
		}

		assert testList.size() == taskList.size();		//case when jsonFile has been manually modified
	
		assertEquals(taskList, testList);			//general case
	}
}