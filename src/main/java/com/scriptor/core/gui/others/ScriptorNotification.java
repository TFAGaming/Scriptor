package com.scriptor.core.gui.others;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;

public class ScriptorNotification {
    private String title;
    private String message;
    private Icon icon;
    private List<JButton> buttons = new ArrayList<JButton>();

    public ScriptorNotification(String title, String message, Icon icon, List<JButton> buttonsList) {
        this.title = title;
        this.message = message;
        this.icon = icon;

        if (buttonsList != null) {
            this.buttons = buttonsList;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Icon getIcon() {
        return icon;
    }

    public List<JButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<JButton> buttonsList) {
        this.buttons = buttonsList;
    }
}
