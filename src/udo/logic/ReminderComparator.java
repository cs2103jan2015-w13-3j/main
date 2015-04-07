package udo.logic;
import java.util.Comparator;

import java.util.GregorianCalendar;

import udo.storage.Task;
/**
 * @author A0112115A
 */
public class ReminderComparator implements Comparator<Task> {
	@Override
	public int compare(Task task1, Task task2) {
		GregorianCalendar reminder1 = task1.getReminder();
		GregorianCalendar reminder2 = task2.getReminder();
		return reminder1.compareTo(reminder2);
	}
}
