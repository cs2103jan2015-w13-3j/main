package udo.logic;

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
    
    private Node add(String s, Integer pos, Node node) {
        if (node == null) {
            node = new Node(s.charAt(pos), false);
        }
        
        if (s.charAt(pos) < node.myChar) {
            node.left = add(s, pos, node.left);
        } else if (s.charAt(pos) > node.myChar) {
            node.right = add(s, pos, node.right);
        } else {
            if (pos + 1 == s.length()) {
                node.isWordEnd = true;
            } else {
                node.center = add(s, pos + 1, node.center); 
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

    public static void main(String[] args) {
        
    }

}
