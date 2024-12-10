package com.scriptor.core.managers;

import javax.swing.*;

import com.scriptor.Scriptor;
import com.scriptor.core.terminal.ScriptorTerminal;

import java.awt.event.*;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ScriptorTerminalTabManager {
    private final Scriptor scriptor;
    private final JTabbedPane tabbedPane;
    private int _terminalsCount = 0;

    public List<ScriptorTerminal> arrayTerminals = new ArrayList<ScriptorTerminal>();

    public ScriptorTerminalTabManager(Scriptor scriptor, JTabbedPane tabbedPane) {
        this.scriptor = scriptor;
        this.tabbedPane = tabbedPane;

        newTerminal();
    }

    public void newTerminal() {
        ScriptorTerminal terminal = new ScriptorTerminal(scriptor, scriptor.config.getDirectoryPath());

        _terminalsCount++;

        arrayTerminals.add(terminal);

        tabbedPane.addTab("Terminal #" + _terminalsCount, terminal);
        addCloseButton();

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void closeTabByIndex(int index) {
        tabbedPane.removeTabAt(index);
        arrayTerminals.remove(index);

        if (arrayTerminals.size() == 0) {
            newTerminal();
        }
    }

    public void closeAllTabs() {
        int size = arrayTerminals.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTabByIndex(index);
        }
    }

    public ScriptorTerminal getCurrentTerminal() {
        int index = tabbedPane.getSelectedIndex();

        if (index != 1) {
            return arrayTerminals.get(index);
        } else {
            return null;
        }
    }

    public int getIndex() {
        return tabbedPane.getSelectedIndex();
    }

    private void addCloseButton() {
        int index = tabbedPane.getTabCount() - 1;

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);

        JLabel tabTitle = new JLabel(tabbedPane.getTitleAt(index));

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.indexOfTabComponent(tabPanel);

                closeTabByIndex(index);
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        tabbedPane.setTabComponentAt(index, tabPanel);
    }
}
