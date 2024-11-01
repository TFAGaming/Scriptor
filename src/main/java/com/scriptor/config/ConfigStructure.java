package com.scriptor.config;

import java.util.List;

public class ConfigStructure {
    private boolean showWhatsNewOnStartUp;
    private String directoryPath;
    private List<String> paths;
    private boolean extended;
    private int zoom;

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
}
