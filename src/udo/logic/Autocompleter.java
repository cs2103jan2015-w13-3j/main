package udo.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import udo.util.Config.CommandName;

public class Autocompleter {
    // Used to store command and options keywords
    TernarySearchTree keywordsTree;
    // Used to store words from english dictionary
    TernarySearchTree dictTree;
    // Used to store words extracted from tasks' content
    TernarySearchTree taskContentTree;
    
    List<String> commandHistory;
    
    String dictPath = "res/dict.txt";

    public Autocompleter() {
        keywordsTree = new TernarySearchTree();
        dictTree = new TernarySearchTree();
        taskContentTree = new TernarySearchTree();
        
        commandHistory = new LinkedList<String>();
        
        addKeywordsToTree(keywordsTree);
        addDictWordsToTree(dictTree);
    }

    private void addKeywordsToTree(TernarySearchTree keywordsTree) {
        for (CommandName cmdName : CommandName.values()) {
            keywordsTree.add(cmdName.toString().toLowerCase());
        }
    }

    private void addDictWordsToTree(TernarySearchTree dictTree) {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(dictPath));
            String s = reader.readLine();

            while (s != null) {
                dictTree.add(s);
                s = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get a list of suggested words that can possible autocompleted
     * from the input text
     * @param text
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text) {
        List<String> result = getSuggestions(text, null);
        return result;
    }

    /**
     * Get the last word in a text string
     * @param text
     * @return
     */
    private String getLastWord(String[] text) {
        if (text != null && text.length > 0) {
            return text[text.length - 1];
        }

        return null;
    }

    /**
     * Get a list of suggested words that can possible autocompleted
     * from the input text with the maximum of maxWords words in the list
     * @param text
     * @param maxWords
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text, Integer maxWords) {
        String[] tokenizedString = text.split("\\s");
        String lastWord = getLastWord(tokenizedString);

        List<String> keywordsList = null;
        List<String> dictWordsList;
        List<String> contentWordsList;
        
        ArrayList<String> result = new ArrayList<>();

        if (lastWord != null) {
            if (tokenizedString.length == 1) {
                keywordsList = keywordsTree.searchPrefix(lastWord);
                if (keywordsList != null) {
                    result.addAll(keywordsList);
                }
            }

            if (tokenizedString.length == 1 ||
                keywordsList == null || keywordsList.size() == 0) {

                contentWordsList = taskContentTree.searchPrefix(lastWord);
                if (contentWordsList != null) {
                    result.addAll(contentWordsList);
                }

                if (maxWords == null) {
                    dictWordsList = dictTree.searchPrefix(lastWord);
                } else {
                    dictWordsList = dictTree.searchPrefix(lastWord, maxWords);
                }
                if (dictWordsList != null) {
                    result.addAll(dictWordsList);
                }
            }
        }

        return result; 
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
        Autocompleter completer = new Autocompleter();
        System.out.println(completer.getSuggestions("/", 5));
    }
}