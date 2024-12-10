package com.scriptor.core.gui.frames;

import javax.swing.*;

import com.scriptor.Scriptor;
import com.scriptor.core.terminal.ScriptorTerminal;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ScriptorTerminalCommandsHistory extends JFrame {
    private Scriptor scriptor;

    public ScriptorTerminalCommandsHistory(Scriptor scriptor, List<String> commands) {
        this.scriptor = scriptor;

        setTitle("Terminal Commands History");
        setSize(400, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(scriptor);
        setResizable(false);
        setAlwaysOnTop(true);

        setLayout(new BorderLayout());

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        String[][] array = new String[commands.size()][];

        for (int i = 0; i < commands.size(); i++) {
            array[i] = new String[]{commands.get(i)};
        }
 
        String[] columnNames = { "Terminal Commands History (" + commands.size() + ")" };
 
        JTable table = new JTable(array, columnNames);
        table.setBounds(30, 40, 200, 300);
        table.setFocusable(false);
        table.setEnabled(false);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                String command = commands.get(row);

                ScriptorTerminal terminal = scriptor.terminalTabManager.getCurrentTerminal();

                terminal.commandTextField.setText(command);
            }
        });
 
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}