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
import udo.util.Config;
import udo.util.Utility;

public class Autocompleter {
    private static final String SEPARATOR = " ";

    // Used to store command and options keywords
    TernarySearchTree keywordsTree;
    // Used to store words from english dictionary
    TernarySearchTree dictTree;
    // Used to store words extracted from tasks' content
    TernarySearchTree taskContentTree;

    List<String> commandHistory;

    private static final Logger log = Logger.getLogger(
                                          Autocompleter.class.getName());

    String dictPath = "res/dict.txt";
    String keywordsPath = "res/keywords.txt";

    public Autocompleter() {
        keywordsTree = new TernarySearchTree();
        dictTree = new TernarySearchTree();
        taskContentTree = new TernarySearchTree();

        commandHistory = new LinkedList<String>();

        addKeywordsToTree();
        addDictWordsToTree();
    }

    /**
     * Add all words in all tasks' content in the task list
     * which don't appear in the dictionary or keywords
     * @param tasks
     */
    public void addTaskContentToTree(List<Task> tasks) {
        for (Task t : tasks) {
            String[] tokens = t.getContent().split("\\s");
            for (String token : tokens) {
                if (!keywordsTree.contains(token) &&
                    !dictTree.contains(token)) {
                    taskContentTree.add(token);
                }
            }
        }
    }

    /**
     * Add all words contained in the file in keywordsPath to the
     * keywords ternary search tree
     */
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

    /**
     * Add all words read from the dictionary store in dictPath to the
     * dictionary ternary search tree
     */
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
    private String getLastWord(String text) {
        String[] tokens = text.split("\\s");

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

        String lastWord = getLastWord(text);
        String lastWordLower = lastWord.toLowerCase();

        List<String> keywordsList = null;
        List<String> dictWordsList;
        List<String> contentWordsList;

        ArrayList<String> result = new ArrayList<>();

        if (lastWord != null && lastWord.length() > 0) {
            keywordsList = retrieveWords(keywordsTree,
                                         maxWords, lastWordLower);
            result.addAll(keywordsList);

            contentWordsList = retrieveWords(taskContentTree,
                                             maxWords, lastWord);
            result.addAll(contentWordsList);

            dictWordsList = retrieveWords(dictTree,
                                          maxWords, lastWordLower);
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

    /**
     * Fill in the the remaining of text with the serialized content
     * of the the given task
     * @param text
     * @param task
     * @return autocompleted string
     */
    public String autocomplete(String text, Task task) {
        String taskStr = taskToCmdStr(task);

        if (text != null && text.length() > 0) {
            if (Character.isWhitespace(text.charAt(text.length() - 1))) {
                return text + taskStr;
            } else {
                return text + SEPARATOR + taskStr;
            }
        }

        return text;
    }

    /**
     * Convert a task data structure to an equivalent command string
     * @param task
     * @return
     */
    private String taskToCmdStr(Task task) {
        switch (task.getTaskType()) {
            case DEADLINE:
                return deadlineTaskToCmdStr(task);
            case EVENT:
                return eventTaskToCmdStr(task);
            case TODO:
                return todoTaskToCmdStr(task);
        }

        return "";
    }

    /**
     * Convert a todo task datastructure to its equivalent command string
     * @param task
     * @return
     */
    private String todoTaskToCmdStr(Task task) {
        assert(task != null);
        return task.getContent();
    }

    /**
     * Convert an event task data structure to its equivalent command string
     * @param task
     * @return
     */
    private String eventTaskToCmdStr(Task task) {
        StringBuilder builder = new StringBuilder();

        assert(task.getContent() != null);
        builder.append(task.getContent());
        builder.append(SEPARATOR);

        assert(task.getStart() != null);
        appendOptionStr(builder, Config.OPT_START[Config.OPT_LONG],
                        Utility.calendarToString(task.getStart()));

        assert(task.getEnd() != null);
        appendOptionStr(builder, Config.OPT_END[Config.OPT_LONG],
                        Utility.calendarToString(task.getEnd()));

        if (task.getReminder() != null) {
            appendOptionStr(builder, Config.OPT_REMINDER[Config.OPT_LONG],
                            Utility.calendarToString(task.getReminder()));
        }

        if (task.getPriority()) {
            appendOptionStr(builder, Config.OPT_PRIO[Config.OPT_LONG], "");
        }

        return builder.toString();
    }

    /**
     * Convert a deadline task datastructure to its equivalent command string
     * @param task
     * @return
     */
    private String deadlineTaskToCmdStr(Task task) {
        StringBuilder builder = new StringBuilder();

        assert(task.getContent() != null);
        builder.append(task.getContent());
        builder.append(SEPARATOR);

        assert(task.getDeadline() != null);
        appendOptionStr(builder, Config.OPT_DEADLINE[Config.OPT_LONG],
                        Utility.calendarToString(task.getDeadline()));

        if (task.getReminder() != null) {
            appendOptionStr(builder, Config.OPT_REMINDER[Config.OPT_LONG],
                            Utility.calendarToString(task.getReminder()));
        }

        if (task.getPriority()) {
            appendOptionStr(builder, Config.OPT_PRIO[Config.OPT_LONG], "");
        }

        return builder.toString();
    }

    private void appendOptionStr(StringBuilder builder,
                                 String option,
                                 String argument) {
        builder.append(Config.OPTION_MAKER);
        builder.append(option);
        builder.append(SEPARATOR);
        builder.append(argument);
        builder.append(SEPARATOR);
    }

    /**
     * Remove the last word from the given text
     * @param text
     * @return
     */
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

        System.out.println(completer.getSuggestions("/", 5));
        System.out.println(completer.getSuggestions("d "));
        System.out.println(completer.getSuggestions("d"));

        System.out.println(completer.autocomplete("ad"));
        System.out.println(completer.autocomplete("sing a song /de"));
        System.out.println(completer.autocomplete("do homework "));
        System.out.println(completer.autocomplete("go for meeting /start tomo"));
        System.out.println(completer.autocomplete("submit report /dl next fri"));
    }
}
