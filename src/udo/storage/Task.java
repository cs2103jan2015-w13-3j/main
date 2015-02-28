package udo.storage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {
    public static enum TaskType {DEADLINE, EVENT, TODO};

	//class defines Task objects
	private TaskType taskType;
	private String content;
	private GregorianCalendar deadline;
	private GregorianCalendar start;
	private GregorianCalendar end;
	private Integer duration;
	private GregorianCalendar reminder;
	private String label;
	private Boolean priority;
	private Boolean done;
	
	public Task() {
	}
	
	//constructor
	public Task(TaskType taskType, String content, GregorianCalendar start, GregorianCalendar end, 
	            int duration, GregorianCalendar reminder, String label, boolean priority) {
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
	
	public TaskType getTaskType() {
		return taskType;
	}
	
	public String getContent() {
		return content;
	}
	
	public GregorianCalendar getDeadline() {
	    return deadline;
	}

	public GregorianCalendar getStart() {
		return start;
	}

	public GregorianCalendar getEnd() {
		return end;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public GregorianCalendar getReminder() {
		return reminder;
	}
	
	public String getLabel() {
		return label;
	}

	public boolean isPriority() {
		return priority;
	}
	
	public boolean isDone() {
		return done;
	}

	public void setTaskType(TaskType type) {
		this.taskType = type;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setDeadline(GregorianCalendar deadline) {
	    this.deadline = deadline;
	}
	
	public void setStart(GregorianCalendar start) {
		this.start = start;
	}
	
	public void setEnd(GregorianCalendar end) {
		this.end = end;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setReminder(GregorianCalendar reminder) {
		this.reminder = reminder;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setPriority() {
		this.priority = !(this.priority);
	}
	
	public void setDone() {
		this.done = !(this.done);
	}
	
	public String toString() {
		String finalString = "";

		String startDate = "nil";
		String endDate = "nil";
		String reminderDate = "nil";
		
		if (start != null){
			startDate = start.get(Calendar.DAY_OF_MONTH) + "/" + start.get(Calendar.MONTH) + "/" + start.get(Calendar.YEAR);
		}
		
		if (end != null){
			endDate = end.get(Calendar.DAY_OF_MONTH) + "/" + end.get(Calendar.MONTH) + "/" + end.get(Calendar.YEAR);
		}
		
		if (reminder != null){
			reminderDate = reminder.get(Calendar.DAY_OF_MONTH) + "/" + reminder.get(Calendar.MONTH) + "/" + reminder.get(Calendar.YEAR);
		}
		
		finalString += taskType + "     " + content + "      " + duration + "      " + startDate + "     " + endDate + "    " + reminderDate + "     " + label 
				+ "     ";
		return finalString;
	}
}