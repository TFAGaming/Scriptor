package com.scriptor.core.managers;

import javax.swing.*;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.others.ScriptorNotificiation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ScriptorNotificationsManager {
    private boolean isHidden = true;
    private Scriptor scriptor;
    private List<ScriptorNotificiation> notifications;
    private JPopupMenu popupMenu;

    public ScriptorNotificationsManager(Scriptor scriptor, List<ScriptorNotificiation> notifications) {
        this.scriptor = scriptor;
        this.notifications = notifications;
    }

    public void newNotification(ScriptorNotificiation notification) {
        this.notifications.add(notification);
    }

    public void showNotifications(JButton button) {
        isHidden = false;

        popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);

        if (notifications.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("No notifications...");
            
            emptyItem.setEnabled(false);
            popupMenu.add(emptyItem);
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                ScriptorNotificiation notification = notifications.get(i);

                JPanel notificationPanel = new JPanel(new BorderLayout());
                notificationPanel.setPreferredSize(new Dimension(400, 50));

                // Notification title and message
                JLabel notificationLabel = new JLabel(
                        "<html><p style='width:250px;'><b>" + notification.getTitle() + "</b><br>"
                                + notification.getMessage() + "</p></html>");
                notificationPanel.add(notificationLabel, BorderLayout.WEST);

                // Dismiss button
                JButton dismissButton = new JButton();
                dismissButton.setIcon(scriptor.getIcon("notification_close.gif"));
                dismissButton.setPreferredSize(new Dimension(20, 10));
                dismissButton.setBorderPainted(false);
                dismissButton.setFocusPainted(false);
                dismissButton.setContentAreaFilled(false);
                dismissButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                dismissButton.addActionListener(new RemoveNotificationAction(notification, button));

                notificationPanel.add(dismissButton, BorderLayout.EAST);

                popupMenu.add(notificationPanel);

                if (i != notifications.size() - 1) {
                    popupMenu.addSeparator();
                }
            }
        }

        // Show the popup menu above the button
        popupMenu.show(button, 0, -popupMenu.getPreferredSize().height);
    }

    public void hideNotifications() {
        isHidden = true;

        popupMenu.setVisible(false);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public class RemoveNotificationAction extends AbstractAction {
        private final ScriptorNotificiation notification;
        private final JButton button;

        public RemoveNotificationAction(ScriptorNotificiation notification, JButton button) {
            this.notification = notification;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            notifications.remove(notification);

            hideNotifications();
            showNotifications(button);
        }
    }
}