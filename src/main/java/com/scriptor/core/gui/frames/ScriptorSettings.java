package com.scriptor.core.gui.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.panels.ScriptorPluginsPanel;
import com.scriptor.core.gui.ui.VerticalLabelUI;
import com.scriptor.core.plugins.ScriptorPlugin;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ScriptorSettings extends JFrame {
    private Scriptor scriptor;

    public ScriptorSettings(Scriptor scriptor) {
        this.scriptor = scriptor;

        setTitle("Scriptor Configuration");
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        tabbedPane.setFocusable(false);

        tabbedPane.addTab("General", createGeneralSettingsPanel());
        JLabel tab1 = new JLabel("  General  ");
        tab1.setFont(new Font(tab1.getFont().getName(), Font.BOLD, 12));
        tab1.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(0, tab1);

        tabbedPane.addTab("Editor", createEditorSettingsPanel());
        JLabel tab2 = new JLabel("  Editor  ");
        tab2.setFont(new Font(tab2.getFont().getName(), Font.BOLD, 12));
        tab2.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(1, tab2);

        tabbedPane.addTab("Plugins", createPluginsSettingsPanel());
        JLabel tab3 = new JLabel("  Plugins  ");
        tab3.setFont(new Font(tab3.getFont().getName(), Font.BOLD, 12));
        tab3.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(2, tab3);

        splitPane.setLeftComponent(tabbedPane);

        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createGeneralSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String languages[] = { "English US" };
        JComboBox<String> languageComboBox = new JComboBox<String>(languages);
        languageComboBox.setSelectedIndex(0);
        JLabel languageLabel = new JLabel("Language: ");
        languageLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        panel.add(newJPanelLeftLayout(languageLabel, languageComboBox));

        JCheckBox checkForUpdatesCheckBox = new JCheckBox();
        checkForUpdatesCheckBox.setSelected(scriptor.config.getCheckForUpdatesOnStartup());
        checkForUpdatesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setCheckForUpdatesOnStartup(checkForUpdatesCheckBox.isSelected());
            }
        });
        JLabel checkForUpdatesLabel = new JLabel("Check for updates on startup");
        panel.add(newJPanelLeftLayout(checkForUpdatesCheckBox, checkForUpdatesLabel));

        JCheckBox showWhatsNewCheckBox = new JCheckBox();
        showWhatsNewCheckBox.setSelected(scriptor.config.getShowWhatsNewOnStartUp());
        showWhatsNewCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setShowWhatsNewOnStartUp(showWhatsNewCheckBox.isSelected());
            }
        });
        JLabel showWhatsNewLabel = new JLabel("Show what's new on startup");
        panel.add(newJPanelLeftLayout(showWhatsNewCheckBox, showWhatsNewLabel));

        JCheckBox openPreviousSessionCheckBox = new JCheckBox();
        openPreviousSessionCheckBox.setSelected(scriptor.config.getOpenPreviousFilesOnStartup());
        openPreviousSessionCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setOpenPreviousFilesOnStartup(openPreviousSessionCheckBox.isSelected());
            }
        });
        JLabel openPreviousSessionLabel = new JLabel("Open previous files on startup");
        panel.add(newJPanelLeftLayout(openPreviousSessionCheckBox, openPreviousSessionLabel));

        JCheckBox autoSaveCheckBox = new JCheckBox();
        autoSaveCheckBox.setSelected(scriptor.config.getAutoSaveFileEdits());
        autoSaveCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setAutoSaveFileEdits(autoSaveCheckBox.isSelected());
            }
        });
        JLabel autoSaveLabel = new JLabel("Auto-save file edits");
        panel.add(newJPanelLeftLayout(autoSaveCheckBox, autoSaveLabel));

        return panel;
    }

    private JPanel createEditorSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JCheckBox autoIndentCheckBox = new JCheckBox();
        autoIndentCheckBox.setSelected(scriptor.config.getAutoIndent());
        autoIndentCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setAutoIndent(autoIndentCheckBox.isSelected());
            }
        });
        JLabel autoIndentLabel = new JLabel("Enable Auto-Indent");
        panel.add(newJPanelLeftLayout(autoIndentCheckBox, autoIndentLabel));

        String tabSizes[] = { "1", "2", "3", "4", "5", "6", "7", "8" };
        JComboBox<String> tabSizeComboBox = new JComboBox<String>(tabSizes);
        tabSizeComboBox.setSelectedIndex(scriptor.config.getIndentTabSize() - 1);
        tabSizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setIndentTabSize(tabSizeComboBox.getSelectedIndex() + 1);
            }
        });
        tabSizeComboBox.setSelectedIndex(3);
        JLabel languageLabel = new JLabel("Indent Tab size: ");
        languageLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        panel.add(newJPanelLeftLayout(languageLabel, tabSizeComboBox));

        JCheckBox bracketMatchingCheckBox = new JCheckBox();
        bracketMatchingCheckBox.setSelected(scriptor.config.getBracketMatching());
        bracketMatchingCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setBracketMatching(bracketMatchingCheckBox.isSelected());
            }
        });
        JLabel bracketMatchingNewLabel = new JLabel("Enable Bracket matching");
        panel.add(newJPanelLeftLayout(bracketMatchingCheckBox, bracketMatchingNewLabel));

        JCheckBox syntaxHighlightingEnabledCheckBox = new JCheckBox();
        syntaxHighlightingEnabledCheckBox.setSelected(scriptor.config.getSyntaxHighlightingEnabled());
        syntaxHighlightingEnabledCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.config.setSyntaxHighlightingEnabled(syntaxHighlightingEnabledCheckBox.isSelected());
            }
        });
        JLabel syntaxHighlightingEnabledLabel = new JLabel("Enable Syntax Highlighting");
        panel.add(newJPanelLeftLayout(syntaxHighlightingEnabledCheckBox, syntaxHighlightingEnabledLabel));

        return panel;
    }

    private JPanel createPluginsSettingsPanel() {
        this.scriptor.pluginsHandler.loadPlugins();
        List<ScriptorPlugin> plugins = this.scriptor.pluginsHandler.getPlugins();

        ScriptorPluginsPanel panel = new ScriptorPluginsPanel(this.scriptor, plugins);

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
}
