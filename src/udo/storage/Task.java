package udo.storage;

import java.util.GregorianCalendar;

import udo.util.Utility;

/**
 * This class defines, creates and modifies the task object
 * @author A0113038U
 *
 */
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
	
	//@author A0093587M
	@Override
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
	    copy.setPriority(this.priority);
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

	//@author A0114906J
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
    private int compareWithTodo(Task task) {           
        TaskType taskType = task.getTaskType();
        
        if(!taskType.equals(TaskType.TODO)) {
            return 1;
        } else {
            return compareLex(this, task);
        }
    }

    private int compareWithDeadline(Task task) {
        TaskType taskType = task.getTaskType();
        GregorianCalendar deadlineTime = this.getDeadline();
        GregorianCalendar eventStart = task.getStart();
        
        switch(taskType) {
            case EVENT:
                return compareDeadlineWithEvent(deadlineTime, eventStart, taskType);
            case DEADLINE:
                return compareTwoDeadlines(this, task);
            case TODO: 
                return -1;
            default: 
                return 0;
        }
    }

    private int compareWithEvent(Task task) {
        TaskType taskType = task.getTaskType();
        assert(taskType != null);
        GregorianCalendar eventStart = this.getStart();
        GregorianCalendar deadlineTime = task.getDeadline();

        switch(taskType) {
            case EVENT:
                return compareTwoEvents(task);
            case DEADLINE:
                return compareDeadlineWithEvent(eventStart, deadlineTime, taskType);
            case TODO: 
                return -1;
            default: 
                return 0;
        }
    }
    
    /**
     * Compares 2 Tasks of event types. Sorts them in order of start time, 
     * end time and lexicographically
     * 
     * @param task
     * @return
     */
    private int compareTwoEvents(Task task) {
        
        int comparisonStartValue = this.getStart().compareTo(task.getStart());
        int comparisonEndValue = this.getEnd().compareTo(task.getEnd());
        
        if(comparisonStartValue != 0 ) {
            return comparisonStartValue;
        } else if (comparisonEndValue != 0) {
            return comparisonEndValue;
        } else {
            return compareLex(this, task);
        }
    }
    
    /**
     * Compares events and deadlines, if they land on the same day, deadlines 
     * will be placed before events
     * 
     * @param cal1 is the caller's calendar to be compared
     * @param cal2 is the specified calendar to be compared to
     * @param tasktype refers to TaskType of the specified task
     * @return 1 if 2 dates are equal or cal1 is after cal2  
     *         -1 if cal1 is before cal2
     */
    private int compareDeadlineWithEvent(GregorianCalendar cal1, 
                                         GregorianCalendar cal2, 
                                         TaskType taskType) {        
        if(Utility.isSameDate(cal1, cal2)) {
            return (taskType.equals(TaskType.DEADLINE)) ? 1 : -1;
        } else {
            return cal1.compareTo(cal2);
        }
    }
    
    private int compareTwoDeadlines(Task task, 
                                    Task specifiedTask) {
        GregorianCalendar time = task.getDeadline();
        GregorianCalendar specifiedTime = specifiedTask.getDeadline();

        int comparisonValue = time.compareTo(specifiedTime);
        
        return (comparisonValue == 0) ? compareLex(task, specifiedTask)
                                      : comparisonValue;
    }
    
    private int compareLex(Task task, Task specifiedTask) {
        String title = task.getContent();
        String specifiedTitle = specifiedTask.getContent();
        
        return title.compareTo(specifiedTitle);
    }
    
}