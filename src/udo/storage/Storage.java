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
    	test = st.query(new GregorianCalendar(2010,01,03));
    	printTest(test);
    	//test = st.query(TASK_TYPE.DEADLINE);
    	//printTest(test);
    	//test = st.search("read");
    	//printTest(test);
    	test = st.query(true);
    	printTest(test);
    	boolean done;
    	done = st.changeStatus(2);
    	test = st.query(true);
    	printTest(test);
    	done = st.delete(1);
    	test = st.query();
    	printTest(test);
    	*/
    	
	    st.exit();
	}

   
	public static void printTest(ArrayList<Task> test){
		for (int i =0; i < test.size(); i++)
			System.out.println(test.get(i));
		System.out.println("");
	}
	
	//read from json file
	public Storage(){
    	taskList = new ArrayList<Task>();
    	
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
    
	//add functions
    public boolean add(String taskType, String content, GregorianCalendar start, GregorianCalendar end,
    		int duration, GregorianCalendar reminder, String label, boolean priority){
    	Task newTask = new Task(taskType, content, start, end, duration, reminder, label, priority);
    	taskList.add(newTask);
    	return true;
    }

    //delete function
    public boolean delete(int index){
    	taskList.remove(index);
    	return true;
    }

    //modify function (still working)
    public boolean modify(int index, String taskType, String content, GregorianCalendar start, GregorianCalendar end,
    		int duration, GregorianCalendar reminder, String label, boolean priority){
    	
    	return true;
    }
    
    //query
    public ArrayList<Task> query(){
    	return taskList;
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
    		if (taskList.get(i).getTaskType().equals("deadline") && isSameDate(date,taskList.get(i).getEnd())){
    			returnList.add(taskList.get(i));
    		}
    		else if (taskList.get(i).getTaskType().equals("event") && isBefore(date,taskList.get(i).getEnd()) &&
    				isAfter(date,taskList.get(i).getStart())){
    			returnList.add(taskList.get(i));
    		}
    		
    	}
    	return returnList;
    }

    public ArrayList<Task> query(TASK_TYPE taskType){
    	ArrayList<Task> returnList = new ArrayList<Task>();
    	switch(taskType){
    	case EVENT:
    		for (int i = 0; i < taskList.size(); i++){
    			if (taskList.get(i).getTaskType().equals("event")){
    				returnList.add(taskList.get(i));
    			}
    		} break;
    	case DEADLINE:	
    		for (int i = 0; i < taskList.size(); i++){
    			if (taskList.get(i).getTaskType().equals("deadline")){
    				returnList.add(taskList.get(i));
    			}
    		} break;
    	default:
    		for (int i = 0; i < taskList.size(); i++){
    			if (taskList.get(i).getTaskType().equals("todo")){
    				returnList.add(taskList.get(i));
    			}
    		} break;
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
    public boolean changeStatus(int index){
    	taskList.get(index).setPriority();
    	return true;
    }
    
    //store to json file when exits
    public void exit() throws IOException{
    	
    	//buggy:
    	/*
    	FileWriter fw = new FileWriter("tasks.json");
    	BufferedWriter bw = new BufferedWriter(fw);
    	Gson gson = new Gson();
    	String s= "";
    	bw.write(s);
    	bw.close();
    	FileWriter fw2 = new FileWriter("tasks.json",true);
    	BufferedWriter bw2 = new BufferedWriter(fw2);
    	for (int i = 0;i < taskList.size(); i++){
    		s = gson.toJson(taskList.get(i));
    		bw2.write(s);
    	}
     	bw2.close();*/
    }
}
