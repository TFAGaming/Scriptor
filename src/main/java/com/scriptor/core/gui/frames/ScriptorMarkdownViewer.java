package com.scriptor.core.gui.frames;

import javax.swing.*;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.scriptor.Scriptor;

import java.awt.*;
import java.io.*;

public class ScriptorMarkdownViewer extends JFrame {
    private Scriptor scriptor;

    public ScriptorMarkdownViewer(Scriptor scriptor, String path) {
        this.scriptor = scriptor;

        setTitle("Markdown Viewer - " + path);
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);

        System.out.println(path);

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }

                String markdownText = sb.toString();

                editorPane.setText(convertToMarkdown(markdownText));
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVisible(true);
    }

    public String convertToMarkdown(String markdownText) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlText = renderer.render(document);

        return htmlText;
    }
}
