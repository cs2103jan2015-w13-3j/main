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

import udo.storage.Task;

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
    String keywordsPath = "res/keywords.txt";

    public Autocompleter() {
        keywordsTree = new TernarySearchTree();
        dictTree = new TernarySearchTree();
        taskContentTree = new TernarySearchTree();

        commandHistory = new LinkedList<String>();

        addKeywordsToTree();
        addDictWordsToTree();
        addDateTimeWords();
    }

    private void addDateTimeWords() {

    }

    public void addTaskContentToTree(Task task) {
        String[] tokens = task.getContent().split("//s");
        for (String token : tokens) {
            token = token.toLowerCase();
            if (!keywordsTree.contains(token) &&
                !dictTree.contains(token)) {
                taskContentTree.add(token);
            }
        }
    }

    public void addTaskContentToTree(List<Task> tasks) {
        for (Task t : tasks) {
            String[] tokens = t.getContent().split("//s");
            for (String token : tokens) {
                token = token.toLowerCase();
                if (!keywordsTree.contains(token) &&
                    !dictTree.contains(token)) {
                    taskContentTree.add(token);
                }
            }
        }
    }

    private void addKeywordsToTree() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(keywordsPath));
            String s = reader.readLine();

            while (s != null) {
                keywordsTree.add(s);
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

    private void addDictWordsToTree() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(dictPath));
            String s = reader.readLine();

            while (s != null) {
                if (!keywordsTree.contains(s)) {
                    dictTree.add(s);
                }
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
     * Get the last word in a text string, a space optimized version
     * where the text is tokenized beforehand
     * @param text
     * @return
     */
    private String getLastWord(String[] tokens, String text) {
        if (text.length() == 0 ||
            Character.isWhitespace(text.charAt(text.length() - 1))) {
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
        if (maxWords != null && maxWords <= 0) {
            return new ArrayList<String>();
        }

        String[] tokens = text.split("\\s");
        String lastWord = getLastWord(tokens, text).toLowerCase();

        List<String> keywordsList = null;
        List<String> dictWordsList;
        List<String> contentWordsList;

        ArrayList<String> result = new ArrayList<>();

        if (lastWord != null && lastWord.length() > 0) {
            keywordsList = retrieveWords(keywordsTree,
                                         maxWords, lastWord);
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
     * Fill in the autocompleted word for the last word in text
     * @param text
     * @return the autocompleted string
     */
    public String autocomplete(String text) {
        List<String> suggestions = getSuggestions(text, 1);

        if (suggestions.size() > 0) {
            return dropLastWord(text) + suggestions.get(0);
        }

        return text;
    }

    private String dropLastWord(String text) {
        int endIndex = getLastWhitespaceIndex(text);

        if (endIndex == -1) {
            return "";
        }

        return text.substring(0, endIndex + 1);
    }

    private int getLastWhitespaceIndex(String text) {
        return Math.max(text.lastIndexOf(" "), text.lastIndexOf("\t"));
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

        /*
        System.out.println(completer.getSuggestions("/", 5));
        System.out.println(completer.getSuggestions("d "));
        System.out.println(completer.getSuggestions("d"));
        */

        System.out.println(completer.autocomplete("ad"));
        System.out.println(completer.autocomplete("sing a song /de"));
        System.out.println(completer.autocomplete("do homework "));
        System.out.println(completer.autocomplete("go for meeting /start tomo"));
        System.out.println(completer.autocomplete("submit report /dl next fri"));
    }
}
