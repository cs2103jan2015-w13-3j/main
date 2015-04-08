package udo.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import udo.storage.Task;
import udo.util.Config;
import udo.util.Utility;

//@author A0093587M
public class Autocompleter {
    private static final String SEPARATOR = " ";
	private static final SimpleDateFormat fmt =
	        new SimpleDateFormat("MMM d, yyyy HH:mm");

    // Used to store command and options keywords
    TernarySearchTree keywordsTree;
    // Used to store words from english dictionary
    TernarySearchTree dictTree;
    // Used to store words extracted from tasks' content
    TernarySearchTree taskContentTree;

    private Logic logic;

    private static final int historyMaxSize = 50;
    private List<String> cmdHistory;
    private ListIterator<String> cmdHistoryIter;
    private static enum HistoryOp {NONE, PREV, NEXT};
    private HistoryOp prevHistoryOp;

    private static final Logger log = Logger.getLogger(
                                          Autocompleter.class.getName());

    String dictPath = "/english.txt";
    String keywordsPath = "/keywords.txt";

    private int tabsCount;
    private List<String> lastSuggestions;

    public Autocompleter() {
        tabsCount = 0;
        lastSuggestions = new ArrayList<>();

        keywordsTree = new TernarySearchTree();
        dictTree = new TernarySearchTree();
        taskContentTree = new TernarySearchTree();
        addPrioritizedWords(taskContentTree);

        cmdHistory = new LinkedList<String>();
        cmdHistoryIter = cmdHistory.listIterator();
        prevHistoryOp = HistoryOp.NONE;

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
                token = token.toLowerCase();

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
            InputStream wordsStream = this.getClass().
                    getResourceAsStream(keywordsPath);
            reader = new BufferedReader(new InputStreamReader(wordsStream));

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
            InputStream wordsStream = getClass().
                    getResourceAsStream(dictPath);
            reader = new BufferedReader(new InputStreamReader(wordsStream));

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

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    /********************************
     * Code for autocompleting words*
     *******************************/

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
     * Get a list of suggested words that can possible autocompleted
     * from the input text with the maximum of maxWords words in the list
     * @param text
     * @param maxWords
     * @return list of suggested words
     */
    public List<String> getSuggestions(String text, Integer maxWords) {
        tabsCount = 0;

        if (maxWords != null && maxWords <= 0) {
            lastSuggestions.clear();
            return lastSuggestions;
        }

        String lastWord = getLastWord(text).toLowerCase();

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
            lastSuggestions = result.subList(0, maxWords);
            return lastSuggestions;
        }

        lastSuggestions = result;
        return lastSuggestions;
    }

    /**
     * Get the last word in a text string
     * @param text
     * @return the last word in the text or empty string if the last
     *         character is a white space
     */
    private String getLastWord(String text) {
        if (text.length() == 0 ||
            Character.isWhitespace(text.charAt(text.length() - 1))) {
            return "";
        }

        String[] tokens = text.split("\\s");

        if (tokens != null && tokens.length > 0) {
            return tokens[tokens.length - 1];
        }

        return "";
    }

    /**
     * Get and return the last word in the text string
     * Trailing white spaces are ignored
     * @param text
     * @return
     */
    private String getLastWordIgnoreSpace(String text) {
        String[] tokens = text.split("\\s");

        if (tokens != null && tokens.length > 0) {
            return tokens[tokens.length - 1];
        }

        return "";
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
        List<String> suggestions;

        if (lastSuggestions.size() == 0) {
            suggestions = getSuggestions(text, 2);
        } else {
            suggestions = lastSuggestions;
        }

        if (suggestions.size() > 0) {
            assert(tabsCount < suggestions.size());

            String lastWord = getLastWord(text);
            String autocompletedWord = suggestions.get(tabsCount);

            if (lastWord.equals(autocompletedWord) && suggestions.size() > 1) {
                tabsCount = (tabsCount + 1) % suggestions.size();
                autocompletedWord = suggestions.get(tabsCount);
            }

            tabsCount = (tabsCount + 1) % suggestions.size();

            return dropLastWord(text) + autocompletedWord;
        }

        return text;
    }

    /********************************
     * Code for autocompleting tasks*
     *******************************/

    /**
     * Fill in the the remaining of text with the serialized content
     * of the the given task
     * @param text
     * @param task
     * @return autocompleted string
     */
    public String autocomplete(String text, Task task) {
        assert(text != null && text.length() > 0);

        String[] tokens = text.split("\\s");
        String autocompletedPart;

        if (tokens.length > 2) {
            String lastWord = getLastWordIgnoreSpace(text);
            autocompletedPart = autocompleteOption(lastWord, task);

            if (autocompletedPart.equals("")) {
                return autocomplete(text);
            }
        } else {
            autocompletedPart = taskToCmdStr(task);
        }

        logic.updateGuiStatus("");
        tabsCount = 0;
        lastSuggestions.clear();

        if (Character.isWhitespace(text.charAt(text.length() - 1))) {
            return text + autocompletedPart;
        } else {
            return text + SEPARATOR + autocompletedPart;
        }
    }

    private String autocompleteOption(String lastWord, Task task) {
        if (lastWord.length() == 0 ||
            lastWord.charAt(0) != Config.OPTION_MARKER_CHAR) {
            return "";
        }

        lastWord = lastWord.substring(1);

        if (InputParser.isDeadlineOption(lastWord) &&
            task.getDeadline() != null) {
            return Utility.calendarToString(task.getDeadline(), fmt);
        }
        if (InputParser.isStartOption(lastWord) &&
            task.getStart() != null) {
            return Utility.calendarToString(task.getStart(), fmt);
        }
        if (InputParser.isEndOption(lastWord) &&
            task.getEnd() != null) {
            return Utility.calendarToString(task.getEnd(), fmt);
        }
        if (InputParser.isReminderOption(lastWord) &&
            task.getReminder() != null) {
            return Utility.calendarToString(task.getReminder(), fmt);
        }

        return "";
    }

    /**
     * Convert a task data structure to an equivalent command string
     * @param task
     * @return
     */
    private String taskToCmdStr(Task task) {
        assert(task != null);

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
        StringBuilder builder = new StringBuilder();

        assert(task.getContent() != null);
        builder.append(task.getContent());
        builder.append(SEPARATOR);

        if (task.getReminder() != null) {
            appendOptionStr(builder, Config.OPT_REMINDER[Config.OPT_LONG],
                            Utility.calendarToString(task.getReminder(), fmt));
        }

        if (task.getPriority()) {
            appendOptionStr(builder, Config.OPT_PRIO[Config.OPT_LONG], "");
        }

        return builder.toString();
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
                        Utility.calendarToString(task.getStart(), fmt));

        assert(task.getEnd() != null);
        appendOptionStr(builder, Config.OPT_END[Config.OPT_LONG],
                        Utility.calendarToString(task.getEnd(), fmt));

        if (task.getReminder() != null) {
            appendOptionStr(builder, Config.OPT_REMINDER[Config.OPT_LONG],
                            Utility.calendarToString(task.getReminder(), fmt));
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
                        Utility.calendarToString(task.getDeadline(), fmt));

        if (task.getReminder() != null) {
            appendOptionStr(builder, Config.OPT_REMINDER[Config.OPT_LONG],
                            Utility.calendarToString(task.getReminder(), fmt));
        }

        if (task.getPriority()) {
            appendOptionStr(builder, Config.OPT_PRIO[Config.OPT_LONG], "");
        }

        return builder.toString();
    }

    private void appendOptionStr(StringBuilder builder,
                                 String option,
                                 String argument) {
        builder.append(Config.OPTION_MARKER);
        builder.append(option);
        builder.append(SEPARATOR);

        if (!argument.equals("")) {
            builder.append(argument);
            builder.append(SEPARATOR);
        }
    }

    private void addPrioritizedWords(TernarySearchTree tree) {
        tree.add("december");
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

    /*******************************************
     * Code for autocompleting command history *
     ******************************************/

    /**
     * Add a command text to the commands history
     * @param cmd
     */
    public void addToHistory(String cmd) {
        if (cmdHistory.size() >= historyMaxSize) {
            cmdHistory.remove(0);
        }

        cmdHistory.add(cmd);

        cmdHistoryIter = cmdHistory.listIterator(cmdHistory.size());
        prevHistoryOp = HistoryOp.NONE;
    }

    /**
     * @return the previous command text in history
     */
    public String getPreviousCmd() {
        log.fine("Getting previous command in history");
        assert(cmdHistoryIter != null);

        if (prevHistoryOp == HistoryOp.NEXT) {
            cmdHistoryIter.previous();
        }

        if (cmdHistoryIter.hasPrevious()) {
            prevHistoryOp = HistoryOp.PREV;
            return cmdHistoryIter.previous();
        } else if (cmdHistory.size() > 0) {
            prevHistoryOp = HistoryOp.PREV;
            cmdHistoryIter = cmdHistory.listIterator(cmdHistory.size());
            return cmdHistoryIter.previous();
        }

        return "";
    }

    /**
     * @return the next command text in history
     */
    public String getNextCmd() {
        log.fine("Getting next command in history");
        assert(cmdHistoryIter != null);

        if (prevHistoryOp == HistoryOp.PREV) {
            cmdHistoryIter.next();
        }

        if (cmdHistoryIter.hasNext()) {
            prevHistoryOp = HistoryOp.NEXT;
            return cmdHistoryIter.next();
        }

        prevHistoryOp = HistoryOp.NONE;
        return "";
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
