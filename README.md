# Auto-Complete Engine (AVL Tree Implementation)

A Java-based word auto-completion and spell-checking engine powered by a self-balancing AVL Binary Search Tree. The engine efficiently ingests large word dictionaries, provides fast lookup for exact spell checks, performs recursive tree traversals for prefix-based autocomplete queries, and calculates next-character frequency distributions.

---

##  Features

* **Balanced Storage:** Utilizes a self-balancing AVL Tree to maintain $O(\log n)$ search and insertion guarantees across thousands of vocabulary entries.
* **Spell Validation:** Fast membership verification to check whether a given token exists within the loaded dictionary.
* **Prefix Autocomplete:** Recursive tree traversal algorithm that aggregates all words matching a target prefix, sorted by length (ascending) with optional result limits.
* **Next-Character Frequency Analysis:** Evaluates a set of matched candidate words to construct a frequency map of valid subsequent characters.

---

##  Core Architecture & Methods

The core logic resides within the `AutoComplete` class, interfacing with generic `AVLTree<String>` and `AVLNode<String>` structures.

### 1. `AVLTree<String> loadWords(String filename)`
Reads words from a newline-delimited text file, trims whitespace, and sequentially inserts them into an AVL Tree while preserving balanced binary search tree invariants.

### 2. `boolean validSpelling(AVLTree<String> tree, String word)`
Validates whether the provided word exists within the dictionary by executing a lookup against the AVL Tree structure.

### 3. `ArrayList<String> autoComplete(AVLTree<String> tree, String prefix, int limit)`
Public entry point for prefix search:
1. Initiates a recursive traversal starting at the AVL Tree root.
2. Collects all matching terms starting with `prefix`.
3. Sorts matched words in increasing order of length using a custom `Comparator`.
4. Trims the collection to the specified `limit` count (or returns all matches if limit is negative/unbounded).

### 4. `private ArrayList<String> autoComplete(AVLNode<String> root, String prefix, ArrayList<String> list)`
Recursive helper performing a tree traversal:
* Checks the current node against the prefix.
* Recursively traverses left and right subtrees (`getLeft()`, `getRight()`) to aggregate all matching tokens.

### 5. `HashMap<Character, Integer> possibleNextLetter(ArrayList<String> list, String prefix)`
Inspects a list of prefix-matched words to identify the immediate next character following `prefix` for each word. Returns a map containing each candidate character and its occurrence count.

---

##  Usage Example

```java
// 1. Initialize engine and load dictionary
AutoComplete ac = new AutoComplete();
AVLTree<String> tree = ac.loadWords("words.txt");

// 2. Spell check
boolean isValid = ac.validSpelling(tree, "captain"); // returns true

// 3. Autocomplete with limit
// Prefix "cap" produces: "captor", "captain", "captive", "capture", etc.
ArrayList<String> suggestions = ac.autoComplete(tree, "cap", 5);

// 4. Analyze next character frequency distribution
HashMap<Character, Integer> nextLetters = ac.possibleNextLetter(suggestions, "cap");
// Result: 'i' -> 1 ("capitalist"), 't' -> 7 ("captain", "captor", etc.)
