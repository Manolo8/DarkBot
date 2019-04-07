package com.github.manolo8.darkbot.gui;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.BoxInfo;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.config.NpcInfo;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.modules.CollectorModule;
import com.github.manolo8.darkbot.modules.EventModule;
import com.github.manolo8.darkbot.modules.LootModule;
import com.github.manolo8.darkbot.modules.LootNCollectorModule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigGui extends JFrame {

    private final Main main;
    private final Config config;

    private JTabbedPane tabbedPane;

    private AdvancedConfig advancedPane;
    private ZoneEditor preferredZones;
    private ZoneEditor avoidedZones;
    private JPanel ggPane;

    public ConfigGui(Main main) throws HeadlessException {
        super("DarkBot - Config");

        this.main = main;
        this.config = main.config;

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setAlwaysOnTop(main.config.MISCELLANEOUS.DISPLAY.ALWAYS_ON_TOP);

        initComponents();
        setComponentPosition();
        setComponentData();

        validate();
        repaint();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                main.config.changed = true;
            }
        });
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        advancedPane = new AdvancedConfig();
        preferredZones = new ZoneEditor();
        avoidedZones = new ZoneEditor();
        ggPane = new JPanel();
    }

    private void setComponentPosition() {
        tabbedPane.addTab("General", advancedPane);
        tabbedPane.addTab("Preferred Zones", preferredZones);
        tabbedPane.addTab("Avoided Zones", avoidedZones);
        //tabbedPane.addTab("GG", ggPane);

        add(tabbedPane);

        // GG
        ggPane.setLayout(new BoxLayout(ggPane, BoxLayout.Y_AXIS));
    }

    private void setComponentData() {
        for (String string : main.starManager.getGGMaps())
            ggPane.add(new JCheckBox(string));

        advancedPane.setEditingConfig(config);
        preferredZones.setup(main, config.PREFERRED);
        avoidedZones.setup(main, config.AVOIDED);
    }

}
