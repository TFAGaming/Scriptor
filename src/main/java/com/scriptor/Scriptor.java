package com.scriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.*;

import com.scriptor.core.config.ScriptorConfig;
import com.scriptor.core.gui.components.JClosableComponent;
import com.scriptor.core.gui.frames.ScriptorWhatsNew;
import com.scriptor.core.gui.menus.ScriptorMenubar;
import com.scriptor.core.gui.others.ScriptorNotification;
import com.scriptor.core.gui.panels.ScriptorFileExplorer;
import com.scriptor.core.gui.toolbar.ScriptorPrimaryToolbar;
import com.scriptor.core.gui.toolbar.ScriptorTerminalToolbar;
import com.scriptor.core.managers.ScriptorNotificationsManager;
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
    public JPanel statusBarPanel;

    public JSplitPane primarySplitPane;
    public JSplitPane secondarySplitPane;

    public ScriptorFileExplorer filesExplorer;
    public ScriptorPluginsHandler pluginsHandler = new ScriptorPluginsHandler("plugins");
    public ScriptorTextAreaTabManager textAreaTabManager;
    public ScriptorTerminalTabManager terminalTabManager;
    public ScriptorNotificationsManager notificationsManager = new ScriptorNotificationsManager(this,
            new ArrayList<ScriptorNotification>());

    public List<JClosableComponent> removedComponents = new ArrayList<JClosableComponent>();

    public Scriptor() {
        logger.clearAll();

        pluginsHandler.loadPlugins();

        setTitle("Scriptor");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(getIcon("scriptor_icon.png").getImage());

        setSystemLookAndFeel();

        // Tab panes
        textAreaTabPane = new JTabbedPane();
        textAreaTabPane.setFocusable(false);

        textAreaTabManager = new ScriptorTextAreaTabManager(this, this.textAreaTabPane);

        terminalTabPane = new JTabbedPane();
        terminalTabPane.setFocusable(false);

        terminalTabManager = new ScriptorTerminalTabManager(this, this.terminalTabPane);

        // Files explorer
        filesExplorer = new ScriptorFileExplorer(this, config.getDirectoryPath() == null
                ? System.getProperty("user.dir")
                : config.getDirectoryPath(), true);

        // Menu bar
        setJMenuBar(new ScriptorMenubar(this));

        // Toolbar
        ScriptorPrimaryToolbar toolBar = new ScriptorPrimaryToolbar(this);

        add(toolBar, BorderLayout.NORTH);

        List<JComponent> __components = new ArrayList<JComponent>();
        __components.add(new ScriptorTerminalToolbar(this));

        // Split panes
        primarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textAreaTabPane,
                new JClosableComponent(this, JClosableComponentType.TERMINAL, __components, terminalTabPane));
        primarySplitPane.setResizeWeight(0.5);
        primarySplitPane.setDividerLocation(0.3);

        JLabel __label1 = new JLabel("File System Tree");

        List<JComponent> __components1 = new ArrayList<JComponent>();
        __components1.add(__label1);

        secondarySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JClosableComponent(this, JClosableComponentType.FILE_EXPLORER, __components1, filesExplorer),
                primarySplitPane);
        secondarySplitPane.setResizeWeight(0.1);
        secondarySplitPane.setDividerLocation(0.8);

        add(secondarySplitPane);

        // Status bar
        statusBarPanel = new JPanel(new BorderLayout());

        JLabel statusBarLabel = new JLabel("Getting ready...");
        statusBarLabel.setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton statusBarNotificationButton = new JButton();
        statusBarNotificationButton.setIcon(getIcon("notification.png"));
        statusBarNotificationButton.setBorderPainted(false);
        statusBarNotificationButton.setFocusPainted(false);
        statusBarNotificationButton.setContentAreaFilled(false);
        statusBarNotificationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statusBarNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (notificationsManager.isHidden()) {
                    notificationsManager.showNotifications(statusBarNotificationButton);
                } else {
                    notificationsManager.hideNotifications();
                }
            }
        });

        statusBarPanel.add(statusBarNotificationButton, BorderLayout.EAST);
        statusBarPanel.add(statusBarLabel, BorderLayout.WEST);

        add(statusBarPanel, BorderLayout.SOUTH);

        // Window position and size
        if (config.getExtended()) {
            setExtendedState(MAXIMIZED_BOTH);
        } else {
            Dimension windowSize = config.getWindowSize();

            if (windowSize != null) {
                setSize(windowSize);
            }

            Point windowPosition = config.getWindowPosition();

            if (windowPosition == null) {
                setLocationRelativeTo(null);
            } else {
                setLocation(windowPosition);
            }
        }

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
                terminalTabManager.stopAllProcesses();

                List<String> expandedFolders = filesExplorer.getExpandedFolders();

                config.setExpandedFolders(expandedFolders);
                config.setWindowSize(getSize());
                config.setWindowPosition(getLocation());
            }
        });

        // Others
        textAreaTabManager.openPreviousTabs();

        new ScriptorKeybinds(this, textAreaTabPane);

        updateStatusBar();

        newWelcomeNotification();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scriptor scriptor = new Scriptor();
            scriptor.setVisible(true);

            if (scriptor.config.getShowWhatsNewOnStartUp()) {
                new ScriptorWhatsNew(scriptor);
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
            language = ScriptorProgrammingLanguagesUtils.getLanguageByFileExtension(fileExtension);
        }

        String newLabelText = language + " | Length: " + textArea.getText().trim().length() + ", Lines: "
                + textArea.getText().trim().split("\n").length + " | Line: " + lineNumber + ", Column: " + column
                + " | Zoom: " + config.getZoom() + ", Encoding: UTF-8";

        for (Component component : statusBarPanel.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setText(newLabelText);
            }
        }
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
                    case JClosableComponentType.FILE_EXPLORER:
                        removedComponents.remove(i);

                        secondarySplitPane.add(component);
                        secondarySplitPane.revalidate();
                        secondarySplitPane.repaint();

                        secondarySplitPane.setDividerSize(5);
                        break;
                    case JClosableComponentType.TERMINAL:
                        removedComponents.remove(i);

                        primarySplitPane.add(component);
                        primarySplitPane.revalidate();
                        primarySplitPane.repaint();

                        primarySplitPane.setDividerSize(5);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void newWelcomeNotification() {
        List<JButton> buttons = new ArrayList<JButton>();

        ScriptorNotification notification = new ScriptorNotification("Ready!", "Scriptor is now ready to use.", null,
                null);

        JButton okButton = new JButton("OK!");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notificationsManager.removeNotification(notification, null);
            }
        });

        final Scriptor scriptor = this;

        JButton whatsNewButton = new JButton("What's New?");
        whatsNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ScriptorWhatsNew(scriptor);

                notificationsManager.removeNotification(notification, null);
            }
        });

        buttons.add(okButton);
        buttons.add(whatsNewButton);

        notification.setButtons(buttons);

        notificationsManager.newNotification(notification);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Nothing happens here.
    }
}
