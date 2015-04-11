package udo.testdriver;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFx {
	private static GuiTest controller;
	private static Gui ju;
	@BeforeClass
	public static void setUpClass() {
		removeExistingTasks();
		FXTestUtils.launchApp(Gui.class);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// here is that closure I talked about above, you instantiate the
		// getRootNode abstract method
		// which requires you to return a 'parent' object, luckily for us,
		// getRoot() gives a parent!
		// getRoot() is available from ALL Node objects, which makes it easy.
		controller = new GuiTest() {

			@Override
			protected Parent getRootNode() {
				return ju.getPrimaryStage().getScene().getRoot();
			}
		};	
	}
	private static void removeExistingTasks() {
		try {
			(new RandomAccessFile("tasks.json", "rws")).setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test1() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.type("New task");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Task: New task added sucessfully"));
	}
	@Test
	public void test2() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.type("Delete 2");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Error: specified task's index is not valid"));
	}
	@Test
	public void test3() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.push(KeyCode.DOWN);	
		controller.type("Delete 1");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Error: specified task's index is not valid"));
	}
	@Test
	public void test4() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.type("Undo");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Undo completed"));
	}
	@Test
	public void test5() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.type("Undo");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("No more operation to undo"));
	}
	@Test
	public void test6() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.push(KeyCode.DOWN);
		controller.type("Mod");
		controller.push(KeyCode.TAB);	
		controller.type(" 1 Date with Emma Watson /dl 5pm");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Nah Nah"));

	}
	@Test
	public void test7() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.push(KeyCode.UP);
		for(int i=0; i<7; i++) {
			controller.push(KeyCode.BACK_SPACE);
		}
		controller.type("Date with Emma Watson modified sucessfully");
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Search results for free slots"));

	}
	@Test
	public void test8() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.push(KeyCode.DOWN);
		controller.type("sea");
		controller.push(KeyCode.TAB);	
		controller.type(" /fr");
		controller.push(KeyCode.TAB);	
		controller.push(KeyCode.ENTER);	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verifyThat("#_status",
				hasText("Search results for free slots"));
	}
}
