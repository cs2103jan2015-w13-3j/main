package udo.testdriver;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Test;

import udo.storage.Storage;
import udo.storage.Task;
import udo.storage.Task.TaskType;
public class StorageTest {

	private static ArrayList<Task> taskList = new ArrayList<Task>();
	private static ArrayList<Task> doneTasks = new ArrayList<Task>();
	private static String storageFile = "task.json";
	private static Storage st;
	
	public void initialize(){
		clearFile(storageFile);
		st = new Storage();
	}
	
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
	
	@Test
	public void testAdd() {	
		initialize();

		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		Task task2 = new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false);

		st.add(task0);
		st.add(task1);
		st.add(task2);
	
		
		assertEquals(task0, st.query().get(st.query().size() - 3));
		assertEquals(task1, st.query().get(st.query().size() - 2));
		assertEquals(task2, st.query().get(st.query().size() - 1));

	}
	
	@Test
	public void testDel() {
		initialize();
		assertEquals(false, st.delete(0));
		st.add(new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false));
		assertEquals(true, st.delete(0));
		assertEquals(0, st.query().size());
	
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		Task task2 = new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false);

		st.add(task0);
		st.add(task1);
		st.add(task2);
		assertEquals(false, st.delete(3));
		assertEquals(true, st.delete(0));
		
		assertEquals(task2,st.query().get(0));
		assertEquals(task1,st.query().get(1));
	}

	@Test
	public void TestMod(){
		initialize();
		assertEquals(false, st.modify(0, new Task()));
		
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		
		st.add(task0);
		assertEquals(true, st.modify(0, task1));
		assertEquals(task1, st.query().get(0));
		
	}
}
