package com.scriptor.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.text.*;

import com.scriptor.Scriptor;
import com.scriptor.menus.TerminalComponentPopupMenu;

public class ScriptorTerminal extends JPanel {
    private String _Previouscommand = "";
    private JTextPane terminalArea;
    private String currentDirectory = "";
    private Process currentProcess;
    private BufferedWriter processWriter;
    private Thread processOutputThread;
    private boolean awaitingInput = false;

    public ScriptorTerminal(Scriptor scriptor, String dirPath) {
        if (dirPath == null) {
            currentDirectory = System.getProperty("user.dir");
        } else {
            currentDirectory = dirPath;
        }

        setLayout(new BorderLayout());

        terminalArea = new JTextPane();
        terminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalArea.setBackground(Color.WHITE);
        terminalArea.setForeground(Color.BLACK);
        terminalArea.setCaretColor(Color.BLACK);
        terminalArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(terminalArea);
        add(scrollPane, BorderLayout.CENTER);

        appendPrompt();

        terminalArea.addKeyListener(new KeyAdapter() {
            private StringBuilder currentInput = new StringBuilder();

            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();

                if (keyChar == KeyEvent.VK_ENTER) {
                    String command = currentInput.toString();
                    appendToTerminal("\n", Color.BLACK);

                    if (awaitingInput && currentProcess != null) {
                        try {
                            processWriter.write(command + "\n");
                            processWriter.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        executeCommand(command);
                    }

                    currentInput.setLength(0);
                } else if (keyChar == KeyEvent.VK_BACK_SPACE) {
                    if (currentInput.length() > 0) {
                        currentInput.setLength(currentInput.length() - 1);
                        try {
                            StyledDocument doc = terminalArea.getStyledDocument();
                            int length = doc.getLength();
                            if (length > 0) {
                                doc.remove(length - 1, 1);
                            }
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (!Character.isISOControl(keyChar)) {
                    currentInput.append(keyChar);

                    appendToTerminal(Character.toString(keyChar), Color.BLACK);
                }
            }
        });

        // Add Focus Listener to show/hide caret
        terminalArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                terminalArea.getCaret().setVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                terminalArea.getCaret().setVisible(false);
            }
        });

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
                TerminalComponentPopupMenu menu = new TerminalComponentPopupMenu(scriptor);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // Initially hide the caret
        terminalArea.getCaret().setVisible(false);
    }

    public boolean isAlive() {
        return currentProcess == null ? false : currentProcess.isAlive();
    }

    public void executeCommand(String command) {
        _Previouscommand = command;

        if (command.trim().startsWith("exit")) {
            appendPrompt();
            return;
        } else if (command.trim().startsWith("cls")) {
            clearTerminal();
        } else if (command.trim().startsWith("cd")) {
            changeDirectory(command);
            appendPrompt();
        } else {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd.exe", "/c", command);
                processBuilder.directory(new File(currentDirectory));

                processBuilder.redirectErrorStream(true);
                currentProcess = processBuilder.start();

                processWriter = new BufferedWriter(new OutputStreamWriter(currentProcess.getOutputStream()));

                processOutputThread = new Thread(() -> {
                    try (BufferedReader processReader = new BufferedReader(
                            new InputStreamReader(currentProcess.getInputStream()))) {   
                        int character;
                        StringBuilder buffer = new StringBuilder();

                        while ((character = processReader.read()) != -1) {
                            buffer.append((char) character);
                            if (character == '\n' || character == '\r') {
                                final String outputLine = buffer.toString();

                                buffer.setLength(0);

                                SwingUtilities.invokeLater(() -> appendToTerminal(outputLine, Color.BLACK));
                            } else {
                                final String partialOutput = buffer.toString();

                                SwingUtilities.invokeLater(() -> appendToTerminal(partialOutput, Color.BLACK));

                                buffer.setLength(0);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                processOutputThread.start();

                new Thread(() -> {
                    try {
                        currentProcess.waitFor();
                        SwingUtilities.invokeLater(() -> {
                            appendPrompt();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (IOException ex) {
                appendToTerminal("Error: Unable to execute command\n", Color.RED, true);
                ex.printStackTrace();
            }
        }
    }

    private void changeDirectory(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            appendToTerminal("Missing operand\n", Color.BLACK);
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
        } else {
            appendToTerminal("No such directory: " + newPath + "\n", Color.BLACK);
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
        if (details.length > 0 && details[0])
            StyleConstants.setBold(style, true);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendPrompt() {
        appendToTerminal(currentDirectory + "> ", Color.decode("#808080"));
    }

    public void closeProcess() {
        if (currentProcess != null) {
            currentProcess.destroy();
            currentProcess = null;
            awaitingInput = false;
        }
    }

    public void restartProcess() {
        closeProcess();

        appendToTerminal(_Previouscommand + "\n", Color.BLACK);

        executeCommand(_Previouscommand);
    }

    public void setAwaitingInput(boolean awaiting) {
        this.awaitingInput = awaiting;
    }

    public void clearTerminal() {
        closeProcess();

        terminalArea.setText("");

        appendPrompt();
    }
}
