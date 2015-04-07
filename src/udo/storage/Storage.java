
package udo.storage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import udo.util.Utility;

/**
 * This class processes the ArrayList of Task objects passed to it 
 * via the methods provided or read from the JSON file at the program 
 * start up. After each successful operation, it automatically updates
 * the task list in the JSON file.
 * @author A0113038U
 *
 */

public class Storage {

	private static final String REGEX_SPACE = "\\s+";
	private static final String REGEX_WILDCARD = "\\*";
	private static final double NEAR_MATCH_RATIO = 3.0;
	private static final double ROUND_UP = 0.4;
	public static final String EOL = System.getProperty("line.separator");

	private static ArrayList<Task> taskList;

	private static Task prevTask;		//store the previous task that was added/modified/deleted
	private static String prevCmd;		//store the previous command excluding display/searching
	private static String lastPath;		//store the current path of json file
	private static String prevPath;		//store the last path used to store json file
	private static ArrayList<Task> copycat;		//store the taskList before delete/marking done multiple tasks
	private static Integer maxId;    		//store current maximum group Id

	/**
	 * Constructor
	 */
	public Storage(){
		initialize();
		readTaskList();
	}
	
	/**
	 * This method provides initialization of some objects
	 */
	private void initialize() {
		taskList = new ArrayList<Task>();
		prevTask = new Task();
		prevCmd = "";
		copycat = new ArrayList<Task>();
	}

	
	/**
	 * @return current path to store json file
	 */
	public String getPath(){
		return new String(lastPath);
	}

	/**
	 * Method performs reading task list from Json file
	 * @author A0112115A
	 */
	private void readTaskList() {
		try {
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
					if (!storeTasks())
						writeSettingDefault();
				}
			}
			else {
				writeSettingDefault();
			}
		} catch (Exception ex) {
			writeSettingDefault();
		}
	}

	/**
	 * @author A0112115A
	 */
	public void writeSettingDefault() {
		File settingFile = new File("setting.txt");
		lastPath = "tasks.json";
		try {
			taskList = JsonProcessor.readJson(lastPath);
		}
		catch (Exception ex) {

			ex.printStackTrace();;
			storeTasks();
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


	/**
	 * change data file's directory
	 * @param path
	 * @return true if directory is changed, else false
	 * @author A0112115A
	 */
	public boolean chDir(String path) {
		prevCmd = "chDir";
		updateLastPath(path);
		return writeNewDir(path);

	}

	/**
	 * @author A0112115A
	 * @param path
	 * @return
	 */
	private boolean writeNewDir(String path) {
		if (storeTasks()) {
			try {
				File settingFile = new File("setting.txt");
				FileWriter fw;
				fw = new FileWriter(settingFile);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(lastPath);
				bw.close();
				fw.close();

				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * @author A0112115A
	 * @param path
	 * @return
	 */
	private boolean updateLastPath(String path) {
		prevPath = lastPath;
		if (path.endsWith(".json"))
			lastPath = path;
		else {
			if (!lastPath.equals("tasks.json")) {
				int nameIndex = lastPath.lastIndexOf("\\") +1;
				String fileName = lastPath.substring(nameIndex, lastPath.length());
				if (path.endsWith("\\"))
					lastPath = path.concat(fileName);
				else
					lastPath = path.concat("\\"+fileName);
			}
			else {
				if (path.endsWith("\\"))
					lastPath = path.concat("tasks.json");
				else
					lastPath = path.concat(File.separator+"tasks.json");
			}
		}
		return true;
	}

	/**
	 * @author A0112115A
	 * @return
	 */
	public boolean undoChDir() {
		prevCmd = "";
		lastPath = prevPath;
		JsonProcessor.writeJson(lastPath, taskList);
		return writeNewDir(lastPath);
	}
	
	/** 
	 * Method used to add new task to taskList
	 * @param newTask
	 * @return true if task is successfully added, else false
	 */
	public boolean add(Task newTask) {
		if (newTask == null){
			return false;
		}
		prevTask = newTask;
		prevCmd = "add";
		doAddTask(newTask);
		storeTasks();

		return true;
	}

	private void doAddTask(Task newTask) {
		newTask.setIndex(taskList.size());
		newTask.setGroupId(0);
		taskList.add(newTask);
	}

	/**
	 * Method for adding dummy tasks
	 * @param dummyTasks
	 * @return true if dummy tasks are added, else false
	 */
	public boolean add(List<Task> dummyTasks){
		if (dummyTasks.size() == 0){
			return false;
		}

		updateMaxGroupId();

		maxId++;

		addDummyTasks(dummyTasks);

		storeTasks();
		return true;
	}
	
	/**
	 * Method to store taskList to Json file
	 * @return true if taskList is successfully saved, else false
	 */
	private boolean storeTasks() {
		return JsonProcessor.writeJson(lastPath, taskList);
	}

	/**
	 * Method used to store group ID for dummy tasks and store to json file
	 * @param dummyTasks
	 * @return true if action is successfully done, else false
	 */
	private boolean addDummyTasks(List<Task> dummyTasks) {
		for (int i = 0; i < dummyTasks.size(); i++){
			dummyTasks.get(i).setGroupId(maxId);
			dummyTasks.get(i).setIndex(taskList.size());
			taskList.add(dummyTasks.get(i));

		}
		JsonProcessor.writeJson(lastPath,  taskList);
		return true;
	}

	/**
	 * Method used to get a group ID that is not used by any dummy tasks
	 */
	private void updateMaxGroupId() {
		maxId = 0;
		for (int i = 0; i < taskList.size(); i++){
			if (taskList.get(i).getGroupId() > maxId){
				maxId = taskList.get(i).getGroupId();
			}
		}
	}

	/**
	 * Method to keep a Task with a specified index and 
	 * delete all tasks with same groupID 
	 * @param index
	 * @return true if confirm is done successfully, else false
	 */
	public boolean confirm(Integer index){
		if(!isValidIndex(index)){
			return false;
		}

		Integer groupId = taskList.get(index).getGroupId();
		Task keptTask = taskList.get(index);

		updateMaxGroupId();

		if (groupId == null || groupId < 1 || maxId == 0){
			return false;
		}

		removeUnconfirmedTasks(index, groupId, keptTask);
		storeTasks();
		return true;
	}

	/**
	 * Delete all tasks with same groupID as keptTask,
	 * update keptTask groupID to 0
	 * @param index
	 * @param groupId
	 * @param keptTask
	 */
	private void removeUnconfirmedTasks(Integer index, Integer groupId,
			Task keptTask) {
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
	}

	/**
	 * Method performs delete function, 
	 * swap deleted task with last task on list
	 * @param index
	 * @return true if task is successfully deleted, else false
	 */
	public boolean delete(Integer index){

		if (!isValidIndex(index))
			return false;

		prevTask = taskList.get(index);
		prevCmd = "del";
		swapWithLastTask(index);
		storeTasks();
		return true;
	}

	/**
	 * Method to delete a list of Tasks at the specified indices
	 * @param list of indices
	 * @return true if all the tasks are successfully deleted, else false
	 */
	public boolean delete(List<Integer> indices){
		if (areValidIndices(indices)){
			return false;
		}

		for (int i = 0; i <indices.size(); i++){
			if (!isValidIndex(indices.get(i))){
				return false;
			}
		}

		prevCmd = "mult";
		copycat = Utility.deepCopy(taskList);

		for (int i = 0; i < indices.size(); i ++){
			int index = indices.get(i);
			taskList.set(index, new Task());
		}

		for (int i = 0; i < taskList.size(); i ++){
			if (taskList.get(i).getContent() == null){
				taskList.remove(i);
				i--;
			} else {
				taskList.get(i).setIndex(i);
			}
		}
		storeTasks();
		return true;
	}
	
	//check if list of indices are valid
	private boolean areValidIndices(List<Integer> indices) {
		return indices.size() > taskList.size() || indices.size() == 0;
	}

	//check if an index is valid
	private boolean isValidIndex(Integer index) {
		if(index == null || index < 0||index >= taskList.size()|| taskList.size() == 0)
			return false;
		return true;

	}

	/**
	 * Swap task in specified index with the last task on the list
	 * @param index
	 */
	private void swapWithLastTask(Integer index) {
		if (taskList.size() > 1) {
			taskList.set(index, taskList.get(taskList.size() -1));
			taskList.get(index).setIndex(index);
			taskList.remove(taskList.size()-1);
		} else {
			taskList.clear();
		}
	}

	/**
	 * Method to replace task in specified index with a modified Task
	 * @param index
	 * @param modifiedTask
	 * @return true if task is successfully replaced, else false
	 */
	public boolean modify(Integer index, Task modifiedTask){

		if (!isValidIndex(index)){
			return false;
		}
		prevTask = taskList.get(index);
		prevCmd = "mod";

		doModifyTask(index, modifiedTask);

		storeTasks();
		return true;
	}

	/**
	 * Perform update in modified Task's index and groupID
	 * @param index
	 * @param modifiedTask
	 * @return true if task's index and groupID is updated, else false
	 */
	private boolean doModifyTask(Integer index, Task modifiedTask) {
		modifiedTask.setIndex(index);
		if (modifiedTask.getGroupId() == null){
			modifiedTask.setGroupId(0);
		}
		taskList.set(index, modifiedTask);
		JsonProcessor.writeJson(lastPath,  taskList);
		return true;
	}
	
	/**
	 * Get free time slots from TimeSlots class
	 * @return ArrayList of Task with free time slots
	 */
	public ArrayList<Task> findFreeSlots(){
		TimeSlots timeSlots = new TimeSlots(query());
		return timeSlots.getFreeSlots();
	}

	/**
	 * @return the copy of whole taskList
	 */
	public ArrayList<Task> queryAll(){
		return Utility.deepCopy(taskList);
	}

	/**
	 * @param index
	 * @return a task at specified index
	 */
	public Task query(Integer index){
		if (!isValidIndex(index)){
			return new Task();
		}
		return taskList.get(index).copy();
	}

	/**
	 * @param label
	 * @return the tasks matched the label
	 */
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
	
	/**
	 * @param priority
	 * @return the tasks matched priority level
	 */
	public ArrayList<Task> query(boolean priority){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(int i=0; i<taskList.size();i++){
			if (taskList.get(i).getPriority() == priority){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}

	/** 
	 * @param date
	 * @return the tasks matched the date specified
	 */
	public ArrayList<Task> query(GregorianCalendar date){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (date != null){
			for (int i =0; i < taskList.size(); i++){
				if (taskList.get(i).getTaskType() == Task.TaskType.DEADLINE &&
						isNearDate(date,taskList.get(i).getDeadline())){
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
	
	/**
	 * @param taskType
	 * @return the tasks matched task type: event/deadline/todo
	 */
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

	/**
	 * @param date1
	 * @param date2
	 * @return true if date2 is within 2 days from date1, else false
	 */
	private boolean isNearDate(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) > date2.get(Calendar.YEAR)){
			return false;
		} else if (date2.get(Calendar.YEAR) - date1.get(Calendar.YEAR) > 1){
			return false;
		} else if (date2.get(Calendar.YEAR) > date1.get(Calendar.YEAR)){
			return ((365 - date1.get(Calendar.DAY_OF_YEAR) + date2.get(Calendar.DAY_OF_YEAR)) <= 2);
		} else {
			return (date2.get(Calendar.DAY_OF_YEAR) - date1.get(Calendar.DAY_OF_YEAR) <= 2
					&& date2.get(Calendar.DAY_OF_YEAR) - date1.get(Calendar.DAY_OF_YEAR) >= 0);
		}
	}
	
	/**
	 * @param date1
	 * @param date2
	 * @return true if date1 occurs same or after date2
	 */
	private boolean isAfter(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)){
			return date1.get(Calendar.YEAR) > date2.get(Calendar.YEAR);
		}
		return date1.get(Calendar.DAY_OF_YEAR) >= date2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * @param date1
	 * @param date2
	 * @return true if date1 occurs same or before date2
	 */
	private boolean isBefore(GregorianCalendar date1, GregorianCalendar date2){
		if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)){
			return date1.get(Calendar.YEAR) < date2.get(Calendar.YEAR);
		}
		return date1.get(Calendar.DAY_OF_YEAR) <= date2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Perform search operation
	 * @param searchedContent
	 * @return the tasks matched searched content
	 */
	public ArrayList<Task> search(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchedContent != null){

			String searchedAfter = searchedContent.trim().replaceAll(" +", " ").toLowerCase();

			if (isWildCardSearch(searchedAfter)){
				returnList = wildcardSearch(searchedAfter);
			} else{
				returnList = nearMatchSearch(searchedAfter);
			}
		}
		return returnList;
	}

	/**
	 * @param searchedContent
	 * @return true if searchedContent indicates wildcard search, else false
	 */
	private boolean isWildCardSearch(String searchedContent) {
		return (searchedContent.contains("*")) || (searchedContent.contains("?"));
	}

	/**
	 * Perform wild card search
	 * @param searchedContent
	 * @return the tasks matched wild card search
	 */
	public ArrayList<Task> wildcardSearch(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i = 0; i< taskList.size(); i++){
			if (isWildcardMatched(taskList.get(i).getContent().toLowerCase(),
					searchedContent)){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}

	/**
	 * @param tameStr
	 * @param cardStr
	 * @return true if tameStr is matched wildcard search with cardStr
	 */
	public boolean isWildcardMatched(String tameStr, String cardStr){

		String[] cards = cardStr.split(REGEX_SPACE);
		String[] tame = tameStr.split(REGEX_SPACE);
			
		int j = 0;
		for (int i = 0; i < cards.length; i++){
			if (j == tame.length){
				return false;
			}

			boolean isMatch = true;

			if (!cards[i].contains("*") && !compare(cards[i],tame[j])){
				isMatch = false;
			} else {
				String removedCard[] = cards[i].split(REGEX_WILDCARD);
				String temp = tame[j];
				if (cards[i].charAt(0) != '*'){
					int index = firstIndexOf(temp, removedCard[0]);
					if (index != 0){
						isMatch = false;
					} else {
						temp = temp.substring(removedCard[0].length());
					}
				} 
				if (removedCard.length > 1 && cards[i].charAt(cards[i].length() - 1) !='*'){
					int index = lastIndexOf(temp, removedCard[removedCard.length-1]);
					if (index != temp.length() - removedCard[removedCard.length -1].length()){
						isMatch = false;
					}	
				}
				for (int k = 1; k < removedCard.length; k++){
					if (removedCard[k].length() > 0){
						int index = firstIndexOf(temp, removedCard[k]);
						if (index == -1){
							isMatch = false;
						} else {
							temp = temp.substring(index + removedCard[k].length());
						}
					}
				}
			}
			if (!isMatch){
				i--;
			}
			j++;
		}
		return true;
	}

	/**
	 * Compare 2 strings, with '?' counts as a missing single letter
	 * @param str1
	 * @param str2
	 * @return true if 2 strings are matched, else false
	 */
	public boolean compare(String str1, String str2){
		if (str1.length() != str2.length()){
			return false;
		} else {
			for (int i = 0; i < str1.length(); i++){

				if (str1.charAt(i) != '?' && str1.charAt(i) != str2.charAt(i)){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param str1
	 * @param str2
	 * @return the index of first occurrence of str2 in str1
	 */
	public int firstIndexOf(String str1, String str2){
		for (int i = 0; i <= str1.length() - str2.length(); i++){
			if (compare(str2,str1.substring(i, i + str2.length()))){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param str1
	 * @param str2
	 * @return index of last occurrence of str2 in str1
	 */
	public int lastIndexOf(String str1, String str2){
		if (str1.length() < str2.length()){
			return -1;
		}
		if (compare(str2,str1.substring(str1.length() - str2.length()))){
			return str1.length() - str2.length();
		} else {
			return -1;
		}
	}
	
	/**
	 * Perform near match search using edit distance algorithm
	 * @param searchedContent
	 * @return the tasks that matched near match search
	 */
	public ArrayList<Task> nearMatchSearch(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i = 0; i < taskList.size(); i++){
			if (isNearMatched(taskList.get(i).getContent().toLowerCase(), searchedContent)){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}

	// find minimum number of 3 integers
	private int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * Check if 2 strings are matched by near match search
	 * @param str1
	 * @param str2
	 * @return true if 2 strings are matched, else false
	 */
	public boolean isNearMatched(String str1, String str2){
		if (findDist(str1,str2) <=( int) (str1.length()/NEAR_MATCH_RATIO + ROUND_UP)){
			return true;
		}
		String[] newStr1 = str1.trim().split(REGEX_SPACE);
		String[] newStr2 = str2.trim().split(REGEX_SPACE);
		if (newStr1.length < newStr2.length){
			return false;
		}
		int str2Index = 0;
		int str1Index = 0;
		while (str2Index < newStr2.length && str1Index < newStr1.length){
			int dist = findDist(newStr1[str1Index],newStr2[str2Index]);
			if (dist <= (int)(newStr1[str1Index].length()/NEAR_MATCH_RATIO + ROUND_UP)){
				str1Index++;
				str2Index++;
			} else {
				str1Index++;
			}
		}
		if (str2Index == newStr2.length){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Lavenstein's algorithm to find edit distance
	 * @param str1
	 * @param str2
	 * @return distance between 2 strings
	 * @author Lavenstein
	 */
	public int findDist(String str1, String str2) {

		int[][] distance = new int[str2.length() + 1][str1.length() + 1];

		for (int i = 0; i <= str2.length(); i++) {
			distance[i][0] = i;
		}
		for (int j = 1; j <= str1.length(); j++) {
			distance[0][j] = j;
		}
		for (int i = 1; i <= str2.length(); i++) {
			for (int j = 1; j <= str1.length(); j++) {
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(j - 1) == str2.charAt(i - 1)) ? 0 : 1));
			}
		}
		return distance[str2.length()][str1.length()];
	}

	/**
	 * Perform change priority of a task at specified index
	 * @param index
	 * @return true if task is successfully changed priority, else false
	 */
	public boolean togglePriority(Integer index){
		if (!isValidIndex(index)){
			return false;
		}
		prevTask = taskList.get(index).copy();
		prevCmd = "mod";
		taskList.get(index).setPriority(!taskList.get(index).getPriority());
		storeTasks();
		return true;
	}

	/**
	 * Perform mark an undone task at specified index as done
	 * @param index
	 * @return true if task is marked as done successfully, else false
	 */
	public boolean markDone(Integer index){
		if (!isValidIndex(index)){
			return false;
		}

		if (!taskList.get(index).isDone()){
			prevTask = taskList.get(index).copy();
			prevCmd = "mod";
			taskList.get(index).setDone();
		} else {
			return false;
		}

		storeTasks();
		return true;
	}
	
	/**
	 * Perform mark as done multiple tasks at specified indices
	 * @param list of indices
	 * @return true if the tasks at indices are marked as done, else false
	 */
	public boolean markDone(List<Integer> indices){
		if (areValidIndices(indices)){
			return false;
		}

		ArrayList<Task> temp = Utility.deepCopy(taskList);

		for (int i = 0; i < indices.size(); i++){
			if (!markDone(indices.get(i))){
				return false;
			}
		}
		copycat = temp;
		prevCmd = "mult";

		storeTasks();
		return true;
	}
	
	/**
	 * Method to retrieve tasks have been done
	 * @return list of done tasks
	 */
	public ArrayList<Task> getDone(){
		ArrayList <Task> doneTasks = new ArrayList<Task>();
		for (int i = 0;i < taskList.size(); i++){
			if (taskList.get(i).isDone()){
				doneTasks.add(taskList.get(i).copy());
			}
		}
		return doneTasks;
	}
	
	/**
	 * Method to retrieve tasks are undone
	 * @return list of undone tasks
	 */
	public ArrayList<Task> query(){
		ArrayList<Task> undoneTasks = new ArrayList<Task>();
		for (int i = 0;i < taskList.size(); i++){
			if (!taskList.get(i).isDone()){
				undoneTasks.add(taskList.get(i).copy());
			}
		}
		return undoneTasks;
	}

	/**
	 * Perform undo operation
	 * @return true if last command is successfully undone, else false
	 */
	public boolean undo(){
		switch(prevCmd){
		case "add":
			undoAdd();
			break;
		case "mod":
			undoModify();
			break;
		case "del":
			undoDelete();
			break;
		case "chDir":
			return undoChDir();
		case "mult":
			undoMultipleTasks();
			break;
		default: return false;
		}
		storeTasks();
		return true;
	}

	/**
	 * Perform undo on delete/mark done multiple tasks
	 */
	private void undoMultipleTasks() {
		prevCmd = "";
		taskList = Utility.deepCopy(copycat);
	}

	/**
	 * Perform undo the modify function
	 */
	private void undoModify() {
		taskList.set(prevTask.getIndex(), prevTask);
		prevCmd = "";
	}

	/**
	 * Perform undo the add function
	 */
	private void undoAdd() {
		taskList.remove(taskList.size() -1);
		prevCmd = "";
	}

	/**
	 * Perform undo the delete function
	 */
	private void undoDelete() {
		prevCmd = "";
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
