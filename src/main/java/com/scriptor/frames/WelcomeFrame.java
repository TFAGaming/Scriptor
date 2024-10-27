package com.scriptor.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.scriptor.Scriptor;
import com.scriptor.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame(Scriptor scriptor) {
        setTitle("What\'s new?");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setIconImage(scriptor.getIcon("scriptor_icon.png").getImage());

        JLabel label = new JLabel("Version: " + Utils.getVersion());
        label.setBorder(new EmptyBorder(0, 10, 10, 10));

        JTextArea textArea = new JTextArea(10, 20);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        try (BufferedReader br = new BufferedReader(new FileReader("whatsnew.txt"))) {
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }

                String whatsnew = sb.toString();

                textArea.setText(whatsnew);
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JCheckBox checkBox = new JCheckBox("Never show this again.");
        checkBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton okButton = new JButton("OK");
        okButton.setBorder(new EmptyBorder(0, 10, 0, 10));
        okButton.setPreferredSize(new Dimension(80, 3));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDisplayable()) {
                    dispose();

                    if (checkBox.isSelected()) {
                        scriptor.config.setShowWhatsNewOnStartUp(false);
                    }
                }
            }     
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 0, 10)); 

        bottomPanel.add(checkBox, BorderLayout.WEST);
        bottomPanel.add(okButton, BorderLayout.EAST);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }
}
