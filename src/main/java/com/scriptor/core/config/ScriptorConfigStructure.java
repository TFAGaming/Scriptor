package com.scriptor.core.config;

import java.util.List;

public class ScriptorConfigStructure {
    private boolean showWhatsNewOnStartUp;
    private String directoryPath;
    private List<String> paths;
    private boolean extended;
    private int zoom;
    private List<String> expandedFolders;

    public boolean getShowWhatsNewOnStartUp() { return showWhatsNewOnStartUp; }
    public void setShowWhatsNewOnStartUp(boolean value) { this.showWhatsNewOnStartUp = value; }

    public String getDirectoryPath() { return directoryPath; }
    public void setDirectoryPath(String path) { this.directoryPath = path; }

    public List<String> getPaths() { return paths; }
    public void setPaths(List<String> paths) { this.paths = paths; }

    public boolean getExtended() { return extended; }
    public void setExtended(boolean value) { this.extended = value; }

    public int getZoom() { return zoom; }
    public void setZoom(int value) { this.zoom = value; }

    public List<String> getExpandedFolders() { return expandedFolders; }
    public void setExpandedFolders(List<String> folders) { this.expandedFolders = folders; }
}
