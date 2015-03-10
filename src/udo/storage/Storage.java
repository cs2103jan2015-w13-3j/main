package udo.storage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

//import org.json.simple.*;



//import com.google.gson.Gson;

import java.io.*;


import udo.storage.Task.TaskType;


public class Storage {

	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static ArrayList<Task> taskList;
	public enum TASK_TYPE{ EVENT, DEADLINE, TODO};
	private static Task prevTask;
	private static String prevCmd;

	public static void main(String[] args) throws IOException{

		Storage st = new Storage();
		prevTask = new Task();

		//testing purposes:

		/*boolean function = st.add(new Task(TaskType.DEADLINE, "meeting", new GregorianCalendar(2005,01,01), null, null,
						0, new GregorianCalendar(2005,01,02), "work",true, false));
		boolean function2 = st.add(new Task(TaskType.TODO, "fighting", null,null, null,
				120, new GregorianCalendar(2011,01,02), "personal", false, false));
		boolean function3 = st.add(new Task(TaskType.EVENT, "reading books", null, new GregorianCalendar(2006,03,01), new GregorianCalendar(2005,04,01),
				0, null, "leisure", false, false));
		if (function&&function2&&function3) System.out.println("Adding successfully");
	*/
		//ArrayList<Task> test = new ArrayList<Task>();
		//test = st.query();
		//printTest(test);
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
		//boolean done;
		//done = st.changeStatus(2);
		//test = st.query(true);
		//printTest(test);
		//boolean done = st.delete(2);
		boolean done = st.undo();
		//test = st.query();
		//printTest(test);
		//done = st.modify(2, "deadline", null, null, new GregorianCalendar(2006,01,05),0, new GregorianCalendar(2006,01,02), null);
		//done = st.modify(1, "event", "hanging out", new GregorianCalendar(2013,05,04), new GregorianCalendar(2013,05,05), -1, new GregorianCalendar(2013,05,03), "leisure");
		//done = st.modify(0, "todo", null,null, null, 3, null, null);
		//test = st.query();
		//printTest(test);
		st.chDir("testTask.json");
		st.exit();
	}


	/*public static void printTest(ArrayList<Task> test){
		for (int i =0; i < test.size(); i++)
			System.out.println(test.get(i));
		System.out.println("");
	}*/

	//read from json file
	String lastPath;
	public Storage(){
		taskList = new ArrayList<Task>();
		prevTask = new Task();
		prevCmd = "";
		try {
			System.out.println("Reading JSON file from setting");
			FileReader fr = new FileReader("setting.txt");
			BufferedReader br = new BufferedReader(fr);
			lastPath = br.readLine();
			br.close();
			taskList = JsonProcessor.readJson(lastPath);
		} catch (Exception ex) {
			File settingFile = new File("setting.txt");
			lastPath = "task.json";
			JsonProcessor.writeJson(lastPath, taskList);
			try {
				FileWriter fw = new FileWriter(settingFile);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(lastPath);
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	//store to json file when exits
	public void exit() throws IOException{
		JsonProcessor.writeJson(lastPath, taskList);
	}
	//change data file's directory
	public boolean chDir(String path) {
		JsonProcessor.writeJson(path, taskList);
		File settingFile = new File("setting.txt");
		lastPath = path;
		try {
			FileWriter fw = new FileWriter(settingFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(path);
			bw.close();
			fw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean add(Task newTask) {
		if (newTask == null){
			return false;
		}
	    newTask.setIndex(taskList.size());
		taskList.add(newTask);
		JsonProcessor.writeJson(lastPath, taskList);
		prevTask = newTask;
		prevCmd = "add";
		return true;
	}

	//delete function, swap deleted task with last task on list 
	public boolean delete(Integer index){
		if (index == null || index < 0||index >= taskList.size()|| taskList.size() == 0){
			return false;
		}
		
		prevTask = taskList.get(index);
		prevCmd = "del";
		if (taskList.size() > 1) {
    		taskList.set(index, taskList.get(taskList.size() -1));
		    System.out.println("Heyyy " + index);
    		taskList.get(index).setIndex(index);
    		taskList.remove(taskList.size()-1);
		} else {
		    taskList.clear(); 
		}
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//modify function
	public boolean modify(Integer index, Task modifiedTask){
		if (index == null || index < 0||index >= taskList.size()||taskList.size() == 0){
			return false;
		}
		prevTask = taskList.get(index);
		prevCmd = "mod";
		taskList.set(index, modifiedTask);
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//query
	public ArrayList<Task> query(){
		return taskList;
	}

	//query a specific task
	public Task query(Integer index){
		if (index == null || index < 0||index >= taskList.size()||taskList.size() == 0){
			return new Task();
		}
		return taskList.get(index);
	}


	public ArrayList<Task> query(String label){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (label != null){
			for(int i=0; i<taskList.size();i++){
				if (taskList.get(i).getLabel().equals(label)){
					returnList.add(taskList.get(i));
				}
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(boolean priority){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(int i=0; i<taskList.size();i++){
			if (taskList.get(i).getPriority() == priority){
				returnList.add(taskList.get(i));
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(GregorianCalendar date){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (date != null){
			for (int i =0; i < taskList.size(); i++){
				if (taskList.get(i).getTaskType() == Task.TaskType.DEADLINE &&
					isSameDate(date,taskList.get(i).getDeadline())){
					returnList.add(taskList.get(i));
				}
				else if (taskList.get(i).getTaskType() == Task.TaskType.EVENT &&
					isBefore(date,taskList.get(i).getEnd()) &&
					isAfter(date,taskList.get(i).getStart())){
					returnList.add(taskList.get(i));
				}
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(Task.TaskType taskType){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (taskType != null){
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getTaskType() == taskType) {
					returnList.add(taskList.get(i));
				}
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
		if (searchedContent != null){
			for (int i =0; i< taskList.size(); i++){
				if (taskList.get(i).getContent().contains(searchedContent)){
					returnList.add(taskList.get(i));
				}
			}
		}
		return returnList;
	}

	//toggle priority
	public boolean togglePriority(Integer index){
		if (index == null || index < 0||index >= taskList.size()||taskList.size() == 0){
			return false;
		}
		prevTask = taskList.get(index);
		prevCmd = "mod";
		taskList.get(index).setPriority(!taskList.get(index).getPriority());
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//mark as done or undone
	public boolean markDone(Integer index){
		if (index == null || index < 0||index >= taskList.size()||taskList.size() == 0){
			return false;
		}
		prevTask = taskList.get(index);
		prevCmd = "mod";
		taskList.get(index).setDone();
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	public boolean undo(){
		switch(prevCmd){
		case "add":
			taskList.remove(taskList.size() -1);
			break;
		case "mod":
			taskList.set(prevTask.getIndex(), prevTask);
			break;
		case "del":	
			if(taskList.size() ==0 || prevTask.getIndex() == taskList.size()){
				taskList.add(prevTask);
			}
			else {
				Task temp = taskList.get(prevTask.getIndex());
				temp.setIndex(taskList.size());
				taskList.add(temp);
				taskList.set(prevTask.getIndex(), prevTask);
			}
			break;
		default: return false;
		}
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}
}
