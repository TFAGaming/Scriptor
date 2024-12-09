package com.scriptor.core.gui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.scriptor.Scriptor;
import com.scriptor.core.plugins.ScriptorPlugin;

public class ScriptorPluginsPanel extends JPanel {
    private Scriptor scriptor;
    
    public ScriptorPluginsPanel(Scriptor scriptor, List<ScriptorPlugin> plugins) {
        this.scriptor = scriptor;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < plugins.size(); i++) {
            ScriptorPlugin plugin = plugins.get(i);
            
            contentPanel.add(createPluginPanel(this.scriptor, plugin));

            if (i < plugins.size() - 1) {
                contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private static JPanel createPluginPanel(Scriptor scriptor, ScriptorPlugin plugin) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(String.format("<html><b>%s</b> (v%s)</html>", plugin.getName(), plugin.getVersion()));
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 12));
        panel.add(nameLabel, BorderLayout.NORTH);

        JLabel descriptionLabel = new JLabel(
                "<html><p style='width:300px;'>" + plugin.getDescription() + "</p></html>");
        panel.add(descriptionLabel, BorderLayout.CENTER);

        JCheckBox enabledCheckbox = new JCheckBox("Enabled");
        enabledCheckbox.setFocusable(false);
        enabledCheckbox.setSelected(plugin.getEnabled());
        enabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                plugin.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        panel.add(enabledCheckbox, BorderLayout.EAST);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new BorderLayout());

        JButton editButton = new JButton("Edit JSON");
        editButton.setFocusable(false);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptor.textAreaTabManager.openFileFromPath(plugin.getFile().getPath().toString());
            }
        });
        secondaryPanel.add(editButton, BorderLayout.WEST);

        panel.add(secondaryPanel, BorderLayout.SOUTH);

        return panel;
    }
}
