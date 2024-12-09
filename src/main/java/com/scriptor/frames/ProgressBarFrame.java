package com.scriptor.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.scriptor.Scriptor;

import java.awt.*;

public class ProgressBarFrame extends JFrame {
    private Scriptor scriptor;
    private JLabel label;
    private JProgressBar progressBar;
    private String text;
    private int max;
    private int counter = 0;

    public ProgressBarFrame(Scriptor scriptor, String title, String text, int max) {
        this.scriptor = scriptor;

        setTitle(title);
        setSize(400, 100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(scriptor);
        setResizable(false);
        setAlwaysOnTop(true);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        this.text = text;
        this.max = max;

        label = new JLabel(text + " (" + counter + " out of " + max + ")");
        label.setBorder(new EmptyBorder(0, 0, 10, 10));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMaximum(max);
        progressBar.setValue(0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.SOUTH);

        add(panel);
    }

    public void increment() {
        label.setText(text + " (" + counter + " out of " + max + ")");

        counter++;

        if (counter >= max) {
            if (isDisplayable()) {
                dispose();
            }
        } else {
            progressBar.setValue(counter);
        }
    }

    public boolean isFinished() {
        return counter >= max;
    }
}