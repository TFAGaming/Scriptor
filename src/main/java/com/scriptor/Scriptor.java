package com.scriptor;

import javax.swing.*;
import javax.swing.event.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.scriptor.config.ScriptorConfig;
import com.scriptor.core.*;
import com.scriptor.frames.WelcomeFrame;

import org.apache.commons.io.FilenameUtils;

import java.awt.event.*;
import java.io.*;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Scriptor extends JFrame implements ActionListener {
    public ScriptorConfig config = new ScriptorConfig("config.json");
    public ScriptorLogger logger = new ScriptorLogger();
    public JTabbedPane tabbedTextAreaPane;
    public JTabbedPane tabbedTerminalPane;
    public ScriptorFilesExplorer filesExplorer;
    public JFileChooser fileChooser;
    public List<String> arrayPaths = new ArrayList<String>();
    public List<Boolean> arraySavedPaths = new ArrayList<Boolean>();
    public List<RSyntaxTextArea> arrayTextAreas = new ArrayList<RSyntaxTextArea>();
    public List<ScriptorTerminal> arrayTerminals = new ArrayList<ScriptorTerminal>();
    public boolean _switchedTab = false;

    public Scriptor() {
        logger.clearAll();

        setTitle("Scriptor");
        setSize(1600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setBackground(Color.decode("#f0f0f0"));

        setIconImage(getIcon("scriptor_icon.png").getImage());

        customizeUI();

        tabbedTextAreaPane = new JTabbedPane();
        tabbedTextAreaPane.setBackground(Color.decode("#f0f0f0"));

        tabbedTerminalPane = new JTabbedPane();
        tabbedTerminalPane.setBackground(Color.decode("#f0f0f0"));

        filesExplorer = new ScriptorFilesExplorer(this, config.getDirectoryPath() == null
                ? System.getProperty("user.dir")
                : config.getDirectoryPath());

        setJMenuBar(new ScriptorMenubar(this));

        fileChooser = new JFileChooser(config.getDirectoryPath());
        fileChooser.setAcceptAllFileFilterUsed(true);

        ScriptorToolbar toolBar = new ScriptorToolbar(this);

        add(toolBar, BorderLayout.NORTH);

        JSplitPane primarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedTextAreaPane, tabbedTerminalPane);
        primarySplitPane.setResizeWeight(0.5);
        primarySplitPane.setDividerLocation(0.3);

        JSplitPane secondarySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filesExplorer,
                primarySplitPane);
        secondarySplitPane.setResizeWeight(0.1);
        secondarySplitPane.setDividerLocation(0.8);

        add(secondarySplitPane);

        if (config.getExtended()) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        setLocationRelativeTo(null);

        addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent event) {
                if ((event.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    if (!config.getExtended()) {
                        config.setExtended(true);
                    }
                } else if ((event.getNewState() & Frame.NORMAL) == Frame.NORMAL) {
                    if (config.getExtended()) {
                        config.setExtended(false);
                    }
                }
            }
        });

        new ScriptorKeybinds(this, tabbedTextAreaPane);

        openPreviousTabs();

        newTerminal();

        tabbedTextAreaPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedTextAreaPane.getSelectedIndex();

                if (selectedIndex >= 0 && arrayPaths.size() > 0) {
                    String path = arrayPaths.get(selectedIndex);

                    setTitle("Scriptor - " + (path == null ? "Untitled" : path));
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scriptor scriptor = new Scriptor();
            scriptor.setVisible(true);

            if (scriptor.config.getShowWhatsNewOnStartUp()) {
                WelcomeFrame welcomeFrame = new WelcomeFrame(scriptor);

                welcomeFrame.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Nothing happens here.
    }

    private void customizeUI() {
        try {
            try {
                UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private RTextScrollPane newTextAreaWithScrollPane(String filePath) {
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);

        if (filePath != null) {
            String fileExtension = FilenameUtils.getExtension(filePath);

            textArea.setSyntaxEditingStyle(Utils.getSyntaxConstantByFileExtension(fileExtension));
        } else {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        }

        textArea.setAnimateBracketMatching(false);
        textArea.setCodeFoldingEnabled(true);

        Font font = textArea.getFont();
        textArea.setFont(font.deriveFont((float) config.getZoom()));

        Utils.setTextSyntaxHighlightingColors(textArea);

        RTextScrollPane scrollTextAreaPane = new RTextScrollPane(textArea);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int selectedIndex = tabbedTextAreaPane.getSelectedIndex();

                if (selectedIndex != -1) {
                    if (_switchedTab) {
                        return;
                    }

                    if (arrayPaths.get(selectedIndex) == null) {
                        setTitle("Scriptor - " + "Untitled*");
                        updateTextAreaTabTitle(selectedIndex, "Untitled*");

                        return;
                    }

                    File file = new File(arrayPaths.get(selectedIndex));

                    arraySavedPaths.set(selectedIndex, false);

                    if (file.exists() && file.isFile()) {
                        setTitle("Scriptor - " + file.getPath() + "*");
                        updateTextAreaTabTitle(selectedIndex, file.getName() + "*");
                    } else {
                        setTitle("Scriptor - " + "Untitled*");
                        updateTextAreaTabTitle(selectedIndex, "Untitled*");
                    }
                }
            }
        });

        arrayTextAreas.add(textArea);

        return scrollTextAreaPane;
    }

    private ScriptorTerminal newTerminalForTerminalPane() {
        ScriptorTerminal terminal = new ScriptorTerminal(this, config.getDirectoryPath());

        return terminal;
    }

    private RSyntaxTextArea newTextArea(String filePath) {
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);

        if (filePath != null) {
            String fileExtension = FilenameUtils.getExtension(filePath);

            textArea.setSyntaxEditingStyle(Utils.getSyntaxConstantByFileExtension(fileExtension));
        } else {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        }

        textArea.setAnimateBracketMatching(false);
        textArea.setCodeFoldingEnabled(true);

        Font font = textArea.getFont();
        textArea.setFont(font.deriveFont((float) config.getZoom()));

        Utils.setTextSyntaxHighlightingColors(textArea);

        return textArea;
    }

    public void zoomIn() {
        if (arrayTextAreas.size() > 0) {
            for (RSyntaxTextArea textArea : arrayTextAreas) {
                Font font = textArea.getFont();
                float size = font.getSize() + 1.0f;

                if (size > 35.0f) {
                    return;
                }

                textArea.setFont(font.deriveFont(size));
                config.setZoom(Math.round(size));
            }
        }
    }

    public void zoomOut() {
        if (arrayTextAreas.size() > 0) {
            for (RSyntaxTextArea textArea : arrayTextAreas) {
                Font font = textArea.getFont();
                float size = font.getSize() - 1.0f;

                if (size < 5.0f) {
                    return;
                }

                textArea.setFont(font.deriveFont(size));
                config.setZoom(Math.round(size));
            }
        }
    }

    public void newFile() {
        tabbedTextAreaPane.addTab("Untitled", newTextAreaWithScrollPane(null));
        addCloseButtonToTextAreaTab();

        arrayPaths.add(null);
        arraySavedPaths.add(false);
        config.setPaths(arrayPaths);

        tabbedTextAreaPane.setSelectedIndex(tabbedTextAreaPane.getTabCount() - 1);

        setTitle("Scriptor - " + "Untitled");
    }

    public void newTerminal() {
        ScriptorTerminal terminal = newTerminalForTerminalPane();

        arrayTerminals.add(terminal);

        tabbedTerminalPane.addTab("Terminal #" + arrayTerminals.size(), terminal);
        addCloseButtonToTerminalTab();

        tabbedTerminalPane.setSelectedIndex(tabbedTerminalPane.getTabCount() - 1);
    }

    public void openFile() {
        fileChooser.setDialogTitle("Open File");
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            openFileFromPath(selectedFile.getPath());
        }
    }

    public void openFolder() {
        fileChooser.setDialogTitle("Open Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();

            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            filesExplorer.setPath(selectedFolder.getPath());
            config.setDirectoryPath(selectedFolder.getPath());
        }
    }

    public void openFileFromPath(String path) {
        File file = new File(path);

        if (!(file.exists() && file.isFile())) {
            showMessageDialog(null, "Unable to open the file from the path:\n" + path, "File Not Found", ERROR_MESSAGE);

            return;
        }

        if (arrayPaths.contains(path)) {
            setUserToTabByPath(path);

            return;
        }

        String extension = FilenameUtils.getExtension(file.getPath());

        if (!Utils.getSupportedAndEditableExtensions().contains(extension)) {
            int response = showConfirmDialog(null,
                    "The file extension \'." + extension
                            + "\' is not supported.\nDo you want to open the file with the default associated program?",
                    "Unsupported File Extension",
                    YES_NO_CANCEL_OPTION);

            if (response == YES_OPTION) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            } else if (response == CANCEL_OPTION) {
                return;
            }
        }

        _switchedTab = true;

        tabbedTextAreaPane.addTab(file.getName(), newTextAreaWithScrollPane(path));
        addCloseButtonToTextAreaTab();

        arrayPaths.add(file.getPath());
        arraySavedPaths.add(true);

        config.setPaths(arrayPaths);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            RSyntaxTextArea textArea = arrayTextAreas.get(arrayTextAreas.size() - 1);

            while ((line = reader.readLine()) != null) {
                textArea.insert(line + "\n", textArea.getText().length());
            }

            reader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        tabbedTextAreaPane.setSelectedIndex(tabbedTextAreaPane.getTabCount() - 1);
        setTitle("Scriptor - " + (path == null ? "Untitled" : path));

        _switchedTab = false;
    }

    public void saveFile() {
        int selectedIndex = tabbedTextAreaPane.getSelectedIndex();

        if (selectedIndex != -1) {
            RSyntaxTextArea textArea = arrayTextAreas.get(selectedIndex);

            String string = textArea.getText();
            File selectedFile = null;

            if (arrayPaths.get(selectedIndex) == null) {
                fileChooser.setDialogTitle("Save File");
                int reponse = fileChooser.showSaveDialog(this);

                if (reponse == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            } else {
                selectedFile = new File(arrayPaths.get(selectedIndex));

                if (!(selectedFile.exists() && selectedFile.isFile())) {
                    arrayPaths.set(selectedIndex, null);
                    arraySavedPaths.set(selectedIndex, false);

                    config.setPaths(arrayPaths);

                    saveFile();

                    return;
                }
            }

            if (selectedFile != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                    writer.write(string);
                    writer.close();

                    boolean wasNullBefore = arrayPaths.get(selectedIndex) == null;

                    arrayPaths.set(selectedIndex, selectedFile.getPath());
                    arraySavedPaths.set(selectedIndex, true);
                    config.setPaths(arrayPaths);

                    setTitle("Scriptor - " + selectedFile.getPath());
                    updateTextAreaTabTitle(selectedIndex, selectedFile.getName());

                    if (wasNullBefore) {
                        filesExplorer.refreshTree();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void saveFileByIndex(int index) {
        if (index != -1) {
            RSyntaxTextArea textArea = arrayTextAreas.get(index);

            String string = textArea.getText();
            File selectedFile = null;

            if (arrayPaths.get(index) == null) {
                fileChooser.setDialogTitle("Save File");
                int reponse = fileChooser.showSaveDialog(this);

                if (reponse == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            } else {
                selectedFile = new File(arrayPaths.get(index));

                if (!(selectedFile.exists() && selectedFile.isFile())) {
                    arrayPaths.set(index, null);
                    arraySavedPaths.set(index, false);

                    config.setPaths(arrayPaths);

                    saveFile();

                    return;
                }
            }

            if (selectedFile != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                    writer.write(string);
                    writer.close();

                    arrayPaths.set(index, selectedFile.getPath());
                    arraySavedPaths.set(index, true);
                    config.setPaths(arrayPaths);

                    setTitle("Scriptor - " + selectedFile.getPath());
                    updateTextAreaTabTitle(index, selectedFile.getName());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void saveAsFile() {
        int selectedIndex = tabbedTextAreaPane.getSelectedIndex();
        if (selectedIndex != -1) {
            RSyntaxTextArea textArea = arrayTextAreas.get(selectedIndex);

            String string = textArea.getText();
            File selectedFile;

            fileChooser.setDialogTitle("Save As File");
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            } else {
                return;
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(string);
                writer.close();

                arrayPaths.set(selectedIndex, selectedFile.getPath());
                arraySavedPaths.set(selectedIndex, true);

                config.setPaths(arrayPaths);

                setTitle("Scriptor - " + selectedFile.getPath());
                updateTextAreaTabTitle(selectedIndex, selectedFile.getName());

                arrayTextAreas.set(selectedIndex, newTextArea(selectedFile.getPath()));

                filesExplorer.refreshTree();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void closeTextAreaTabByIndex(int index) {
        if (!arraySavedPaths.get(index)) {
            if (!(arrayPaths.get(index) == null && arrayTextAreas.get(index).getText().length() == 0)) {
                int response = showConfirmDialog(null,
                        arrayPaths.get(index) == null ? "Do you want to save this file before closing the tab?"
                                : "Do you want to save this file before closing the tab?\n"
                                        + arrayPaths.get(index),
                        "Close?",
                        YES_NO_CANCEL_OPTION);
                if (response == YES_OPTION) {
                    saveFile();

                    closeTextAreaTabByIndex(index);

                    return;
                } else if (response == NO_OPTION) {
                    tabbedTextAreaPane.removeTabAt(index);

                    arraySavedPaths.remove(index);
                    arrayPaths.remove(index);
                    arrayTextAreas.remove(index);

                    config.setPaths(arrayPaths);

                    if (arrayPaths.size() == 0) {
                        newFile();
                    }

                    return;
                } else {
                    return;
                }
            }
        }

        tabbedTextAreaPane.removeTabAt(index);

        arraySavedPaths.remove(index);
        arrayPaths.remove(index);
        arrayTextAreas.remove(index);

        config.setPaths(arrayPaths);

        if (arrayPaths.size() == 0) {
            newFile();
        }
    }

    public void closeTerminalTabByIndex(int index) {
        tabbedTerminalPane.removeTabAt(index);
        arrayTerminals.remove(index);

        if (arrayTerminals.size() == 0) {
            newTerminal();
        }
    }

    private void addCloseButtonToTextAreaTab() {
        int index = tabbedTextAreaPane.getTabCount() - 1;

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);

        JLabel tabTitle = new JLabel(tabbedTextAreaPane.getTitleAt(index));

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = tabbedTextAreaPane.indexOfTabComponent(tabPanel);

                closeTextAreaTabByIndex(index);
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        tabbedTextAreaPane.setTabComponentAt(index, tabPanel);
    }

    private void addCloseButtonToTerminalTab() {
        int index = tabbedTerminalPane.getTabCount() - 1;

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);

        JLabel tabTitle = new JLabel(tabbedTerminalPane.getTitleAt(index));

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = tabbedTerminalPane.indexOfTabComponent(tabPanel);

                closeTerminalTabByIndex(index);
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        tabbedTerminalPane.setTabComponentAt(index, tabPanel);
    }

    public void updateTextAreaTabTitle(int index, String newTitle) {
        JPanel tabPanel = (JPanel) tabbedTextAreaPane.getTabComponentAt(index);

        if (tabPanel != null && tabPanel.getComponent(0) instanceof JLabel) {
            JLabel tabTitle = (JLabel) tabPanel.getComponent(0);
            tabTitle.setText(newTitle);
        }
    }

    private void openPreviousTabs() {
        List<String> paths = config.getPaths();

        if (paths == null || (paths != null && paths.size() == 0)) {
            newFile();

            return;
        }

        for (String path : paths) {
            if (path == null) {
                continue;
            }

            openFileFromPath(path);
        }

        if (arrayPaths.size() == 0) {
            newFile();
        }
    }

    public void closeAllTextAreaTabs() {
        int size = arrayPaths.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTextAreaTabByIndex(index);
        }
    }

    public void saveAllTextAreaTabs() {
        int size = arrayPaths.size();

        for (int index = size - 1; index >= 0; index--) {
            saveFileByIndex(index);
        }
    }

    public void closeAllTerminalTabs() {
        int size = arrayTerminals.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTerminalTabByIndex(index);
        }
    }

    public void setUserToTabByPath(String path) {
        if (arrayPaths.contains(path)) {
            int index = arrayPaths.indexOf(path);

            tabbedTextAreaPane.setSelectedIndex(index);
        }
    }

    public RSyntaxTextArea getCurrentTextArea() {
        int index = tabbedTextAreaPane.getSelectedIndex();

        if (index != 1) {
            return arrayTextAreas.get(index);
        } else {
            return null;
        }
    }

    public ScriptorTerminal getCurrentTerminal() {
        int index = tabbedTerminalPane.getSelectedIndex();

        if (index != 1) {
            return arrayTerminals.get(index);
        } else {
            return null;
        }
    }

    public ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }
}
