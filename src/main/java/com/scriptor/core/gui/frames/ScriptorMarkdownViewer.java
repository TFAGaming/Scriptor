package com.scriptor.core.gui.frames;

import javax.swing.*;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.scriptor.Scriptor;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScriptorMarkdownViewer extends JFrame {
    private Scriptor scriptor;

    public ScriptorMarkdownViewer(Scriptor scriptor, String path) {
        this.scriptor = scriptor;

        setTitle("Markdown Viewer - " + path);
        setSize(1000, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setIconImage(this.scriptor.getIcon("scriptor_icon.png").getImage());

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }

            String markdownText = sb.toString();

            editorPane.setText(convertToMarkdown(markdownText, readFile("./resources/github.css")));

            reader.close();
        } catch (IOException e) {
            scriptor.logger.insert(e.toString());
        }

        setVisible(true);
    }

    public String convertToMarkdown(String markdownText, String css) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        String htmlContent = renderer.render(document);

        String styledHtml = """
                <html>
                <head>
                <style>
                %s
                </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(css, htmlContent);

        return styledHtml;
    }

    private String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
