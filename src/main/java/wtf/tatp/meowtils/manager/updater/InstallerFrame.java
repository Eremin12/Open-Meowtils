package wtf.tatp.meowtils.manager.updater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import wtf.tatp.meowtils.util.ColorUtil;

public class InstallerFrame {
    //Meowtils Installer
    private static final Color THEME_COLOR = new Color(166, 94, 255);
    private static final Color THEME_COLOR_LIGHT = new Color(189, 140, 255);
    private static final Color THEME_COLOR_ACCENT = new Color(204, 173, 255);
    private static final Color BOTTOM_BAR_COLOR = new Color(30, 33, 41);
    private static final Color CONTENT_BACKGROUND_COLOR = new Color(20, 20, 20);
    private static final Color SECTION_BACKGROUND_COLOR = new Color(26, 26, 30);
    private static final Color SECTION_OUTLINE_COLOR = new Color(45, 45, 52);
    private static final Color DISABLED_BUTTON_COLOR = new Color(180, 180, 180);
    private static final Color SUCCESS_COLOR = ColorUtil.GREEN.brighter();
    private static final Color ERROR_COLOR = ColorUtil.RED.brighter();
    private static final Color SEPARATOR_COLOR = THEME_COLOR_ACCENT;
    private static final String INSTALLATION_GUIDE = "https://Windy.team/";//"https://docs.tatp.wtf/guides/installation/";
    private static final int INFO_SHOW_DELAY = 3000;
    private static final int WIDTH = 460;
    private static final int HEIGHT = 300;

    private static Point mouseDown;
    private static JFrame frame;

    public static void main(String[] args) {
        open();
    }

    public static void open() {
        if (frame != null && frame.isVisible()) {
            SwingUtilities.invokeLater(() -> frame.toFront());
            return;
        }
        SwingUtilities.invokeLater(InstallerFrame::buildFrame);
    }

    private static void buildFrame() {
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BOTTOM_BAR_COLOR);
        frame.setContentPane(root);

        root.add(buildActionBar(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildToolbar(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 32));
        bar.setBackground(THEME_COLOR);

        JLabel title = new JLabel("  Meowtils Installer");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Open Sans", Font.BOLD, 13));

        final JButton close = new JButton("x");
        close.setFont(new Font("Open Sans", Font.PLAIN, 11));
        close.setForeground(DISABLED_BUTTON_COLOR);
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                close.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setForeground(InstallerFrame.DISABLED_BUTTON_COLOR);
            }
        });
        close.addActionListener(e -> closeFrame());

        bar.add(title, BorderLayout.WEST);
        bar.add(close, BorderLayout.EAST);

        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                InstallerFrame.mouseDown = e.getPoint();
            }
        });

        bar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                InstallerFrame.frame.setLocation(p.x - InstallerFrame.mouseDown.x, p.y - InstallerFrame.mouseDown.y);
            }
        });

        return bar;
    }

    private static JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CONTENT_BACKGROUND_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        content.add(buildForgeSection());
        content.add(Box.createVerticalStrut(14));

        content.add(buildSeparator());
        content.add(Box.createVerticalStrut(14));

        content.add(buildLunarSection());
        content.add(Box.createVerticalGlue());

        return content;
    }

    private static JPanel buildForgeSection() {
        JPanel section = section();

        section.add(sectionTitle("Install on Forge"));
        section.add(Box.createVerticalStrut(5));

        section.add(linkLabel("docs.tatp.wtf"));
        section.add(Box.createVerticalStrut(8));

        section.add(infoLabel("Move this jar into your mods folder."));

        return section;
    }

    private static JPanel buildLunarSection() {
        JPanel section = section();
        JLabel statusLabel = new JLabel("");

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(DISABLED_BUTTON_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JButton installButton = new JButton("Install");
        installButton.setFont(new Font("Open Sans", Font.BOLD, 11));
        installButton.setForeground(Color.WHITE);
        installButton.setBackground(THEME_COLOR);
        installButton.setFocusPainted(false);
        installButton.setBorderPainted(false);
        installButton.setOpaque(true);
        installButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installButton.setPreferredSize(new Dimension(80, 24));

        installButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (installButton.isEnabled()) installButton.setBackground(InstallerFrame.THEME_COLOR_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (installButton.isEnabled() &&
                        installButton.getBackground() != InstallerFrame.SUCCESS_COLOR &&
                        installButton.getBackground() != InstallerFrame.ERROR_COLOR) {
                    installButton.setBackground(InstallerFrame.THEME_COLOR);
                }
            }
        });
        installButton.addActionListener(e -> lunarInstall(installButton, statusLabel));

        section.add(sectionTitle("Install on Lunar Client"));
        section.add(Box.createVerticalStrut(10));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(installButton);
        panel.add(Box.createHorizontalStrut(14));
        panel.add(linkLabel("Manual Install"));
        section.add(panel);
        section.add(Box.createVerticalStrut(7));
        section.add(statusLabel);

        return section;
    }

    private static JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bar.setBackground(BOTTOM_BAR_COLOR);
        bar.setPreferredSize(new Dimension(0, 8));

        return bar;
    }

    private static void closeFrame() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    private static JPanel section() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(SECTION_BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECTION_OUTLINE_COLOR, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        return p;
    }

    private static JSeparator buildSeparator() {
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(SEPARATOR_COLOR);
        separator.setBackground(SEPARATOR_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }

    private static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Open Sans", Font.BOLD, 13));
        label.setForeground(THEME_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel infoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(DISABLED_BUTTON_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel linkLabel(String text) {
        final JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(THEME_COLOR_LIGHT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(INSTALLATION_GUIDE));
                } catch (Exception ignored) {}
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(InstallerFrame.THEME_COLOR_ACCENT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(InstallerFrame.THEME_COLOR_LIGHT);
            }
        });
        return label;
    }

    private static void lunarInstall(JButton button, JLabel status) {
        button.setEnabled(false);
        status.setForeground(DISABLED_BUTTON_COLOR);
        status.setText("Installing...");

        new Thread(() -> {
            try {
                File jarFile = new File(InstallerFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File launchDir = jarFile.getParentFile();
                File outputDir = new File(launchDir, "Meowtils-Lunar");
                outputDir.mkdirs();

                File modJar = new File(outputDir, "Meowtils.jar");
                File agentJar = new File(outputDir, "lunar-agent.jar");
                File agentJson = new File(outputDir, "agent-mods.json");

                Files.copy(jarFile.toPath(), modJar.toPath(), StandardCopyOption.REPLACE_EXISTING);

                try (InputStream i = InstallerFrame.class.getResourceAsStream("/lunar-agent.meowtils")) {
                    if (i == null) {
                        throw new IllegalStateException("Agent jar not found in resources.");
                    }
                    Files.copy(i, agentJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                String modJarPath = modJar.getAbsolutePath().replace("\\", "\\\\");
                String json = "{\n  \"mods\": [\n    {\n      \"jar\": \"" + modJarPath + "\",\n      \"mixin\": \"mixins.meowtils.json\",\n      \"property\": \"meowtils.agent.injected\"\n    }\n  ]\n}";

                try (FileWriter f = new FileWriter(agentJson)) {
                    f.write(json);
                }

                String jvmArg = "-javaagent:" + agentJar.getAbsolutePath() + "=" + agentJson.getAbsolutePath();

                SwingUtilities.invokeLater(() -> {
                    button.setBackground(SUCCESS_COLOR);
                    button.setText("Done");
                    status.setForeground(SUCCESS_COLOR);
                    status.setText("Installed! Add JVM argument: " + jvmArg);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    button.setEnabled(true);
                    button.setBackground(ERROR_COLOR);
                    button.setText("Failed");
                    status.setForeground(ERROR_COLOR);
                    status.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }
}