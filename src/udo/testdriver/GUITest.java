package udo.testdriver;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import udo.gui.GUI;
import udo.gui.GUIFormatter;


/**
 * This test class only tests the order and date headers after the list is
 * formatted. Unable to check for display properties like colours and styles
 */
public class GUITest {
    
    //Code reuse from Thien
    private void removeExistingTasks() {
        File settingFile = new File("setting.txt");
        if (settingFile.isFile()) {
            settingFile.delete();
        }

        File tasksFile = new File("setting.txt");
        if (tasksFile.isFile()) {
            tasksFile.delete();
        }
    }
    
    @Test
    public void test1() {
        removeExistingTasks();
        
        //declare array
        //check expected input and output
    }
    
    
}
