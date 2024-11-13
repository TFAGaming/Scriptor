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

        JTextArea textArea = new JTextArea(10, 20);
        textArea.setEditable(false);

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

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 25));

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
