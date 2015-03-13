package udo.logic;

import java.util.LinkedList;
import java.util.List;

public class Autocompleter {
    TernarySearchTree keywordsTree;
    TernarySearchTree dictTree;
    
    List<String> commandHistory;
    
    String dictPath = "res/dict.txt";

    public Autocompleter() {
        /* TODO: Create ternary search trees and add words to them
         * Initialize commands history
         */
        keywordsTree = new TernarySearchTree();
        dictTree = new TernarySearchTree();
        
        commandHistory = new LinkedList<String>();
        
        addDictWordsToTree(dictTree);
        addKeywordsToTree(keywordsTree);
    }

    private void addKeywordsToTree(TernarySearchTree keywordsTree2) {
        // TODO Auto-generated method stub
        
    }

    private void addDictWordsToTree(TernarySearchTree dictTree2) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Get a list of suggested words that can possible autocompleted
     * from the input text
     * @param text
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text) {
        return null;
    }

    /**
     * Get a list of suggested words that can possible autocompleted
     * from the input text with the maximum of maxWords words in the list
     * @param text
     * @param maxWords
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text, int maxWords) {
        return null;
    }
    
    /**
     * Fill in the autocompleted word for the last word in text
     * @param text
     * @return the autocompleted string
     */
    public String autocomplete(String text) {
        return null;
    }
    
    /**
     * Add a command text to the commands history
     * @param cmd
     */
    public void addToHistory(String cmd) {
        
    }
    
    /**
     * @return the previous command text in history
     */
    public String getPreviousCmd() {
        return null;
    }
    
    /**
     * @return the next command text in history
     */
    public String getNextCmd() {
        return null;
    }

    public static void main(String[] args) {

    }
}