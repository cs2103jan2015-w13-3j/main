package udo.testdriver;

import static org.junit.Assert.assertTrue;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

import java.io.IOException;
import java.io.RandomAccessFile;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;

import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import udo.gui.Gui;

/**
 * This class test the GUI by automatically typing in commands
 * The test case is passed when it satisfies provided conditions
 */
//@author A0112115A

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFx {
	private static GuiTest controller;
	private static Gui ju;
	public static final String TESTS_PATH = "res/tests/gui/";

	@BeforeClass
	public static void setUpClass() {
		removeExistingTasks();
		FXTestUtils.launchApp(Gui.class);

		sleep();

		controller = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return ju.getPrimaryStage().getScene().getRoot();
			}
		};	
	}

	//This method provides a brief pause for tester's viewing
	public static void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void removeExistingTasks() {
		try {
			(new RandomAccessFile("tasks.json", "rws")).setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//A valid task is added
	@Test
	public void test1_1() {
		sleep();

		controller.type("New task");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task: New task added successfully"));
	}

	//An invalid index number is given
	@Test
	public void test1_2() {
		sleep();

		controller.type("Delete 2");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Error: specified task's index is not valid"));
	}

	//A valid index number is given
	@Test
	public void test1_3() {
		sleep();

		controller.push(KeyCode.DOWN);	
		controller.type("Delete 1");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task: New task deleted successfully"));
	}

	//Valid undo
	@Test
	public void test1_4() {
		sleep();

		controller.type("Undo");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Undo completed"));
	}

	//Invalid undo
	@Test
	public void test1_5() {

		sleep();

		controller.type("Undo");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Error: no more operation to undo"));
	}

	//Check if autocomplete and modify work
	@Test
	public void test1_6() {
		sleep();

		controller.push(KeyCode.DOWN);
		controller.type("Mod");
		controller.push(KeyCode.TAB);	
		controller.type(" 1 Date with Emma Watson /dl tomorrow 5pm");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task: Date with Emma Watson modified successfully"));

	}

	//Check if long status is displayed correctly
	@Test
	public void test1_7() {
		sleep();

		controller.push(KeyCode.UP);

		for(int i=0; i<16; i++) {
			controller.push(KeyCode.BACK_SPACE);
		}

		controller.type("and talk with Bill Gates /s 5pm /e 9pm");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task: Date with Emma Watson and talk with Bill... modified successfully"));
	}

	//Check if search free slots works
	@Test
	public void test1_8() {
		sleep();

		controller.push(KeyCode.DOWN);
		controller.type("sea");
		controller.push(KeyCode.TAB);	
		controller.type(" /fr");
		controller.push(KeyCode.TAB);	
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Search results for free slots"));
	}

	//Check if display works
	@Test
	public void test1_9() {
		sleep();

		controller.type("dis");
		controller.push(KeyCode.TAB);	
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Displaying all tasks"));
	}

	//Check if search works
	@Test
	public void test2_1() {
		sleep();

		controller.type("search Emma Watson");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Search results for: Emma Watson"));
	}

	//Check if uncofirmed tasks work
	@Test
	public void test2_2() {
		sleep();

		controller.type("unconfirmed submit report /dl tomorrow 5pm or tomorrow 10pm");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task: unconfirmed submit report added successfully"));
	}

	//Check if confirm tasks work
	@Test
	public void test2_3() {
		sleep();

		controller.type("confirm 3");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Task unconfirmed submit report confirmed"));
	}

	//Check if cd works
	@Test
	public void test2_4() {
		sleep();

		controller.type("cd");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Tasks file is at: tasks.json"));
	}


	//Check if cd works
	@Test
	public void test2_5() {
		sleep();

		controller.type("cd ..");
		controller.push(KeyCode.ENTER);	
		controller.type("cd");
		controller.push(KeyCode.ENTER);	

		sleep();

		verifyThat("#_status",
				hasText("Tasks file is at: ..\\"+"tasks.json"));
	}

	//Check if undo cd works
	@Test
	public void test2_6() {
		sleep();

		controller.type("undo");
		controller.push(KeyCode.ENTER);	
		controller.type("cd");
		controller.push(KeyCode.ENTER);	

		sleep();
		verifyThat("#_status",
				hasText("Tasks file is at: tasks.json"));
	}
	//A valid task is added
		@Test
		public void test2_7() {
			sleep();

			controller.type("New task");
			controller.push(KeyCode.ENTER);	

			controller.type("Done3");
			controller.push(KeyCode.ENTER);
			
			sleep();

			verifyThat("#_status",
					hasText("Task New task is done"));
		}
	//Check if final output files are the same as expected
	@Test
	public void test2_9() {
		sleep();
		assertTrue(IntegrationTest.isContentSimilar("tasks.json", "res/tests/gui/tasks.json"));
		assertTrue(IntegrationTest.isContentSimilar("../tasks.json", "res/tests/gui/tasks.json"));
	}
}
