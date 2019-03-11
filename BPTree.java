package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author Aleks Lesiewicz
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    
    
    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        
        this.branchingFactor = branchingFactor;
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        if (root == null) {
            root = new LeafNode();
            root.insert(key, value);
        } else {
            root.insert(key, value);
            
            if (root.isOverflow()) {
                Node sibling = root.split();
                InternalNode newRoot = new InternalNode();
                newRoot.children.add(root);
                newRoot.children.add(sibling);
                
                newRoot.keys.add(sibling.keys.get(0));
                
                if (root instanceof BPTree.InternalNode)
                    sibling.keys.remove(0);
                root = newRoot;
            }
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && 
            !comparator.contentEquals("==") && 
            !comparator.contentEquals("<=") )
            return new ArrayList<V>();
        this.toString();
        
        return root.rangeSearch(key, comparator);
    }
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        List<K> values;
        
        /**
         * Package constructor
         */
        Node() {
            keys = new ArrayList<K>();
            values = new ArrayList<K>();
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();
        
        public String toString() {
            return keys.toString();
        }
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            children = new ArrayList<Node>();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return children.get(0).keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return keys.size() == branchingFactor;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            int childToSearchIndex = 0;
            int i = 0;
            
            //  3,4,5
            // a,b,c,d
            // Find the index to search. While the key is greater than the values
            // Keep incrementing the child index
            while (childToSearchIndex < keys.size() && 
                key.compareTo(keys.get(i++)) > 0)
                childToSearchIndex++;
            
            children.get(childToSearchIndex).insert(key, value);
            
            //TODO Check overflow
            if (children.get(childToSearchIndex).isOverflow()) {
                Node splitNode = children.get(childToSearchIndex).split();
                children.add(childToSearchIndex + 1, splitNode);
                keys.add(childToSearchIndex, splitNode.keys.get(0));
                
                if (splitNode instanceof BPTree.InternalNode)
                    splitNode.keys.remove(0);
            }
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            InternalNode newSibling = new InternalNode();
            int middleIndex = keys.size() / 2;
            
            // Add the keys and values from the middle index and on
            newSibling.keys.addAll(keys.subList(middleIndex, keys.size()));
            newSibling.children.addAll(children.subList(middleIndex + 1, children.size()));
            
            // Then remove those values from this node
            keys.subList(middleIndex, keys.size()).clear();
            children.subList(middleIndex + 1, children.size()).clear();
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            int childToSearchIndex = 0;
            int i = 0;
            
            if (comparator.equals("<=")) {
                while (childToSearchIndex < keys.size() && 
                    key.compareTo(keys.get(i++)) >= 0)
                    childToSearchIndex++;
            } else {
                while (childToSearchIndex < keys.size() && 
                    key.compareTo(keys.get(i++)) > 0)
                    childToSearchIndex++;
            }
           
            
            return children.get(childToSearchIndex).rangeSearch(key, comparator);
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            super();
            values = new ArrayList<V>();
            next = null;
            previous = null;
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return keys.size() == branchingFactor;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            if (keys.size() == 0) {
                keys.add(key);
                values.add(value);
                return;
            }
            int indexToInsert = 0;
            
            while (indexToInsert < keys.size() && 
                key.compareTo(keys.get(indexToInsert)) > 0)
                indexToInsert++;
            
            if (indexToInsert != keys.size()) {
                keys.add(indexToInsert, key);
                values.add(indexToInsert, value);
            } else {
                keys.add(key);
                values.add(value);
            }
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            LeafNode newSibling = new LeafNode();
            int middleIndex = keys.size() / 2;
            
            // Add the keys and values from the middle index and on
            newSibling.keys.addAll(keys.subList(middleIndex, keys.size()));
            newSibling.values.addAll(values.subList(middleIndex, values.size()));
            
            // Then remove those values from this node
            keys.subList(middleIndex, keys.size()).clear();
            values.subList(middleIndex, values.size()).clear();
            
            if (next != null)
                next.previous = newSibling;
            newSibling.next = next;
            
            next = newSibling;
            newSibling.previous = this;
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {            
            if (comparator.equals("==")) {
                List<V> leftSearch = searchLeft(this, comparator, keys.size() - 1, key);
                List<V> rightSearch = searchRight(next, comparator, 0, key);
               
                leftSearch.addAll(rightSearch);
                return leftSearch;
            } else if (comparator.equals("<=")) {
                return searchLeft(this, comparator, keys.size() - 1, key);
            } else {
                return searchRight(this, comparator, 0, key);
            }
            
        }
        
    } // End of class LeafNode
    
    /**
     * Range search to the right
     * @param node The leaf node
     * @param comparator the range search parameter
     * @param index the index to start at
     * @param key the key to search on
     * @return the food items that match the search
     */
    private List<V> searchRight(LeafNode node, String comparator, 
        int index, K key) {
        List<V> list = new ArrayList<V>();
        
        while (node != null) {
            if (comparator.equals(">=") && 
                key.compareTo(node.keys.get(index)) <= 0)
                list.add(node.values.get(index));
            else if (comparator.equals("==") && 
                key.compareTo(node.keys.get(index)) == 0)
                list.add(node.values.get(index));
            
            if (++index == node.keys.size()) {
                node = node.next;
                index = 0;
            }
        }
        
        return list;
    }
    
    /**
     * Range search to the left
     * @param node the node to range search on
     * @param comparator the parameter to search
     * @param index the index to start at
     * @param key the key to search on
     * @return the list that meet the range search criteria
     */
    private List<V> searchLeft(LeafNode node, String comparator, int index, K key) {
        List<V> list = new ArrayList<V>();
        
        while (node != null) {
            if (comparator.equals("<=") &&
                key.compareTo(node.keys.get(index)) >= 0)
                list.add(node.values.get(index));
            else if (comparator.equals("==") &&
                key.compareTo(node.keys.get(index))== 0)
                list.add(node.values.get(index));
        
            if (--index < 0) {
                node = node.previous;
                index = node != null ? node.keys.size() - 1 : 0;
            }
        }
        
        return list;
    }
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            Double j = dd[rnd1.nextInt(4)];

            if (i == 6) {
                System.out.print("");
            }
            
            list.add(j);
            bpTree.insert(j,j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
            //Arrays.toString(list.toArray(null));
        }
        
        //System.out.println(Arrays.toString(list.toArray(new Double[list.size()])));
       
        
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, "<=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree