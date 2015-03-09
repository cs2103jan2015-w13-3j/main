package udo.storage;

import java.util.GregorianCalendar;

import udo.util.Utility;

public class Task implements Comparable<Task> {
    public static enum TaskType {DEADLINE, EVENT, TODO};

	//class defines Task objects
	private TaskType taskType;
	private String content;
	private String deadline;
	private String start;
	private String end;
	private Integer duration;
	private String reminder;
	private String label;
	private boolean priority;
	private boolean done;

	public Task() {
	    done = false;
	    priority = false;
	}
	
	//constructor
	public Task(TaskType taskType, String content, GregorianCalendar deadline,
	            GregorianCalendar start, GregorianCalendar end, int duration,
	            GregorianCalendar reminder, String label, boolean priority, boolean done) {
		this.taskType = taskType;
		this.content = content;
		this.deadline = Utility.calendarToString(deadline);
		this.start = Utility.calendarToString(start);
		this.end = Utility.calendarToString(end);
		this.duration = duration;
		this.reminder = Utility.calendarToString(reminder);
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
	    return Utility.stringToCalendar(deadline);
	}

	public GregorianCalendar getStart() {
		return Utility.stringToCalendar(start);
	}

	public GregorianCalendar getEnd() {
		return Utility.stringToCalendar(end);
	}
	
	public Integer getDuration() {
		return duration;
	}
	
	public GregorianCalendar getReminder() {
		return Utility.stringToCalendar(reminder);
	}
	
	public String getLabel() {
		return label;
	}

	public boolean getPriority() {
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
	    this.deadline = Utility.calendarToString(deadline);
	}
	
	public void setStart(GregorianCalendar start) {
		this.start = Utility.calendarToString(start);
	}
	
	public void setEnd(GregorianCalendar end) {
		this.end = Utility.calendarToString(end);
	}
	
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public void setReminder(GregorianCalendar reminder) {
		this.reminder = Utility.calendarToString(reminder);
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

	    if (!priority) {
	        finalString = taskType + ": ";
	    } else {
	        finalString = "Important " + taskType + ": ";
	    }

		finalString += content + "\n";

		if (deadline != null) {
		    finalString += "  deadline: " + deadline + "\n";
		}
		if (start != null) {
			finalString += "  start: " + start + "\n";
		}
		if (end != null) {
			finalString += "  end: " + end + "\n";
		}
		if (reminder != null) {
			finalString += "  reminder: " + reminder + "\n";
		}
		finalString += "  done: " + done;
		return finalString;
	}
	
	public Task copy() {
	    // TODO: COPY index
	    Task copy = new Task();
	    copy.setTaskType(taskType);
	    copy.setContent(content);
	    copy.setDeadline(this.getDeadline());
	    copy.setStart(this.getStart());
	    copy.setEnd(this.getEnd());
	    copy.setDuration(this.getDuration());
	    copy.setReminder(this.getReminder());
	    copy.setLabel(this.getLabel());
	    if (this.isDone()) copy.setDone();

	    return copy;
	}
	
	public int compareTo(Task task2) {
	    TaskType taskType = this.getTaskType();
	    	    
	    switch (taskType) {
	        case EVENT:
	            return compareWithEvent(task2);
	        case DEADLINE:
	            return compareWithDeadline(task2);
	        case TODO:
	            return compareWithTodo(task2);	            
	    }
	    return 0;
	}
	
    /**
     * Todo tasks are always displayed at the end
     */	
    private int compareWithTodo(Task task2) {           
        TaskType task2TaskType = task2.getTaskType();
        
        if(!task2TaskType.equals(TaskType.TODO)) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareWithDeadline(Task task2) {
        TaskType task2TaskType = task2.getTaskType();
        GregorianCalendar cal1 = this.getDeadline();
        
        if(task2TaskType.equals(TaskType.TODO)) {
            return 1;
        } else if (task2TaskType.equals(TaskType.DEADLINE)){
            GregorianCalendar cal2 = task2.getDeadline();
            return cal1.compareTo(cal2);
        } else {
            GregorianCalendar cal2 = task2.getStart();
            return cal1.compareTo(cal2);
        }
    }

    private int compareWithEvent(Task task2) {
        TaskType task2TaskType = task2.getTaskType();
        assert(task2TaskType != null);
        GregorianCalendar cal1 = this.getStart();
        System.out.println("IN COMPARABLE" + task2);
        if(task2TaskType.equals(TaskType.TODO)) {
            return -1;
        } else if (task2TaskType.equals(TaskType.DEADLINE)){
            GregorianCalendar cal2 = task2.getDeadline();
            return cal1.compareTo(cal2);
        } else {
            GregorianCalendar cal2 = task2.getStart();
            return cal1.compareTo(cal2);
        }
        
    }
}