package udo.storage;

import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.Collections;
import udo.storage.Task.TaskType;

/**
 * This class processes the task list to determine
 * the occupied time slots and free time slots
 */

//@author A0113038U
public class TimeSlots {
    private static final String CONTENT_FREE_SLOT = "Free slot";

	private ArrayList<Task> occupiedSlots;
	private ArrayList<Task> freeSlots;
	
	public TimeSlots(){
		occupiedSlots = new ArrayList<Task>();
		freeSlots = new ArrayList<Task>();
	}
	
	public TimeSlots(ArrayList<Task> taskList){
		occupiedSlots = new ArrayList<Task>();
		freeSlots = new ArrayList<Task>();
		
		addEventTasks(taskList);
		
		Collections.sort(occupiedSlots, new StartTimeComparator());
		
		mergeSlots();
		setFreeSlots();
	}

	private void addEventTasks(ArrayList<Task> taskList) {
		for (int i = 0; i < taskList.size(); i++){
			if (taskList.get(i).getTaskType() == TaskType.EVENT){
				occupiedSlots.add(taskList.get(i).copy());
			}
		}
	}

	private void mergeSlots(){
		for (int i = 0; i <occupiedSlots.size() -1; i++){
			if (occupiedSlots.get(i).getEnd().compareTo(occupiedSlots.get(i+1).getStart()) >= 0){
					if(occupiedSlots.get(i).getEnd().compareTo(occupiedSlots.get(i+1).getEnd()) <= 0){
						occupiedSlots.get(i).setEnd(occupiedSlots.get(i+1).getEnd());
					}	
						occupiedSlots.remove(occupiedSlots.get(i+1));
						i--;
			}
		}
	}
	
	private void setFreeSlots(){
		for (int i = 0; i < occupiedSlots.size() -1; i++){
			Task temp = new Task();

			temp.setTaskType(TaskType.EVENT);
			temp.setContent(CONTENT_FREE_SLOT);
			temp.setStart(occupiedSlots.get(i).getEnd());
			temp.setEnd(occupiedSlots.get(i+1).getStart());
			
			temp.setIndex(freeSlots.size());
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
