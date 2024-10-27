package com.scriptor.core;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import com.scriptor.Scriptor;
import com.scriptor.Utils;
import com.scriptor.frames.ProgressBarFrame;

import javax.swing.*;
import javax.swing.tree.*;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public class ScriptorFilesExplorer extends JPanel {
    private Scriptor editor;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private ProgressBarFrame progressBarFrame;
    private Thread thread;
    private String rootPath = "";
    private int filesCounter = 0;
    private JPopupMenu folderContextMenu;
    private JPopupMenu fileContextMenu;

    public ScriptorFilesExplorer(Scriptor editor, String rootPath) {
        setLayout(new BorderLayout());

        this.rootPath = rootPath;
        this.editor = editor;

        rootNode = new DefaultMutableTreeNode(new FileNode(new File(rootPath)));
        treeModel = new DefaultTreeModel(rootNode);

        fileTree = new JTree(treeModel);
        fileTree.setShowsRootHandles(true);

        fileTree.setCellRenderer(new FileTreeCellRenderer());

        populateTreeWithThread(rootNode, new File(rootPath));

        JScrollPane scrollPane = new JScrollPane(fileTree);
        add(scrollPane, BorderLayout.CENTER);

        createFolderContextMenu();
        createFileContextMenu();

        fileTree.addMouseListener(new RightClickMouseListener());
        fileTree.addMouseListener(new LeftClickMouseListener());
    }

    public void setPath(String newPath) {
        rootPath = newPath;

        rootNode.removeAllChildren();
        treeModel.reload();

        File newRoot = new File(newPath);
        rootNode.setUserObject(new FileNode(newRoot));
        populateTreeWithThread(rootNode, newRoot);

        treeModel.reload();
    }

    public void refreshTree() {
        rootNode.removeAllChildren();
        treeModel.reload();

        File newRoot = new File(this.rootPath);
        rootNode.setUserObject(new FileNode(newRoot));
        populateTreeWithThread(rootNode, newRoot);

        treeModel.reload();
    }

    private void populateTreeWithThread(DefaultMutableTreeNode rootNode, File fileRoot) {
        if (thread != null) {
            progressBarFrame.dispose();
            progressBarFrame = null;

            thread = null;
        }

        countFilesAndDirectories(new File(rootPath).toPath());

        progressBarFrame = new ProgressBarFrame(editor, "Files Explorer", "Loading Files...", filesCounter);
        progressBarFrame.setVisible(true);

        thread = new Thread(() -> {
            populateTree(rootNode, fileRoot);

            SwingUtilities.invokeLater(() -> {
                progressBarFrame.dispose();
                progressBarFrame = null;

                thread = null;
            });
        });

        thread.start();
    }

    private void populateTree(DefaultMutableTreeNode rootNode, File fileRoot) {
        File[] files = fileRoot.listFiles();

        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                }
            });

            for (File file : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));

                rootNode.add(childNode);

                if (progressBarFrame != null) {
                    progressBarFrame.increment();
                }

                if (file.isDirectory()) {
                    populateTree(childNode, file);
                }
            }
        }
    }

    public void countFilesAndDirectories(Path path) {
        filesCounter = 0;

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    filesCounter++;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    filesCounter++;
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            showMessageDialog(editor, "Unable to access to the file or directory:\n" + path, "Error", ERROR_MESSAGE);
        }
    }

    private class FileNode {
        private File file;

        public FileNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return file.getName().isEmpty() ? file.getPath() : file.getName();
        }
    }

    private class FileTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
                    hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            FileNode fileNode = (FileNode) node.getUserObject();
            File file = fileNode.getFile();

            if (file.isFile()) {
                setIcon(ScriptorFilesExplorer.this.getFileIcon(file));
            } else if (file.isDirectory()) {
                try (Stream<Path> files = Files.list(file.toPath())) {
                    long count = files.count();

                    if (count == 0) {
                        setIcon(ScriptorFilesExplorer.this.getFolderIcon(true));
                    } else {
                        setClosedIcon(ScriptorFilesExplorer.this.getFolderIcon(true));
                        setOpenIcon(ScriptorFilesExplorer.this.getFolderIcon(false));
                    }
                } catch (IOException e) {

                }
            }

            return component;
        }
    }

    private class RightClickMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            if (SwingUtilities.isRightMouseButton(event)) {
                TreePath path = fileTree.getPathForLocation(event.getX(), event.getY());

                if (path != null) {
                    fileTree.setSelectionPath(path);

                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    FileNode fileNode = (FileNode) selectedNode.getUserObject();
                    File file = fileNode.getFile();

                    if (file.isDirectory()) {
                        folderContextMenu.show(fileTree, event.getX(), event.getY());
                    } else if (file.isFile()) {
                        fileContextMenu.show(fileTree, event.getX(), event.getY());
                    }
                }
            }
        }
    }

    private class LeftClickMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                TreePath path = fileTree.getPathForLocation(event.getX(), event.getY());

                if (path != null) {
                    fileTree.setSelectionPath(path);

                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    FileNode fileNode = (FileNode) selectedNode.getUserObject();
                    File file = fileNode.getFile();

                    if (file.isFile()) {
                        editor.openFileFromPath(file.getPath());
                    }
                }
            }
        }
    }

    private void createFolderContextMenu() {
        folderContextMenu = new JPopupMenu();

        JMenuItem createFileItem = new JMenuItem("Create File");
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewItem(false);
            }
        });

        JMenuItem createFolderItem = new JMenuItem("Create Folder");
        createFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewItem(true);
            }
        });

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameItem();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });

        folderContextMenu.add(createFileItem);
        folderContextMenu.add(createFolderItem);
        folderContextMenu.addSeparator();
        folderContextMenu.add(renameItem);
        folderContextMenu.add(deleteItem);
    }

    private void createFileContextMenu() {
        fileContextMenu = new JPopupMenu();

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameItem();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });

        fileContextMenu.add(renameItem);
        fileContextMenu.add(deleteItem);
    }

    private void createNewItem(boolean isFolder) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return;
        }

        FileNode selectedFileNode = (FileNode) selectedNode.getUserObject();
        File selectedDirectory = selectedFileNode.getFile();

        if (!selectedDirectory.isDirectory()) {
            return;
        }

        String type = !isFolder ? "File" : "Folder";

        String newName = JOptionPane.showInputDialog(editor, "Enter " + type + " Name:",
                "Create " + type, JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                File newItem = new File(selectedDirectory, newName);

                if (isFolder) {
                    Files.createDirectory(newItem.toPath());
                } else {
                    Files.createFile(newItem.toPath());
                }

                refreshTree();
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(editor,
                        "Error creating " + type.toLowerCase() + ":\n" + selectedDirectory.getPath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void renameItem() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        if (selectedNode == null)
            return;

        FileNode selectedFileNode = (FileNode) selectedNode.getUserObject();
        File selectedFile = selectedFileNode.getFile();

        if (!selectedFile.exists()) {
            return;
        }

        String type = selectedFile.isFile() ? "File" : "Folder";

        String newName = JOptionPane.showInputDialog(editor, "Enter the new name for " + selectedFile.getName() + ":",
                "Rename " + type, JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                File newFile = new File(Paths.get(selectedFile.getParent(), newName).toString());

                if (newFile.exists()) {
                    JOptionPane.showMessageDialog(editor,
                            "Unable to rename the " + type.toLowerCase()
                                    + " because a file or a folder already exists with that name.\n"
                                    + newFile.getPath(),
                            "Error", JOptionPane.ERROR_MESSAGE);

                    return;
                }

                boolean success = selectedFile.renameTo(newFile);

                if (!success) {
                    throw new IOException();
                }

                refreshTree();
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(editor,
                        "Error renaming " + type.toLowerCase() + ":\n" + selectedFile.getPath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteItem() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        if (selectedNode == null)
            return;

        FileNode selectedFileNode = (FileNode) selectedNode.getUserObject();
        File selectedFile = selectedFileNode.getFile();

        if (!selectedFile.exists()) {
            return;
        }

        String type = selectedFile.isFile() ? "File" : "Folder";

        int response = showConfirmDialog(null,
                "Are you sure that you want to delete this " + type.toLowerCase() + "?\n" + selectedFile.getPath(),
                "Delete " + type, YES_NO_OPTION);

        if (response == YES_OPTION) {
            try {
                if (selectedFile.isDirectory()) {
                    FileUtils.deleteDirectory(selectedFile);
                } else {
                    FileUtils.delete(selectedFile);
                }

                refreshTree();
            } catch (IOException ioException) {
                ioException.printStackTrace();

                JOptionPane.showMessageDialog(editor,
                        "Error deleting " + type.toLowerCase() + ":\n" + selectedFile.getPath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Icon getFileIcon(File file) {
        File fileIcon = new File("resources/icons/" + Utils.getLanguageIconNameByFile(file));

        return fileIcon == null ? null : resizeSVGToIcon(fileIcon.getPath(), 16, 16);
    }

    public Icon getFolderIcon(boolean isClosed) {
        File fileIcon = new File(isClosed ? "resources/folder.png" : "resources/folder_open.png");

        return new ImageIcon(fileIcon.getPath());
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
