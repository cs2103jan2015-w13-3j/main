package udo.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author A0093587M
public class TernarySearchTree {
    protected static class Node {
        private char myChar;
        private Node left, right, center;
        protected Boolean isWordEnd;

        public Node(char myChar, Boolean isWordEnd) {
            this.myChar = myChar;
            this.isWordEnd = isWordEnd;
        }
    }

    private Node root = null;

    private static final Logger log = Logger.getLogger(TernarySearchTree.class.getName());

    /**
     * Add the string s into the search tree
     * @param s the string from which characters are added into the tree
     * @param index the index of the first character to be added
     * @param node the start node of the search tree
     * @return the start of the search tree that contains the added characters
     */
    private Node add(String s, Integer index, Node node) {
        if (index > s.length()) {
            return null;
        }

        if (node == null) {
            node = new Node(s.charAt(index), false);
        }

        if (s.charAt(index) < node.myChar) {
            node.left = add(s, index, node.left);
        } else if (s.charAt(index) > node.myChar) {
            node.right = add(s, index, node.right);
        } else {
            if (index + 1 == s.length()) {
                node.isWordEnd = true;
            } else {
                node.center = add(s, index + 1, node.center);
            }
        }

        return node;
    }

    /**
     * Add the string s into the search tree
     * @param s
     */
    public void add(String s) {
        log.log(Level.FINEST, "Adding " + s);

        if (s == null) {
            return;
        }

        s = s.trim();
        if (s.equalsIgnoreCase("")) {
            return;
        }

        root = add(s, 0, root);
    }

    /**
     * Search if the tree contains the word s
     * @param s
     * @return
     */
    public boolean contains(String s) {
        if (s == null) {
            return false;
        }

        Node n = searchString(s);

        if (n != null && n.isWordEnd) {
            return true;
        }

        return false;
    }

    /**
     * Search for a sequence of characters in the tree that matches
     * s. The sequence does not have to form a full word
     * @param s
     * @return
     */
    private Node searchString(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        int index = 0;
        Node curNode = root;

        while (curNode != null) {
            if (s.charAt(index) < curNode.myChar) {
                curNode = curNode.left;
            } else if (s.charAt(index) > curNode.myChar) {
                curNode = curNode.right;
            } else {
                index++;
                if (index == s.length()) {
                    return curNode;
                }
                curNode = curNode.center;
            }
        }

        return null;
    }

    /**
     * Search a return a list of words with the specified prefix
     * @param prefix
     * @param numWords upperbound for number of words to return in the list
     *                 if this is null, there will be no upperbound
     * @return a list of words with specified prefix
     */
    public List<String> searchPrefix(String prefix, Integer numWords) {
        if (prefix == null) {
            return new ArrayList<String>();
        }

        Node n = searchString(prefix);

        if (n != null) {
            if (n.isWordEnd) {
                return searchTree(prefix, true, n.center, numWords);
            } else {
                return searchTree(prefix, false, n.center, numWords);
            }
        }

        return new ArrayList<String>();
    }

    /**
     * Search a return a list of words with the specified prefix
     * @param prefix
     * @return
     */
    public List<String> searchPrefix(String prefix) {
        return searchPrefix(prefix, null);
    }

    /**
     * Find words in the tree rooted at some node appended by prefix
     * @param prefix the prefix from the root to this tree
     * @param prefixIncluded indicate whether the prefix is a valid word
     * @param node the root of the tree for the search
     * @param numWords maximum number of words to search for or
     *                 search for all words if this is null
     * @return list of found words
     */
    private List<String> searchTree(String prefix, Boolean prefixIncluded,
                                    Node node, Integer numWords) {
        List<Character> curWord = stringToCharList(prefix);
        List<String> result = new ArrayList<>();

        if (prefixIncluded) {
            result.add(prefix);
        }
        searchTreeHelper(node, numWords, curWord, result);

        return result;
    }

    private List<Character> stringToCharList(String prefix) {
        List<Character> result = new ArrayList<>();

        if (prefix != null) {
            for (int i = 0; i < prefix.length(); i++) {
                result.add(prefix.charAt(i));
            }
        }

        return result;
    }

    private void searchTreeHelper(Node node, Integer numWords,
                                  List<Character> curWord,
                                  List<String> result) {
        if (numWords != null && result.size() >= numWords) {
            return;
        }
        if (node == null) {
            return;
        }

        searchTreeHelper(node.left, numWords, curWord, result);
        if (numWords != null && result.size() == numWords) {
            return;
        }

        curWord.add(node.myChar);

        if (node.isWordEnd) {
            result.add(charListToString(curWord));
        }
        searchTreeHelper(node.center, numWords, curWord, result);

        curWord.remove(curWord.size() - 1);

        searchTreeHelper(node.right, numWords, curWord, result);
    }

    private String charListToString(List<Character> curWord) {
        StringBuilder builder = new StringBuilder(curWord.size());

        for (char c : curWord) {
            builder.append(c);
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        TernarySearchTree t = new TernarySearchTree();

        t.add("cat"); t.add("category"); t.add("catalyzt");
        t.add("dog"); t.add("dogmatic"); t.add("dogwood");
        t.add("add"); t.add("addition"); t.add("additional");

        List<String> r = t.searchPrefix("");
        for (String s : r) {
            System.out.println(s);
        }

        System.out.println(t.contains("dogwood"));
        System.out.println(t.contains("catalyst"));
    }

}
