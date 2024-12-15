package com.scriptor.core.terminal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

import javax.swing.text.*;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.components.JExtendedTextField;

public class ScriptorTerminal extends JPanel {
    private Scriptor scriptor;
    private JTextField dirPathLabel;

    private String currentDirectory = "";
    private Process currentProcess;
    private BufferedWriter processWriter;
    private Thread processOutputThread;

    private List<String> commands = new ArrayList<String>();
    private int commandsIndex = -1;

    private boolean awaitingInput = false;

    public JTextPane terminalArea;
    public JExtendedTextField commandTextField;

    public ScriptorTerminal(Scriptor scriptor, String dirPath) {
        this.scriptor = scriptor;

        if (dirPath == null) {
            currentDirectory = System.getProperty("user.dir");
        } else {
            currentDirectory = dirPath;
        }

        setLayout(new BorderLayout());

        terminalArea = new JTextPane();
        terminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalArea.setBackground(Color.decode("#FFFFFF"));
        terminalArea.setForeground(Color.decode("#000000"));
        terminalArea.setCaretColor(Color.decode("#000000"));
        terminalArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(terminalArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new BorderLayout());
        secondaryPanel.setBorder(new EmptyBorder(5, 1, 5, 1));

        commandTextField = new JExtendedTextField(16);
        commandTextField.setPlaceholder("Type a command or send an input for an active process.");

        commandTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                char keyChar = event.getKeyChar();

                if (keyChar == KeyEvent.VK_ENTER) {
                    String command = commandTextField.getText();

                    if (!awaitingInput && isAlive()) {
                        appendToTerminal(command + "\n", Color.decode("#000000"));

                        try {
                            commandTextField.setText("");

                            processWriter.write(command + "\n");
                            processWriter.flush();
                        } catch (IOException ex) {
                            scriptor.logger.insert(ex.toString());
                        }
                    } else if (!awaitingInput && !isAlive()) {
                        commandTextField.setText("");

                        appendToTerminal(command + "\n", Color.decode("#000000"));

                        executeCommand(command);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (commands.size() == 0) {
                    return;
                }

                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.VK_UP) {
                    commandTextField.setText(commands.get(commandsIndex));

                    commandsIndex--;

                    if (commandsIndex < 0) {
                        commandsIndex = commands.size() - 1;
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    commandTextField.setText(commands.get(commandsIndex));

                    commandsIndex++;

                    if (commandsIndex > (commands.size() - 1)) {
                        commandsIndex = 0;
                    }
                }
            }
        });

        secondaryPanel.add(commandTextField, BorderLayout.CENTER);

        dirPathLabel = new JTextField();
        dirPathLabel.setText(">>");
        dirPathLabel.setEnabled(false);
        dirPathLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        secondaryPanel.add(dirPathLabel, BorderLayout.WEST);

        add(secondaryPanel, BorderLayout.SOUTH);

        appendPrompt();
        appendToTerminal("Path: " + dirPath + "\n", Color.decode("#000000"));
        appendPrompt();

        terminalArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            private void showPopupMenu(MouseEvent e) {
                TerminalPopupMenu menu = new TerminalPopupMenu(scriptor);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        terminalArea.getCaret().setVisible(false);
    }

    public boolean isAlive() {
        return currentProcess == null ? false : currentProcess.isAlive();
    }

    public void executeCommand(String command) {
        commands.add(command);
        commandsIndex = commands.size() - 1;

        if (command.trim().startsWith("exit")) {
            appendPrompt();
        } else if (command.trim().startsWith("cls") || command.trim().startsWith("clear")) {
            clearTerminal();
        } else if (command.trim().startsWith("cd")) {
            changeDirectory(command);
        } else if (command.trim().startsWith("path")) {
            appendToTerminal("Process directory path: " + currentDirectory + "\n", Color.decode("#000000"));
            appendPrompt();
        } else {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd.exe", "/c", command);
                processBuilder.directory(new File(currentDirectory));

                processBuilder.redirectErrorStream(true);
                currentProcess = processBuilder.start();

                processWriter = new BufferedWriter(
                        new OutputStreamWriter(currentProcess.getOutputStream(), StandardCharsets.UTF_8));

                processOutputThread = new Thread(() -> {
                    try (BufferedReader processReader = new BufferedReader(
                            new InputStreamReader(currentProcess.getInputStream(), StandardCharsets.UTF_8))) {
                        int character;
                        StringBuilder buffer = new StringBuilder();

                        while ((character = processReader.read()) != -1) {
                            buffer.append((char) character);
                            if (character == '\n' || character == '\r') {
                                final String outputLine = buffer.toString();

                                buffer.setLength(0);

                                SwingUtilities.invokeLater(() -> appendToTerminal(outputLine, Color.decode("#000000")));
                            } else {
                                final String partialOutput = buffer.toString();

                                SwingUtilities.invokeLater(() -> appendToTerminal(partialOutput, Color.decode("#000000")));

                                buffer.setLength(0);
                            }
                        }
                    } catch (IOException ex) {
                        scriptor.logger.insert(ex.toString());
                    }
                });

                processOutputThread.start();

                new Thread(() -> {
                    try {
                        currentProcess.waitFor();
                        SwingUtilities.invokeLater(() -> {
                            appendToTerminal("\n", Color.decode("#000000"));
                            appendPrompt();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (IOException ex) {
                appendToTerminal("Error: Unable to execute command\n", Color.RED, true);
                scriptor.logger.insert(ex.toString());
            }
        }
    }

    private void changeDirectory(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            appendToTerminal("Missing arguments.\n", Color.decode("#000000"));
            return;
        }

        List<String> list = new ArrayList<String>();

        for (int i = 1; i < parts.length; i++) {
            list.add(parts[i]);
        }

        String newPath = String.join(" ", list).trim();
        File dir;

        if (newPath.equals("..")) {
            dir = new File(currentDirectory).getParentFile();
        } else {
            dir = new File(currentDirectory, newPath);
        }

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();

            appendToTerminal("Changed directory path to: " + currentDirectory + "\n", Color.decode("#000000"));
            appendPrompt();
        } else {
            appendToTerminal("No such directory: " + newPath + "\n", Color.decode("#000000"));
            appendPrompt();
        }
    }

    public void changeDirectoryWithoutCommandString(String path) {
        File dir = new File(path);

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
        }
    }

    private void appendToTerminal(String text, Color color, boolean... details) {
        StyledDocument doc = terminalArea.getStyledDocument();
        Style style = terminalArea.addStyle("style", null);
        StyleConstants.setForeground(style, color);

        if (details.length > 0 && details[0]) {
            StyleConstants.setBold(style, true);
        }

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            scriptor.logger.insert(e.toString());
        }
    }

    private void appendPrompt() {
        appendToTerminal("terminal@scriptor~ ", Color.decode("#808080"));
    }

    public void closeProcess() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            currentProcess = null;
            awaitingInput = false;
        }
    }

    public void restartProcess() {
        if (commands.size() == 0) {
            showMessageDialog(scriptor, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);

            return;
        }

        closeProcess();

        appendToTerminal(commands.get(commands.size() - 1) + "\n", Color.decode("#000000"));

        executeCommand(commands.get(commands.size() - 1));
    }

    public void runPreviousCommand() {
        if (commands.size() == 0) {
            showMessageDialog(scriptor, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);

            return;
        }

        closeProcess();

        appendToTerminal(commands.get(commands.size() - 1) + "\n", Color.decode("#000000"));

        executeCommand(commands.get(commands.size() - 1));
    }

    public void setAwaitingInput(boolean awaiting) {
        this.awaitingInput = awaiting;
    }

    public void clearTerminal() {
        closeProcess();

        terminalArea.setText("");

        appendPrompt();
    }

    public List<String> getCommands() {
        return this.commands;
    }
}
