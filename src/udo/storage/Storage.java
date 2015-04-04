package udo.storage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.io.*;

import udo.storage.Task.TaskType;
import udo.util.Utility;

public class Storage {

	private static final String REGEX_SPACE = "\\s+";
	private static final String REGEX_WILDCARD = "\\*";
	private static final double NEAR_MATCH_RATIO = 4.0;
	private static final double ROUND_UP = 0.25;
	public static final String EOL = System.getProperty("line.separator");

	private static ArrayList<Task> taskList;

	private static Task prevTask;
	private static String prevCmd;
	private static String lastPath;
	private static String prevPath;
	private static ArrayList<Task> copycat;
	private static Integer maxId;    		//store current maximum group Id

	public static void main(String[] args) throws IOException{

		Storage st = new Storage();

		st.exit();
	}


	//read from json file

	public Storage(){
		initialize();
		readTaskList();
	}

	private void initialize() {
		taskList = new ArrayList<Task>();
		prevTask = new Task();
		prevCmd = "";
		copycat = new ArrayList<Task>();
	}

	//return current path to store json file
	public String getPath(){
		return new String(lastPath);
	}

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


	public void writeSettingDefault() {
		File settingFile = new File("setting.txt");
		lastPath = "task.json";
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

	//store to json file when exits
	public void exit() throws IOException{
		storeTasks();
	}

	//change data file's directory
	public boolean chDir(String path) {
		prevCmd = "chDir";
		updateLastPath(path);
		return writeNewDir(path);

	}


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


	private boolean updateLastPath(String path) {
		prevPath = lastPath;
		if (path.endsWith(".json"))
			lastPath = path;
		else { 
			if (!lastPath.equals("task.json")) {
				int nameIndex = lastPath.lastIndexOf("\\") +1;
				String fileName = lastPath.substring(nameIndex, lastPath.length());
				if (path.endsWith("\\"))
					lastPath = path.concat(fileName);
				else
					lastPath = path.concat("\\"+fileName);
			}
			else {
				if (path.endsWith("\\"))
					lastPath = path.concat("Task.json");
				else
					lastPath = path.concat("\\"+"Task.json");
			}
		}
		return true;
	}


	public boolean undoChDir() {
		prevCmd = "";
		lastPath = prevPath;
		JsonProcessor.writeJson(lastPath, taskList);
		return writeNewDir(lastPath);	
	}

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

	//method for adding dummy tasks
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

	private boolean storeTasks() {
		return JsonProcessor.writeJson(lastPath, taskList);
	}

	private boolean addDummyTasks(List<Task> dummyTasks) {
		for (int i = 0; i < dummyTasks.size(); i++){
			dummyTasks.get(i).setGroupId(maxId);
			dummyTasks.get(i).setIndex(taskList.size());
			taskList.add(dummyTasks.get(i));

		}
		JsonProcessor.writeJson(lastPath,  taskList);
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

		updateMaxGroupId();

		if (groupId == null || groupId < 1 || maxId == 0){
			return false;
		}

		removeUnconfirmedTasks(index, groupId, keptTask);
		storeTasks();
		return true;
	}

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

	//delete function, swap deleted task with last task on list 
	public boolean delete(Integer index){

		if (!isValidIndex(index))
			return false;

		prevTask = taskList.get(index);
		prevCmd = "del";
		swapWithLastTask(index);
		storeTasks();
		return true;
	}

	public boolean delete(List<Integer> indices){
		if (indices.size() > taskList.size() || indices.size() == 0){
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

	private boolean isValidIndex(Integer index) {
		if(index == null || index < 0||index >= taskList.size()|| taskList.size() == 0)		
			return false; 
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

		doModifyTask(index, modifiedTask);

		storeTasks();
		return true;
	}

	private boolean doModifyTask(Integer index, Task modifiedTask) {
		modifiedTask.setIndex(index);
		if (modifiedTask.getGroupId() == null){
			modifiedTask.setGroupId(0);
		}
		taskList.set(index, modifiedTask);
		JsonProcessor.writeJson(lastPath,  taskList);
		return true;
	}

	public ArrayList<Task> findFreeSlots(){
		TimeSlots timeSlots = new TimeSlots(query());
		return timeSlots.getFreeSlots();
	}

	//query
	public ArrayList<Task> queryAll(){
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

			String searchedAfter = searchedContent.trim().replaceAll(" +", " ").toLowerCase();

			if (isWildCardSearch(searchedAfter)){
				returnList = wildcardSearch(searchedAfter);
			} else{
				returnList = nearMatchSearch(searchedAfter);
			}
		}
		return returnList;
	}

	private boolean isWildCardSearch(String searchedContent) {
		return (searchedContent.contains("*")) || (searchedContent.contains("?"));
	}

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

	public boolean isWildcardMatched(String tameStr, String cardStr){			

		String[] cards = cardStr.split(REGEX_WILDCARD);
		String[] tame = tameStr.split(REGEX_SPACE);
		int curr = -1;
		for (int i = 0; i < cards.length; i++){
			String card = cards[i];
			if (card.length() > 0){
				String[] temp = card.trim().split(REGEX_SPACE);
				int cardIndex = 0;

				int tameIndex = curr+1;
				boolean cont = false;
				while (cardIndex < temp.length && tameIndex < tame.length){
					if (compare(temp[cardIndex], tame[tameIndex])){
						curr = tameIndex;
						cardIndex++;
						tameIndex++;

						cont = true;

					} else {
						if (cont == true){
							cont = false;
							cardIndex = 0;
						}
						if (cardIndex == 0){
							tameIndex++;
						}
					}
				}
				if (cardIndex < temp.length){
					return false;
				}
			}	
		}	


		return true;
	}


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

	//near match search using edit distance algorithm
	public ArrayList<Task> nearMatchSearch(String searchedContent){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (int i = 0; i < taskList.size(); i++){
			if (isNearMatched(taskList.get(i).getContent().toLowerCase(), searchedContent)){
				returnList.add(taskList.get(i).copy());
			}
		}
		return returnList;
	}


	private int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}


	public boolean isNearMatched(String str1, String str2){
		if (findDist(str1,str2) <=( int) (str1.length()/4.0 + 0.25)){
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

	//Lavenstein's algorithm
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

	//toggle priority
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

	//mark as done or undone
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

	public boolean markDone(List<Integer> indices){
		if (indices.size() > taskList.size() || indices.size() == 0){
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
	//method to retrieve tasks have been done
	public ArrayList<Task> getDone(){
		ArrayList <Task> doneTasks = new ArrayList<Task>();
		for (int i = 0;i < taskList.size(); i++){
			if (taskList.get(i).isDone()){
				doneTasks.add(taskList.get(i).copy());
			}
		}
		return doneTasks;
	}

	public ArrayList<Task> query(){
		ArrayList<Task> undoneTasks = new ArrayList<Task>();
		for (int i = 0;i < taskList.size(); i++){
			if (!taskList.get(i).isDone()){
				undoneTasks.add(taskList.get(i).copy());
			}
		}
		return undoneTasks;
	}

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


	private void undoMultipleTasks() {
		prevCmd = "";
		taskList = Utility.deepCopy(copycat);
	}

	private void undoModify() {
		taskList.set(prevTask.getIndex(), prevTask);
		prevCmd = "";
	}

	private void undoAdd() {
		taskList.remove(taskList.size() -1);
		prevCmd = "";
	}


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
