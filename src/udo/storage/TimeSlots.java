package udo.storage;

import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.Collections;
import udo.storage.Task.TaskType;

public class TimeSlots {
	private ArrayList<Task> occupiedSlots;
	private ArrayList<Task> freeSlots;
	
	public TimeSlots(){
		occupiedSlots = new ArrayList<Task>();
		freeSlots = new ArrayList<Task>();
	}
	
	public TimeSlots(ArrayList<Task> taskList){
		occupiedSlots = new ArrayList<Task>();
		freeSlots = new ArrayList<Task>();
		for (int i = 0; i < taskList.size(); i++){
			if (taskList.get(i).getTaskType() == TaskType.EVENT){
				occupiedSlots.add(taskList.get(i).copy());
			}
		}
		Collections.sort(occupiedSlots, new StartTimeComparator());
		mergeSlots();
		setFreeSlots();
	}

	private void mergeSlots(){
		for (int i = 0; i <occupiedSlots.size() -1; i++){
			if (occupiedSlots.get(i).getEnd().compareTo(occupiedSlots.get(i+1).getStart()) >= 0){
				occupiedSlots.get(i).setEnd(occupiedSlots.get(i+1).getEnd());
				occupiedSlots.remove(occupiedSlots.get(i+1));
				i--;
			}
		}
	}
	
	private void setFreeSlots(){
		for (int i = 0; i < occupiedSlots.size() -1; i++){
			Task temp = new Task();
			temp.setStart(occupiedSlots.get(i).getEnd());
			temp.setEnd(occupiedSlots.get(i+1).getStart());
			freeSlots.add(temp);
		}
	}

	public ArrayList<Task> getOccupiedSlots(){
		return occupiedSlots;
	}
	
	public ArrayList<Task> getFreeSlots(){
		return freeSlots;
	}
}
