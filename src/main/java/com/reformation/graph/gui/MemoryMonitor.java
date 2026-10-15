package com.reformation.graph.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Tracks Memory allocated & used, displayed in graph form.
 */
public class MemoryMonitor extends JPanel {
    private static final long serialVersionUID = 7534680149920892993L;

    private static final Color BG_COLOR = new Color(30, 30, 38);
    private static final Color PANEL_BG = new Color(40, 42, 54);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 235);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 160);
    private static final Color ACCENT_CYAN = new Color(80, 200, 220);
    private static final Color ACCENT_BLUE = new Color(90, 130, 240);
    private static final Color GRID_COLOR = new Color(60, 63, 78);
    private static final Color BAR_USED_TOP = new Color(80, 200, 220);
    private static final Color BAR_USED_BOT = new Color(90, 130, 240);
    private static final Color BAR_FREE = new Color(50, 52, 65);
    private static final Color LINE_COLOR = new Color(80, 200, 220);
    private static final Color FILL_TOP = new Color(80, 200, 220, 60);
    private static final Color FILL_BOT = new Color(90, 130, 240, 10);

    static JCheckBox dateStampCB = new JCheckBox("Output Date Stamp");
    public Surface surf;
    JPanel controls;
    boolean doControls;
    JTextField tf;

    public class Surface extends JPanel implements Runnable {
        private static final long serialVersionUID = -1595300881624449042L;

        private static final int RENDER_INTERVAL_MS = 16;
        private static final int POINT_SPACING = 7;
        private static final float LERP_SPEED = 6.0f;
        private static final float SPLINE_TENSION = 0.4f;

        public Thread thread;
        public long sleepAmount = 500;
        private int w, h;
        private BufferedImage bimg;
        private Graphics2D big;
        private Font labelFont = new Font("SansSerif", Font.PLAIN, 11);
        private Font valueFont = new Font("SansSerif", Font.BOLD, 12);
        private Runtime r = Runtime.getRuntime();
        private float columnOffset;
        private float[] pts;
        private int ptNum;
        private int ascent, descent;
        private String usedStr;

        private long lastSampleTime;
        private float displayedFree, displayedTotal, displayedUsed;
        private float targetFree, targetTotal;
        private float displayedTipY;
        private boolean hasSampled;

        public Surface() {
            setBackground(BG_COLOR);
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (thread == null) start(); else stop();
                }
            });
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            return new Dimension(340, 180);
        }

        private void takeSample(int graphY, int graphH) {
            targetFree = r.freeMemory();
            targetTotal = r.totalMemory();
            if (!hasSampled) {
                displayedFree = targetFree;
                displayedTotal = targetTotal;
                hasSampled = true;
            }

            float freeRatio = displayedFree / displayedTotal;
            float newY = graphY + graphH * freeRatio;

            int maxPts = (w - 42) / POINT_SPACING;
            if (maxPts < 2) maxPts = 2;

            if (pts == null || pts.length != maxPts) {
                float[] old = pts;
                int oldNum = ptNum;
                pts = new float[maxPts];
                ptNum = 0;
                if (old != null && oldNum > 0) {
                    int copyLen = Math.min(oldNum, maxPts);
                    int srcOff = oldNum > maxPts ? oldNum - maxPts : 0;
                    System.arraycopy(old, srcOff, pts, 0, copyLen);
                    ptNum = copyLen;
                }
            }

            if (ptNum >= pts.length) {
                System.arraycopy(pts, 1, pts, 0, ptNum - 1);
                ptNum--;
            }
            pts[ptNum] = newY;
            displayedTipY = newY;
            ptNum++;

            if (MemoryMonitor.dateStampCB.isSelected()) {
                System.out.println(new Date().toString() + " " + usedStr);
            }
        }

        public void paintComponent(Graphics g) {
            if (big == null) {
                return;
            }

            big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            big.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            big.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            big.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            big.setColor(BG_COLOR);
            big.fillRect(0, 0, w, h);

            float dt = RENDER_INTERVAL_MS / 1000.0f;
            float lerpFactor = 1.0f - (float) Math.exp(-LERP_SPEED * dt);
            displayedFree += (targetFree - displayedFree) * lerpFactor;
            displayedTotal += (targetTotal - displayedTotal) * lerpFactor;
            displayedUsed = displayedTotal - displayedFree;

            String allocStr = formatBytes((long) displayedTotal) + " allocated";
            usedStr = formatBytes((long) displayedUsed) + " used";

            big.setFont(labelFont);
            big.setColor(TEXT_SECONDARY);
            big.drawString(allocStr, 6, ascent + 2);
            big.setFont(valueFont);
            big.setColor(ACCENT_CYAN);
            big.drawString(usedStr, 6, h - descent - 1);

            float ssH = ascent + descent + 4;
            float remainingHeight = h - (ssH * 2);
            float barWidth = 16.0f;
            int totalBars = 10;
            float blockHeight = remainingHeight / totalBars;
            float barX = 8;

            float usedRatio = displayedUsed / displayedTotal;
            int usedBars = (int) (usedRatio * totalBars);

            for (int i = 0; i < totalBars; i++) {
                float y = ssH + i * blockHeight;
                RoundRectangle2D.Float bar = new RoundRectangle2D.Float(
                        barX, y, barWidth, blockHeight - 2, 4, 4);
                int barIndex = totalBars - 1 - i;
                if (barIndex < usedBars) {
                    float t = (float) barIndex / totalBars;
                    GradientPaint gp = new GradientPaint(
                            barX, y, blendColor(BAR_USED_TOP, BAR_USED_BOT, t),
                            barX, y + blockHeight, blendColor(BAR_USED_TOP, BAR_USED_BOT, t + 0.1f));
                    big.setPaint(gp);
                } else {
                    big.setColor(BAR_FREE);
                }
                big.fill(bar);
            }

            int graphX = 34;
            int graphY = (int) ssH;
            int graphW = w - graphX - 8;
            int graphH = (int) remainingHeight;

            RoundRectangle2D.Float graphBg = new RoundRectangle2D.Float(
                    graphX, graphY, graphW, graphH, 6, 6);
            big.setColor(new Color(35, 37, 48));
            big.fill(graphBg);

            big.setColor(GRID_COLOR);
            big.setStroke(new BasicStroke(1.0f));
            int graphRows = 10;
            float rowHeight = (float) graphH / graphRows;
            for (int j = 0; j <= graphRows; j++) {
                float y = graphY + j * rowHeight;
                big.draw(new Line2D.Float(graphX, y, graphX + graphW, y));
            }

            int graphColumns = 15;
            float colWidth = (float) graphW / graphColumns;
            float scrollSpeed = colWidth / (sleepAmount / (float) RENDER_INTERVAL_MS) * 0.3f;
            columnOffset -= scrollSpeed;
            if (columnOffset < 0) columnOffset += colWidth;

            big.setStroke(new BasicStroke(0.5f));
            for (float x = graphX + columnOffset; x < graphX + graphW; x += colWidth) {
                big.draw(new Line2D.Float(x, graphY, x, graphY + graphH));
            }

            big.setStroke(new BasicStroke(1.0f));

            if (ptNum >= 2) {
                float tipTargetY = graphY + graphH * (displayedFree / displayedTotal);
                displayedTipY += (tipTargetY - displayedTipY) * lerpFactor;

                float totalSpan = (ptNum - 1) * POINT_SPACING;
                float startX = graphX + graphW - totalSpan;

                GeneralPath fillPath = new GeneralPath();
                GeneralPath linePath = new GeneralPath();

                fillPath.moveTo(startX, graphY + graphH);
                fillPath.lineTo(startX, pts[0]);
                linePath.moveTo(startX, pts[0]);

                for (int k = 0; k < ptNum - 1; k++) {
                    float x0 = startX + k * POINT_SPACING;
                    float x1 = startX + (k + 1) * POINT_SPACING;
                    float y0 = pts[k];
                    float y1 = (k + 1 == ptNum - 1) ? displayedTipY : pts[k + 1];

                    float yPrev = (k > 0) ? pts[k - 1] : y0;
                    float yNext = (k + 2 < ptNum)
                            ? ((k + 2 == ptNum - 1) ? displayedTipY : pts[k + 2])
                            : y1;

                    float tan0 = SPLINE_TENSION * (y1 - yPrev);
                    float tan1 = SPLINE_TENSION * (yNext - y0);

                    float cp1x = x0 + POINT_SPACING / 3.0f;
                    float cp1y = y0 + tan0 / 3.0f;
                    float cp2x = x1 - POINT_SPACING / 3.0f;
                    float cp2y = y1 - tan1 / 3.0f;

                    fillPath.curveTo(cp1x, cp1y, cp2x, cp2y, x1, y1);
                    linePath.curveTo(cp1x, cp1y, cp2x, cp2y, x1, y1);
                }

                float endX = startX + (ptNum - 1) * POINT_SPACING;
                fillPath.lineTo(endX, graphY + graphH);
                fillPath.closePath();

                GradientPaint fillGradient = new GradientPaint(
                        0, graphY, FILL_TOP,
                        0, graphY + graphH, FILL_BOT);
                big.setPaint(fillGradient);
                big.fill(fillPath);

                big.setColor(LINE_COLOR);
                big.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                big.draw(linePath);
                big.setStroke(new BasicStroke(1.0f));
            }

            big.setColor(GRID_COLOR);
            big.setStroke(new BasicStroke(1.5f));
            big.draw(graphBg);
            big.setStroke(new BasicStroke(1.0f));

            g.drawImage(bimg, 0, 0, this);
        }

        private String formatBytes(long bytes) {
            if (bytes >= 1_048_576) {
                return String.format("%.1f MB", bytes / 1_048_576.0);
            }
            return String.valueOf(bytes / 1024) + " KB";
        }

        private Color blendColor(Color c1, Color c2, float t) {
            t = Math.max(0, Math.min(1, t));
            int red = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
            int green = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
            int blue = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
            return new Color(red, green, blue);
        }

        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName("MemoryMonitor");
            thread.start();
        }

        public synchronized void stop() {
            thread = null;
            notify();
        }

        public void run() {
            Thread me = Thread.currentThread();

            while (thread == me && !isShowing() || getSize().width == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) { return; }
            }

            lastSampleTime = System.currentTimeMillis();
            targetFree = r.freeMemory();
            targetTotal = r.totalMemory();
            displayedFree = targetFree;
            displayedTotal = targetTotal;
            hasSampled = false;

            while (thread == me && isShowing()) {
                Dimension d = getSize();
                if (d.width != w || d.height != h) {
                    w = d.width;
                    h = d.height;
                    bimg = (BufferedImage) createImage(w, h);
                    big = bimg.createGraphics();
                    big.setFont(labelFont);
                    FontMetrics fm = big.getFontMetrics(labelFont);
                    ascent = fm.getAscent();
                    descent = fm.getDescent();
                }

                long now = System.currentTimeMillis();
                if (now - lastSampleTime >= sleepAmount) {
                    float ssH = ascent + descent + 4;
                    int graphY = (int) ssH;
                    int graphH = (int) (h - (ssH * 2));
                    takeSample(graphY, graphH);
                    lastSampleTime = now;
                }

                repaint();
                try {
                    Thread.sleep(RENDER_INTERVAL_MS);
                } catch (InterruptedException e) { break; }
            }
            thread = null;
        }
    }

    public MemoryMonitor() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BG);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 78), 1, true),
                "Memory Monitor");
        titledBorder.setTitleColor(TEXT_PRIMARY);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        setBorder(titledBorder);

        add(surf = new Surface());

        controls = new JPanel();
        controls.setBackground(PANEL_BG);
        controls.setPreferredSize(new Dimension(340, 180));
        Font font = new Font("SansSerif", Font.PLAIN, 11);
        JLabel label = new JLabel("Sample Rate");
        label.setFont(font);
        label.setForeground(TEXT_PRIMARY);
        controls.add(label);
        tf = new JTextField("500");
        tf.setPreferredSize(new Dimension(50, 22));
        tf.setBackground(BG_COLOR);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_CYAN);
        controls.add(tf);
        label = new JLabel("ms");
        label.setFont(font);
        label.setForeground(TEXT_SECONDARY);
        controls.add(label);
        dateStampCB.setFont(font);
        dateStampCB.setForeground(TEXT_PRIMARY);
        dateStampCB.setBackground(PANEL_BG);
        controls.add(dateStampCB);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                removeAll();
                if ((doControls = !doControls)) {
                    surf.stop();
                    add(controls);
                } else {
                    try {
                        surf.sleepAmount = Long.parseLong(tf.getText().trim());
                    } catch (Exception ex) {}
                    surf.start();
                    add(surf);
                }
                validate();
                repaint();
            }
        });
    }

    public static void main(String s[]) {
        final MemoryMonitor demo = new MemoryMonitor();
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
            public void windowDeiconified(WindowEvent e) { demo.surf.start(); }
            public void windowIconified(WindowEvent e) { demo.surf.stop(); }
        };
        JFrame f = new JFrame("Memory Monitor");
        f.addWindowListener(l);
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(400, 260));
        f.setVisible(true);
        demo.surf.start();
    }
}
