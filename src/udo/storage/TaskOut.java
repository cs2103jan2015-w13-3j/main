package udo.storage;

import java.util.Calendar;

import udo.storage.Task.TaskType;

public class TaskOut {
	//class defines Task objects
	private TaskType taskType;
	private String content;
	private int duration;
	private String label;
	private boolean priority;
	private boolean done;
	private String start;
	private String end;
	private String reminder;

	//constructor
	//Task Outmatted
	public TaskOut(TaskType taskType, String content, String start, String end, 
			int duration, String reminder, String label, boolean priority){
		this.taskType = taskType;
		this.content = content;
		this.start = start;
		this.end = end;
		this.duration = duration;
		this.reminder = reminder;
		this.label = label;
		this.priority = priority;
		this.done = false;
	}
	public String getStartOut(){
		return start;
	}
	public String getEndOut(){
		return end;
	}
	public String getReminderOut(){
		return reminder;
	}
	//Task Default
	public TaskType getTaskType(){
		return taskType;
	}

	public String getContent(){
		return content;
	}

	public int getDuration(){
		return duration;
	}

	public String getLabel(){
		return label;
	}

	public boolean isPriority(){
		return priority;
	}

	public boolean isDone(){
		return done;
	}

	public void setTaskType(TaskType type){
		this.taskType = type;
	}

	public void setContent(String content){
		this.content = content;
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public void setPriority(){
		this.priority = !(this.priority);
	}

	public void setDone(){
		this.done = !(this.done);
	}

	public String toString(){
		String finalString = "";

		String startDate = "nil";
		String endDate = "nil";
		String reminderDate = "nil";

		if (start != null){
			startDate = start;
		}


		if (end != null){
			endDate = end;
		}

		if (reminder != null){
			reminderDate = reminder;
		}

		finalString += taskType + "     " + content + "      " + duration + "      " + startDate + "     " + endDate + "    " + reminderDate + "     " + label 
				+ "     ";
		return finalString;
	}
}

