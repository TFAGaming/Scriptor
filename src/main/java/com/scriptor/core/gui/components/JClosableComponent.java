package com.scriptor.core.gui.components;

import javax.swing.*;

import com.scriptor.Scriptor;
import com.scriptor.core.utils.JClosableComponentType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class JClosableComponent extends JPanel {
    private final int type;

    public JClosableComponent(Scriptor scriptor, int type, List<JComponent> headerComponents, JComponent component) {
        this.type = type;

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JPanel secondaryHeaderPanel = new JPanel();
        secondaryHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (JComponent headerComponent : headerComponents) {
            secondaryHeaderPanel.add(headerComponent);
        }

        headerPanel.add(secondaryHeaderPanel, BorderLayout.WEST);

        JButton closeButton = new JButton();
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(16, 16));

        Image scaledImage = scriptor.getIcon("close_component.gif").getImage().getScaledInstance(12, 12,
                Image.SCALE_FAST);
        closeButton.setIcon(new ImageIcon(scaledImage));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = JClosableComponent.this.getParent();

                if (parent != null) {
                    parent.remove(JClosableComponent.this);
                    parent.revalidate();
                    parent.repaint();

                    scriptor.removedComponents.add(JClosableComponent.this);

                    switch (JClosableComponent.this.getType()) {
                        case JClosableComponentType.FILE_EXPLORER:
                            scriptor.secondarySplitPane.setDividerSize(0);
                            break;
                        case JClosableComponentType.TERMINAL:
                            scriptor.primarySplitPane.setDividerSize(0);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        headerPanel.add(closeButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        add(component, BorderLayout.CENTER);
    }

    public int getType() {
        return this.type;
    }
}