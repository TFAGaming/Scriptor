package com.scriptor.core.gui.menus;

import static javax.swing.JOptionPane.*;

import java.awt.event.*;

import javax.swing.*;

import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.components.JClosableComponentType;
import com.scriptor.core.gui.frames.ScriptorMarkdownViewer;

public class ScriptorMenubar extends JMenuBar {
    public ScriptorMenubar(Scriptor scriptor) {
        /*
         * File menu
         */
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItemNewFile = createMenuItem("New File", scriptor.getIcon("new_file.png"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menuItemNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.newFile();
            }
        });

        JMenuItem menuItemOpenFile = createMenuItem("Open File", scriptor.getIcon("file_open.png"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.openFile();
            }
        });

        JMenuItem menuItemOpenFolder = createMenuItem("Open Folder", scriptor.getIcon("folder_open.png"),
                TOOL_TIP_TEXT_KEY, null);
        menuItemOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.openFolder();
            }
        });

        JMenuItem menuItemSave = createMenuItem("Save", scriptor.getIcon("save.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveFile();
            }
        });

        JMenuItem menuItemSaveAs = createMenuItem("Save As", scriptor.getIcon("saveas.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveAsFile();
            }
        });

        JMenuItem menuItemSaveAll = createMenuItem("Save All", scriptor.getIcon("saveall.gif"), TOOL_TIP_TEXT_KEY,
                null);
        menuItemSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveAllTabs();
            }
        });

        JMenuItem menuItemCloseTab = createMenuItem("Close Tab", scriptor.getIcon("close_tab.png"), TOOL_TIP_TEXT_KEY,
                null);
        menuItemCloseTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = scriptor.textAreaTabManager.getCurrentIndex();

                if (index != 1) {
                    scriptor.textAreaTabManager.closeTabByIndex(index);
                }
            }
        });

        JMenuItem menuItemCloseAllTabs = createMenuItem("Close All Tabs", scriptor.getIcon("close_all_tabs.png"),
                TOOL_TIP_TEXT_KEY, null);
        menuItemCloseAllTabs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.closeAllTabs();
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

        JMenuItem menuItemUndo = createMenuItem("Undo", scriptor.getIcon("undo.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuItemUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JMenuItem menuItemRedo = createMenuItem("Redo", scriptor.getIcon("redo.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuItemRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JMenuItem menuItemCut = createMenuItem("Cut", scriptor.getIcon("cut.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        menuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                textArea.cut();
            }
        });

        JMenuItem menuItemCopy = createMenuItem("Copy", scriptor.getIcon("copy.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        menuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                textArea.copy();
            }
        });

        JMenuItem menuItemPaste = createMenuItem("Paste", scriptor.getIcon("paste.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        menuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                textArea.paste();
            }
        });

        JMenuItem menuItemDelete = createMenuItem("Delete", scriptor.getIcon("trash.gif"), TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                textArea.replaceSelection("");
            }
        });

        JMenuItem menuItemSelectAll = createMenuItem("Select All", null, TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        menuItemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

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

        /*
         * View menu
         */

        JMenu viewMenu = new JMenu("View");

        JMenuItem menuItemOpenExplorer = createMenuItem("Open Explorer", scriptor.getIcon("tree_explorer.gif"),
                TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        menuItemOpenExplorer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.addBackComponent(JClosableComponentType.FILE_EXPLORER);
            }
        });

        JMenuItem menuItemOpenTerminal = createMenuItem("Open Terminal", scriptor.getIcon("console.gif"),
                TOOL_TIP_TEXT_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        menuItemOpenTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.addBackComponent(JClosableComponentType.TERMINAL);
            }
        });

        JMenuItem menuItemMarkdownViewer = createMenuItem("Markdown Viewer", scriptor.getIcon("markdown_viewer.png"),
                TOOL_TIP_TEXT_KEY, null);
        menuItemMarkdownViewer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = scriptor.textAreaTabManager.getCurrentPath();

                if (path != null) {
                    String extension = FilenameUtils.getExtension(path);

                    if (extension.equalsIgnoreCase("md") || extension.equalsIgnoreCase("markdown")
                            || extension.equalsIgnoreCase("txt")) {
                        new ScriptorMarkdownViewer(scriptor, path);
                    } else {
                        showMessageDialog(scriptor, "The current file is not a markdown or a plain text file.",
                                "Markdown Viewer",
                                WARNING_MESSAGE);
                    }
                }
            }
        });

        viewMenu.add(menuItemOpenExplorer);
        viewMenu.add(menuItemOpenTerminal);
        viewMenu.addSeparator();
        viewMenu.add(menuItemMarkdownViewer);

        /*
         * End
         */

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
    }

    private JMenuItem createMenuItem(String text, ImageIcon menuItemIcon, String tooltip, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);

        if (tooltip != null) {
            menuItem.setToolTipText(tooltip);
        }

        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }

        if (menuItemIcon != null) {
            menuItem.setIcon(menuItemIcon);
        }

        return menuItem;
    }
}
