package com.scriptor.core.managers;

import javax.swing.*;
import javax.swing.event.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import com.scriptor.Scriptor;
import com.scriptor.core.plugins.ScriptorPluginsHandler;
import com.scriptor.core.utils.ScriptorProgrammingLanguagesUtils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FilenameUtils;

import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ScriptorTextAreaTabManager {
    private final Scriptor scriptor;
    private final JTabbedPane tabbedPane;

    private boolean _switchedTab = false;

    public JFileChooser fileChooser;
    public List<String> arrayPaths = new ArrayList<String>();
    public List<Boolean> arraySavedPaths = new ArrayList<Boolean>();
    public List<RSyntaxTextArea> arrayTextAreas = new ArrayList<RSyntaxTextArea>();
    public ScriptorPluginsHandler pluginsHandler = new ScriptorPluginsHandler("plugins");

    public ScriptorTextAreaTabManager(Scriptor scriptor, JTabbedPane tabbedPane) {
        this.scriptor = scriptor;
        this.tabbedPane = tabbedPane;

        this.fileChooser = new JFileChooser(scriptor.config.getDirectoryPath());
        this.fileChooser.setAcceptAllFileFilterUsed(true);

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();

                if (selectedIndex >= 0 && arrayPaths.size() > 0) {
                    String path = arrayPaths.get(selectedIndex);

                    scriptor.setTitle("Scriptor - " + (path == null ? "Untitled" : path));
                }
            }
        });

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                scriptor.updateStatusBar();
            }
        });
    }

    public void newFile() {
        tabbedPane.addTab("Untitled", newTextArea(null));

        arrayPaths.add(null);
        arraySavedPaths.add(false);
        scriptor.config.setPaths(arrayPaths);

        addCloseButton();

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        scriptor.setTitle("Scriptor - " + "Untitled");
    }

    public void openFile() {
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(scriptor);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            openFileFromPath(selectedFile.getPath());
        }
    }

    public void openFolder() {
        fileChooser.setDialogTitle("Open Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnVal = fileChooser.showOpenDialog(scriptor);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();

            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            scriptor.filesExplorer.setPath(selectedFolder.getPath());
            scriptor.config.setDirectoryPath(selectedFolder.getPath());
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

        if (!ScriptorProgrammingLanguagesUtils.getSupportedAndEditableExtensions().contains(extension)) {
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

        tabbedPane.addTab(file.getName(), newTextArea(path));

        arrayPaths.add(file.getPath());
        arraySavedPaths.add(true);

        addCloseButton();

        scriptor.config.setPaths(arrayPaths);

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;

            RSyntaxTextArea textArea = arrayTextAreas.get(arrayTextAreas.size() - 1);

            while ((line = reader.readLine()) != null) {
                textArea.insert(line + "\n", textArea.getText().length());
            }

            reader.close();

            // Set scroll bar on top
            RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                    textArea);

            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        _switchedTab = false;

        if (arrayPaths.size() == 2 && arrayPaths.get(0) == null && arrayTextAreas.get(0).getText().length() == 0) {
            closeTabByIndex(0);
        }

        scriptor.setTitle("Scriptor - " + (path == null ? "Untitled" : path));
        scriptor.updateStatusBar();
    }

    public void saveFile() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex != -1) {
            RSyntaxTextArea textArea = arrayTextAreas.get(selectedIndex);

            String string = textArea.getText();
            File selectedFile = null;

            if (arrayPaths.get(selectedIndex) == null) {
                fileChooser.setDialogTitle("Save File");
                int reponse = fileChooser.showSaveDialog(scriptor);

                if (reponse == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            } else {
                selectedFile = new File(arrayPaths.get(selectedIndex));

                if (!(selectedFile.exists() && selectedFile.isFile())) {
                    arrayPaths.set(selectedIndex, null);
                    arraySavedPaths.set(selectedIndex, false);

                    scriptor.config.setPaths(arrayPaths);

                    saveFile();

                    return;
                }
            }

            if (selectedFile != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8));
                    writer.write(string);
                    writer.close();

                    boolean wasNullBefore = arrayPaths.get(selectedIndex) == null;

                    arrayPaths.set(selectedIndex, selectedFile.getPath());
                    arraySavedPaths.set(selectedIndex, true);
                    scriptor.config.setPaths(arrayPaths);

                    scriptor.setTitle("Scriptor - " + selectedFile.getPath());
                    updateTabTitle(selectedIndex, selectedFile.getName());

                    if (wasNullBefore) {
                        scriptor.filesExplorer.refreshTree();
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
                int reponse = fileChooser.showSaveDialog(scriptor);

                if (reponse == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            } else {
                selectedFile = new File(arrayPaths.get(index));

                if (!(selectedFile.exists() && selectedFile.isFile())) {
                    arrayPaths.set(index, null);
                    arraySavedPaths.set(index, false);

                    scriptor.config.setPaths(arrayPaths);

                    saveFile();

                    return;
                }
            }

            if (selectedFile != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8));
                    writer.write(string);
                    writer.close();

                    arrayPaths.set(index, selectedFile.getPath());
                    arraySavedPaths.set(index, true);
                    scriptor.config.setPaths(arrayPaths);

                    scriptor.setTitle("Scriptor - " + selectedFile.getPath());
                    updateTabTitle(index, selectedFile.getName());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void saveAsFile() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            RSyntaxTextArea textArea = arrayTextAreas.get(selectedIndex);

            String string = textArea.getText();
            File selectedFile;

            fileChooser.setDialogTitle("Save As File");
            int returnVal = fileChooser.showSaveDialog(scriptor);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            } else {
                return;
            }

            try {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8));
                writer.write(string);
                writer.close();

                arrayPaths.set(selectedIndex, selectedFile.getPath());
                arraySavedPaths.set(selectedIndex, true);

                scriptor.config.setPaths(arrayPaths);

                scriptor.setTitle("Scriptor - " + selectedFile.getPath());
                updateTabTitle(selectedIndex, selectedFile.getName());

                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(selectedFile), StandardCharsets.UTF_8));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        textArea.insert(line + "\n", textArea.getText().length());
                    }

                    reader.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                scriptor.filesExplorer.refreshTree();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void closeTabByIndex(int index) {
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

                    closeTabByIndex(index);

                    return;
                } else if (response == NO_OPTION) {
                    tabbedPane.removeTabAt(index);

                    arraySavedPaths.remove(index);
                    arrayPaths.remove(index);
                    arrayTextAreas.remove(index);

                    scriptor.config.setPaths(arrayPaths);

                    if (arrayPaths.size() == 0) {
                        newFile();
                    }

                    return;
                } else {
                    return;
                }
            }
        }

        tabbedPane.removeTabAt(index);

        arraySavedPaths.remove(index);
        arrayPaths.remove(index);
        arrayTextAreas.remove(index);

        scriptor.config.setPaths(arrayPaths);

        if (arrayPaths.size() == 0) {
            newFile();
        }
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

                RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                        textArea);
                if (scrollPane != null) {
                    Gutter gutter = scrollPane.getGutter();

                    Font lineNumberFont = gutter.getLineNumberFont();

                    gutter.setLineNumberFont(lineNumberFont.deriveFont(size));
                }

                scriptor.config.setZoom(Math.round(size));
                scriptor.updateStatusBar();
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

                RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                        textArea);
                if (scrollPane != null) {
                    Gutter gutter = scrollPane.getGutter();

                    Font lineNumberFont = gutter.getLineNumberFont();

                    gutter.setLineNumberFont(lineNumberFont.deriveFont(size));
                }

                scriptor.config.setZoom(Math.round(size));
                scriptor.updateStatusBar();
            }
        }
    }

    public void updateTabTitle(int index, String newTitle) {
        JPanel tabPanel = (JPanel) tabbedPane.getTabComponentAt(index);

        if (tabPanel != null && tabPanel.getComponent(0) instanceof JLabel) {
            JLabel tabTitle = (JLabel) tabPanel.getComponent(0);
            tabTitle.setText(newTitle);
        }
    }

    public void openPreviousTabs() {
        List<String> paths = scriptor.config.getPaths();

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

    public void closeAllTabs() {
        int size = arrayPaths.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTabByIndex(index);
        }
    }

    public void saveAllTabs() {
        int size = arrayPaths.size();

        for (int index = size - 1; index >= 0; index--) {
            saveFileByIndex(index);
        }
    }

    public void setUserToTabByPath(String path) {
        if (arrayPaths.contains(path)) {
            int index = arrayPaths.indexOf(path);

            tabbedPane.setSelectedIndex(index);
        }
    }

    public int getCurrentIndex() {
        return tabbedPane.getSelectedIndex();
    }

    public RSyntaxTextArea getCurrentTextArea() {
        int index = tabbedPane.getSelectedIndex();

        if (arrayTextAreas.size() == 0 || index == -1) {
            return null;
        }

        return arrayTextAreas.get(index);
    }

    public String getCurrentPath() {
        int currentIndex = tabbedPane.getSelectedIndex();

        if (arrayPaths.size() > 0) {
            String path = arrayPaths.get(currentIndex);

            return path;
        } else {
            return null;
        }
    }

    private RTextScrollPane newTextArea(String filePath) {
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);

        if (filePath != null) {
            String fileExtension = FilenameUtils.getExtension(filePath);

            textArea.setSyntaxEditingStyle(ScriptorProgrammingLanguagesUtils.getSyntaxConstantByFileExtension(fileExtension));
        } else {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        }

        textArea.setBracketMatchingEnabled(true);
        textArea.setAnimateBracketMatching(false);
        textArea.setAutoIndentEnabled(true);
        textArea.setDragEnabled(true);
        textArea.setTabSize(4);
        textArea.setCodeFoldingEnabled(true);
        textArea.setHyperlinksEnabled(true);
        textArea.setHighlightSecondaryLanguages(false);

        Font font = textArea.getFont();
        textArea.setFont(font.deriveFont((float) scriptor.config.getZoom()));

        addSyntaxHighlighting(textArea);

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setWheelScrollingEnabled(true);

        Gutter gutter = scrollPane.getGutter();

        Font lineNumberFont = gutter.getLineNumberFont();
        gutter.setLineNumberFont(lineNumberFont.deriveFont((float) scriptor.config.getZoom()));

        gutter.setBookmarkingEnabled(true);
        gutter.setBookmarkIcon(scriptor.getIcon("bookmark.gif"));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();

                if (selectedIndex != -1) {
                    scriptor.updateStatusBar();

                    if (_switchedTab) {
                        return;
                    }

                    if (arrayPaths.get(selectedIndex) == null) {
                        scriptor.setTitle("Scriptor - " + "Untitled*");
                        updateTabTitle(selectedIndex, "Untitled*");

                        return;
                    }

                    File file = new File(arrayPaths.get(selectedIndex));

                    arraySavedPaths.set(selectedIndex, false);

                    if (file.exists() && file.isFile()) {
                        scriptor.setTitle("Scriptor - " + file.getPath() + "*");
                        updateTabTitle(selectedIndex, file.getName() + "*");
                    } else {
                        scriptor.setTitle("Scriptor - " + "Untitled*");
                        updateTabTitle(selectedIndex, "Untitled*");
                    }
                }
            }
        });

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                scriptor.updateStatusBar();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                scriptor.updateStatusBar();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                scriptor.updateStatusBar();
            }
        });

        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scriptor.updateStatusBar();
            }
        });

        textArea.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
                    if (e.getWheelRotation() < 0) {
                        scriptor.textAreaTabManager.zoomIn();
                    } else if (e.getWheelRotation() > 0) {
                        scriptor.textAreaTabManager.zoomOut();
                    }
                } else {
                    e.getComponent().getParent().dispatchEvent(e);
                }
            }
        });

        arrayTextAreas.add(textArea);

        return scrollPane;
    }

    private void addCloseButton() {
        int index = tabbedPane.getTabCount() - 1;

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tabTitle = new JLabel(tabbedPane.getTitleAt(index),
                arrayPaths.get(index) == null ? null : getFileIcon(new File(arrayPaths.get(index))), JLabel.LEFT);
        tabTitle.setIconTextGap(5);

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.indexOfTabComponent(tabPanel);

                closeTabByIndex(index);

                scriptor.updateStatusBar();
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        tabbedPane.setTabComponentAt(index, tabPanel);
    }

    private void addSyntaxHighlighting(RSyntaxTextArea textArea) {
        SyntaxScheme scheme = textArea.getSyntaxScheme();

        scheme.getStyle(Token.ANNOTATION).foreground = getConfigSyntaxHighlightingTokenColor("ANNOTATION");
        scheme.getStyle(Token.RESERVED_WORD).foreground = getConfigSyntaxHighlightingTokenColor("RESERVED_WORD");
        scheme.getStyle(Token.RESERVED_WORD_2).foreground = getConfigSyntaxHighlightingTokenColor("RESERVED_WORD");

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_STRING_DOUBLE_QUOTE");
        scheme.getStyle(Token.LITERAL_CHAR).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_CHAR");
        scheme.getStyle(Token.LITERAL_BACKQUOTE).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_BACKQUOTE");

        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_BOOLEAN");

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_NUMBER_DECIMAL_INT");
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_NUMBER_FLOAT");
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = getConfigSyntaxHighlightingTokenColor("LITERAL_NUMBER_HEXADECIMAL");

        scheme.getStyle(Token.REGEX).foreground = getConfigSyntaxHighlightingTokenColor("REGEX");

        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = getConfigSyntaxHighlightingTokenColor("COMMENT_MULTILINE");
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = getConfigSyntaxHighlightingTokenColor("COMMENT_DOCUMENTATION");
        scheme.getStyle(Token.COMMENT_EOL).foreground = getConfigSyntaxHighlightingTokenColor("COMMENT_EOL");

        scheme.getStyle(Token.SEPARATOR).foreground = getConfigSyntaxHighlightingTokenColor("SEPARATOR");
        scheme.getStyle(Token.OPERATOR).foreground = getConfigSyntaxHighlightingTokenColor("OPERATOR");
        scheme.getStyle(Token.IDENTIFIER).foreground = getConfigSyntaxHighlightingTokenColor("IDENTIFIER");
        scheme.getStyle(Token.VARIABLE).foreground = getConfigSyntaxHighlightingTokenColor("VARIABLE");
        scheme.getStyle(Token.FUNCTION).foreground = getConfigSyntaxHighlightingTokenColor("FUNCTION");
        scheme.getStyle(Token.PREPROCESSOR).foreground = getConfigSyntaxHighlightingTokenColor("PREPROCESSOR");

        // HTML / XML related
        scheme.getStyle(Token.MARKUP_CDATA).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_CDATA");
        scheme.getStyle(Token.MARKUP_COMMENT).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_COMMENT");
        scheme.getStyle(Token.MARKUP_DTD).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_DTD");
        // scheme.getStyle(Token.MARKUP_ENTITY_REFERENCE).foreground = Color.BLUE;
        // scheme.getStyle(Token.MARKUP_PROCESSING_INSTRUCTION).foreground = Color.BLUE;
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_TAG_ATTRIBUTE");
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_TAG_ATTRIBUTE_VALUE");
        scheme.getStyle(Token.MARKUP_TAG_DELIMITER).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_TAG_DELIMITER");
        scheme.getStyle(Token.MARKUP_TAG_NAME).foreground = getConfigSyntaxHighlightingTokenColor("MARKUP_TAG_NAME");

        textArea.setSyntaxScheme(scheme);
    }

    private Color getConfigSyntaxHighlightingTokenColor(String token) {
        return Color.decode((String) scriptor.pluginsHandler.getMergedConfig().get("syntax.highlight.tokens." + token + ".color"));
    }

    private Icon getFileIcon(File file) {
        File fileIcon = new File("resources/icons/" + ScriptorProgrammingLanguagesUtils.getLanguageIconNameByFile(file));

        return fileIcon == null ? null : resizeSVGToIcon(fileIcon.getPath(), 14, 14);
    }

    private Icon resizeSVGToIcon(String svgFilePath, int width, int height) {
        try {
            PNGTranscoder transcoder = new PNGTranscoder();

            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

            TranscoderInput input = new TranscoderInput(new File(svgFilePath).toURI().toString());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            transcoder.transcode(input, output);

            byte[] imageData = outputStream.toByteArray();
            ImageIcon icon = new ImageIcon(imageData);

            outputStream.close();

            return icon;
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
            return null;
        }
    }
}
