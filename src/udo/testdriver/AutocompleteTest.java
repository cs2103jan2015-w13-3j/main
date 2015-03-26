package udo.testdriver;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import udo.logic.Autocompleter;
import udo.logic.TernarySearchTree;

public class AutocompleteTest {

    private void setUpTree(TernarySearchTree t) {
        t.add("cat"); t.add("category"); t.add("catalyzt");
        t.add("dog"); t.add("dogmatic"); t.add("dogwood");
        t.add("add"); t.add("addition"); t.add("additional");
    }

    @Test
    public void ternarySearchTreeTest1() {
        TernarySearchTree t = new TernarySearchTree();
        setUpTree(t);

        List<String> expected = new ArrayList<>();
        expected.add("add");
        expected.add("addition");
        expected.add("additional");

        List<String> actual = t.searchPrefix("ad");

        assertEquals(expected, actual);
    }

    @Test
    public void ternarySearchTreeTest2() {
        TernarySearchTree t = new TernarySearchTree();
        setUpTree(t);

        List<String> expected = new ArrayList<>();
        List<String> actual1 = t.searchPrefix("abc");
        List<String> actual2 = t.searchPrefix("cad");

        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

    @Test
    public void ternarySearchTreeTest3() {
        TernarySearchTree t = new TernarySearchTree();
        setUpTree(t);

        List<String> expected = new ArrayList<>();
        expected.add("cat");
        expected.add("catalyzt");
        expected.add("category");

        List<String> actual = t.searchPrefix("cat");

        assertEquals(expected, actual);
    }

    @Test
    public void ternarySearchTreeTestEmpty() {
        TernarySearchTree t = new TernarySearchTree();
        setUpTree(t);

        t.add("");
        assertEquals(0, t.searchPrefix("").size());
    }

    @Test
    public void testSuggestionsNonPositive() {
        Autocompleter autocompleter = new Autocompleter();

        assertEquals(0, autocompleter.getSuggestions("ad", 0).size());
        assertEquals(0, autocompleter.getSuggestions("ad", -1).size());
    }

    @Test
    public void testSuggestionsEmpty() {
        Autocompleter autocompleter = new Autocompleter();

        assertEquals(0, autocompleter.getSuggestions("").size());
        assertEquals(0, autocompleter.getSuggestions("modify ").size());
    }

    @Test
    public void testSuggetionOptions() {
        Autocompleter autocompleter = new Autocompleter();

        List<String> expected = new ArrayList<String>();
        expected.add("/d");
        expected.add("/deadline");
        expected.add("/dl");
        expected.add("/do");
        expected.add("/done");

        assertEquals(expected, autocompleter.getSuggestions("/", 5));
    }

    @Test
    public void testDictAutocomplete1() {
        Autocompleter autocompleter = new Autocompleter();

        assertEquals("add", autocompleter.autocomplete("ad"));
        assertEquals("sing a song /deadline",
                     autocompleter.autocomplete("sing a song /de"));
        assertEquals("do homework ", autocompleter.autocomplete("do homework "));
        assertEquals("go for meeting /start tomorrow",
                     autocompleter.autocomplete("go for meeting /start tomo"));
        assertEquals("submit report /dl next friday",
                     autocompleter.autocomplete("submit report /dl next fr"));
    }

    @Test
    public void testHistoryAutocompleteEmtpy() {
        Autocompleter completer = new Autocompleter();

        assertEquals("", completer.getPreviousCmd());
        assertEquals("", completer.getNextCmd());
    }

    @Test
    public void testHistoryAutocomplete1() {
        Autocompleter completer = new Autocompleter();

        completer.addToHistory("command 1");
        completer.addToHistory("command 2");
        completer.addToHistory("command 3");
        completer.addToHistory("command 4");

        assertEquals("command 4", completer.getPreviousCmd());
        assertEquals("command 3", completer.getPreviousCmd());
        assertEquals("command 4", completer.getNextCmd());

        assertEquals("", completer.getNextCmd());

        assertEquals("command 4", completer.getPreviousCmd());
        assertEquals("command 3", completer.getPreviousCmd());
        assertEquals("command 2", completer.getPreviousCmd());
        assertEquals("command 1", completer.getPreviousCmd());
        assertEquals("command 4", completer.getPreviousCmd());
        assertEquals("command 3", completer.getPreviousCmd());
    }
}
