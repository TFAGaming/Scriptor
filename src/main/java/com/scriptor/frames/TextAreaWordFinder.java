package com.scriptor.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.scriptor.Scriptor;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextAreaWordFinder extends JFrame {
    private RSyntaxTextArea textArea;
    private JTextField searchField;
    private JTextField replaceField;
    private JCheckBox caseSensitiveCheck;
    private JCheckBox wholeWordCheck;
    private JCheckBox regexCheck;
    private JCheckBox replaceCheck;
    private JButton upButton;
    private JButton downButton;
    private JButton replaceButton;
    private int currentMatchIndex = -1;
    private java.util.List<Integer> matchPositions;

    public TextAreaWordFinder(Scriptor scriptor, RSyntaxTextArea textArea) {
        setTitle("Search & Replace");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(scriptor);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);

        setIconImage(scriptor.getIcon("scriptor_icon.png").getImage());

        this.textArea = textArea;

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 0));

        searchField = new JTextField(38);

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField, BorderLayout.WEST);

        // Options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsPanel.setBorder(new EmptyBorder(5, 5, 10, 0));

        caseSensitiveCheck = new JCheckBox("Case Sensitive");

        wholeWordCheck = new JCheckBox("Whole Word");

        regexCheck = new JCheckBox("Regex");

        optionsPanel.add(regexCheck);
        optionsPanel.add(wholeWordCheck);
        optionsPanel.add(caseSensitiveCheck);

        // Replace panel
        JPanel replacePanel = new JPanel();
        replacePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        replacePanel.setBorder(new EmptyBorder(0, 5, 10, 0));

        replaceCheck = new JCheckBox("Replace: ");
        replaceCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isEnabled = e.getStateChange() == ItemEvent.SELECTED;

                replaceField.setEnabled(isEnabled);
                replaceButton.setEnabled(isEnabled);

                upButton.setEnabled(!isEnabled);
                downButton.setEnabled(!isEnabled);
            }
        });

        replaceField = new JTextField(35);
        replaceField.setEnabled(false);

        replacePanel.add(replaceCheck);
        replacePanel.add(replaceField, BorderLayout.WEST);

        // Remaining
        add(searchPanel, BorderLayout.NORTH);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        secondaryPanel.add(optionsPanel, BorderLayout.NORTH);
        secondaryPanel.add(replacePanel, BorderLayout.SOUTH);

        add(secondaryPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        upButton = new JButton("Up");
        upButton.setPreferredSize(new Dimension(80, 25));
        downButton = new JButton("Down");
        downButton.setPreferredSize(new Dimension(80, 25));
        replaceButton = new JButton("Replace");
        replaceButton.setPreferredSize(new Dimension(80, 25));
        replaceButton.setEnabled(false);

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(replaceButton);

        add(buttonPanel, BorderLayout.SOUTH);

        upButton.addActionListener(e -> findNextMatch(true));
        downButton.addActionListener(e -> findNextMatch(false));
        searchField.addActionListener(e -> findMatches());

        setVisible(true);
    }

    private void findMatches() {
        String searchText = searchField.getText();

        if (searchText.length() == 0) {
            showMessageDialog(this, "You cannot search an empty string!", "Warning", WARNING_MESSAGE);

            return;
        }

        String text = textArea.getText();
        matchPositions = new java.util.ArrayList<>();
        currentMatchIndex = -1;

        if (searchText.isEmpty()) {
            return;
        }

        int flags = caseSensitiveCheck.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern pattern;

        if (regexCheck.isSelected()) {
            pattern = Pattern.compile(searchText, flags);
        } else {
            String escapedText = Pattern.quote(searchText);
            if (wholeWordCheck.isSelected()) {
                escapedText = "\\b" + escapedText + "\\b";
            }
            pattern = Pattern.compile(escapedText, flags);
        }

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matchPositions.add(matcher.start());
        }

        if (!matchPositions.isEmpty()) {
            currentMatchIndex = 0;
            highlightMatch();
        }
    }

    private void findNextMatch(boolean isUp) {
        if (matchPositions == null || matchPositions.isEmpty()) {
            return;
        }

        if (isUp) {
            currentMatchIndex = (currentMatchIndex - 1 + matchPositions.size()) % matchPositions.size();
        } else {
            currentMatchIndex = (currentMatchIndex + 1) % matchPositions.size();
        }

        highlightMatch();
    }

    private void highlightMatch() {
        if (currentMatchIndex == -1 || matchPositions.isEmpty()) {
            return;
        }

        int position = matchPositions.get(currentMatchIndex);

        textArea.requestFocus();
        textArea.select(position, position + searchField.getText().length());
    }
}
