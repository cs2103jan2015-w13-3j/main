package udo.storage;
import java.util.GregorianCalendar;

//import org.json.simple.*;

import com.google.gson.Gson;

import java.io.*;

public class Storage {
    File storageFile;
    
	public static void main(String[] args) throws IOException{
        // TODO Auto-generated method stub
    	Storage st = new Storage();
		boolean function = st.add("event", "meeting", new GregorianCalendar(2005,01,01), new GregorianCalendar(2005,01,03),
    			new GregorianCalendar(2005,01,02), "work", true);
    	if (function) System.out.println("Adding successfully");
	}

    public Storage(){
    	try{
    		FileReader fr = new FileReader("tasks.json");
    		fr.close();
    	} catch (IOException e){
    		storageFile = new File("tasks.json");
    	}
    }
    
    public boolean add(String taskType, String content, GregorianCalendar start, GregorianCalendar end,
    		GregorianCalendar reminder, String label, boolean priority) throws IOException{
    	BufferedWriter bw = new BufferedWriter(new FileWriter("tasks.json", true));
    	Tasks newTask = new Tasks(taskType, content, start, end, reminder, label, priority);
    	Gson gson = new Gson();
    	String addingString = gson.toJson(newTask);
    	bw.write(addingString);
    	bw.close();
    	return true;
    }
}
