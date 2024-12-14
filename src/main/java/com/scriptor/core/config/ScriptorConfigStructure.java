package com.scriptor.core.config;

import java.util.List;

public class ScriptorConfigStructure {
    private int language;
    private boolean showWhatsNewOnStartUp;
    private boolean checkForUpdatesOnStartup;
    private boolean openPreviousFilesOnStartup;
    private boolean autoSaveFileEdits;
    private boolean autoIndent;
    private int indentTabSize;
    private boolean bracketMatching;
    private boolean syntaxHighlighting;
    private String directoryPath;
    private List<String> paths;
    private boolean extended;
    private int zoom;
    private List<String> expandedFolders;
    private List<Double> windowPosition;
    private List<Double> windowSize;

    public boolean getShowWhatsNewOnStartUp() { return showWhatsNewOnStartUp; }
    public void setShowWhatsNewOnStartUp(boolean value) { this.showWhatsNewOnStartUp = value; }

    public String getDirectoryPath() { return directoryPath; }
    public void setDirectoryPath(String path) { this.directoryPath = path; }

    public List<String> getPaths() { return paths; }
    public void setPaths(List<String> paths) { this.paths = paths; }

    public boolean getExtended() { return extended; }
    public void setExtended(boolean value) { this.extended = value; }

    public boolean getCheckForUpdatesOnStartup() { return checkForUpdatesOnStartup; }
    public void setCheckForUpdatesOnStartup(boolean value) { this.checkForUpdatesOnStartup = value; }

    public boolean getOpenPreviousFilesOnStartup() { return openPreviousFilesOnStartup; }
    public void setOpenPreviousFilesOnStartup(boolean value) { this.openPreviousFilesOnStartup = value; }

    public boolean getAutoSaveFileEdits() { return autoSaveFileEdits; }
    public void setAutoSaveFileEdits(boolean value) { this.autoSaveFileEdits = value; }

    public boolean getAutoIndent() { return autoIndent; }
    public void setAutoIndent(boolean value) { this.autoIndent = value; }

    public boolean getBracketMatching() { return bracketMatching; }
    public void setBracketMatching(boolean value) { this.bracketMatching = value; }

    public boolean getSyntaxHighlighting() { return syntaxHighlighting; }
    public void setSyntaxHighlighting(boolean value) { this.syntaxHighlighting = value; }

    public int getLanguage() { return language; }
    public void setLanguage(int languageId) { this.language = languageId; }

    public int getIndentTabSize() { return indentTabSize; }
    public void setIndentTabSize(int size) { this.indentTabSize = size; }

    public int getZoom() { return zoom; }
    public void setZoom(int value) { this.zoom = value; }

    public List<String> getExpandedFolders() { return expandedFolders; }
    public void setExpandedFolders(List<String> folders) { this.expandedFolders = folders; }

    public List<Double> getWindowPosition() { return windowPosition; }
    public void setWindowPosition(List<Double> position) { this.windowPosition = position; }

    public List<Double> getWindowSize() { return windowSize; }
    public void setWindowSize(List<Double> size) { this.windowSize = size; }
}
