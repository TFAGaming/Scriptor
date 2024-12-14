package com.scriptor.core.gui.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.scriptor.Scriptor;
import com.scriptor.core.gui.ui.VerticalLabelUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptorInformation extends JFrame {
    private Scriptor scriptor;

    public ScriptorInformation(Scriptor scriptor) {
        this.scriptor = scriptor;

        setTitle("Scriptor Information");
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        tabbedPane.setFocusable(false);

        tabbedPane.addTab("General", createGeneralInfoPanel());
        JLabel tab1 = new JLabel("  General  ");
        tab1.setFont(new Font(tab1.getFont().getName(), Font.BOLD, 12));
        tab1.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(0, tab1);

        tabbedPane.addTab("Contributors", createContributorsInfoPanel());
        JLabel tab2 = new JLabel("  Contributors  ");
        tab2.setFont(new Font(tab2.getFont().getName(), Font.BOLD, 12));
        tab2.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(1, tab2);

        tabbedPane.addTab("License", createLicenseInfoPanel());
        JLabel tab3 = new JLabel("  License  ");
        tab3.setFont(new Font(tab3.getFont().getName(), Font.BOLD, 12));
        tab3.setUI(new VerticalLabelUI(false));
        tabbedPane.setTabComponentAt(2, tab3);

        splitPane.setLeftComponent(tabbedPane);

        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createGeneralInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        textArea.setLineWrap(true);

        List<String> data = new ArrayList<String>();

        data.add("Version: " + Scriptor.getVersion());
        data.add("Written in: Java (100%)");
        data.add("License: The MIT License");

        String dataString = "";

        for (String each : data) {
            dataString += each + "\n";
        }

        textArea.setText(dataString);

        JScrollPane sp = new JScrollPane(textArea);

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        });

        textArea.setEditable(false);

        panel.add(sp);

        return panel;
    }

    private JPanel createContributorsInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        textArea.setLineWrap(true);

        List<String> contributors = new ArrayList<String>();

        contributors.add("TFAGaming");

        String contributorsListString = "";

        for (String contributor : contributors) {
            contributorsListString += "• " + contributor + "\n";
        }

        textArea.setText("The list of contributors to this project:\r\n" + contributorsListString);

        JScrollPane sp = new JScrollPane(textArea);

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        });

        textArea.setEditable(false);

        panel.add(sp);

        return panel;
    }

    private JPanel createLicenseInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        textArea.setLineWrap(true);

        textArea.setText("The MIT License\r\nCopyright 2024 TFAGaming\r\n" + //
                "\r\n" + //
                "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\r\n"
                + //
                "\r\n" + //
                "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\r\n"
                + //
                "\r\n" + //
                "THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\r\n"
                + //
                "\r\n" + //
                "");

        JScrollPane sp = new JScrollPane(textArea);

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        });

        textArea.setEditable(false);

        panel.add(sp);

        return panel;
    }
}
