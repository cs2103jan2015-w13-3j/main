package udo.storage;
import java.util.Comparator;
import java.util.GregorianCalendar;
/**
 * @author A0113038U
 */
public class StartTimeComparator implements Comparator<Task> {
	@Override
	public int compare(Task task1, Task task2) {
		GregorianCalendar start1 = task1.getStart();
		GregorianCalendar start2 = task2.getStart();
		return start1.compareTo(start2);
	}
}
