package com.scriptor.core.gui.panels;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.frames.ScriptorProgressBar;
import com.scriptor.core.utils.ScriptorProgrammingLanguagesUtils;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ScriptorFileExplorer extends JPanel {
    private Scriptor scriptor;

    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private ScriptorProgressBar progressBarFrame;
    private List<String> expandedFolders = new ArrayList<String>();
    private Thread thread;
    private String rootPath = "";
    private int filesCounter = 0;

    public ScriptorFileExplorer(Scriptor scriptor, String rootPath, boolean openExpandedFolders) {
        this.rootPath = rootPath;
        this.scriptor = scriptor;

        setLayout(new BorderLayout());

        rootNode = new DefaultMutableTreeNode(new FileNode(new File(rootPath)));
        treeModel = new DefaultTreeModel(rootNode);

        fileTree = new JTree(treeModel);
        fileTree.setShowsRootHandles(true);
        fileTree.setFocusable(false);

        fileTree.setCellRenderer(new FileTreeCellRenderer());

        populateTreeWithThread(rootNode, new File(rootPath), true);

        JScrollPane scrollPane = new JScrollPane(fileTree);
        add(scrollPane, BorderLayout.CENTER);

        fileTree.addMouseListener(new RightClickMouseListener());
        fileTree.addMouseListener(new LeftClickMouseListener());
        fileTree.addTreeExpansionListener(new FolderExpansionListener());
        fileTree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.getUserObject() instanceof FileNode) {
                        FileNode fileNode = (FileNode) node.getUserObject();
                        if (fileNode.getFile().isFile() || fileNode.getFile().isDirectory()) {
                            fileTree.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            return;
                        }
                    }
                }

                fileTree.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void setPath(String newPath) {
        rootPath = newPath;

        rootNode.removeAllChildren();
        treeModel.reload();

        File newRoot = new File(newPath);
        rootNode.setUserObject(new FileNode(newRoot));
        populateTreeWithThread(rootNode, newRoot, false);

        treeModel.reload();
    }

    public void refreshTree() {
        rootNode.removeAllChildren();
        treeModel.reload();

        File newRoot = new File(this.rootPath);
        rootNode.setUserObject(new FileNode(newRoot));
        populateTreeWithThread(rootNode, newRoot, false);

        treeModel.reload();
    }

    public void addPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        DefaultMutableTreeNode parentNode = findParentNode(file.getParentFile());
        if (parentNode == null) {
            return;
        }

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new FileNode(file));
        treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        fileTree.expandPath(new TreePath(parentNode.getPath())); // Expand to show the new node
    }

    public void renamePath(String oldPath, String newPath) {
        DefaultMutableTreeNode nodeToRename = findNodeByPath(oldPath);
        if (nodeToRename == null) {
            return;
        }

        FileNode fileNode = (FileNode) nodeToRename.getUserObject();
        File newFile = new File(newPath);

        fileNode.setFile(newFile);
        treeModel.nodeChanged(nodeToRename);
    }

    public void removePath(String path) {
        DefaultMutableTreeNode nodeToRemove = findNodeByPath(path);

        if (nodeToRemove != null) {
            treeModel.removeNodeFromParent(nodeToRemove);
        }
    }

    public List<String> getExpandedFolders() {
        return new ArrayList<String>(expandedFolders);
    }

    public void restoreExpandedFolders(List<String> folderPaths) {
        for (String folderPath : folderPaths) {
            DefaultMutableTreeNode node = findNodeByPath(folderPath);

            if (node != null) {
                fileTree.expandPath(new TreePath(node.getPath()));
            }
        }
    }

    private void populateTreeWithThread(DefaultMutableTreeNode rootNode, File fileRoot, boolean openExpandedFolders) {
        if (thread != null) {
            progressBarFrame.dispose();
            progressBarFrame = null;

            thread = null;
        }

        countFilesAndDirectories(new File(rootPath).toPath());

        progressBarFrame = new ScriptorProgressBar(scriptor, "Files Explorer", "Loading Files...", filesCounter);
        progressBarFrame.setVisible(true);

        thread = new Thread(() -> {
            populateTree(rootNode, fileRoot);

            SwingUtilities.invokeLater(() -> {
                progressBarFrame.dispose();
                progressBarFrame = null;

                thread = null;

                if (scriptor.config.getExpandedFolders().size() > 0) {
                    restoreExpandedFolders(scriptor.config.getExpandedFolders());
                }
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

    private DefaultMutableTreeNode findNodeByPath(String path) {
        return findNodeRecursively(rootNode, path);
    }

    private DefaultMutableTreeNode findNodeRecursively(DefaultMutableTreeNode currentNode, String path) {
        FileNode fileNode = (FileNode) currentNode.getUserObject();

        if (fileNode.getFile().getPath().equals(path)) {
            return currentNode;
        }

        for (int i = 0; i < currentNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) currentNode.getChildAt(i);
            DefaultMutableTreeNode result = findNodeRecursively(childNode, path);

            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private DefaultMutableTreeNode findParentNode(File parentFile) {
        return findNodeByPath(parentFile.getPath());
    }

    private void countFilesAndDirectories(Path path) {
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
            showMessageDialog(scriptor, "Unable to access to the file or directory:\n" + path, "Error", ERROR_MESSAGE);
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

        public void setFile(File file) {
            this.file = file;
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
                setIcon(ScriptorFileExplorer.this.getFileIcon(file));
            } else if (file.isDirectory()) {
                try (Stream<Path> files = Files.list(file.toPath())) {
                    long count = files.count();

                    if (count == 0) {
                        setIcon(ScriptorFileExplorer.this.getFolderIcon(true));
                    } else {
                        setClosedIcon(ScriptorFileExplorer.this.getFolderIcon(true));
                        setOpenIcon(ScriptorFileExplorer.this.getFolderIcon(false));
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
                        createFolderContextMenu(file.getPath()).show(fileTree, event.getX(), event.getY());
                    } else if (file.isFile()) {
                        createFileContextMenu().show(fileTree, event.getX(), event.getY());
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
                        scriptor.textAreaTabManager.openFileFromPath(file.getPath());
                    }
                }
            }
        }
    }

    private class FolderExpansionListener implements TreeExpansionListener {
        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            FileNode fileNode = (FileNode) node.getUserObject();

            if (!expandedFolders.contains(fileNode.getFile().getPath())) {
                expandedFolders.add(fileNode.getFile().getPath());
            }
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            FileNode fileNode = (FileNode) node.getUserObject();

            expandedFolders.remove(fileNode.getFile().getPath());
        }
    }

    private JPopupMenu createFolderContextMenu(String path) {
        JPopupMenu folderContextMenu = new JPopupMenu();

        JMenuItem refreshTree = new JMenuItem("Refresh Explorer");
        refreshTree.setIcon(scriptor.getIcon("refresh.gif"));
        refreshTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTree();
            }
        });

        JMenuItem createFileItem = new JMenuItem("New File");
        createFileItem.setIcon(scriptor.getIcon("new_file.png"));
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewItem(false);
            }
        });

        JMenuItem createFolderItem = new JMenuItem("New Folder");
        createFolderItem.setIcon(scriptor.getIcon("new_folder.gif"));
        createFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewItem(true);
            }
        });

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.setIcon(scriptor.getIcon("rename.gif"));
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameItem();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setIcon(scriptor.getIcon("trash.gif"));
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });

        if (path.equalsIgnoreCase(rootPath)) {
            renameItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }

        folderContextMenu.add(refreshTree);
        folderContextMenu.addSeparator();
        folderContextMenu.add(createFileItem);
        folderContextMenu.add(createFolderItem);
        folderContextMenu.addSeparator();
        folderContextMenu.add(renameItem);
        folderContextMenu.add(deleteItem);

        return folderContextMenu;
    }

    private JPopupMenu createFileContextMenu() {
        JPopupMenu fileContextMenu = new JPopupMenu();

        JMenuItem refreshTree = new JMenuItem("Refresh Explorer");
        refreshTree.setIcon(scriptor.getIcon("refresh.gif"));
        refreshTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTree();
            }
        });

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.setIcon(scriptor.getIcon("rename.gif"));
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameItem();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setIcon(scriptor.getIcon("trash.gif"));
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });

        fileContextMenu.add(refreshTree);
        fileContextMenu.addSeparator();
        fileContextMenu.add(renameItem);
        fileContextMenu.add(deleteItem);

        return fileContextMenu;
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

        String newName = JOptionPane.showInputDialog(scriptor, "Enter " + type.toLowerCase() + " name:",
                "New " + type, JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                File newItem = new File(selectedDirectory, newName);

                if (isFolder) {
                    Files.createDirectory(newItem.toPath());
                } else {
                    Files.createFile(newItem.toPath());
                }

                addPath(newItem.getPath());
            } catch (IOException | InvalidPathException error) {
                JOptionPane.showMessageDialog(scriptor,
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

        String newName = JOptionPane.showInputDialog(scriptor, "Enter the new name for " + selectedFile.getName() + ":",
                "Rename " + type, JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                File newFile = new File(Paths.get(selectedFile.getParent(), newName).toString());

                if (newFile.exists()) {
                    JOptionPane.showMessageDialog(scriptor,
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

                renamePath(selectedFile.getPath(), newFile.getPath());
            } catch (IOException | InvalidPathException error) {
                JOptionPane.showMessageDialog(scriptor,
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

                removePath(selectedFile.getPath());
            } catch (IOException | InvalidPathException error) {
                JOptionPane.showMessageDialog(scriptor,
                        "Error deleting " + type.toLowerCase() + ":\n" + selectedFile.getPath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Icon getFileIcon(File file) {
        File fileIcon = new File("resources/icons/" + ScriptorProgrammingLanguagesUtils.getLanguageIconNameByFile(file));

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
            scriptor.logger.insert(e.toString());
            return null;
        }
    }
}
