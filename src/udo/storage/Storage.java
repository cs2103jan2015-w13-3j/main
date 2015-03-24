package udo.storage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

//import org.json.simple.*;



//import com.google.gson.Gson;

import java.util.List;
import java.io.*;

import udo.storage.Task.TaskType;
import udo.util.Utility;

//import java.util.logging.Level;
//import java.util.logging.Logger;

public class Storage {

	public static final String EOL = System.getProperty("line.separator");
	//private static File storageFile;
	private static ArrayList<Task> taskList;
	private static ArrayList<Task> doneTasks;

	private static Task prevTask;
	private static String prevCmd;

	private static Integer maxId;    		//store current maximum group Id
	//private static final Logger logger = Logger.getLogger(Storage.class.getName());

	public static void main(String[] args) throws IOException{

		Storage st = new Storage();

		st.chDir("task.json");
		st.exit();
	}


	public static void printTest(ArrayList<Task> test){
		for (int i =0; i < test.size(); i++)
			System.out.println(test.get(i));
		System.out.println("");
	}

	//read from json file
	String lastPath;
	public Storage(){
		taskList = new ArrayList<Task>();
		doneTasks = new ArrayList<Task>();
		prevTask = new Task();
		prevCmd = "";
		readTaskList();
		readDoneTasks();
	}


	private void readDoneTasks() {
		try{
			doneTasks = JsonProcessor.readJson("done.json");
		} catch (Exception e){
			JsonProcessor.writeJson("done.json", doneTasks);
			System.out.println(e);
		}
	}


	private void readTaskList() {
		try {
			//System.out.println("Reading JSON file from setting");
			FileReader fr = new FileReader("setting.txt");
			BufferedReader br = new BufferedReader(fr);
			lastPath = br.readLine();
			br.close();
			if (lastPath!=null) {
				try {
				taskList = JsonProcessor.readJson(lastPath);
				}
				catch (Exception e) {
					System.out.println(e);
				}
			}
			else {
				writeSettingDefault();
			}
		} catch (Exception ex) {
			writeSettingDefault();
		}
	}


	public void writeSettingDefault() {
		File settingFile = new File("setting.txt");
		lastPath = "task.json";
		try {
			taskList = JsonProcessor.readJson(lastPath);
		}
		catch (Exception ex) {
			System.out.println(ex);
			JsonProcessor.writeJson(lastPath, taskList);
		}

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
		//assert newTask != null;
		if (newTask == null){
			return false;
		}
		newTask.setIndex(taskList.size());
		newTask.setGroupId(0);
		taskList.add(newTask);
		JsonProcessor.writeJson(lastPath, taskList);
		prevTask = newTask;
		prevCmd = "add";
		return true;
	}

	//method for adding dummy tasks
	public boolean add(List<Task> dummyTasks){
		if (dummyTasks.size() == 0){
			return false;
		}

		updateMaxGroupId();
		
		maxId++;
		for (int i = 0; i < dummyTasks.size(); i++){
			dummyTasks.get(i).setGroupId(maxId);
			dummyTasks.get(i).setIndex(taskList.size());
			taskList.add(dummyTasks.get(i));

		}
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//find maximum group Id
	private void updateMaxGroupId() {
		maxId = 0;
		for (int i = 0; i < taskList.size(); i++){
			if (taskList.get(i).getGroupId() > maxId){
				maxId = taskList.get(i).getGroupId();
			}
		}
	}

	//delete dummy tasks
	public boolean confirm(Integer index){
		if(!isValidIndex(index)){
			return false;
		}
		
		Integer groupId = taskList.get(index).getGroupId();
		Task keptTask = taskList.get(index);
		
		if (maxId == null){
			updateMaxGroupId();
		}

		if (groupId == null || groupId < 1 || maxId == 0){
			return false;
		}

		for (int i = 0; i < taskList.size(); i++){
			if (taskList.get(i).getGroupId() == groupId && taskList.get(i).getIndex() != index){
				Task lastTask = taskList.get(taskList.size() -1);

				if (lastTask.getGroupId() == groupId && lastTask.getIndex() == index){	
					index = i;
				}
				taskList.set(i, lastTask);
				taskList.get(i).setIndex(i);
				taskList.remove(taskList.size()-1);
				i--;		
			}
		}
		keptTask.setGroupId(0);
		maxId = 0;
		JsonProcessor.writeJson(lastPath,taskList);
		return true;
	}

	//delete function, swap deleted task with last task on list 
	public boolean delete(Integer index){

		//assert index >= 0 : "index is invalid";

		if (!isValidIndex(index))
			return false;

		prevTask = taskList.get(index);
		prevCmd = "del";
		swapWithLastTask(index);
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}
	
	private boolean isValidIndex(Integer index) {
		if(index == null || index < 0||index >= taskList.size()|| taskList.size() == 0){			
			return false; 
			}
		return true;
	}


	private void swapWithLastTask(Integer index) {
		if (taskList.size() > 1) {
			taskList.set(index, taskList.get(taskList.size() -1));
			taskList.get(index).setIndex(index);
			taskList.remove(taskList.size()-1);
		} else {
			taskList.clear(); 
		}
	}

	//modify function
	public boolean modify(Integer index, Task modifiedTask){
		
		if (!isValidIndex(index)){
			return false;
		}
		prevTask = taskList.get(index);
		prevCmd = "mod";
		modifiedTask.setIndex(index);
		if (modifiedTask.getGroupId() == null){
			modifiedTask.setGroupId(0);
		}
		taskList.set(index, modifiedTask);

		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	public ArrayList<Task> findFreeSlots(){
		TimeSlots timeSlots = new TimeSlots(taskList);
		return timeSlots.getFreeSlots();
	}

	//query
	public ArrayList<Task> query(){
		return Utility.deepCopy(taskList);
	}

	//query a specific task
	public Task query(Integer index){
		if (!isValidIndex(index)){
			return new Task();
		}
		return taskList.get(index).copy();
	}


	public ArrayList<Task> query(String label){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (label != null){
			for(int i=0; i<taskList.size();i++){
				if (taskList.get(i).getLabel().equals(label)){
					returnList.add(taskList.get(i).copy());
				}
			}
		}
		return returnList;
	}

	public ArrayList<Task> query(boolean priority){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(int i=0; i<taskList.size();i++){
			if (taskList.get(i).getPriority() == priority){
				returnList.add(taskList.get(i).copy());
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
					returnList.add(taskList.get(i).copy());
				}
				else if (taskList.get(i).getTaskType() == Task.TaskType.EVENT &&
						isBefore(date,taskList.get(i).getEnd()) &&
						isAfter(date,taskList.get(i).getStart())){
					returnList.add(taskList.get(i).copy());
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
					returnList.add(taskList.get(i).copy());
				}
			}
		}
		return returnList;
	}

	private boolean isSameDate(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)){
			return false;
		}
		return date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
	}

	private boolean isAfter(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)){
			return date1.get(Calendar.YEAR) > date2.get(Calendar.YEAR);
		}
		return date1.get(Calendar.DAY_OF_YEAR) >= date2.get(Calendar.DAY_OF_YEAR);
	}

	private boolean isBefore(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)){
			return date1.get(Calendar.YEAR) < date2.get(Calendar.YEAR);
		}
		return date1.get(Calendar.DAY_OF_YEAR) <= date2.get(Calendar.DAY_OF_YEAR);
	}

	//search function
	public ArrayList<Task> search(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchedContent != null){
			returnList = exactSearch(searchedContent);
			if (returnList.size() > 0){
				return returnList;
			}
			if ((searchedContent.contains("*")) || (searchedContent.contains("?"))){
				returnList = wildcardSearch(searchedContent);
			} else{
				returnList = nearMatchSearch(searchedContent);
			}
		}
		return returnList;
	}

	public ArrayList<Task> exactSearch(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		String cmpStr = searchedContent.toLowerCase();
		for (int i =0; i< taskList.size(); i++){
			String cmpStr2 = taskList.get(i).getContent().toLowerCase();
			if (cmpStr2.contains(cmpStr)){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}

	public ArrayList<Task> wildcardSearch(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i = 0; i< taskList.size(); i++){
			if (isWildcardMatched(taskList.get(i).copy().getContent().toLowerCase(), 
					searchedContent.toLowerCase())){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}

	public boolean isWildcardMatched(String tameStr, String cardStr){
		if (cardStr.contains("*")){
			String[] cards = cardStr.split("\\*");
			if (cardStr.charAt(0) != '*'){
				int index = firstIndexOf(tameStr, cards[0]);
				System.out.println("Yes");
				if (index != 0){
					return false;
				}
				tameStr = tameStr.substring(cards[0].length());
			} 
			for (int i = 1; i < cards.length; i++){
				String card = cards[i];
				int index = firstIndexOf(tameStr, card);
				System.out.println("No");
				if (index == -1){
					return false;
				}
				tameStr = tameStr.substring(index + card.length());
			}	

			if (cardStr.charAt(cardStr.length() -1) != '*'){
				if (tameStr.length() > 0){
					return false;
				}
			}
			return true;
		} else {
			int index = firstIndexOf(tameStr, cardStr);
			if (index == -1){
				return false;
			}
			return true;
		}
	}

	public int firstIndexOf(String str1, String str2){
		int str1Index =0;
		int str2Index = 0;
		int index = -1;
		while (str1Index < str1.length() && str2Index < str2.length()){
			if (str2.charAt(str2Index) == '?'){
				if (index == -1){
					index = str1Index;
				}
				str2Index++;
				str1Index++;
			}
			else {
				if (str1.charAt(str1Index) == str2.charAt(str2Index)){
					if (index == -1){
						index = str1Index;
					}
					str2Index++;
					str1Index++;
				} else {
					if (str2Index == 0){
						str1Index++;
					}
					if (index != -1){
						str1Index = index + 1;
					}
					str2Index = 0;
					index = -1;
				}
			}
		}
		if (str2Index == str2.length()){
			return index;
		}
		return -1;
	}

	public ArrayList<Task> nearMatchSearch(String searchedContent){
		//stub
		ArrayList<Task> returnList = new ArrayList<Task>();

		return returnList;
	}

	//toggle priority
	public boolean togglePriority(Integer index){
		if (!isValidIndex(index)){
			return false;
		}
		prevTask = taskList.get(index).copy();
		prevCmd = "mod";
		taskList.get(index).setPriority(!taskList.get(index).getPriority());
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//mark as done or undone
	public boolean markDone(Integer index){
		if (!isValidIndex(index)){
			return false;
		}
		prevTask = taskList.get(index).copy();
		prevCmd = "done";

		if (taskList.get(index).isDone() == false){
			taskList.get(index).setDone();
			doneTasks.add(taskList.get(index));
			doneTasks.get(doneTasks.size() -1).setIndex(doneTasks.size() -1);
			swapWithLastTask(index);
		} else {
			return false;
		}
		JsonProcessor.writeJson("done.json", doneTasks);
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}

	//method to retrieve tasks have been done
	public ArrayList<Task> getDone(){
		return Utility.deepCopy(doneTasks);
	}

	public boolean undo(){
		switch(prevCmd){
		case "add":
			taskList.remove(taskList.size() -1);
			prevCmd = "";
			break;
		case "mod":
			taskList.set(prevTask.getIndex(), prevTask);
			prevCmd = "";
			break;
		case "del":	
			undoDelete();
			prevCmd = "";
			break;
		case "done":
			undoDelete();
			doneTasks.remove(doneTasks.size() -1);
			JsonProcessor.writeJson("done.json", doneTasks);
			prevCmd = "";
			break;
		default: return false;
		}
		JsonProcessor.writeJson(lastPath, taskList);
		return true;
	}


	private void undoDelete() {
		if(taskList.size() ==0 || prevTask.getIndex() == taskList.size()){
			taskList.add(prevTask);
		}
		else {
			Task temp = taskList.get(prevTask.getIndex());
			temp.setIndex(taskList.size());
			taskList.add(temp);
			taskList.set(prevTask.getIndex(), prevTask);
		}
	}
}
