package udo.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.util.Config.CommandName;

public class Autocompleter {
    // Used to store command and options keywords
    TernarySearchTree keywordsTree;
    // Used to store words from english dictionary
    TernarySearchTree dictTree;
    // Used to store words extracted from tasks' content
    TernarySearchTree taskContentTree;

    List<String> commandHistory;

    private static final Logger log = Logger.getLogger(Autocompleter.class.getName());

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
            log.log(Level.SEVERE, e.toString(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.toString(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, e.toString(), e);
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
    private String getLastWord(String[] tokens, String text) {
        if (Character.isWhitespace(text.charAt(text.length() - 1))) {
            return "";
        }

        if (tokens != null && tokens.length > 0) {
            return tokens[tokens.length - 1];
        }

        return "";
    }

    /**
     * Get a list of suggested words that can possible autocompleted
     * from the input text with the maximum of maxWords words in the list
     * @param text
     * @param maxWords
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text, Integer maxWords) {
        String[] tokens = text.split("\\s");
        String lastWord = getLastWord(tokens, text);

        List<String> keywordsList = null;
        List<String> dictWordsList;
        List<String> contentWordsList;

        ArrayList<String> result = new ArrayList<>();

        if (lastWord != null && lastWord.length() > 0) {
            keywordsList = getKeywordsList(tokens, lastWord);
            result.addAll(keywordsList);

            contentWordsList = retrieveWords(taskContentTree,
                                             maxWords, lastWord);
            result.addAll(contentWordsList);

            dictWordsList = retrieveWords(dictTree,
                                          maxWords, lastWord);
            result.addAll(dictWordsList);
        }

        if (maxWords != null && result.size() > maxWords) {
            return result.subList(0, maxWords);
        }

        return result;
    }

    /**
     * Retrieve a maximum of maxWords words from the tree starting with
     * the specified prefix
     * @param maxWords
     * @param lastWord
     * @return
     */
    private List<String> retrieveWords(TernarySearchTree tree,
                                       Integer maxWords, String prefix) {
        List<String> wordsList = null;

        if (maxWords == null) {
            wordsList = tree.searchPrefix(prefix);
        } else {
            wordsList = tree.searchPrefix(prefix, maxWords);
        }

        return wordsList;
    }

    /**
     * @param tokenizedString
     * @param lastWord
     * @return
     */
    private List<String> getKeywordsList(String[] tokenizedString,
                                         String lastWord) {
        List<String> result = null;

        if (tokenizedString.length == 1) {
            result = keywordsTree.searchPrefix(lastWord);
        }

        if (result == null) {
            return new ArrayList<String>();
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
        System.out.println(completer.getSuggestions("d "));
        System.out.println(completer.getSuggestions("d"));
    }
}
