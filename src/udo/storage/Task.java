package udo.storage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import udo.util.Utility;

public class Task implements Comparable<Task> {
    public static enum TaskType {DEADLINE, EVENT, TODO};

	//class defines Task objects
	private Integer groupId;
    private Integer index;
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
	//The constructor Task(Task.TaskType, String, null, GregorianCalendar, int, GregorianCalendar, String, boolean, boolean)
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
		this.done = done;
	}
	
	public Integer getGroupId(){
		return groupId;
	}
	
	public Integer getIndex(){
		return index;
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

	public void setGroupId(Integer groupId){
		this.groupId = groupId;
	}
	
	public void setIndex(Integer index){
		this.index = index;
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
	    String finalString = "";
	    if (groupId != null){
	    	finalString += "Group: " + groupId + "\n"; 
	    }
	    
	    if (index != null){
	    	finalString += index + ". ";
	    }
	    
	    if (!priority) {
	        finalString += taskType + ": ";
	    } else {
	        finalString += "Important " + taskType + ": ";
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
	    Task copy = new Task();
	    
	    copy.setGroupId(this.getGroupId());
	    copy.setIndex(this.getIndex());
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
	
	@Override
	public int hashCode() {
	    if (getIndex() == null) {
	        return super.hashCode();
	    }
	    
	    return getIndex();
	}
	
	@Override
	public boolean equals(Object taskObj) {
	    if (taskObj == null) {
	        return false;
	    }
	    if (taskObj == this) {
	        return true;
	    }
	    if (!(taskObj instanceof Task)) {
	        return false;
	    }

	    Task task = (Task) taskObj;
	    
	    if (index == null || task.index == null) {
	        return false;
	    }

	    return index.equals(task.index);
	}
	
	@Override
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
        TaskType taskType = task2.getTaskType();
        
        if(!taskType.equals(TaskType.TODO)) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareWithDeadline(Task task2) {
        TaskType taskType = task2.getTaskType();
        GregorianCalendar deadlineTime = this.getDeadline();
        GregorianCalendar deadlineTime2 = task2.getDeadline();
        GregorianCalendar eventStart2 = task2.getStart();
        
        if(taskType.equals(TaskType.TODO)) {
            return -1;
        } else if (taskType.equals(TaskType.DEADLINE)){           
            return deadlineTime.compareTo(deadlineTime2);
        } else {
            return compareDeadlineEvent(deadlineTime, eventStart2, taskType);
        }
    }

    private int compareWithEvent(Task task2) {
        TaskType taskType = task2.getTaskType();
        assert(taskType != null);
        GregorianCalendar eventStart = this.getStart();
        GregorianCalendar eventStart2 = task2.getStart();
        GregorianCalendar deadlineTime2 = task2.getDeadline();

        if(taskType.equals(TaskType.TODO)) {
            return -1;
        } else if (taskType.equals(TaskType.DEADLINE)){
            return compareDeadlineEvent(eventStart, deadlineTime2, taskType);
        } else {           
            return compareStartAndEnd(task2, eventStart, eventStart2);
        }
        
    }

    private int compareStartAndEnd(Task task2, GregorianCalendar eventStart,
                                   GregorianCalendar eventStart2) {
        int comparedStartValue = eventStart.compareTo(eventStart2);
        if(comparedStartValue == 0) {
            return this.getEnd().compareTo(task2.getEnd());
        } else {
            return comparedStartValue;
        }
    }
    
    /**
     * Compares events and deadlines, if they land on the same day, deadlines 
     * will be placed before events
     * 
     * @param cal1
     * @param cal2
     * @param tasktype refers to TaskType of the specified task
     * @return 1 if 2 dates are equal or cal1 is after cal2  
     *         -1 if cal1 is before cal2
     */
    private int compareDeadlineEvent(GregorianCalendar cal1, 
                                     GregorianCalendar cal2, 
                                     TaskType taskType) {        
        if(Utility.isSameDate(cal1, cal2)) {
            return (taskType.equals(TaskType.DEADLINE)) ? 1 : -1;
        } else {
            return cal1.compareTo(cal2);
        }
    }
    
}