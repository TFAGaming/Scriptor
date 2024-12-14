package com.scriptor.core.terminal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.scriptor.Scriptor;

public class TerminalPopupMenu extends JPopupMenu {
    public TerminalPopupMenu(Scriptor scriptor) {
        JMenuItem endProcessButton = new JMenuItem("End Process");
        endProcessButton.setIcon(scriptor.getIcon("process_stop.gif"));
        endProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                if (terminal != null) {
                    terminal.closeProcess();
                }
            }
        });

        JMenuItem clearTerminalButton = new JMenuItem("Clear Terminal");
        clearTerminalButton.setIcon(scriptor.getIcon("erase.gif"));
        clearTerminalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                if (terminal != null) {
                    terminal.clearTerminal();
                }
            }
        });

        add(endProcessButton);
        add(clearTerminalButton);
    }
}
