package UI.Pages;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Pagină de localitate pentru Iași cu puncte de interes definite prin lat/lon (structură ca la harta României).
 */
public class IasiLocalityPage extends JPanel {

    // Structură identică cu cea de la harta României: nume, latitudine, longitudine
    public static class CityNode {
        public final String name;
        public final double lat;
        public final double lon;

        public CityNode(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    // Limitele geografice pentru această hartă (identifică-le după imaginea folosită!)
    // Pot fi ajustate pentru a se potrivi cu imaginea folosită la Iași.
    private static final double IASI_LAT_MIN = 47.110, IASI_LAT_MAX = 47.210;
    private static final double IASI_LON_MIN = 27.520, IASI_LON_MAX = 27.720;

    // Lista cu coordonatele reale pentru punctele importante din Iași
    private static final List<CityNode> NODES = Arrays.asList(
            new CityNode("Gara Iași",         47.1670, 27.5719),
            new CityNode("Palatul Culturii",  47.1585, 27.6014),
            new CityNode("Copou",             47.1879, 27.5740),
            new CityNode("Tătărași Sud",      47.1450, 27.6600),
            new CityNode("Tătărași Nord",      47.1700, 27.6250),
            new CityNode("Pacurari",      47.1820, 27.5459),


            new CityNode("Podu Roș",          47.1428, 27.6012),
            new CityNode("Dacia",             47.1692, 27.5499),
            new CityNode("Alexandru cel Bun",47.1602, 27.5599),
            new CityNode("Nicolina 1",        47.1252, 27.5851),
            new CityNode("Tiki Beach",        47.1688, 27.6541),
            new CityNode("Bularga",           47.1230, 27.6350),
            new CityNode("Galata",            47.1380, 27.5765)
    );

    // Conversie lat/lon în coordonate pe imagine (proiecție simplificată)
    private Point latLonToPoint(double lat, double lon, int imgW, int imgH) {
        double x = (lon - IASI_LON_MIN) / (IASI_LON_MAX - IASI_LON_MIN) * imgW;
        double y = (IASI_LAT_MAX - lat) / (IASI_LAT_MAX - IASI_LAT_MIN) * imgH;
        return new Point((int) x, (int) y);
    }

    public IasiLocalityPage(Runnable onBack) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header cu buton Înapoi
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel label = new JLabel("Harta localității: Iași", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(33, 37, 41));
        header.add(label, BorderLayout.CENTER);

        JButton backBtn = new JButton("Înapoi");
        styleNiceButton(backBtn, new Color(38, 132, 255), new Color(0, 96, 192));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> onBack.run());
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        rightHeader.setOpaque(false);
        rightHeader.add(backBtn);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Split pane: stânga (hartă), dreapta (legendă)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.75);
        split.setContinuousLayout(true);
        split.setDividerSize(3);
        split.setBorder(null);

        // Panel stânga: imagine cu puncte
        JPanel mapPanel = new JPanel() {
            private BufferedImage img;
            {
                try {
                    URL imgUrl = getClass().getResource("/UI/images/harta-iasi.png");
                    if (imgUrl == null) {
                        // fallback la imagine generică, dacă nu găsești imaginea originală
                        imgUrl = getClass().getResource("/UI/images/harta-localitate-placeholder.png");
                    }
                    if (imgUrl != null) {
                        img = ImageIO.read(imgUrl);
                    }
                } catch (Exception ignored) {}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                int panelW = getWidth(), panelH = getHeight();
                if (img != null) {
                    double imgAspect = (double) img.getWidth() / img.getHeight();
                    int drawW = panelW, drawH = (int) (panelW / imgAspect);
                    if (drawH > panelH) {
                        drawH = panelH;
                        drawW = (int) (panelH * imgAspect);
                    }
                    int xImg = (panelW - drawW) / 2, yImg = (panelH - drawH) / 2;
                    g.drawImage(img, xImg, yImg, drawW, drawH, null);

                    // Desenează nodurile pe bază de lat/lon
                    for (int i = 0; i < NODES.size(); i++) {
                        CityNode node = NODES.get(i);
                        Point p = latLonToPoint(node.lat, node.lon, drawW, drawH);
                        int px = xImg + p.x;
                        int py = yImg + p.y;
                        // bulină
                        g.setColor(new Color(38, 132, 255));
                        g.fillOval(px - 8, py - 8, 16, 16);
                        g.setColor(Color.WHITE);
                        g.drawOval(px - 8, py - 8, 16, 16);
                        // număr punct
                        g.setColor(Color.WHITE);
                        g.setFont(getFont().deriveFont(Font.BOLD, 12f));
                        String nr = String.valueOf(i+1);
                        int strW = g.getFontMetrics().stringWidth(nr);
                        int strH = g.getFontMetrics().getHeight();
                        g.drawString(nr, px - strW/2, py + strH/4);
                    }
                } else {
                    g.setColor(Color.RED);
                    g.setFont(getFont().deriveFont(Font.BOLD, 24f));
                    g.drawString("Harta nu este disponibilă!", 30, 60);
                }
            }
        };
        mapPanel.setBackground(Color.WHITE);

        split.setLeftComponent(mapPanel);

        // Panel dreapta: legendă puncte
        JPanel legendPanel = new JPanel();
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(30, 18, 20, 18));
        legendPanel.add(Box.createVerticalStrut(12));
        JLabel legendTitle = new JLabel("Legendă locuri Iași:");
        legendTitle.setFont(new Font("Arial", Font.BOLD, 17));
        legendTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        legendPanel.add(legendTitle);
        legendPanel.add(Box.createVerticalStrut(18));
        for (int i = 0; i < NODES.size(); i++) {
            CityNode node = NODES.get(i);
            JPanel row = new JPanel();
            row.setOpaque(false);
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            JLabel dot = new JLabel();
            dot.setPreferredSize(new Dimension(18, 18));
            dot.setMaximumSize(new Dimension(18, 18));
            dot.setMinimumSize(new Dimension(18, 18));
            dot.setIcon(new DotIcon(new Color(38, 132, 255), 16));
            row.add(dot);
            row.add(Box.createHorizontalStrut(8));
            JLabel txt = new JLabel((i+1) + ". " + node.name);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            row.add(txt);
            legendPanel.add(row);
            legendPanel.add(Box.createVerticalStrut(9));
        }
        legendPanel.add(Box.createVerticalGlue());
        split.setRightComponent(legendPanel);

        add(split, BorderLayout.CENTER);

        // Ajustare automată a split-ului la resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int w = getWidth();
                split.setDividerLocation((int)(w * 0.75));
            }
        });
    }

    // Metodă utilitară pentru stilizarea butoanelor
    private void styleNiceButton(JButton btn, Color main, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(main);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(main.darker(), 2, true),
                BorderFactory.createEmptyBorder(8, 0, 8, 0))
        );
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(main);
            }
        });
    }

    // Icon pentru bulină
    private static class DotIcon implements Icon {
        private final Color color;
        private final int size;
        public DotIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }
        @Override
        public int getIconWidth() { return size; }
        @Override
        public int getIconHeight() { return size; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
            g.setColor(Color.WHITE);
            g.drawOval(x, y, size, size);
        }
    }

    // Main pentru testare standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Localitate Iași");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(1000, 700));
            IasiLocalityPage page = new IasiLocalityPage(() -> {
                JOptionPane.showMessageDialog(frame, "Înapoi apăsat!");
            });
            frame.setContentPane(page);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}