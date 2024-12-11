package com.scriptor.core.gui.toolbar;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.scriptor.Scriptor;
import com.scriptor.Utils;
import com.scriptor.core.gui.frames.ScriptorSettings;
import com.scriptor.core.gui.components.JClosableComponentType;
import com.scriptor.core.gui.frames.ScriptorFindAndReplace;

public class ScriptorPrimaryToolbar extends JToolBar {
    public ScriptorPrimaryToolbar(Scriptor scriptor) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewFile = createButton(getIcon("new_file.png"), "New File");
        buttonNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.newFile();
            }
        });

        JButton buttonFolder = createButton(getIcon("folder_open.png"), "Open Folder");
        buttonFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.openFolder();
            }
        });

        JButton buttonSave = createButton(getIcon("save.gif"), "Save");
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveFile();
            }
        });

        JButton buttonSaveAll = createButton(getIcon("saveall.gif"), "Save All");
        buttonSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveAllTabs();
            }
        });

        JButton buttonCopy = createButton(getIcon("copy.gif"), "Copy");
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null) {
                    textArea.copy();
                }
            }
        });

        JButton buttonPaste = createButton(getIcon("paste.gif"), "Paste");
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null) {
                    textArea.paste();
                }
            }
        });

        JButton buttonCut = createButton(getIcon("cut.gif"), "Cut");
        buttonCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null) {
                    textArea.cut();
                }
            }
        });

        JButton buttonUndo = createButton(getIcon("undo.gif"), "Undo Edit");
        buttonUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null && textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JButton buttonRedo = createButton(getIcon("redo.gif"), "Redo Edit");
        buttonRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null && textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JButton buttonZoomIn = createButton(getIcon("zoom_in.png"), "Zoom In");
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.zoomIn();
            }
        });

        JButton buttonZoomOut = createButton(getIcon("zoom_out.png"), "Zoom Out");
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.zoomOut();
            }
        });

        JButton buttonSearchText = createButton(getIcon("search_text.png"), "Find & Replace...");
        buttonSearchText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RSyntaxTextArea textArea = scriptor.textAreaTabManager.getCurrentTextArea();

                if (textArea != null) {
                    new ScriptorFindAndReplace(scriptor, textArea);
                }
            }
        });

        JButton buttonTreeExplorerToggle = createButton(getIcon("tree_explorer.gif"), "Toggle File Explorer");
        buttonTreeExplorerToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.addBackComponent(JClosableComponentType.FILE_EXPLORER);
            }
        });

        JButton buttonTerminalToggle = createButton(getIcon("console.gif"), "Toggle Terminal");
        buttonTerminalToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.addBackComponent(JClosableComponentType.TERMINAL);
            }
        });

        JButton buttonSettings = createButton(getIcon("settings.gif"), "Settings");
        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptorSettings configFrame = new ScriptorSettings(scriptor);

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

        add(buttonTreeExplorerToggle);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonTerminalToggle);
        add(Box.createRigidArea(new Dimension(4, 0)));

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
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }
}
