package com.scriptor.core.utils;

import javax.swing.*;

import com.scriptor.Scriptor;

import java.awt.event.*;

public class ScriptorKeybinds {
    public ScriptorKeybinds(Scriptor scriptor, JTabbedPane tabbedPane) {
        tabbedPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveFile();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        tabbedPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.saveAsFile();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        tabbedPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.newFile();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        tabbedPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.openFile();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
