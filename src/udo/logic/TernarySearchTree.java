package udo.logic;

import java.util.ArrayList;
import java.util.List;

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
    
    private Node add(String s, Integer index, Node node) {
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
    
    public void add(String s) {
        if (s == null) {
            return;
        }
        
        s = s.trim();
        if (s.equalsIgnoreCase("")) {
            return;
        }
        
        root = add(s, 0, root);
    }
    
    public List<String> searchPrefix(String prefix, Integer numWords) {
        int index = 0;
        Node curNode = root;
        
        while (curNode != null) {
            if (index == prefix.length()) {
                return searchTree(curNode.center, numWords);
            }
            
            if (prefix.charAt(index) < curNode.myChar) {
                curNode = curNode.left;
            } else if (prefix.charAt(index) > curNode.myChar) {
                curNode = curNode.right;
            } else {
                index++;
                curNode = curNode.center;
            }
        }
        
        return null;
    }
    
    public List<String> searchPrefix(String prefix) {
        return searchPrefix(prefix, null);
    }
    
    /**
     * Find words in the tree rooted at some node
     * @param node the root of the tree for the search
     * @param numWords maximum number of words to search for or
     *                 search for all words if this is null
     * @return list of found words
     */
    private List<String> searchTree(Node node, Integer numWords) {
        List<Character> curWord = new ArrayList<>();
        List<String> result = new ArrayList<>();
        
        searchTreeHelper(node, numWords, curWord, result);
        
        return result;
    }

    private void searchTreeHelper(Node node, Integer numWords,
                                  List<Character> curWord,
                                  List<String> result) {
        if (numWords != null && result.size() == numWords) {
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
        
    }

}
