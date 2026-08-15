
/**
* SpellCheck.java
* based on code by Lewis/Chase
*/

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import itsc2214.AVLTree;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Wrapper class with a GUI for the AutoComplete Project
 * ITSC2214 Fall 2025
 * 
 * @author Manuel A. Pérez-Quiñones
 */

public class SpellCheck {

    /**
     * Main program, run when this class is run as an app.
     * 
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        new SpellCheck("words.txt");
    }

    // Instance variables.
    private JFrame win = null;
    private JLabel message;
    private JTextField inputField;

    private AutoComplete autocomp;
    private AVLTree<String> tree;
    private int count = 0;

    /**
     * SpellCheck constructor for images loaded from a files.
     */
    public SpellCheck(String inFile) {
        tree = new AVLTree<String>();

        try {
            File myObj = new File(inFile);
            Scanner scan = new Scanner(myObj);
            while (scan.hasNext()) {
                tree.add(scan.nextLine().trim());
                count++;
            }
            scan.close();
        } catch (FileNotFoundException f) {
        }

        autocomp = new AutoComplete();

        setupWindow(inFile);
    }

    /**
     * Setup the GUI window with display of the image and
     * buttons.
     * 
     * @param filename name of the file to be loaded, could be null
     */
    private void setupWindow(String filename) {
        // create window with border layout
        win = new JFrame("Auto complete, dictionary: " + filename);
        win.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        // Instructions on top
        panel.add(new JLabel("Type part of a word to see completions, press return to check spelling."));
        win.getContentPane().add(panel, BorderLayout.NORTH);

        // Text input field
        inputField = new JTextField("", 20); // Initial text and 20 columns
        panel.add(inputField);
        win.getContentPane().add(panel, BorderLayout.CENTER);

        // Message area
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        message = new JLabel("Words: " + count);
        panel.add(message);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredText = inputField.getText();
                if (autocomp.validSpelling(tree, enteredText))
                    message.setText(enteredText + " is spelled correctly.");
                else
                    message.setText(enteredText + " is misspelled.");
            }
        });
        win.getContentPane().add(panel, BorderLayout.SOUTH);

        javax.swing.text.Document doc = inputField.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentChange(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentChange(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void handleDocumentChange(DocumentEvent e) {
                try {
                    // Get the current text from the document
                    String currentText = e.getDocument().getText(0, e.getDocument().getLength());
                    if (currentText.trim().length() == 0) {
                        message.setText("Type something");
                        return;
                    } else {
                        ArrayList<String> list = autocomp.autoComplete(tree, currentText, 5);
                        String text = String.join(", ", list);
                        message.setText(text);
                    }

                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

        });

        // Set the window size to fit everything snuggly
        win.setSize(600, 100);
        win.setVisible(true);
    }
}
