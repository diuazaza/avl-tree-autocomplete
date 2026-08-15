
/**
* AutoCompleteTest.java
* Unit tests validating the AutoComplete class methods.
*/

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import itsc2214.AVLTree;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Unit test class for validating the AutoComplete class methods.
 * ITSC2214 Summer 2026
 *
 * @author dpate235
 */
public class AutoCompleteTest {

    // Rule to automatically manage temporary test files during test execution
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private AutoComplete ac;
    private AVLTree<String> testTree;

    /**
     * Sets up testing environment before each unit test execution.
     */
    @Before
    public void setUp() {
        // Instantiate clean AutoComplete instance and test tree
        ac = new AutoComplete();
        testTree = new AVLTree<String>();

        // Populate test AVL tree with initial dataset
        testTree.add("capitalist");
        testTree.add("captain");
        testTree.add("captivate");
        testTree.add("captivating");
        testTree.add("captive");
        testTree.add("captivity");
        testTree.add("captor");
        testTree.add("capture");
        testTree.add("apple");
    }

    /**
     * Tests validSpelling method for words in and out of the tree.
     */
    @Test
    public void testValidSpelling() {
        // Assert true for words present in the tree
        assertTrue(ac.validSpelling(testTree, "captain"));
        assertTrue(ac.validSpelling(testTree, "apple"));

        // Assert false for missing word and null input
        assertFalse(ac.validSpelling(testTree, "banana"));
        assertFalse(ac.validSpelling(testTree, null));
    }

    /**
     * Tests validSpelling method when a null tree is passed in.
     */
    @Test
    public void testValidSpellingNullTree() {
        // Verify null tree edge case safely returns false
        assertFalse(ac.validSpelling(null, "captain"));
    }

    /**
     * Tests autoComplete method returning all matching items ordered by length.
     */
    @Test
    public void testAutoCompleteUnlimited() {
        // Fetch matches without limit (-1)
        ArrayList<String> results = ac.autoComplete(testTree, "cap", -1);

        // Verify total match count and length-based sorting order ("captor" len 6,
        // "captain" len 7, etc.)
        assertEquals(8, results.size());
        assertEquals("captor", results.get(0));
        assertEquals("captain", results.get(1));
        assertEquals("capture", results.get(3));
    }

    /**
     * Tests autoComplete method when restricted by a positive limit.
     */
    @Test
    public void testAutoCompleteWithLimit() {
        // Request top 3 matches starting with "cap"
        ArrayList<String> results = ac.autoComplete(testTree, "cap", 3);

        // Verify size is capped at 3 and items remain ordered by length
        assertEquals(3, results.size());
        assertEquals("captor", results.get(0));
        assertEquals("captain", results.get(1));
        assertEquals("captive", results.get(2));
    }

    /**
     * Tests autoComplete method when the limit exceeds the number of
     * available matches, expecting the full match list to be returned.
     */
    @Test
    public void testAutoCompleteLimitExceedsSize() {
        // Set limit higher than total tree nodes
        ArrayList<String> results = ac.autoComplete(testTree, "cap", 100);

        // Expect entire set of 8 matches without array out of bounds issues
        assertEquals(8, results.size());
    }

    /**
     * Tests autoComplete method when no words match the given prefix.
     */
    @Test
    public void testAutoCompleteNoMatches() {
        // Search for non-existent prefix
        ArrayList<String> results = ac.autoComplete(testTree, "xyz", -1);

        // Verify empty list is returned
        assertTrue(results.isEmpty());
    }

    /**
     * Tests autoComplete method when a null tree is passed in.
     */
    @Test
    public void testAutoCompleteNullTree() {
        // Pass null tree parameter
        ArrayList<String> results = ac.autoComplete(null, "cap", -1);

        // Expect empty list response
        assertTrue(results.isEmpty());
    }

    /**
     * Tests possibleNextLetter method counts for given word prefixes.
     */
    @Test
    public void testPossibleNextLetter() {
        // Collect "cap" prefix matches
        ArrayList<String> results = ac.autoComplete(testTree, "cap", -1);

        // Get character frequency map following "cap"
        HashMap<Character, Integer> counts = ac.possibleNextLetter(results, "cap");

        // Verify character frequency mappings ('i' in capitalist -> 1, 't' in
        // captain/captivate/etc -> 7)
        assertEquals(Integer.valueOf(1), counts.get('i'));
        assertEquals(Integer.valueOf(7), counts.get('t'));

        // Assert unmapped character returns null
        assertNull(counts.get('z'));
    }

    /**
     * Tests possibleNextLetter method when given an empty list of words.
     */
    @Test
    public void testPossibleNextLetterEmptyList() {
        // Pass empty match list
        HashMap<Character, Integer> counts = ac.possibleNextLetter(new ArrayList<String>(), "cap");

        // Expect empty frequency map
        assertTrue(counts.isEmpty());
    }

    /**
     * Tests loadWords method correctly reads words from a file into a tree.
     *
     * @throws IOException if the temporary test file cannot be created
     */
    @Test
    public void testLoadWords() throws IOException {
        // Create temporary test file and write sample dictionary words
        File wordsFile = tempFolder.newFile("words.txt");
        try (PrintWriter writer = new PrintWriter(wordsFile)) {
            writer.println("alpha");
            writer.println("beta");
            writer.println("gamma");
        }

        // Load words into tree via file reader method
        AVLTree<String> loadedTree = ac.loadWords(wordsFile.getAbsolutePath());

        // Verify written words exist in populated tree and missing words return false
        assertTrue(ac.validSpelling(loadedTree, "alpha"));
        assertTrue(ac.validSpelling(loadedTree, "beta"));
        assertTrue(ac.validSpelling(loadedTree, "gamma"));
        assertFalse(ac.validSpelling(loadedTree, "delta"));
    }

    /**
     * Tests loadWords method returns an empty tree when the file
     * does not exist.
     */
    @Test
    public void testLoadWordsFileNotFound() {
        // Attempt loading non-existent file path
        AVLTree<String> emptyTree = ac.loadWords("nonexistent_file_12345.txt");

        // Verify loaded tree is empty and returns false for checks
        assertFalse(ac.validSpelling(emptyTree, "anything"));
    }

    /**
     * Tests line 61 conditions: tree == null, prefix == null, or tree.getRoot() ==
     * null.
     */
    @Test
    public void testAutoCompleteConditionCoverage() {
        // Test null tree, null prefix, and empty tree guard clauses
        assertTrue(ac.autoComplete(null, "cap", 5).isEmpty());
        assertTrue(ac.autoComplete(testTree, null, 5).isEmpty());

        AVLTree<String> emptyTree = new AVLTree<String>();
        assertTrue(ac.autoComplete(emptyTree, "cap", 5).isEmpty());
    }

    /**
     * Tests line 96 conditions: word != null and word.startsWith(prefix).
     */
    @Test
    public void testAutoCompleteHelperConditions() {
        // Test handling of tree with null values
        AVLTree<String> treeWithNullNode = new AVLTree<String>();
        treeWithNullNode.add(null);
        ArrayList<String> nullResults = ac.autoComplete(treeWithNullNode, "cap", -1);
        assertTrue(nullResults.isEmpty());

        // Test handling of tree with non-matching prefix entries
        AVLTree<String> treeWithNonMatching = new AVLTree<String>();
        treeWithNonMatching.add("apple");
        ArrayList<String> nonMatchingResults = ac.autoComplete(treeWithNonMatching, "cap", -1);
        assertTrue(nonMatchingResults.isEmpty());
    }

    /**
     * Tests line 117 conditions and unexecuted statements for null list or prefix.
     */
    @Test
    public void testPossibleNextLetterNullInputs() {
        // Validate null inputs return an empty map safely
        assertTrue(ac.possibleNextLetter(null, "cap").isEmpty());

        ArrayList<String> list = new ArrayList<String>();
        list.add("captain");
        assertTrue(ac.possibleNextLetter(list, null).isEmpty());
    }

    /**
     * Tests line 124 conditions for null words, non-matching prefixes, and exact
     * prefix lengths.
     */
    @Test
    public void testPossibleNextLetterWordConditions() {
        // Populate edge-case word list (null, non-matching, exact prefix length, valid
        // extension)
        ArrayList<String> words = new ArrayList<String>();
        words.add(null);
        words.add("apple");
        words.add("cap");
        words.add("capt");

        // Run next letter analysis
        HashMap<Character, Integer> result = ac.possibleNextLetter(words, "cap");

        // Expect only 't' from "capt" to be counted
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), result.get('t'));
    }
}