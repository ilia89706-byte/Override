package com.override.engine;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import com.override.Override;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.model.enums.AesKeyStrength;

public class Menu {
    private String projectPath = null;
    private boolean startEngine = false;

    private Color C_YELLOW = new Color(251, 237, 154), C_DARK = new Color(27, 27, 27);
    private Color C_CARD = new Color(53, 53, 55), C_PINK = new Color(237, 127, 133);
    private final Font F_BIG = new Font("Arial", Font.BOLD, 22), F_MID = new Font("Arial", Font.BOLD, 14);

    private static final String HISTORY_FILE = "projects.txt";
    private static final String SETTINGS_FILE = "settings.txt";
    private final List<String> savedProjects = new ArrayList<>();

    private String currentAuthor = "Overrider";
    private String currentTheme = "Override (По умолчанию)";
    private String currentBuildType = "Release";
    private boolean currentAllowTelemetry = false;

    private JFrame frame;
    private JPanel homeTab, browseTab, docTab, settingsTab, settingsContent, pGrid, headerPanel;
    private JLabel lbl, settingsTitle, lblAuthor, lblTheme, lblApi;
    private JComboBox<String> comboTheme;
    private JTextField txtAuthor;
    private JRadioButton rbRelease, rbBeta;
    private JCheckBox chkAutoSave;
    private JButton btnCreate, btnSaveSettings;
    private JScrollPane scroll, settingsScroll;

    private void exportProject(String srcProjectPath, String destBuildPath, String gameName) {
        File srcDir = new File(srcProjectPath);
        File destDir = new File(destBuildPath);
        if (!destDir.exists())
            destDir.mkdirs();

        File finalZip = new File(destDir, gameName + "_build.ovr");

        secretPassword = "";

            ZipFile zipFile = new ZipFile(finalZip, secretPassword);

            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

            packDirectoryRecursively(srcDir, srcDir, zipFile, zipParameters);

            JOptionPane.showMessageDialog(frame,
                    "Архив .ovr успешно собран!\nФайл: " + finalZip.getAbsolutePath(),
                    "Экспорт завершен",
                    JOptionPane.INFORMATION_MESSAGE);

            java.util.Arrays.fill(secretPassword, '0');

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка при создании защищенного архива:\n" + e.getMessage(),
                    "Ошибка сборки",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void packDirectoryRecursively(File rootDir, File currentDir, ZipFile zipFile, ZipParameters params)
            throws IOException {
        File[] files = currentDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isDirectory()) {
                packDirectoryRecursively(rootDir, file, zipFile, params);
            } else {
                String relativePath = rootDir.toURI().relativize(file.getParentFile().toURI()).getPath();

                ZipParameters fileParams = new ZipParameters(params);
                if (!relativePath.isEmpty()) {
                    fileParams.setRootFolderNameInZip(relativePath);
                }

                zipFile.addFile(file, fileParams);
            }
        }
    }

    public String showMenu() {
        loadProjectsHistory();
        loadSettings();

        frame = new JFrame("Override Engine build 0.01");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 580);
        frame.setLocationRelativeTo(null);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(0);
        split.setDividerLocation(200);
        split.setEnabled(false);

        CardLayout cardLayout = new CardLayout();
        JPanel mainContentPanel = new JPanel(cardLayout);

        JPanel side = new JPanel();
        side.setBackground(C_DARK);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel logo = new JLabel("Override");
        logo.setFont(new Font("Arial", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        side.add(logo);
        side.add(Box.createRigidArea(new Dimension(0, 40)));

        String[] nav = { "Мои проекты", "Сообщество", "Документация", "Настройки", "Github" };
        JButton[] navButtons = new JButton[nav.length];

        for (int i = 0; i < nav.length; i++) {
            final int index = i;
            JButton btn = new JButton(nav[i]);
            btn.setFont(F_MID);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(i == 0);
            btn.setBackground(i == 0 ? Color.WHITE : C_DARK);
            btn.setForeground(i == 0 ? C_DARK : new Color(180, 180, 180));
            btn.setMaximumSize(new Dimension(170, 35));

            btn.addActionListener(e -> {
                cardLayout.show(mainContentPanel, "Tab" + index);
                for (int j = 0; j < navButtons.length; j++) {
                    navButtons[j].setContentAreaFilled(j == index);
                    navButtons[j].setBackground(j == index ? Color.WHITE : C_DARK);
                    navButtons[j].setForeground(j == index ? C_DARK : new Color(180, 180, 180));
                }
            });

            navButtons[i] = btn;
            side.add(btn);
            side.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        split.setLeftComponent(side);

        homeTab = new JPanel(new BorderLayout());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 10, 40));

        lbl = new JLabel("Мои проекты");
        lbl.setFont(F_BIG);
        headerPanel.add(lbl, BorderLayout.WEST);

        btnCreate = new JButton("+ Создать проект");
        btnCreate.setFont(F_MID);
        btnCreate.setBackground(C_PINK);
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerPanel.add(btnCreate, BorderLayout.EAST);
        homeTab.add(headerPanel, BorderLayout.NORTH);

        pGrid = new JPanel();
        pGrid.setLayout(new BoxLayout(pGrid, BoxLayout.Y_AXIS));
        pGrid.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        JPanel cSel = createAndroidCard("Выбрать новый...", "Открыть готовый проект с диска", true, true);
        cSel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser ch = new JFileChooser();
                ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (ch.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    String selectedPath = ch.getSelectedFile().getAbsolutePath();
                    saveProjectToHistory(selectedPath);
                    projectPath = selectedPath;
                    startEngine = true;
                    frame.dispose();
                }
            }
        });
        pGrid.add(cSel);
        pGrid.add(Box.createRigidArea(new Dimension(0, 12)));

        btnCreate.addActionListener(e -> {
            JFileChooser ch = new JFileChooser();
            ch.setDialogTitle("Выберите папку, где создать проект");
            ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (ch.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                String name = JOptionPane.showInputDialog(frame, "Введите название проекта:", "Новый проект",
                        JOptionPane.QUESTION_MESSAGE);
                if (name != null && !name.trim().isEmpty()) {
                    File newProjectDir = new File(ch.getSelectedFile(), name.trim());
                    if (!newProjectDir.exists() && newProjectDir.mkdirs()) {

                        new File(newProjectDir, "sprites").mkdir();
                        new File(newProjectDir, "scripts").mkdir();

                        File confFile = new File(newProjectDir, "conf.lua");
                        try (PrintWriter out = new PrintWriter(
                                new OutputStreamWriter(new FileOutputStream(confFile), "UTF-8"))) {
                            out.println("return {");
                            out.println("	windowWidth = 640,");
                            out.println("	windowHeight = 480,");
                            out.println("	windowTitle = \"" + name.trim() + "\",");
                            out.println("	windowIcon = \"overriders/1.png\",");
                            out.println("	fullscreen = false,");
                            out.println("	resizable = true,");
                            out.println("	undecorated = false,");
                            out.println("	alwaysRun = true,");
                            out.println("	topmost = false,");
                            out.println("	transparent = false,");
                            out.println("	maxFPS = 60,");
                            out.println();
                            out.println("	exportName = \"game\",");
                            out.println("	exportIcon = \"\",");
                            out.println("	saveDir = \"\",");
                            out.println("	keystore = \"Override.engine.keystore\",");
                            out.println("}");
                        } catch (IOException ex) {
                            System.err.println("Не удалось создать conf.lua: " + ex.getMessage());
                        }

                        File mainLuaFile = new File(newProjectDir, "main.lua");
                        try (PrintWriter out = new PrintWriter(
                                new OutputStreamWriter(new FileOutputStream(mainLuaFile), "UTF-8"))) {
                            out.println("-- Base script for " + name.trim());
                            out.println("print(\"Project initialized successfully!\")");
                        } catch (IOException ex) {
                            System.err.println("Не удалось создать main.lua: " + ex.getMessage());
                        }

                        saveProjectToHistory(newProjectDir.getAbsolutePath());
                        projectPath = newProjectDir.getAbsolutePath();
                        startEngine = true;
                        Global.projectPath = projectPath;
                        frame.dispose();
                        Override.loadConf();
                        Override.start(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Не удалось создать папку. Возможно, она уже существует.",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        renderProjectList();

        scroll = new JScrollPane(pGrid);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        homeTab.add(scroll, BorderLayout.CENTER);

        browseTab = new JPanel();
        docTab = new JPanel();
        docTab.add(new JLabel("Documentation"));
        settingsTab = new JPanel(new BorderLayout());

        settingsContent = new JPanel(new GridBagLayout());
        settingsContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        settingsTitle = new JLabel("Настройки");
        settingsTitle.setFont(F_BIG);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsContent.add(settingsTitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        lblAuthor = new JLabel("Автор по умолчанию:");
        lblAuthor.setFont(F_MID);
        settingsContent.add(lblAuthor, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtAuthor = new JTextField(currentAuthor);
        txtAuthor.setFont(F_MID);
        settingsContent.add(txtAuthor, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        lblTheme = new JLabel("Тема интерфейса:");
        lblTheme.setFont(F_MID);
        settingsContent.add(lblTheme, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        String[] themes = { "Override (По умолчанию)", "Светлая", "Тёмная", "Системная" };
        comboTheme = new JComboBox<>(themes);
        comboTheme.setSelectedItem(currentTheme);
        comboTheme.setFont(F_MID);
        comboTheme.setBackground(Color.WHITE);
        comboTheme.addActionListener(e -> {
            applyThemeColors((String) comboTheme.getSelectedItem());
        });
        settingsContent.add(comboTheme, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        lblApi = new JLabel("Тип сборки:");
        lblApi.setFont(F_MID);
        settingsContent.add(lblApi, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setOpaque(false);
        rbRelease = new JRadioButton("Override alpha [.exe]", currentBuildType.equals("Release"));
        rbBeta = new JRadioButton("Override java builder [.exe,.jar,compilie .exe]", currentBuildType.equals("Beta"));
        rbRelease.setOpaque(false);
        rbBeta.setOpaque(false);
        rbRelease.setFont(F_MID);
        rbBeta.setFont(F_MID);
        ButtonGroup apiGroup = new ButtonGroup();
        apiGroup.add(rbRelease);
        apiGroup.add(rbBeta);
        radioPanel.add(rbRelease);
        radioPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        radioPanel.add(rbBeta);
        settingsContent.add(radioPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        chkAutoSave = new JCheckBox("-Разрешить отправку данных о ПК при ошибках", currentAllowTelemetry);
        chkAutoSave.setFont(F_MID);
        chkAutoSave.setOpaque(false);
        settingsContent.add(chkAutoSave, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        btnSaveSettings = new JButton("Сохранить изменения");
        btnSaveSettings.setFont(F_MID);
        btnSaveSettings.setBackground(C_PINK);
        btnSaveSettings.setForeground(Color.WHITE);
        btnSaveSettings.setFocusPainted(false);
        btnSaveSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSaveSettings.addActionListener(e -> {
            currentAuthor = txtAuthor.getText();
            currentTheme = (String) comboTheme.getSelectedItem();
            currentBuildType = rbRelease.isSelected() ? "Release" : "Beta";
            currentAllowTelemetry = chkAutoSave.isSelected();
            saveSettings();
            JOptionPane.showMessageDialog(frame, "Настройки успешно сохранены!", "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        settingsContent.add(btnSaveSettings, gbc);
        settingsScroll = new JScrollPane(settingsContent);
        settingsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        settingsScroll.setBorder(null);
        settingsScroll.getVerticalScrollBar().setUnitIncrement(16);
        settingsScroll.setOpaque(false);
        settingsScroll.getViewport().setOpaque(false);
        settingsTab.add(settingsScroll, BorderLayout.CENTER);
        mainContentPanel.add(homeTab, "Tab0");
        mainContentPanel.add(browseTab, "Tab1");
        mainContentPanel.add(docTab, "Tab2");
        mainContentPanel.add(settingsTab, "Tab3");
        split.setRightComponent(mainContentPanel);
        frame.add(split);
        applyThemeColors(currentTheme);
        frame.setVisible(true);
        while (frame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        return startEngine ? projectPath : null;
    }

    private void renderProjectList() {
        Component[] comps = pGrid.getComponents();
        for (int i = comps.length - 1; i >= 2; i--) {
            pGrid.remove(comps[i]);
        }
        if (savedProjects.isEmpty()) {
            pGrid.add(Box.createVerticalGlue());
            JLabel emptyLabel = new JLabel("Проекты отсутствуют");
            emptyLabel.setFont(F_BIG);
            emptyLabel.setForeground(new Color(40, 40, 42, 100));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setOpaque(false);
            emptyPanel.add(emptyLabel);
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pGrid.add(emptyPanel);
            pGrid.add(Box.createVerticalGlue());
        } else {
            for (String path : savedProjects) {
                File dir = new File(path);
                boolean isValid = dir.exists() && dir.isDirectory();
                String name = isValid ? dir.getName() : "Неизвестный проект";
                JPanel card = createAndroidCard(name, path, false, isValid);
                if (isValid) {
                    card.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            Global.projectPath = path;
                            frame.dispose();
                            Override.loadConf();
                            Override.start(false);
                        }
                    });
                } else {
                    card.setToolTipText("Путь к проекту не существует или папка была удалена!");
                }
                pGrid.add(card);
                pGrid.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }
    }

    private void applyThemeColors(String themeName) {
        Color bgTheme, textTheme, cardTextTheme, componentBg;
        switch (themeName) {
            case "Светлая":
                bgTheme = new Color(245, 245, 247);
                textTheme = new Color(30, 30, 30);
                cardTextTheme = new Color(50, 50, 50);
                componentBg = Color.WHITE;
                C_YELLOW = bgTheme;
                break;
            case "Тёмная":
                bgTheme = new Color(40, 40, 42);
                textTheme = new Color(240, 240, 240);
                cardTextTheme = new Color(220, 220, 220);
                componentBg = new Color(60, 60, 63);
                C_YELLOW = bgTheme;
                break;
            case "Системная":
                bgTheme = UIManager.getColor("Panel.background");
                textTheme = UIManager.getColor("Label.foreground");
                cardTextTheme = textTheme;
                componentBg = UIManager.getColor("TextField.background");
                C_YELLOW = bgTheme;
                break;
            case "Override (По умолчанию)":
            default:
                C_YELLOW = new Color(251, 237, 154);
                bgTheme = C_YELLOW;
                textTheme = new Color(53, 53, 55);
                cardTextTheme = textTheme;
                componentBg = Color.WHITE;
                break;
        }
        if (homeTab != null)
            homeTab.setBackground(bgTheme);
        if (browseTab != null)
            browseTab.setBackground(bgTheme);
        if (docTab != null)
            docTab.setBackground(bgTheme);
        if (settingsTab != null)
            settingsTab.setBackground(bgTheme);
        if (settingsContent != null)
            settingsContent.setBackground(bgTheme);
        if (pGrid != null)
            pGrid.setBackground(bgTheme);
        if (headerPanel != null)
            headerPanel.setBackground(bgTheme);
        if (lbl != null)
            lbl.setForeground(cardTextTheme);
        if (settingsTitle != null)
            settingsTitle.setForeground(cardTextTheme);
        if (lblAuthor != null)
            lblAuthor.setForeground(textTheme);
        if (lblTheme != null)
            lblTheme.setForeground(textTheme);
        if (lblApi != null)
            lblApi.setForeground(textTheme);
        if (rbRelease != null)
            rbRelease.setForeground(textTheme);
        if (rbBeta != null)
            rbBeta.setForeground(textTheme);
        if (chkAutoSave != null)
            chkAutoSave.setForeground(textTheme);
        if (txtAuthor != null) {
            txtAuthor.setBackground(componentBg);
            txtAuthor.setForeground(textTheme);
            txtAuthor.setBorder(BorderFactory.createCompoundBorder(new LineBorder(cardTextTheme, 1, true),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        }
        if (comboTheme != null) {
            comboTheme.setBackground(componentBg);
            comboTheme.setForeground(textTheme);
        }
        if (btnCreate != null) {
            btnCreate.setBorder(BorderFactory.createCompoundBorder(new LineBorder(cardTextTheme, 1, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        }
        if (btnSaveSettings != null) {
            btnSaveSettings.setBorder(BorderFactory.createCompoundBorder(new LineBorder(cardTextTheme, 1, true),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        }
        if (pGrid != null) {
            renderProjectList();
            pGrid.revalidate();
            pGrid.repaint();
        }
    }

    private JPanel createAndroidCard(String title, String desc, boolean isAction, boolean isValid) {
        JPanel c = new JPanel(new BorderLayout(15, 0));
        boolean isDark = comboTheme != null && ("Тёмная".equals(comboTheme.getSelectedItem()));
        Color activeCardBg = isDark ? new Color(60, 60, 63) : Color.WHITE;
        c.setBackground(isAction ? C_YELLOW : activeCardBg);
        c.setPreferredSize(new Dimension(600, 65));
        c.setMinimumSize(new Dimension(600, 65));
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        Color borderThemeColor = isDark ? new Color(90, 90, 95) : new Color(220, 220, 220);
        Color borderColor = isAction ? C_CARD : (isValid ? borderThemeColor : new Color(230, 80, 80));
        int borderWidth = (isAction || !isValid) ? 2 : 1;
        c.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderColor, borderWidth, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JPanel leadingIcon = new JPanel(new GridBagLayout());
        leadingIcon.setBackground(isAction ? C_YELLOW : activeCardBg);
        leadingIcon.setPreferredSize(new Dimension(40, 40));
        leadingIcon.setMinimumSize(new Dimension(40, 40));
        leadingIcon.setMaximumSize(new Dimension(40, 40));
        int randomNum = new java.util.Random().nextInt(8) + 1;
        String imagePath = "overriders/" + randomNum + ".png";
        ImageIcon rawIcon = new ImageIcon(imagePath);
        java.awt.Image scaledImage = rawIcon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
        leadingIcon.add(iconLabel);
        c.add(leadingIcon, BorderLayout.WEST);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(F_MID);
        t.setForeground(isDark ? Color.WHITE : C_CARD);
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Arial", Font.PLAIN, 12));
        d.setForeground(isDark ? new Color(180, 180, 180) : Color.GRAY);
        textPanel.add(t);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(d);
        c.add(textPanel, BorderLayout.CENTER);
        if (!isAction) {
            JPanel actionButtonsPanel = new JPanel(new GridBagLayout());
            actionButtonsPanel.setOpaque(false);

            GridBagConstraints gbcBtn = new GridBagConstraints();
            gbcBtn.gridy = 0;
            gbcBtn.insets = new Insets(0, 5, 0, 5);
            gbcBtn.anchor = GridBagConstraints.CENTER;
            gbcBtn.fill = GridBagConstraints.NONE;

            if (isValid) {
                JButton btnExport = new JButton("Собрать");
                btnExport.setFont(new Font("Arial", Font.BOLD, 14));
                btnExport.setForeground(new Color(120, 180, 120));
                btnExport.setBorderPainted(false);
                btnExport.setContentAreaFilled(false);
                btnExport.setFocusPainted(false);
                btnExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnExport.setToolTipText("Собрать проект в готовую игру");

                btnExport.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btnExport.setForeground(C_PINK);
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btnExport.setForeground(new Color(120, 180, 120));
                    }
                });

                btnExport.addActionListener(e -> {
                    JFileChooser ch = new JFileChooser();
                    ch.setDialogTitle("Выберите папку для сохранения готовой сборки");
                    ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (ch.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        String exportPath = ch.getSelectedFile().getAbsolutePath();
                        exportProject(desc, exportPath, title);
                    }
                });

                gbcBtn.gridx = 0;
                actionButtonsPanel.add(btnExport, gbcBtn);
            }

            JButton btnDelete = new JButton("Удалить");
            btnDelete.setFont(new Font("Arial", Font.BOLD, 14));
            btnDelete.setForeground(new Color(180, 180, 180));
            btnDelete.setBorderPainted(false);
            btnDelete.setContentAreaFilled(false);
            btnDelete.setFocusPainted(false);
            btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDelete.setToolTipText("Удалить проект из списка");

            btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btnDelete.setForeground(new Color(230, 80, 80));
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    btnDelete.setForeground(new Color(180, 180, 180));
                }
            });

            btnDelete.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(c),
                        "Вы действительно хотите удалить проект '" + title
                                + "' из списка?\n(Файлы на диске останутся нетронутыми)",
                        "Удаление проекта",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    removeProjectFromHistory(desc);
                    renderProjectList();
                    pGrid.revalidate();
                    pGrid.repaint();
                }
            });

            gbcBtn.gridx = 1;
            actionButtonsPanel.add(btnDelete, gbcBtn);

            c.add(actionButtonsPanel, BorderLayout.EAST);
        }

        if (isAction || isValid) {
            c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        return c;
    }

    private void loadProjectsHistory() {
        savedProjects.clear();
        File file = new File(HISTORY_FILE);
        if (!file.exists())
            return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    savedProjects.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении истории проектов: " + e.getMessage());
        }
    }

    private void saveProjectToHistory(String path) {
        java.util.LinkedHashSet<String> uniqueProjects = new java.util.LinkedHashSet<String>(savedProjects);
        uniqueProjects.remove(path);
        uniqueProjects.add(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String p : uniqueProjects) {
                writer.write(p);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении проекта в историю: " + e.getMessage());
        }
        loadProjectsHistory();
    }

    private void removeProjectFromHistory(String path) {
        savedProjects.remove(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String p : savedProjects) {
                writer.write(p);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при удалении проекта из истории: " + e.getMessage());
        }
    }

    private void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            writer.write("author=" + currentAuthor);
            writer.newLine();
            writer.write("theme=" + currentTheme);
            writer.newLine();
            writer.write("buildType=" + currentBuildType);
            writer.newLine();
            writer.write("telemetry=" + currentAllowTelemetry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении настроек: " + e.getMessage());
        }
    }

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists())
            return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length < 2)
                    continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "author":
                        currentAuthor = value;
                        break;
                    case "theme":
                        currentTheme = value;
                        break;
                    case "buildType":
                        currentBuildType = value;
                        break;
                    case "telemetry":
                        currentAllowTelemetry = Boolean.parseBoolean(value);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении настроек: " + e.getMessage());
        }
    }
}
