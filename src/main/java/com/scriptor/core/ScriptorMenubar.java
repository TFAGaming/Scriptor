package com.scriptor.core;

import java.awt.event.*;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.scriptor.Scriptor;

public class ScriptorMenubar extends JMenuBar {
    public ScriptorMenubar(Scriptor scriptor) {
        /*
         * File menu
         */
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItemNewFile = createMenuItem("New File", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menuItemNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.newFile();
            }
        });

        JMenuItem menuItemOpenFile = createMenuItem("Open File", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
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

        JMenuItem menuItemSave = createMenuItem("Save", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveFile();
            }
        });

        JMenuItem menuItemSaveAs = createMenuItem("Save As", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
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

        /*
         * Edit menu
         */

        JMenu editMenu = new JMenu("Edit");

        JMenuItem menuItemUndo = createMenuItem("Undo", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuItemUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JMenuItem menuItemRedo = createMenuItem("Redo", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuItemRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JMenuItem menuItemCut = createMenuItem("Cut", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        menuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                textArea.cut();
            }
        });

        JMenuItem menuItemCopy = createMenuItem("Copy", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        menuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                textArea.copy();
            }
        });

        JMenuItem menuItemPaste = createMenuItem("Paste", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        menuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                textArea.paste();
            }
        });

        JMenuItem menuItemDelete = createMenuItem("Delete", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                // TODO Delete word
            }
        });

        JMenuItem menuItemSelectAll = createMenuItem("Select All", TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        menuItemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                textArea.selectAll();
            }
        });

        editMenu.add(menuItemUndo);
        editMenu.add(menuItemRedo);
        editMenu.addSeparator();
        editMenu.add(menuItemCut);
        editMenu.add(menuItemCopy);
        editMenu.add(menuItemPaste);
        editMenu.add(menuItemDelete);
        editMenu.add(menuItemSelectAll);

        add(fileMenu);
        add(editMenu);
    }

    private JMenuItem createMenuItem(String text, /* ImageIcon menuItemIcon, */ String tooltip, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);

        if (tooltip != null) {
            menuItem.setToolTipText(tooltip);
        }

        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }

        return menuItem;
    }
}
