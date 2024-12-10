package com.scriptor.core.config;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;

import java.util.*;
import java.io.*;

public class ScriptorConfig {
    private ScriptorConfigStructure structure;
    private String filePath;

    public ScriptorConfig(String filePath) {
        this.filePath = filePath;
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            structure = objectMapper.readValue(new File(filePath), ScriptorConfigStructure.class);
        } catch (FileNotFoundException e) {
            File file = new File("config.json");

            try {
                file.createNewFile();

                FileWriter writer = new FileWriter("config.json");
                writer.write("{}");
                writer.close();

                structure = objectMapper.readValue(new File(filePath), ScriptorConfigStructure.class);

                setDefault();
            } catch (IOException e1) {
                
            }
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Get
     */

    public boolean getShowWhatsNewOnStartUp() {
        return structure.getShowWhatsNewOnStartUp();
    }

    public String getDirectoryPath() {
        return structure.getDirectoryPath();
    }

    public List<String> getPaths() {
        return structure.getPaths();
    }

    public boolean getExtended() {
        return structure.getExtended();
    }

    public int getZoom() {
        return structure.getZoom();
    }

    public List<String> getExpandedFolders() {
        return structure.getExpandedFolders();
    }
    
    /*
      Set
     */

    public void setShowWhatsNewOnStartUp(boolean value) {
        structure.setShowWhatsNewOnStartUp(value);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDirectoryPath(String path) {
        structure.setDirectoryPath(path);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPaths(List<String> paths) {
        structure.setPaths(paths);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setExtended(boolean value) {
        structure.setExtended(value);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setZoom(int value) {
        structure.setZoom(value);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setExpandedFolders(List<String> folders) {
        structure.setExpandedFolders(folders);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefault() throws IOException {
        structure.setShowWhatsNewOnStartUp(true);
        structure.setDirectoryPath(null);
        structure.setPaths(new ArrayList<String>());
        structure.setExtended(false);
        structure.setZoom(12);
        structure.setExpandedFolders(new ArrayList<String>());

        save();
    }

    public void save() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), structure);
    }
}
