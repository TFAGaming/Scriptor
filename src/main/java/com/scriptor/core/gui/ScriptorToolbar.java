package com.scriptor.core.gui;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.scriptor.Scriptor;
import com.scriptor.Utils;

public class ScriptorToolbar extends JToolBar {
    // private Scriptor scriptor;

    public ScriptorToolbar(Scriptor scriptor) {
        // this.scriptor = scriptor;

        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewFile = createButton(getIcon("new_file.png"), "New File");
        buttonNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.newFile();
            }
        });

        JButton buttonFolder = createButton(getIcon("folder_open.png"), "Open Folder");
        buttonFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.openFolder();
            }
        });

        JButton buttonSave = createButton(getIcon("save.gif"), "Save");
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveFile();
            }
        });

        JButton buttonSaveAll = createButton(getIcon("saveall.gif"), "Save All");
        buttonSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.saveAllTextAreaTabs();
            }
        });

        /*JButton buttonCloseTab = createButton(getIcon("tabs_close.png"), "Close Tab");
        buttonCloseTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = scriptor.tabbedTextAreaPane.getSelectedIndex();

                if (index != 1) {
                    scriptor.closeTextAreaTabByIndex(index);
                }
            }
        });

        JButton buttonCloseAllTab = createButton(getIcon("tabs_close_all.png"), "Close All Tabs");
        buttonCloseAllTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.closeAllTextAreaTabs();
            }
        });
        */

        JButton buttonTreeRefresh = createButton(getIcon("tree_explorer.gif"), "Refresh Files Explorer");
        buttonTreeRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.filesExplorer.refreshTree();
            }
        });

        JButton buttonCopy = createButton(getIcon("copy.gif"), "Copy");
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea != null) {
                    textArea.copy();
                }
            }
        });

        JButton buttonPaste = createButton(getIcon("paste.gif"), "Paste");
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea != null) {
                    textArea.paste();
                }
            }
        });

        JButton buttonCut = createButton(getIcon("cut.gif"), "Cut");
        buttonCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea != null) {
                    textArea.cut();
                }
            }
        });

        JButton buttonUndo = createButton(getIcon("undo.gif"), "Undo Edit");
        buttonUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea != null && textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JButton buttonRedo = createButton(getIcon("redo.gif"), "Redo Edit");
        buttonRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                if (textArea != null && textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JButton buttonZoomIn = createButton(getIcon("zoom_in.png"), "Zoom In");
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.zoomIn();
            }
        });

        JButton buttonZoomOut = createButton(getIcon("zoom_out.png"), "Zoom Out");
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.zoomOut();
            }
        });

        JButton buttonSearchText = createButton(getIcon("search_text.png"), "Find & Replace...");
        buttonSearchText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.getCurrentTextArea();

                //System.out.println(textArea);

                if (textArea != null) {
                    new ScriptorFindAndReplace(scriptor, textArea);
                }
            }
        });

        JButton buttonSettings = createButton(getIcon("settings.gif"), "Settings");
        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorConfigFrame configFrame = new ScriptorConfigFrame(scriptor);

                configFrame.setVisible(true);
            }
        });

        JButton buttonHelp = createButton(getIcon("help.gif"), "About");
        buttonHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(scriptor, Utils.getAboutScriptor(), "About Scriptor", INFORMATION_MESSAGE);
            }
        });

        add(buttonNewFile);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonFolder);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSave);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSaveAll);
        //add(Box.createRigidArea(new Dimension(4, 0)));
        //add(buttonCloseTab);
        //add(Box.createRigidArea(new Dimension(4, 0)));
        //add(buttonCloseAllTab);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonTreeRefresh);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonCut);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonCopy);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonPaste);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonUndo);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonRedo);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonZoomIn);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonZoomOut);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSearchText);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonSettings);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonHelp);

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

        return button;
    }

    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }
}