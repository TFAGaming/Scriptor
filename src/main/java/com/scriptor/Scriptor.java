package com.scriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.*;

import com.scriptor.core.config.ScriptorConfig;
import com.scriptor.core.gui.components.ClosableComponentType;
import com.scriptor.core.gui.components.JClosableComponent;
import com.scriptor.core.gui.frames.ScriptorWhatsNew;
import com.scriptor.core.gui.menus.ScriptorMenubar;
import com.scriptor.core.gui.panels.ScriptorFileExplorer;
import com.scriptor.core.gui.toolbar.ScriptorPrimaryToolbar;
import com.scriptor.core.gui.toolbar.ScriptorTerminalToolbar;
import com.scriptor.core.managers.ScriptorTerminalTabManager;
import com.scriptor.core.managers.ScriptorTextAreaTabManager;
import com.scriptor.core.plugins.ScriptorPluginsHandler;
import com.scriptor.core.utils.*;

import org.apache.commons.io.FilenameUtils;

import java.awt.event.*;
import java.io.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scriptor extends JFrame implements ActionListener {
    public ScriptorConfig config = new ScriptorConfig("config.json");
    public ScriptorLogger logger = new ScriptorLogger();
    public JTabbedPane textAreaTabPane;
    public JTabbedPane terminalTabPane;
    public JLabel statusBarLabel;

    public JSplitPane primarySplitPane;
    public JSplitPane secondarySplitPane;

    public ScriptorFileExplorer filesExplorer;
    public ScriptorPluginsHandler pluginsHandler = new ScriptorPluginsHandler("plugins");
    public ScriptorTextAreaTabManager textAreaTabManager;
    public ScriptorTerminalTabManager terminalTabManager;

    public List<JClosableComponent> removedComponents = new ArrayList<JClosableComponent>();

    public Scriptor() {
        logger.clearAll();

        pluginsHandler.loadPlugins();

        setTitle("Scriptor");
        setSize(1600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // setBackground(Color.decode((String)
        // pluginsHandler.getMergedConfig().get("frame.background.color")));

        setIconImage(getIcon("scriptor_icon.png").getImage());

        setSystemLookAndFeel();

        // Tab panes
        textAreaTabPane = new JTabbedPane();
        // textAreaTabPane.setBackground(
        // Color.decode((String)
        // pluginsHandler.getMergedConfig().get("textarea.background.color")));
        textAreaTabPane.setFocusable(false);

        textAreaTabManager = new ScriptorTextAreaTabManager(this, this.textAreaTabPane);

        terminalTabPane = new JTabbedPane();
        // terminalTabPane.setBackground(
        // Color.decode((String)
        // pluginsHandler.getMergedConfig().get("textarea.background.color")));
        terminalTabPane.setFocusable(false);

        terminalTabManager = new ScriptorTerminalTabManager(this, this.terminalTabPane);

        // Files explorer
        filesExplorer = new ScriptorFileExplorer(this, config.getDirectoryPath() == null
                ? System.getProperty("user.dir")
                : config.getDirectoryPath());

        // Menu bar
        setJMenuBar(new ScriptorMenubar(this));

        // Toolbar
        ScriptorPrimaryToolbar toolBar = new ScriptorPrimaryToolbar(this);

        add(toolBar, BorderLayout.NORTH);

        List<JComponent> __components = new ArrayList<JComponent>();
        __components.add(new ScriptorTerminalToolbar(this));

        // Split panes
        primarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textAreaTabPane, new JClosableComponent(this, ClosableComponentType.TERMINAL, __components, terminalTabPane));
        primarySplitPane.setResizeWeight(0.5);
        primarySplitPane.setDividerLocation(0.3);

        JLabel __label1 = new JLabel("File System Tree");

        List<JComponent> __components1 = new ArrayList<JComponent>();
        __components1.add(__label1);

        secondarySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JClosableComponent(this, ClosableComponentType.FILE_EXPLORER, __components1, filesExplorer),
                primarySplitPane);
        secondarySplitPane.setResizeWeight(0.1);
        secondarySplitPane.setDividerLocation(0.8);

        add(secondarySplitPane);

        // Status bar
        statusBarLabel = new JLabel("Getting ready...");
        statusBarLabel.setBorder(new EmptyBorder(5, 5, 5, 0));

        add(statusBarLabel, BorderLayout.SOUTH);

        // Window position and size
        if (config.getExtended()) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        setLocationRelativeTo(null);

        // Events
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<String> expandedFolders = filesExplorer.getExpandedFolders();

                config.setExpandedFolders(expandedFolders);
            }
        });

        // Others
        new ScriptorKeybinds(this, textAreaTabPane);

        textAreaTabManager.openPreviousTabs();

        if (config.getExpandedFolders().size() > 0) {
            filesExplorer.restoreExpandedFolders(config.getExpandedFolders());
        }

        updateStatusBar();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scriptor scriptor = new Scriptor();
            scriptor.setVisible(true);

            if (scriptor.config.getShowWhatsNewOnStartUp()) {
                ScriptorWhatsNew welcomeFrame = new ScriptorWhatsNew(scriptor);

                welcomeFrame.setVisible(true);
            }
        });
    }

    public static String getVersion() {
        return "2024.12.09-1";
    }

    public void setSystemLookAndFeel() {
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

    public void updateStatusBar() {
        RSyntaxTextArea textArea = textAreaTabManager.getCurrentTextArea();

        if (textArea == null) {
            return;
        }

        int caretPosition = textArea.getCaretPosition();
        int lineNumber = 0;
        int column = 0;

        try {
            lineNumber = textArea.getLineOfOffset(caretPosition) + 1;
            int lineStartOffset = textArea.getLineStartOffset(lineNumber - 1);
            column = caretPosition - lineStartOffset;
        } catch (BadLocationException e) {
        }

        String language = "Plain Text";

        String path = textAreaTabManager.getCurrentPath();

        if (path != null) {
            String fileExtension = FilenameUtils.getExtension(new File(path).getName());
            language = Utils.getLanguageByFileExtension(fileExtension);
        }

        statusBarLabel.setText(language + " | Length: " + textArea.getText().trim().length() + ", Lines: "
                + textArea.getText().trim().split("\n").length + " | Line: " + lineNumber + ", Column: " + column
                + " | Zoom: " + config.getZoom() + ", Encoding: UTF-8");
    }

    public ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }

    public void addBackComponent(int type) {
        if (!removedComponents.isEmpty()) {
            for (int i = 0; i < removedComponents.size(); i++) {
                JClosableComponent component = removedComponents.get(i);

                if (component.getType() != type) {
                    continue;
                }

                switch (type) {
                    case ClosableComponentType.FILE_EXPLORER:
                        removedComponents.remove(i);

                        secondarySplitPane.add(component);
                        secondarySplitPane.revalidate();
                        secondarySplitPane.repaint();
                        break;
                    case ClosableComponentType.TERMINAL:
                        removedComponents.remove(i);

                        primarySplitPane.add(component);
                        primarySplitPane.revalidate();
                        primarySplitPane.repaint();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Nothing happens here.
    }
}
