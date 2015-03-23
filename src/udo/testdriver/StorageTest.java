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
		taskList.clear();
		doneTasks.clear();
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
	
	private void addDummyTasks() {
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		Task task2 = new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false);
		
		ArrayList<Task> temp = new ArrayList<Task>();
		temp.add(task1);
		temp.add(task2);
		st.add(temp);
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
	
		taskList.add(task0);
		taskList.add(task1);
		taskList.add(task2);
		assertEquals(taskList, st.query());
	}
	
	@Test
	public void testDelete() {
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
		
		taskList.add(task2);
		taskList.add(task1);
		assertEquals(taskList, st.query());
	}

	@Test
	public void TestModify(){
		initialize();
		assertEquals(false, st.modify(0, new Task()));
		
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		
		st.add(task0);
		assertEquals(true, st.modify(0, task1));
		taskList.add(task1);
		assertEquals(taskList, st.query());
		
	}

	@Test
	public void TestQuery(){
		initialize();
		assertEquals(new ArrayList<Task>(), st.query());
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		st.add(task0);
		taskList.add(task0);
		assertEquals(taskList, st.query());
	}
	
	@Test
	public void TestUndo(){
		initialize();
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		
		assertEquals(false,st.undo());
		st.add(task0);
		assertEquals(true, st.undo());
		assertEquals(new ArrayList<Task>(), st.query());
		assertEquals(false, st.undo());					//cannot undo multiple times
		
		st.add(task0);
		st.modify(0, task1);
		assertEquals(true, st.undo());
		taskList.add(task1);
		assertEquals(taskList, st.query());
	
		st.delete(0);
		assertEquals(true, st.undo());
		assertEquals(taskList, st.query());
	}

	@Test
	public void TestAddDummy(){
		initialize();
		assertEquals(false, st.add(new ArrayList<Task>()));
	
		addDummyTasks();
		for (int i = 0; i < st.query().size(); i++){
			assertEquals(new Integer(1), st.query().get(i).getGroupId());
			assertEquals(new Integer(1), st.query().get(i).getGroupId());
		}
	
		addDummyTasks();
		for (int i = 2; i < st.query().size(); i++){
			assertEquals(new Integer(2), st.query().get(i).getGroupId());
			assertEquals(new Integer(2), st.query().get(i).getGroupId());
		}
	}
	
	@Test
	public void TestConfirm(){
		initialize();
		addDummyTasks();
		
		assertEquals(false, st.confirm(-1));
		assertEquals(false, st.confirm(3));
		assertEquals(true, st.confirm(0));
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		Task task2 = new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false);
		task1.setIndex(0);
		task1.setGroupId(0);
		assertEquals(task1, st.query().get(0));
	
		addDummyTasks();
		task2.setIndex(2);
		task2.setGroupId(1);
		assertEquals(task2, st.query().get(2));
		
		assertEquals(true, st.confirm(2));
		
		task2.setIndex(1);
		task2.setGroupId(0);
		assertEquals(task2, st.query().get(1));
	}
}
