import java.util.*;
import itsc2214.AVLTree;

public class Project4 {

    // Main to test
    public static void main(String[] args) {

        AutoComplete autocomp = new AutoComplete();
        AVLTree<String> tree = autocomp.loadWords("words.txt");

        String prefix;
        if (args.length == 0)
            prefix = "pre";
        else
            prefix = args[0].toLowerCase();

        // Spelling
        if (autocomp.validSpelling(tree, "captain"))
            System.out.println("captain is properly spelled.");
        if (!autocomp.validSpelling(tree, "captian"))
            System.out.println("captian is a typo.");

        ArrayList<String> list = autocomp.autoComplete(tree, prefix, -1);
        for (String w : list)
            System.out.println(w);
        HashMap<Character, Integer> map = autocomp.possibleNextLetter(list, prefix);

        System.out.println("Auto complete: " + prefix);
        System.out.println("# Matches : " + list.size());
        System.out.println(String.join(", ", list));
        map.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });
    }
}