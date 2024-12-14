package com.scriptor.core.managers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.others.ScriptorNotification;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ScriptorNotificationsManager {
    private boolean isHidden = true;
    private Scriptor scriptor;
    private List<ScriptorNotification> notifications;
    private JPopupMenu popupMenu;

    public ScriptorNotificationsManager(Scriptor scriptor, List<ScriptorNotification> notifications) {
        this.scriptor = scriptor;
        this.notifications = notifications;
    }

    public void newNotification(ScriptorNotification notification) {
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
                ScriptorNotification notification = notifications.get(i);

                JPanel notificationPanel = new JPanel(new BorderLayout());
                notificationPanel.setPreferredSize(new Dimension(400, notification.getButtons().size() > 0 ? 70 : 50));

                // Notification title and message
                JLabel notificationLabel = new JLabel(
                        "<html><p style='width:250px;'><b>" + notification.getTitle() + "</b><br>"
                                + notification.getMessage() + "</p></html>");
                notificationLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
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

                if (notification.getButtons().size() > 0) {
                    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                    for (JButton actionButton : notification.getButtons()) {
                        actionButton.setFocusable(false);

                        buttonsPanel.add(actionButton);
                    }

                    notificationPanel.add(buttonsPanel, BorderLayout.SOUTH);
                }

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

    public void removeNotification(ScriptorNotification notification, JButton button) {
        notifications.remove(notification);

        hideNotifications();

        if (button != null) {
            showNotifications(button);
        }
    }

    public class RemoveNotificationAction extends AbstractAction {
        private final ScriptorNotification notification;
        private final JButton button;

        public RemoveNotificationAction(ScriptorNotification notification, JButton button) {
            this.notification = notification;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            removeNotification(notification, button);
        }
    }
}