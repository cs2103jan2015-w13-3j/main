package udo.testdriver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import udo.logic.InputParser;
import udo.logic.command.Command;

//@author A0093587M
public class ParserTest {
    private static final String testsDir = "res/tests/parser/";

    public String readFile(String filename) {
        String result = null;

        try {
            result = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Test
    public void test1() {
        String[] inputs = readFile(testsDir + "input1.txt").
                              split(System.lineSeparator());
        String[] expected = readFile(testsDir + "expected1.txt").split("#");

        InputParser parser = new InputParser();

        assert(inputs.length == expected.length);

        for (int i = 0; i < inputs.length; i++) {
            assertEquals(expected[i].trim(),
                         ignoreOptions(parser.parseCommand(inputs[i])));
        }
    }

    private String ignoreOptions(Command cmd) {
        String result = "Command: " + cmd.getCommandName().toString() + System.lineSeparator();

        if (cmd.getArgIndex() != null) {
            result += "Index: " + cmd.getArgIndex() + System.lineSeparator();
        }
        if (cmd.getArgStr() != null && !cmd.getArgStr().trim().equals("")) {
            result += "Argument: " + cmd.getArgStr() + System.lineSeparator();
        }

        return result.trim();
    }

}
