package com.scriptor.core.gui.frames;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.scriptor.Scriptor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.util.Vector;

public class ScriptorSQLiteViewer extends JFrame {
    private Scriptor scriptor;
    private Connection connection;
    private JComboBox<String> tablesComboBox;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public ScriptorSQLiteViewer(Scriptor scriptor, String path) {
        this.scriptor = scriptor;
 
        setTitle("SQLite Viewer - " + (path == null ? "Null" : path));
        setSize(1000, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        // Layout setup
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton openFileButton = new JButton("Open File");
        openFileButton.setFocusable(false);

        tablesComboBox = new JComboBox<>();
        tablesComboBox.setEnabled(false);
        tablesComboBox.setFocusable(false);
        topPanel.add(openFileButton, BorderLayout.WEST);
        topPanel.add(tablesComboBox, BorderLayout.EAST);

        // Data Table
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        dataTable.setFocusable(false);
        dataTable.setEnabled(false);
        
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button Action: Open SQLite File
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSQLiteFile();
            }
        });

        // ComboBox Action: Select Table
        tablesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) tablesComboBox.getSelectedItem();
                if (selectedTable != null) {
                    loadTableData(selectedTable);
                }
            }
        });

        if (path != null) {
            connectToDatabase(path);
        }

        setVisible(true);
    }

    private void openSQLiteFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQLite Database File");
        fileChooser.setCurrentDirectory(new File(scriptor.config.getDirectoryPath()));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();

                    return filename.endsWith(".sql") || filename.endsWith(".sqlite") || filename.endsWith(".db");
                }
            }

            @Override
            public String getDescription() {
                return "SQL File (*.sql, *.sqlite, *.db)";
            }
        });
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            connectToDatabase(file.getAbsolutePath());
        }
    }

    private void connectToDatabase(String dbFilePath) {
        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

            loadTableNames(dbFilePath);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableNames(String dbFilePath) {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt
                    .executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';");

            tablesComboBox.removeAllItems();
            while (rs.next()) {
                tablesComboBox.addItem(rs.getString("name"));
            }

            tablesComboBox.setEnabled(true);

            setTitle("SQLite Viewer - " + dbFilePath);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading table names: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableData(String tableName) {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            // Get column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Get row data
            Vector<Vector<Object>> rowData = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rowData.add(row);
            }

            // Update table model
            tableModel.setDataVector(rowData, columnNames);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading table data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}