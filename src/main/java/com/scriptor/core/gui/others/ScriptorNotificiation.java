package com.scriptor.core.gui.others;

public class ScriptorNotificiation {
    private String title;
    private String message;

    public ScriptorNotificiation(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
