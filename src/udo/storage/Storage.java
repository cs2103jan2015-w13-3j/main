package udo.storage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

//import org.json.simple.*;



//import com.google.gson.Gson;

import java.io.*;


public class Storage {

	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static ArrayList<Task> taskList;
	public enum TASK_TYPE{ EVENT, DEADLINE, TODO};

	public static void main(String[] args) throws IOException{

		Storage st = new Storage();

		//testing purposes:

		/*boolean function = st.add("event", "meeting", new GregorianCalendar(2005,01,01), new GregorianCalendar(2005,01,03),
    			0, new GregorianCalendar(2005,01,02), "work", true);
		boolean function2 = st.add("deadline", "fighting", null, new GregorianCalendar(2010,01,03),
    			0, new GregorianCalendar(2011,01,02), "personal", false);
		boolean function3 = st.add("todo", "reading books", null, null,
    			120, null, "leisure", false);
    	if (function&&function2&&function3) System.out.println("Adding successfully");

    	ArrayList<Task> test = new ArrayList<Task>();
    	test = st.query();
    	printTest(test);
    	//test = st.query("leisure");
    	//printTest(test);
    	//test = st.query(new GregorianCalendar(2010,01,03));
    	//printTest(test);
    	//test = st.query(TASK_TYPE.DEADLINE);
    	//printTest(test);
    	//test = st.search("read");
    	//printTest(test);
    	//test = st.query(true);
    	//printTest(test);
    	boolean done;
    	//done = st.changeStatus(2);
    	//test = st.query(true);
    	//printTest(test);
    	//done = st.delete(1);
    	//test = st.query();
    	//printTest(test);
    	done = st.modify(2, "deadline", null, null, new GregorianCalendar(2006,01,05),0, new GregorianCalendar(2006,01,02), null);
    	done = st.modify(1, "event", "hanging out", new GregorianCalendar(2013,05,04), new GregorianCalendar(2013,05,05), -1, new GregorianCalendar(2013,05,03), "leisure");
	    done = st.modify(0, "todo", null,null, null, 3, null, null);
    	test = st.query();
	    printTest(test);*/

		st.exit();
	}


	/*public static void printTest(ArrayList<Task> test){
		for (int i =0; i < test.size(); i++)
			System.out.println(test.get(i));
		System.out.println("");
	}*/

	//read from json file
	public Storage(){
		taskList = new ArrayList<Task>();
		// generate JSON String in Java
		// let's read
		taskList = JsonProcessor.readJson("E:/Subject/CS2103T/project1/main/src/udo/storage/test2.json");
		//buggy:

		/*
    	try{
    		FileReader fr = new FileReader("tasks.json");
    		BufferedReader br = new BufferedReader(fr);
    		Gson gson = new Gson();
    		Task newT = gson.fromJson(br, Task.class);
    		while (newT != null){
    			taskList.add(newT);
    			newT = gson.fromJson(br, Task.class);
    		}
    		br.close();
    	} catch (IOException e){
    		storageFile = new File("tasks.json");
    	}*/
	}


	public boolean add(Task newTask) {
		taskList.add(newTask);
		return true;
	}

	//delete function, swap deleted task with last task on list 
	public boolean delete(int index){
		if (index >= taskList.size()){
			return false;
		}
		taskList.set(index, taskList.get(taskList.size() -1));
		taskList.remove(taskList.get(taskList.size() -1));
		return true;
	}

	//modify function
	public boolean modify(int index, Task modifiedTask){
		if (index >= taskList.size()){
			return false;
		}
		taskList.set(index, modifiedTask);
		return true;
	}

	//query
	public ArrayList<Task> query(){
		return taskList;
	}

	//query a specific task
	public Task query(int index){
		if (index >= taskList.size()){
			return new Task();
		}
		return taskList.get(index);
	}


	public ArrayList<Task> query(String label){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(int i=0; i<taskList.size();i++){
			if (taskList.get(i).getLabel().equals(label)){
				returnList.add(taskList.get(i));
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(boolean priority){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(int i=0; i<taskList.size();i++){
			if (taskList.get(i).isPriority() == priority){
				returnList.add(taskList.get(i));
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(GregorianCalendar date){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i =0; i < taskList.size(); i++){
			if (taskList.get(i).getTaskType() == Task.TaskType.DEADLINE &&
					isSameDate(date,taskList.get(i).getEnd())){
				returnList.add(taskList.get(i));
			}
			else if (taskList.get(i).getTaskType() == Task.TaskType.EVENT &&
					isBefore(date,taskList.get(i).getEnd()) &&
					isAfter(date,taskList.get(i).getStart())){
				returnList.add(taskList.get(i));
			}

		}

		return returnList;
	}

	public ArrayList<Task> query(Task.TaskType taskType){
		ArrayList<Task> returnList = new ArrayList<Task>();

		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getTaskType() == taskType) {
				returnList.add(taskList.get(i));
			}
		}

		return returnList;
	}

	private boolean isSameDate(GregorianCalendar date1, GregorianCalendar date2){
		return date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
	}

	private boolean isAfter(GregorianCalendar date1, GregorianCalendar date2){
		return date1.get(Calendar.DAY_OF_YEAR) >= date2.get(Calendar.DAY_OF_YEAR);
	}

	private boolean isBefore(GregorianCalendar date1, GregorianCalendar date2){
		return date1.get(Calendar.DAY_OF_YEAR) <= date2.get(Calendar.DAY_OF_YEAR);
	}

	//search function
	public ArrayList<Task> search(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i =0; i< taskList.size(); i++){
			if (taskList.get(i).getContent().contains(searchedContent)){
				returnList.add(taskList.get(i));
			}
		}
		return returnList;
	}

	//toggle priority
	public boolean togglePriority(int index){
		if (index >= taskList.size()){
			return false;
		}
		taskList.get(index).setPriority(!taskList.get(index).isPriority());
		return true;
	}

	//mark as done or undone
	public boolean changeStatus(int index){
		if (index >= taskList.size()){
			return false;
		}
		taskList.get(index).setDone();
		return true;
	}

	//store to json file when exits
	public void exit() throws IOException{
		JsonProcessor.writeJson("E:/Subject/CS2103T/project1/main/src/udo/storage/test2.json", taskList);
	}
}
