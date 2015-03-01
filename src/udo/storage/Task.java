package udo.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	
	public Task() {
	    done = false;
	    priority = false;
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
	
	public Integer getDuration() {
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
	
	public void setPriority(Boolean priority) {
		this.priority = priority;
	}
	
	public void setDone() {
		this.done = !(this.done);
	}
	
	public String toString() {
	    String finalString;

	    if (priority == null || !priority) {
	        finalString = "Task: ";
	    } else {
	        finalString = "Important Task: ";
	    }

		finalString += content + "\n";
		if (deadline != null) {
		    finalString += "  deadline: " + DATE_FORMAT.format(deadline.getTime()) + "\n";
		}
		if (start != null){
			finalString += "  start: " + DATE_FORMAT.format(start.getTime()) + "\n";
		}
		if (end != null){
			finalString += "  end: " + DATE_FORMAT.format(end.getTime()) + "\n";
		}
		if (reminder != null){
			finalString += "  reminder: " + DATE_FORMAT.format(reminder.getTime()) + "\n";
		}
		if (done != null) {
		    finalString += "  done: " + done;
		}
		return finalString;
	}
}