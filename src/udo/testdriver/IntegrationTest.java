package udo.testdriver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import udo.logic.Logic;

//@author A0093587M
public class IntegrationTest {
    public static final String REPORT_LINE_DIFF =
            "line %d:\nexpected: %s\nactual  : %s";
    public static final String REPORT_EMPTY_LINE = "<<empty>>";
    public static final String TESTS_PATH = "res/tests/integration/";

    public static boolean isContentSimilar(String expectedFile,
                                           String actualFile) {
        BufferedReader expectedReader = null;
        BufferedReader actualReader = null;

        try {
            expectedReader = new BufferedReader(
                                 new FileReader(expectedFile));
            actualReader = new BufferedReader(
                                 new FileReader(actualFile));

            String expectedStr = expectedReader.readLine();
            String actualStr = actualReader.readLine();

            boolean isSimilar = true;
            int currentLine = 1;

            while (expectedStr != null || actualStr != null) {
                if (expectedStr == null) {
                    System.out.println(String.format(REPORT_LINE_DIFF,
                                                     currentLine,
                                                     REPORT_EMPTY_LINE,
                                                     actualStr));
                    isSimilar = false;
                } else if (actualStr == null) {
                    System.out.println(String.format(REPORT_LINE_DIFF,
                                                     currentLine,
                                                     expectedStr,
                                                     REPORT_EMPTY_LINE));
                    isSimilar = false;
                } else if (!expectedStr.equals(actualStr.trim())) {
                    System.out.println(String.format(REPORT_LINE_DIFF,
                                                     currentLine,
                                                     expectedStr,
                                                     actualStr));
                    isSimilar = false;
                }

                currentLine++;
                expectedStr = expectedReader.readLine();
                actualStr = actualReader.readLine();
            }

            expectedReader.close();
            actualReader.close();

            return isSimilar;
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return false;
        } catch (IOException e) {
            System.out.println("IO Error");
            return false;
        }
    }

    private static void removeExistingTasks() {
        try {
            (new RandomAccessFile("tasks.json", "rws")).setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTestTitle(int testIndex) {
        System.out.println("Integration test " + testIndex);
        System.out.println("==================");
    }

    public static void test1() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        logic.executeCommand("go to school /deadline April 1, 3015 1pm");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm /end April 1, 3015 4pm");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");

        printTestTitle(1);
        if (isContentSimilar(TESTS_PATH + "expected1.txt", "tasks.json")) {
            System.out.println("TEST1 PASSED");
        } else {
            System.out.println("TEST1 FAILED");
        }
    }

    public static void test2() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        logic.executeCommand("go to school /deadline April 1, 3015 1pm");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm /end April 1, 3015 4pm");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm or April 2, 3015 3pm or April 3, 3015 5pm");

        printTestTitle(2);
        if (isContentSimilar(TESTS_PATH + "expected2.txt", "tasks.json")) {
            System.out.println("TEST2 PASSED");
        } else {
            System.out.println("TEST2 FAILED");
        }
    }

    public static void test3() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        logic.executeCommand("go to school /deadline April 1, 3015 1pm");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm /end April 1, 3015 4pm");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm or April 2, 3015 3pm or April 3, 3015 5pm");
        logic.executeCommand("submit project report /deadline April 18, 3015 23:59pm /reminder April 13, 3015 1pm /important");
        logic.executeCommand("display");

        printTestTitle(3);
        if (isContentSimilar(TESTS_PATH + "expected3.txt", "tasks.json")) {
            System.out.println("TEST3 PASSED");
        } else {
            System.out.println("TEST3 FAILED");
        }
    }

    public static void test4() {
        removeExistingTasks();

        Logic logic = Logic.getInstance();
        logic.setGui(new GuiStub());

        logic.executeCommand("go to school /deadline April 1, 3015 1pm");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm /end April 1, 3015 4pm");
        logic.executeCommand("add watch a movie /duration 2 hours 30 minutes");
        logic.executeCommand("add group meeting /start April 1, 3015 2pm or April 2, 3015 3pm or April 3, 3015 5pm");
        logic.executeCommand("submit project report /deadline April 18, 3015 23:59pm /reminder April 13, 3015 1pm /important");
        logic.executeCommand("undo");
        logic.executeCommand("go to school /deadline April 10, 3015 2pm");
        logic.executeCommand("undo");

        printTestTitle(4);
        if (isContentSimilar(TESTS_PATH + "expected4.txt", "tasks.json")) {
            System.out.println("TEST4 PASSED");
        } else {
            System.out.println("TEST4 FAILED");
        }
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }
}
