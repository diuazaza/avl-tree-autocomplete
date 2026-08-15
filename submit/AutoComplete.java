
/**
* AutoComplete.java
* Implements spell checking, prefix-based autocomplete, and next-letter
* frequency analysis using an AVLTree.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import itsc2214.AVLNode;
import itsc2214.AVLTree;

/**
 * Provides spell checking, prefix-based autocomplete,
 * and next-letter frequency mapping using an AVL tree structure.
 * ITSC2214 Summer 2026
 *
 * @author dpate235
 */
public class AutoComplete {

    /**
     * Default constructor for the AutoComplete class.
     */
    public AutoComplete() {
        // Default constructor
    }

    /**
     * Checks if a given word exists in the provided AVLTree.
     *
     * @param tree the AVLTree containing dictionary words
     * @param word the word to check for valid spelling
     * @return true if the word exists in the tree, false otherwise
     */
    public boolean validSpelling(AVLTree<String> tree, String word) {
        if (tree == null || word == null) {
            return false;
        }

        return tree.contains(word);
    }

    /**
     * Finds matching prefix words, sorts them by length ascending,
     * and trims the output list to the specified limit.
     *
     * @param tree   the AVLTree containing dictionary words
     * @param prefix the prefix to match words against
     * @param limit  maximum number of results to return
     * @return an ArrayList of matching words sorted by length
     */
    public ArrayList<String> autoComplete(AVLTree<String> tree, String prefix, int limit) {
        ArrayList<String> matches = new ArrayList<String>();

        if (tree == null || prefix == null || tree.getRoot() == null) {
            return matches;
        }

        autoComplete(tree.getRoot(), prefix, matches);

        Collections.sort(matches, new Comparator<String>() {
            @Override
            public int compare(String string1, String string2) {
                return Integer.compare(string1.length(), string2.length());
            }
        });

        if (limit > 0 && limit < matches.size()) {
            return new ArrayList<String>(matches.subList(0, limit));
        }

        return matches;
    }

    /**
     * Performs a pre-order traversal of the tree starting at the root node
     * to collect all words starting with the prefix.
     *
     * @param root   current root node in traversal
     * @param prefix the prefix to search for
     * @param list   accumulator list storing matching words
     * @return the list of collected words
     */
    private ArrayList<String> autoComplete(
            AVLNode<String> root, String prefix, ArrayList<String> list) {
        if (root == null) {
            return list;
        }

        String word = root.getValue();
        if (word != null && word.startsWith(prefix)) {
            list.add(word);
        }

        autoComplete(root.getLeft(), prefix, list);
        autoComplete(root.getRight(), prefix, list);

        return list;
    }

    /**
     * Identifies potential next letters following a prefix across a list of
     * matching words and counts their occurrences.
     *
     * @param list   matching words starting with the prefix
     * @param prefix the prefix string preceding the target next letter
     * @return a HashMap mapping next characters to their counts
     */
    public HashMap<Character, Integer> possibleNextLetter(ArrayList<String> list, String prefix) {
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();

        if (list == null || prefix == null) {
            return map;
        }

        int prefixLen = prefix.length();

        for (String word : list) {
            if (word != null && word.startsWith(prefix) && word.length() > prefixLen) {
                char nextChar = word.charAt(prefixLen);
                int currentCount = 0;

                if (map.containsKey(nextChar)) {
                    currentCount = map.get(nextChar);
                }

                map.put(nextChar, currentCount + 1);
            }
        }

        return map;
    }

    /**
     * Loads words from an external file and adds them to an
     * AVLTree. It returns the tree to be used in other
     * parts of the project.
     *
     * @param filename name of the file to load
     * @return AVLTree created with the words added
     */
    public AVLTree<String> loadWords(String filename) {
        AVLTree<String> tree = new AVLTree<String>();
        try {
            File myObj = new File(filename);
            Scanner scan = new Scanner(myObj);
            while (scan.hasNext()) {
                tree.add(scan.nextLine().trim());
            }
            scan.close();
        } catch (FileNotFoundException f) {
        }
        return tree;
    }
}