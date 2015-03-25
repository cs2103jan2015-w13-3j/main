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
		assertEquals(taskList, st.query());  //general case
		Task nullTask = null;
		assertEquals(false , st.add(nullTask));  //return false case
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
		assertEquals(false, st.delete(3));	//case of invalid index
		assertEquals(true, st.delete(0));	//general case
		
		taskList.add(task2);				
		taskList.add(task1);
		assertEquals(taskList, st.query());		//check if deleted task is swapped with last task
	}

	@Test
	public void TestModify(){
		initialize();
		assertEquals(false, st.modify(0, new Task()));		//case of invalid index
		
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		
		st.add(task0);
		assertEquals(true, st.modify(0, task1));		//general case
		taskList.add(task1);
		assertEquals(taskList, st.query());			//check whether task is modified
		
	}

	@Test
	public void TestQuery(){
		initialize();
		assertEquals(new ArrayList<Task>(), st.query());	//case of querying empty taskList
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		st.add(task0);
		taskList.add(task0);
		assertEquals(taskList, st.query());		//general case
	
		Task test = st.query(-1);
		test.setIndex(0);
		Task expected = new Task();
		expected.setIndex(0);
		
		assertEquals(expected, test);			//case of querying invalid index task
		assertEquals(task0, st.query(0));		//general case
	}
	
	@Test
	public void TestUndo(){
		initialize();
		Task task0 = new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
				0, new GregorianCalendar(2005,01,02), "work",true, false);
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		
		assertEquals(false,st.undo());			//case of undo without any previous command
		st.add(task0);
		assertEquals(true, st.undo());			//case of undo add
		assertEquals(new ArrayList<Task>(), st.query());	//check if taskList is actually undone
		assertEquals(false, st.undo());					//case of undo multiple times
		
		st.add(task0);
		st.modify(0, task1);
		assertEquals(true, st.undo());			//case of undo modify
		taskList.add(task1);
		assertEquals(taskList, st.query());		//check if taskList is actually undone
	
		st.delete(0);
		assertEquals(true, st.undo());			//case of undo delete
		assertEquals(taskList, st.query());		//check if taskList is actually undone
	}

	@Test
	public void TestAddDummy(){
		initialize();
		assertEquals(false, st.add(new ArrayList<Task>()));		//case of return false
	
		addDummyTasks();
		for (int i = 0; i < st.query().size(); i++){
			assertEquals(new Integer(1), st.query(i).getGroupId());		//general case
			assertEquals(new Integer(1), st.query(i).getGroupId());		//general case
		}
	
		addDummyTasks();
		for (int i = 2; i < st.query().size(); i++){
			assertEquals(new Integer(2), st.query(i).getGroupId());		//add another groupID
			assertEquals(new Integer(2), st.query(i).getGroupId());		//add another groupID
		}
	}
	
	@Test
	public void TestConfirm(){
		initialize();
		addDummyTasks();
		
		assertEquals(false, st.confirm(-1));			//case of invalid index

		assertEquals(true, st.confirm(0));				//general case
		Task task1 = new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false);
		Task task2 = new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false);
		task1.setIndex(0);
		task1.setGroupId(0);
		assertEquals(task1, st.query(0));			//test if task is actually confirmed
	
		addDummyTasks();
		task2.setIndex(2);
		task2.setGroupId(1);
		assertEquals(task2, st.query(2));			//test maximum groupID after confirming a task
		
		assertEquals(false, st.confirm(0));			//case of confirming a group 0 task
		assertEquals(true, st.confirm(2));			//general case
		
		task2.setIndex(1);
		task2.setGroupId(0);
		assertEquals(task2, st.query(1));			//check whether task is confirmed
	}

	@Test
	public void TestFreeSlots(){
		initialize();
		assertEquals(new ArrayList<Task>(), st.findFreeSlots());	//case of no free slot
		Task event1 = new Task(TaskType.EVENT, "reading", null,  new GregorianCalendar(2015,03,24,18,0),  
				new GregorianCalendar(2015,03,24, 21,0), 3, null, "leisure", false, false);
		Task event2 = new Task(TaskType.EVENT, "eating", null,  new GregorianCalendar(2015,03,24,11,0),  
				new GregorianCalendar(2015,03,24, 13,0), 2, null, "leisure", false, false);
		Task event3 = new Task(TaskType.EVENT, "studying", null,  new GregorianCalendar(2015,03,24,13,0),  
				new GregorianCalendar(2015,03,24, 16,0), 3, null, "personal", false, false);
		st.add(event1);
		st.add(event2);
		
		Task expected = new Task();
		expected.setTaskType(TaskType.EVENT);
		expected.setContent("Free slot");
		expected.setIndex(0);
		expected.setStart(new GregorianCalendar(2015,03,24, 13,0));
		expected.setEnd(new GregorianCalendar(2015,03,24,18,0));
		ArrayList<Task> temp = new ArrayList<Task>();
		
		temp.add(expected);
		

		assertEquals(temp, st.findFreeSlots()); 			//general case
		
		st.add(event3);
		expected.setStart(new GregorianCalendar(2015,03,24, 16,0));
		assertEquals(temp, st.findFreeSlots());			//case of adding another event and updating
														//free time slot.
	}

	@Test
	public void testSearch(){
		initialize();
		assertEquals(new ArrayList<Task>(), st.search("anything"));		//case of searching 
																		//a empty list		
		st.add(new Task(TaskType.TODO, "do homework", null, null, null, 
				3, null, null, false, false ));
		st.add(new Task(TaskType.TODO, "reading books", null, null, null, 
				3, null, null, false, false ));
		st.add(new Task(TaskType.TODO, "eating lunch", null, null, null, 
				3, null, null, false, false ));
		ArrayList<Task> expected = new ArrayList<Task>();
		
		expected.add(st.query(0));
		assertEquals(expected, st.search("home"));			//case of exact search
	
		expected.clear();
		expected.add(st.query(1));
		expected.add(st.query(2));
		assertEquals(expected, st.search("ea"));			//case of exact search 
															//return multiple outputs
		expected.clear();
		expected.add(st.query(0));
		assertEquals(expected, st.search("*home*"));		//case of wildcard search with "*" only
		
	
		assertEquals(expected, st.search("ho?ework"));		//case of wildcard search with "?" only
		
		assertEquals(expected, st.search("d*w?r*"));		//case of wildcard search with 
															//both "*" and "?"
		
		assertEquals(expected, st.search("D*W?r*"));		//case of searching ignore case
	
		assertEquals(expected, st.search("do         *work")); //case with multiple whitespaces
	}
}
