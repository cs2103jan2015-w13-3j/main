package udo.testdriver;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;


/**
 * This test class only tests the order and date headers after the list is
 * formatted. Unable to check for display properties like colours and styles
 */
public class GUITest {
    
    //Code reuse from Thien
    private void removeExistingTasks() {
        try {
            (new RandomAccessFile("task.json", "rws")).setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test1() {
        removeExistingTasks();
        
        //declare array
        //check expected input and output
    }
    
    
}
