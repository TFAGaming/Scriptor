package com.scriptor.core.gui.toolbar;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.List;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.frames.ScriptorTerminalCommandsHistory;
import com.scriptor.core.terminal.ScriptorTerminal;

public class ScriptorTerminalToolbar extends JToolBar {
    public ScriptorTerminalToolbar(Scriptor scriptor) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewTerminal = createButton(getIcon("new.gif"), "New Terminal");
        buttonNewTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.terminalTabManager.newTerminal();
            }
        });

        JButton buttonCloseCurrentTerminal = createButton(getIcon("close.gif"), "Close Terminal");
        buttonCloseCurrentTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = scriptor.terminalTabManager.getIndex();

                scriptor.terminalTabManager.closeTabByIndex(index);
            }
        });

        JButton buttonCloseAllTerminal = createButton(getIcon("close_all.gif"), "Close all");
        buttonCloseAllTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.terminalTabManager.closeAllTabs();
            }
        });

        JButton buttonClearOutput = createButton(getIcon("erase.gif"), "Erase output");
        buttonClearOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                terminal.clearTerminal();
            }
        });

        JButton buttonProcessRestart = createButton(getIcon("process_restart.gif"), "Restart process");
        buttonProcessRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                terminal.restartProcess();
            }
        });

        JButton buttonProcessStop = createButton(getIcon("process_stop.gif"), "Stop process");
        buttonProcessStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                terminal.closeProcess();
            }
        });

        JButton buttonPaste = createButton(getIcon("paste.gif"), "Paste");
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                terminal.commandTextField.paste();
            }
        });

        JButton buttonExecutedCommandsHistory = createButton(getIcon("executed_commands_history.gif"),
                "View commands history");
        buttonExecutedCommandsHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                List<String> commands = terminal.getCommands();

                if (commands.size() == 0) {
                    showMessageDialog(scriptor, "The commands history for this terminal is empty.",
                            "Terminal Commands History",
                            WARNING_MESSAGE);

                    return;
                }

                new ScriptorTerminalCommandsHistory(scriptor, terminal.getCommands());
            }
        });

        add(buttonNewTerminal);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonCloseCurrentTerminal);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonCloseAllTerminal);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonClearOutput);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonProcessRestart);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonProcessStop);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonPaste);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonExecutedCommandsHistory);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JButton createButton(ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton();

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.setPreferredSize(new Dimension(20, 20));

        Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
        button.setIcon(new ImageIcon(scaledImage));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }
}
