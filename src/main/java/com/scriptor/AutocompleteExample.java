package com.scriptor;

import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.awt.event.KeyEvent;

public class AutocompleteExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Autocomplete Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Create RSyntaxTextArea
            RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            textArea.setCodeFoldingEnabled(true);

            // Set up the completion provider
            CompletionProvider provider = createCompletionProvider();
            AutoCompletion ac = new AutoCompletion(provider);
            ac.setAutoActivationEnabled(true); // Enable auto-activation
            ac.setAutoActivationDelay(500); // No delay
            ac.install(textArea); // Install autocomplete on the text area

            textArea.getDocument().addDocumentListener(new DocumentListener() {
                private void checkForDot(DocumentEvent e) {
                    int offset = e.getOffset();
                    try {
                        if (textArea.getText(offset, 1).equals(".")) {
                            SwingUtilities.invokeLater(() -> {
                                textArea.dispatchEvent(
                                        new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                                                KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED));
                            });
                        }
                    } catch (BadLocationException ble) {
                    }
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkForDot(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Do nothing
                }
            });

            // Add text area to the frame
            RTextScrollPane sp = new RTextScrollPane(textArea);
            frame.add(sp);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Create the completion provider with predefined words.
     */
    private static CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // Add words to the autocomplete menu
        provider.addCompletion(new BasicCompletion(provider, "System"));
        provider.addCompletion(new BasicCompletion(provider, "out"));
        provider.addCompletion(new BasicCompletion(provider, "println"));
        provider.addCompletion(new BasicCompletion(provider, "String"));
        provider.addCompletion(new BasicCompletion(provider, "public"));
        provider.addCompletion(new BasicCompletion(provider, "static"));
        provider.addCompletion(new BasicCompletion(provider, "void"));
        provider.addCompletion(new BasicCompletion(provider, "main"));

        return provider;
    }
}