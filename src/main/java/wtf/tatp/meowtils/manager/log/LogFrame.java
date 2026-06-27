package wtf.tatp.meowtils.manager.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.util.ColorUtil;

public class LogFrame {

    private static final Color BOTTOM_BAR_COLOR = new Color(30, 33, 41);
    private static final Color CONSOLE_BACKGROUND_COLOR = new Color(20, 20, 20);
    private static final Color SLIDER_BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color SLIDER_COLOR = new Color(80, 80, 80);
    private static final Color DISABLED_BUTTON_COLOR = new Color(180, 180, 180);
    private static final Color HOVERED_BUTTON_COLOR = new Color(255, 255, 255);
    private static final Pattern LOG_PATTERN = Pattern.compile("^\\[(\\d{2}:\\d{2}:\\d{2})] \\[([A-Z]+)]: (.+)$");
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = (int) (SCREEN_SIZE.width * 0.5);
    private static final int HEIGHT = (int) (SCREEN_SIZE.height * 0.5);

    private static Point mouseDown;
    private static Point resizeOrigin;
    private static Dimension resizeStart;
    private static JTextPane logPane;
    private static JFrame frame;
    private static boolean timestamps = true;

    public static void open() {
        if (frame != null && frame.isVisible()) {
            SwingUtilities.invokeLater(() -> frame.toFront());
            return;
        }
        SwingUtilities.invokeLater(() -> {
            buildFrame();
            readLog();
        });
    }

    private static void buildFrame() {
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };

        panel.setBackground(BOTTOM_BAR_COLOR);
        frame.setContentPane(panel);

        panel.add(buildActionBar(), BorderLayout.NORTH);
        panel.add(buildLogPane(), BorderLayout.CENTER);
        panel.add(buildToolbar(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 32));
        bar.setBackground(getThemeColor());
        bar.setOpaque(true);

        JLabel title = new JLabel("  Meowtils Log");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Open Sans", Font.BOLD, 13));

        JButton close = toolbarButton("x");
        close.addActionListener(e -> {
            frame.dispose();
            frame = null;
            logPane = null;
        });

        bar.add(title, BorderLayout.WEST);
        bar.add(close, BorderLayout.EAST);

        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                LogFrame.mouseDown = e.getPoint();
            }
        });

        bar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                LogFrame.frame.setLocation(p.x - LogFrame.mouseDown.x, p.y - LogFrame.mouseDown.y);
            }
        });

        return bar;
    }

    private static JScrollPane buildLogPane() {
        logPane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };

        logPane.setEditable(false);
        logPane.setFocusable(false);
        logPane.setBackground(CONSOLE_BACKGROUND_COLOR);
        logPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logPane.setMargin(new Insets(8, 10, 8, 10));

        JScrollPane scrollPane = new JScrollPane(logPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(CONSOLE_BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(CONSOLE_BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LogFrame.SLIDER_COLOR;
                this.trackColor = LogFrame.SLIDER_BACKGROUND_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return zeroButton();
            }

            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });

        return scrollPane;
    }

    private static JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bar.setBackground(BOTTOM_BAR_COLOR);
        bar.setPreferredSize(new Dimension(0, 34));

        final Color enabledColor = getThemeColor();
        final Color disabledColor = DISABLED_BUTTON_COLOR;

        final JButton timestampButton = toolbarButton("Timestamps");
        timestampButton.setForeground(enabledColor);
        timestampButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                timestampButton.setForeground(LogFrame.HOVERED_BUTTON_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                timestampButton.setForeground(LogFrame.timestamps ? enabledColor : disabledColor);
            }
        });

        timestampButton.addActionListener(e -> {
            timestamps = !timestamps;
            timestampButton.setForeground(timestamps ? enabledColor : disabledColor);
        });

        JButton copyButton = toolbarButton("Copy");
        copyButton.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(logPane.getText()), null);
            Meowtils.info("Log copied to clipboard.");
            Meowtils.addMessage("Log copied to clipboard.");
        });

        JButton buttonPost = toolbarButton("Upload");
        buttonPost.addActionListener(e -> LogManager.postLog());

        bar.add(timestampButton);
        bar.add(copyButton);
        bar.add(buttonPost);

        JPanel resizePoint = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(80, 80, 80));

                for (int i = 1; i <= 3; i++) {
                    int p = i * 4;
                    g.drawLine(getWidth() - p, getHeight() - 1, getWidth() - 1, getHeight() - p);
                }
            }
        };

        resizePoint.setOpaque(false);
        resizePoint.setPreferredSize(new Dimension(16, 34));
        resizePoint.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        resizePoint.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                LogFrame.resizeOrigin = e.getLocationOnScreen();
                LogFrame.resizeStart = LogFrame.frame.getSize();
            }
        });

        resizePoint.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                LogFrame.frame.setSize(
                        Math.max(400, LogFrame.resizeStart.width + p.x - LogFrame.resizeOrigin.x),
                        Math.max(260, LogFrame.resizeStart.height + p.y - LogFrame.resizeOrigin.y)
                );
            }
        });

        JPanel panelS = new JPanel(new BorderLayout());
        panelS.setOpaque(false);
        panelS.add(bar, BorderLayout.CENTER);
        panelS.add(resizePoint, BorderLayout.EAST);
        return panelS;
    }

    private static void readLog() {
        File logFile = LogManager.findLog();
        if (logFile == null) return;

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(logFile.toPath()), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String captured = line;
                    SwingUtilities.invokeLater(() -> {
                        // Process log line
                    });
                }
            } catch (IOException e) {
                Meowtils.error("Unable to read log file: " + e);
            }
        }, "Meowtils-Log-Read").start();
    }

    public static void appendLog(String level, String message, String timestamp) {
        if (logPane == null) {
            return;
        }

        Color levelColor;
        switch (level.toUpperCase()) {
            case "FATAL":
                levelColor = ColorUtil.DARK_RED;
                break;
            case "ERROR":
                levelColor = ColorUtil.RED;
                break;
            case "WARN":
                levelColor = ColorUtil.GOLD;
                break;
            default:
                levelColor = ColorUtil.WHITE;
                break;
        }

        String time = (timestamp != null && timestamps) ? ("[" + timestamp + "] ") : "";
        String line = time + "[" + level.toUpperCase() + "] " + message + "\n";
        Color color = levelColor;

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = logPane.getStyledDocument();
            Style style = logPane.addStyle(null, null);
            StyleConstants.setForeground(style, color);
            try {
                doc.insertString(doc.getLength(), line, style);
            } catch (BadLocationException e) {
                Meowtils.error("Cannot insert string: \"" + line + "\" to log.");
            }
        });
    }

    private static Color getThemeColor() {
        GUI g = Module.get(GUI.class);
        if (g == null) return Color.WHITE;
        return new Color(g.red, g.green, g.blue);
    }

    private static JButton toolbarButton(String label) {
        final JButton b = new JButton(label);
        b.setFont(new Font("Open Sans", Font.PLAIN, 11));
        b.setForeground(DISABLED_BUTTON_COLOR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setForeground(LogFrame.HOVERED_BUTTON_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setForeground(LogFrame.DISABLED_BUTTON_COLOR);
            }
        });
        return b;
    }
}