package com.scriptor.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.scriptor.Scriptor;
import com.scriptor.core.ScriptorTerminal;

public class TerminalComponentPopupMenu extends JPopupMenu {
    public TerminalComponentPopupMenu(Scriptor scriptor) {
        JMenuItem endProcessButton = new JMenuItem("End Process");
        JMenuItem clearTerminalButton = new JMenuItem("Clear Terminal");

        endProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.getCurrentTerminal();

                if (terminal != null) {
                    terminal.closeProcess();
                }
            }
        });

        clearTerminalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.getCurrentTerminal();

                if (terminal != null) {
                    terminal.clearTerminal();
                }
            }
        });

        add(endProcessButton);
        add(clearTerminalButton);
    }
}
