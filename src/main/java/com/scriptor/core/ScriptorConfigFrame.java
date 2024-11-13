package com.scriptor.core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.scriptor.Scriptor;

import java.awt.*;
import java.awt.event.*;

public class ScriptorConfigFrame extends JFrame {
    public ScriptorConfigFrame(Scriptor scriptor) {
        setTitle("Scriptor Configuration");
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setIconImage(scriptor.getIcon("scriptor_icon.png").getImage());

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setFocusable(false);

        tabbedPane.addTab("General", createGeneralSettingsPanel());
        tabbedPane.addTab("Editor", createEditorSettingsPanel());
        tabbedPane.addTab("Syntax Highlighting", createSyntaxHighlightingSettingsPanel());
        //tabbedPane.addTab("Display", createDisplaySettingsPanel());
        //tabbedPane.addTab("Network", createNetworkSettingsPanel());

        splitPane.setLeftComponent(tabbedPane);

        add(tabbedPane);
    }

    private JPanel createGeneralSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String languages[] = { "English (US)" };
        JComboBox<String> languageComboBox = new JComboBox<String>(languages);
        languageComboBox.setSelectedIndex(0);
        JLabel languageLabel = new JLabel("Language: ");
        languageLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        panel.add(newJPanelLeftLayout(languageLabel, languageComboBox));

        JCheckBox checkForUpdatesCheckBox = new JCheckBox();
        JLabel checkForUpdatesLabel = new JLabel("Check for updates on startup");
        panel.add(newJPanelLeftLayout(checkForUpdatesCheckBox, checkForUpdatesLabel));

        JCheckBox showWhatsNewCheckBox = new JCheckBox();
        JLabel showWhatsNewLabel = new JLabel("Show what's new on startup");
        panel.add(newJPanelLeftLayout(showWhatsNewCheckBox, showWhatsNewLabel));

        JCheckBox openPreviousSessionCheckBox = new JCheckBox();
        JLabel openPreviousSessionLabel = new JLabel("Open previous files on startup");
        panel.add(newJPanelLeftLayout(openPreviousSessionCheckBox, openPreviousSessionLabel));

        JCheckBox autoSaveCheckBox = new JCheckBox();
        JLabel autoSaveLabel = new JLabel("Auto-save file edits");
        panel.add(newJPanelLeftLayout(autoSaveCheckBox, autoSaveLabel));

        return panel;
    }

    private JPanel createEditorSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JCheckBox autoIndentCheckBox = new JCheckBox();
        JLabel autoIndentLabel = new JLabel("Enable Auto-Indent");
        panel.add(newJPanelLeftLayout(autoIndentCheckBox, autoIndentLabel));

        String tabSizes[] = { "1", "2", "3", "4", "5", "6", "7", "8" };
        JComboBox<String> tabSizeComboBox = new JComboBox<String>(tabSizes);
        tabSizeComboBox.setSelectedIndex(3);
        JLabel languageLabel = new JLabel("Indent Tab size: ");
        languageLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        panel.add(newJPanelLeftLayout(languageLabel, tabSizeComboBox));

        JCheckBox bracketMatchingCheckBox = new JCheckBox();
        JLabel bracketMatchingNewLabel = new JLabel("Enable Bracket matching");
        panel.add(newJPanelLeftLayout(bracketMatchingCheckBox, bracketMatchingNewLabel));

		JCheckBox syntaxHighlightingEnabledCheckBox = new JCheckBox();
        JLabel syntaxHighlightingEnabledLabel = new JLabel("Enable Syntax Highlighting");
        panel.add(newJPanelLeftLayout(syntaxHighlightingEnabledCheckBox, syntaxHighlightingEnabledLabel));

        return panel;
    }

    private JPanel createSyntaxHighlightingSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String[] columnNames = { "Token", "Selected Color", "Choose Color" };
        Object[][] rows = {
                { "Reserved Word", "#0000FF", "Choose" },
                { "Literal String Double Quote", "#008000", "Choose" },
                { "Literal Char", "#008000", "Choose" },
                { "Literal Backquote", "#008000", "Choose" },
                { "Literal Boolean", "#AF00DB", "Choose" },
                { "Literal Decimal Integer", "#979412", "Choose" },
                { "Literal Float", "#979412", "Choose" },
                { "Literal Hexadecimal", "#979412", "Choose" },
                { "Regular Expression", "#CB1823", "Choose" },
                { "Comment Multiline", "#808080", "Choose" },
                { "Comment Documentation", "#808080", "Choose" },
                { "Comment EOL", "#808080", "Choose" },
                { "Separator", "#000000", "Choose" },
                { "Operator", "#000000", "Choose" },
                { "Identifier", "#000000", "Choose" },
                { "Variable", "#E8541E", "Choose" },
                { "Function", "#FF0000", "Choose" },
                { "Preprocessor", "#0000FF", "Choose" },
        };

        DefaultTableModel model = new DefaultTableModel(rows, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(1).setCellRenderer(new DisabledTextFieldRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(table));

		table.setDragEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane);

        return panel;
    }

    private JPanel newJPanelLeftLayout(JComponent... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        if (!(components[components.length - 1] == null)) {
            panel.setBorder(new EmptyBorder(5, 5, 0, 0));   
        }

        for (JComponent component : components) {
            if (component == null) {
                continue;
            }

            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            component.setMaximumSize(component.getPreferredSize());
			component.setFocusable(false);

            panel.add(component);
            panel.add(Box.createRigidArea(new Dimension(5, 0)));
        }

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    static class DisabledTextFieldRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JTextField textField = new JTextField(value != null ? value.toString() : "");

            textField.setSize(textField.getPreferredSize());
            textField.setEnabled(false);

            return textField;
        }
    }

    // Renderer for the "Choose" button
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setSize(getPreferredSize());

            setText("Choose");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    // Editor for the "Choose" button to open a color chooser dialog
    static class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private JTable table;
        private int row;

        public ButtonEditor(JTable table) {
            this.table = table;
            button = new JButton("Choose");
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Choose";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String colorHex = table.getValueAt(row, 1).toString();
            Color initialColor;
            try {
                // Parse the color hex code to a Color object
                initialColor = Color.decode(colorHex);
            } catch (NumberFormatException ex) {
                // Default to white if parsing fails
                initialColor = Color.WHITE;
            }

            // Open the color chooser dialog with the initial color set to the current color
            Color selectedColor = JColorChooser.showDialog(button, "Choose Color", initialColor);
            if (selectedColor != null) {
                // Update the color value in the second column of the selected row
                String newColorHex = String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(),
                        selectedColor.getBlue());
                table.setValueAt(newColorHex, row, 1);
            }

            fireEditingStopped();
        }
    }
}
