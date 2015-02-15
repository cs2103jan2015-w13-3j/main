package udo.logic;

import java.util.List;

public class TernarySearchTree {
    public static class Node {
        private char myChar;
        private Node left, right, center;
        private Boolean isWordEnd;
        
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
                return searchTree(curNode, numWords);
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
        return null;
    }

    public static void main(String[] args) {
        
    }

}
