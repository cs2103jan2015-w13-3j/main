package udo.testdriver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.junit.Test;

import udo.logic.Logic;
import udo.storage.Storage;
import udo.storage.Task;
import udo.storage.Task.TaskType;

//@author A0093587M
public class LogicTest {
    private void removeExistingTasks() {
        try {
            (new RandomAccessFile("tasks.json", "rws")).setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* A valid case when a new deadline is added */
    @Test
    public void testAdd1() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        Storage storage = new Storage();

        assertEquals(true,
                     logic.executeCommand("go to school /deadline tomorrow"));

        List<Task> tasks = storage.query();

        assertEquals(1, tasks.size());
        assertEquals(TaskType.DEADLINE, tasks.get(0).getTaskType());
        assertEquals("go to school", tasks.get(0).getContent());
    }

    /* A valid case when a new event is added */
    @Test
    public void testAdd2() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        Storage storage = new Storage();

        assertEquals(true,
                     logic.executeCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm"));

        List<Task> tasks = storage.query();

        assertEquals(1, tasks.size());
        assertEquals(TaskType.EVENT, tasks.get(0).getTaskType());
        assertEquals("go to school", tasks.get(0).getContent());
    }

    /* A valid case in which a floating task is added */
    @Test
    public void testAdd3() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        Storage storage = new Storage();

        assertEquals(true,
                     logic.executeCommand("add watch a movie /duration 2 hours 30 minutes"));

        List<Task> tasks = storage.query();

        assertEquals(1, tasks.size());
        assertEquals(TaskType.TODO, tasks.get(0).getTaskType());
        assertEquals("watch a movie", tasks.get(0).getContent());
    }

    /* A valid case where multiple tasks are added */
    @Test
    public void testAdd4() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        Storage storage = new Storage();

        logic.executeCommand("go to school /deadline tomorrow");
        logic.executeCommand("add go to school /start tomorrow 2pm /end tomorrow 4pm");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");

        List<Task> tasks = storage.query();

        assertEquals(3, tasks.size());
    }

    /* This is an invalid case in which the input dates is misspelled */
    @Test
    public void testInvalidDate() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        assertEquals(false,
                     logic.executeCommand("go to school /deadline tomrrow"));
    }
}
