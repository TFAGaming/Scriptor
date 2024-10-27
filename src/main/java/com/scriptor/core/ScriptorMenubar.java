package com.scriptor.core;

import java.awt.event.*;

import javax.swing.*;

import com.scriptor.Scriptor;

public class ScriptorMenubar extends JMenuBar {
    public ScriptorMenubar(Scriptor scriptor) {
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItemNewFile = createMenuItem("New File", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menuItemNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.newFile();
            }
        });

        JMenuItem menuItemOpenFile = createMenuItem("Open File", TOOL_TIP_TEXT_KEY, null);
        menuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.openFile();
            }
        });

        JMenuItem menuItemOpenFolder = createMenuItem("Open Folder", TOOL_TIP_TEXT_KEY, null);
        menuItemOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.openFolder();
            }
        });

        JMenuItem menuItemSave = createMenuItem("Save", TOOL_TIP_TEXT_KEY, null);
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveFile();
            }
        });

        JMenuItem menuItemSaveAs = createMenuItem("Save As", TOOL_TIP_TEXT_KEY, null);
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveAsFile();
            }
        });

        JMenuItem menuItemSaveAll = createMenuItem("Save All", TOOL_TIP_TEXT_KEY, null);
        menuItemSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveAllTextAreaTabs();
            }
        });

        JMenuItem menuItemCloseTab = createMenuItem("Close Tab", TOOL_TIP_TEXT_KEY, null);
        menuItemCloseTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = scriptor.tabbedTextAreaPane.getSelectedIndex();

                if (index != 1) {
                    scriptor.closeTextAreaTabByIndex(index);
                }
            }
        });

        JMenuItem menuItemCloseAllTabs = createMenuItem("Close All Tabs", TOOL_TIP_TEXT_KEY, null);
        menuItemCloseAllTabs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = scriptor.arrayPaths.size();

                if (count > 0) {
                    scriptor.closeAllTextAreaTabs();
                }
            }
        });

        fileMenu.add(menuItemNewFile);
        fileMenu.addSeparator();
        fileMenu.add(menuItemOpenFile);
        fileMenu.add(menuItemOpenFolder);
        fileMenu.addSeparator();
        fileMenu.add(menuItemSave);
        fileMenu.add(menuItemSaveAs);
        fileMenu.add(menuItemSaveAll);
        fileMenu.addSeparator();
        fileMenu.add(menuItemCloseTab);
        fileMenu.add(menuItemCloseAllTabs);

        add(fileMenu);
    }

    /*
    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon(scriptor.getClass().getResource("/" + iconName));

        return icon;
    }
    */

    private JMenuItem createMenuItem(String text, /* ImageIcon menuItemIcon, */ String tooltip, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);

        if (tooltip != null) {
            menuItem.setToolTipText(tooltip);
        }

        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }

        /*
         * if (menuItemIcon != null) {
         * Image scaledImage = menuItemIcon.getImage().getScaledInstance(16, 16,
         * Image.SCALE_FAST);
         * 
         * menuItem.setIcon(new ImageIcon(scaledImage));
         * }
         */

        return menuItem;
    }
}
