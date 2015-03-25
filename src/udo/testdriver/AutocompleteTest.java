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

}
