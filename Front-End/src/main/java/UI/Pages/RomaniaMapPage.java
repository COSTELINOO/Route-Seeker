package UI.Pages;

import javax.swing.*;
import java.awt.*;
import UI.HttpJwtClient;
import dtos.CityDTO;
import dtos.ConnectionDTO;
import dtos.InformationDTO;
import dtos.LocationDTO;
import staticData.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;



public class RomaniaMapPage extends JPanel {

    private final JComboBox<CityDTO> citiesCombo;
    private JDialog currentCityDialog = null;

    private void styleLandingButton(JButton btn) {
        Color blue = new Color(38, 132, 255);
        Color blueHover = new Color(0, 96, 192);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(blue);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(blue.darker(), 2, true),
                BorderFactory.createEmptyBorder(12, 0, 12, 0))
        );
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(blueHover);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(blue);
            }
        });
    }

    private void styleExportButton(JButton btn) {
        Color green = new Color(40, 167, 69);
        Color greenHover = new Color(33, 136, 56);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(green);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(green.darker(), 2, true),
                BorderFactory.createEmptyBorder(10, 0, 10, 0))
        );
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(greenHover);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(green);
            }
        });
    }

    private static class LegendPanel extends JPanel
    {
        LegendPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);

            add(Box.createVerticalStrut(18));

            JLabel legendaLabel = new JLabel("Legendă puncte:");
            legendaLabel.setFont(new Font("Arial", Font.BOLD, 17));
            legendaLabel.setForeground(new Color(30, 30, 30));
            legendaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(legendaLabel);

            add(Box.createVerticalStrut(26));

            JPanel pointsPanel = new JPanel();
            pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));
            pointsPanel.setOpaque(false);
            pointsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            pointsPanel.add(createLegendRow(new Color(0, 180, 50), "Locații predefinite"));
            pointsPanel.add(Box.createVerticalStrut(18));
            pointsPanel.add(createLegendRow(new Color(7, 148, 255), "Locații random"));
            pointsPanel.add(Box.createVerticalStrut(18));
            pointsPanel.add(createLegendRow(new Color(220, 53, 69), "Locații nedefinite"));

            add(pointsPanel);
            add(Box.createVerticalGlue());
        }

        private static JPanel createLegendRow(Color color, String label) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setAlignmentX(Component.CENTER_ALIGNMENT);

            Dot dot = new Dot(color);
            dot.setAlignmentY(Component.CENTER_ALIGNMENT);
            row.add(dot);

            row.add(Box.createRigidArea(new Dimension(10, 0)));

            JLabel text = new JLabel(label);
            text.setFont(new Font("Segoe UI", Font.BOLD, 16));
            text.setForeground(new Color(50, 50, 50));
            text.setAlignmentY(Component.CENTER_ALIGNMENT);
            row.add(text);

            return row;
        }

        private static class Dot extends JComponent {
            private final Color color;
            Dot(Color color) {
                this.color = color;
                setPreferredSize(new Dimension(20, 20));
                setMinimumSize(new Dimension(20, 20));
                setMaximumSize(new Dimension(20, 20));
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(3, 3, 14, 14);
                g2.setColor(new Color(100, 100, 100, 90));
                g2.drawOval(3, 3, 14, 14);
            }
        }
    }

    // Limitele geografice ale României pentru această hartă (aproximate)
    private static final double ROMANIA_LAT_MIN = 43.6, ROMANIA_LAT_MAX = 48.3;
    private static final double ROMANIA_LON_MIN = 20.2, ROMANIA_LON_MAX = 29.8;

    private Point latLonToPoint(double lat, double lon, int imgW, int imgH) {
        double x = (lon - ROMANIA_LON_MIN) / (ROMANIA_LON_MAX - ROMANIA_LON_MIN) * imgW;
        double y = (ROMANIA_LAT_MAX - lat) / (ROMANIA_LAT_MAX - ROMANIA_LAT_MIN) * imgH;
        return new Point((int) x, (int) y);
    }

    public RomaniaMapPage(Runnable onLogout, Consumer<CityDTO> onNext) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel questionLabel = new JLabel("Unde vrei să călătorești?", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 26));
        questionLabel.setForeground(new Color(33, 37, 41));

        JPanel centerHeader = new JPanel(new GridBagLayout());
        centerHeader.setOpaque(false);
        centerHeader.add(questionLabel);
        header.add(centerHeader, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        styleLandingButton(logoutBtn);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.addActionListener(e -> onLogout.run());

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        rightHeader.setOpaque(false);
        rightHeader.add(logoutBtn);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);
        split.setContinuousLayout(true);
        split.setDividerSize(3);
        split.setBorder(null);

        JPanel mapPanel = new JPanel(new BorderLayout()) {
            private BufferedImage img;
            private java.util.List<Point> cityCenters = null;

            {
                try {
                    URL imgUrl = getClass().getResource("/UI/images/harta-romania.png");

                    if (imgUrl != null) {
                        img = ImageIO.read(imgUrl);
                    }
                } catch (Exception ignored) {}
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (img == null) return;
                        int panelW = getWidth(), panelH = getHeight();
                        double imgAspect = (double) img.getWidth() / img.getHeight();
                        int drawW = panelW, drawH = (int) (panelW / imgAspect);
                        if (drawH > panelH) {
                            drawH = panelH;
                            drawW = (int) (panelH * imgAspect);
                        }
                        int xImg = (panelW - drawW) / 2, yImg = (panelH - drawH) / 2;

                        boolean found = false;

                        List<CityDTO> cities = Cities.list;

                        for (int i = 0; i < cities.size(); i++)
                        {
                            CityDTO city = cities.get(i);
                            Point p = latLonToPoint(city.getPozX(), city.getPozY(), drawW, drawH);
                            int px = xImg + p.x;
                            int py = yImg + p.y;
                            double dist = Math.hypot(px - e.getX(), py - e.getY());
                            if (dist <= 20) {
                                showCityPopup(city, px, py, RomaniaMapPage.this);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            closeCityPopup();
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                if (img != null) {
                    int panelW = getWidth(), panelH = getHeight();
                    double imgAspect = (double) img.getWidth() / img.getHeight();
                    int drawW = panelW, drawH = (int) (panelW / imgAspect);
                    if (drawH > panelH) {
                        drawH = panelH;
                        drawW = (int) (panelH * imgAspect);
                    }
                    int xImg = (panelW - drawW) / 2, yImg = (panelH - drawH) / 2;

                    cityCenters = new java.util.ArrayList<>();
                    List<CityDTO> cities = Cities.list;
                    g.drawImage(img, xImg, yImg, drawW, drawH, null);
                    for (CityDTO city : cities) {
                        Point p = latLonToPoint(city.getPozX(), city.getPozY(), drawW, drawH);
                        int px = xImg + p.x;
                        int py = yImg + p.y;
                        cityCenters.add(new Point(px, py));
                        // Colorează bulina în funcție de stare:
                        Color dotColor;
                        if (Boolean.TRUE.equals(!Cities.existaTraseu.get(city.getName()))) {
                            dotColor = new Color(220, 53, 69); // roșu
                        } else if (Boolean.TRUE.equals(Cities.existaRandom.get(city.getName()))) {
                            dotColor = new Color(7, 148, 255); // galben
                        } else {
                            dotColor = new Color(0, 180, 50); // verde
                        }
                        g.setColor(dotColor);
                        g.fillOval(px - 6, py - 6, 12, 12);
                        g.setColor(Color.WHITE);
                        g.drawOval(px - 6, py - 6, 12, 12);
                        g.setColor(new Color(33, 33, 33));
                        g.setFont(new Font("Arial", Font.ITALIC, 11));
                        g.drawString(city.getName(), px + 10, py + 5);
                    }
                } else {
                    g.setColor(Color.RED);
                    g.setFont(getFont().deriveFont(Font.BOLD, 26f));
                    g.drawString("Harta nu a fost găsită!", 30, 60);
                }
            }
        };
        mapPanel.setBackground(Color.WHITE);

        split.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeCityPopup();
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeCityPopup();
            }
        });

        split.setLeftComponent(mapPanel);

        JPanel rightPanelMain = new JPanel(new BorderLayout());
        rightPanelMain.setBackground(Color.WHITE);

        JPanel rightPanelCenter = new JPanel();
        rightPanelCenter.setBackground(Color.WHITE);
        rightPanelCenter.setLayout(new BoxLayout(rightPanelCenter, BoxLayout.Y_AXIS));
        rightPanelCenter.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        JLabel selectLabel = new JLabel("Alege orașul:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectLabel.setForeground(Color.BLACK);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanelCenter.add(selectLabel);
        rightPanelCenter.add(Box.createVerticalStrut(15));


        Cities.list=HttpJwtClient.sendToServerForList(null,"/cities","GET",false,true,CityDTO.class);
        for (CityDTO city : Cities.list) {
            System.out.println(city.getName()+city.getPozX()+city.getPozY());
        }
        List<CityDTO> cities = Cities.list;
        citiesCombo = new JComboBox<>(cities.toArray(new CityDTO[0]));
        citiesCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        Dimension comboDim = new Dimension(340, 42);
        citiesCombo.setMaximumSize(comboDim);
        citiesCombo.setPreferredSize(comboDim);
        citiesCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        citiesCombo.setSelectedIndex(-1);
        rightPanelCenter.add(citiesCombo);

        // Modificare renderer pentru colorarea textului în funcție de tipul orașului
        citiesCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CityDTO) {
                    CityDTO city = (CityDTO) value;
                    setText(city.getName());

                    // Nu schimbăm culoarea dacă elementul este selectat
                    if (!isSelected) {
                        // Verificăm dacă există traseu și dacă este random
                        Boolean hasPath = Cities.existaTraseu.get(city.getName());
                        Boolean isRandom = Cities.existaRandom.get(city.getName());

                        if (Boolean.TRUE.equals(hasPath)) {
                            if (Boolean.TRUE.equals(isRandom)) {
                                // Traseu există și este random -> albastru
                                setForeground(new Color(7, 148, 255));
                            } else {
                                // Traseu există și nu este random -> verde
                                setForeground(new Color(0, 180, 50));
                            }
                        } else {
                            // Nu există traseu -> roșu
                            setForeground(new Color(220, 53, 69));
                        }
                    }
                }
                return this;
            }
        });

        rightPanelCenter.add(Box.createVerticalStrut(10));
        JLabel CityDTOLabel = new JLabel("", SwingConstants.CENTER);
        CityDTOLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        CityDTOLabel.setForeground(new Color(80, 80, 80));
        CityDTOLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        CityDTOLabel.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        rightPanelCenter.add(CityDTOLabel);



        citiesCombo.addActionListener(e -> {
            CityDTO selected = (CityDTO) citiesCombo.getSelectedItem();
            if (selected != null) {
                CityDTOLabel.setText("<html><div style='text-align:center;'><b>Coordonate:</b> " + selected.getPozX() + ", " + selected.getPozY() + "<br><b>Info:</b> " + Cities.descriptions.get(selected.getName()) + "</div></html>");
            } else {
                CityDTOLabel.setText("");
            }

        });

        rightPanelCenter.add(Box.createVerticalStrut(30));
        LegendPanel legend = new LegendPanel();
        legend.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanelCenter.add(legend);

        rightPanelMain.add(rightPanelCenter, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);

        // Butonul "Mai departe"
        JButton nextBtn = new JButton("Mai departe");
        styleLandingButton(nextBtn);
        nextBtn.setPreferredSize(comboDim);
        nextBtn.setMinimumSize(comboDim);
        nextBtn.setMaximumSize(comboDim);
        nextBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Butonul "Exportă informații oraș"
        JButton exportBtn = new JButton("Exporta orase");
        styleExportButton(exportBtn);
        exportBtn.setPreferredSize(comboDim);
        exportBtn.setMinimumSize(comboDim);
        exportBtn.setMaximumSize(comboDim);
        exportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportBtn.addActionListener(e->

                {
                    updateComboBoxes();
                    repaint();
                }
        );

        rightPanelMain.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = rightPanelCenter.getWidth();
                int margin = 0;
                int desired = Math.min(Math.max(240, width - margin * 2), 300);
                Dimension newDim = new Dimension(desired, 42);
                citiesCombo.setPreferredSize(newDim);
                citiesCombo.setMaximumSize(newDim);
                nextBtn.setPreferredSize(newDim);
                nextBtn.setMaximumSize(newDim);
                exportBtn.setPreferredSize(newDim);
                exportBtn.setMaximumSize(newDim);
                buttonPanel.revalidate();
                rightPanelCenter.revalidate();
                rightPanelCenter.repaint();
            }
        });

        nextBtn.addActionListener(e -> {
            CityDTO selected = (CityDTO) citiesCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vă rugăm să selectați o localitate înainte de a continua.",
                        "Atenționare",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verificăm dacă orașul selectat are traseu
            Boolean hasPath = Cities.existaTraseu.get(selected.getName());
            if (Boolean.FALSE.equals(hasPath)) {
                JOptionPane.showMessageDialog(this,
                        "Orașul selectat nu are trasee definite.\nVă rugăm să selectați un alt oraș.",
                        "Oraș fără trasee",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Continuăm doar dacă există traseu
            Cities.currentCityId = selected.getId();
            Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
            if (Locations.list.size() > 0) {
                Locations.list = Locations.list.stream().filter(entry -> entry.getIdCity().equals(Cities.currentCityId)).collect(Collectors.toList());

                for (LocationDTO location : Locations.list) {
                    System.out.println(location.getName() + " " + location.getStart() + " " + location.getEnd());
                }
                if (Cities.currentCityId.equals(selected.getRandom()) == true) {
                    if (HttpJwtClient.sendToServer("", "/operations/random", "POST", false, false, boolean.class) != null) {
                        Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
                        if (Locations.list.size() > 0) {
                            Connections.list = HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
                        }
                    }
                }
                onNext.accept(selected);
            }
        });


        JPanel nextBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextBtnPanel.setOpaque(false);
        nextBtnPanel.add(nextBtn);
        buttonPanel.add(nextBtnPanel);

        buttonPanel.add(Box.createVerticalStrut(15));

        JPanel exportBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exportBtnPanel.setOpaque(false);
        exportBtnPanel.add(exportBtn);
        buttonPanel.add(exportBtnPanel);

        buttonPanel.add(Box.createVerticalStrut(15));

        rightPanelMain.add(buttonPanel, BorderLayout.SOUTH);

        split.setRightComponent(rightPanelMain);

        add(split, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int w = getWidth();
                split.setDividerLocation((int)(w * 0.7));
            }
        });
    }


    private void updateComboBoxes() {
        if (HttpJwtClient.sendToServer(null,"/files/cities","POST",false,true,String.class)!=null) {
            citiesCombo.removeAllItems();
            Cities.list.clear();
            Cities.data.clear();
            Cities.currentCityId = 1L;
            Cities.existaRandom = new HashMap<>();
            Cities.existaTraseu = new HashMap<>();
            Cities.descriptions = new HashMap<>();
            Informations.list = new ArrayList<>();
            Informations.locationData = new HashMap<>();
            Informations.cityData = new HashMap<>();
            Connections.list = new ArrayList<>();
            Locations.list = new ArrayList<>();

            Cities.list = HttpJwtClient.sendToServerForList(null, "/cities", "GET", false, true, CityDTO.class);
            if (Cities.list.size() > 0) {
                Informations.list = HttpJwtClient.sendToServerForList(null, "/informations", "GET", false, true, InformationDTO.class);
                if (Informations.list.size() > 0) {
                    for (CityDTO citydto : Cities.list) {
                        Cities.data.put(citydto.getName(), citydto);
                        Cities.existaTraseu.put(citydto.getName(), citydto.getExist());
                        Cities.existaRandom.put(citydto.getName(), citydto.getRandom());
                        System.out.println(citydto.getName());
                    }
                    for (InformationDTO infodto : Informations.list) {
                        String cityName = null;

                        if (infodto.getCity() != false && infodto.getIdCity() != null && Cities.data != null) {
                            CityDTO city = Cities.data.entrySet().stream().filter(entry -> entry.getValue().getId().equals(infodto.getIdCity())).map(Map.Entry::getValue).findFirst().get();
                            cityName = city.getName();
                            if (cityName != null) {
                                Informations.cityData.put(cityName, infodto);
                                Cities.descriptions.put(cityName, infodto.getDescription());
                            }
                        }
                        System.out.println(infodto.getId());
                    }

                    CityDTO aux = Cities.list.get(0);
                    CityDTO nou = new CityDTO(0L, null, null, null, false, false, 0.0, 0.0);
                    if (aux.getExist() == true) {
                        if (aux != null) {
                            citiesCombo.addItem(aux);
                        } else {
                            citiesCombo.addItem(nou);
                        }

                        if (Cities.list != null && !Cities.list.isEmpty()) {
                            for (CityDTO loc : Cities.list) {
                                citiesCombo.addItem(loc);
                            }
                        }
                    }
                }
            }
        }
    }



    private void showCityPopup(CityDTO city, int px, int py, Component parentMapPanel) {
        closeCityPopup();
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180,180,180), 2, true),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("<html><b>" + city.getName() + "</b></html>");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 17));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(8));

        JLabel coordLabel = new JLabel("Lat: " + city.getPozX() + ", Lon: " + city.getPozY());
        coordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        coordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(coordLabel);
        panel.add(Box.createVerticalStrut(10));

        // Modificare pentru descrieri lungi - limitează lățimea la 250 pixeli
        JLabel descLabel = new JLabel("<html><div style='text-align:center; width:250px'>" +
                Cities.descriptions.get(city.getName()) +
                "</div></html>");
        descLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(12));

        JButton closeBtn = new JButton("×");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorder(null);
        closeBtn.setForeground(new Color(7, 148, 255));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> closeCityPopup());
        panel.add(closeBtn);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();

        // Obține dimensiunile ecranului
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Point parentLocation = parentMapPanel.getLocationOnScreen();

        // Calculează poziția inițială propusă
        int proposedX = parentLocation.x + px + 20; // dx = 20
        int proposedY = parentLocation.y + py - dialog.getHeight()/2; // dy = -dialog.getHeight()/2

        // Asigură-te că fereastra nu iese în afara marginilor ecranului
        // Verifică marginea din stânga
        if (proposedX < screenBounds.x) {
            proposedX = screenBounds.x + 5;
        }

        // Verifică marginea din dreapta
        if (proposedX + dialog.getWidth() > screenBounds.x + screenBounds.width) {
            // Încearcă să poziționezi la stânga punctului dacă există spațiu
            if (parentLocation.x + px - dialog.getWidth() - 10 > screenBounds.x) {
                proposedX = parentLocation.x + px - dialog.getWidth() - 10;
            } else {
                // Altfel, aliniază cu marginea dreaptă a ecranului
                proposedX = screenBounds.x + screenBounds.width - dialog.getWidth() - 5;
            }
        }

        // Verifică marginea de sus
        if (proposedY < screenBounds.y) {
            proposedY = screenBounds.y + 5;
        }

        // Verifică marginea de jos
        if (proposedY + dialog.getHeight() > screenBounds.y + screenBounds.height) {
            proposedY = screenBounds.y + screenBounds.height - dialog.getHeight() - 5;
        }

        // Aplică poziția finală calculată
        dialog.setLocation(proposedX, proposedY);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(false);
        dialog.setVisible(true);

        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.addMouseListener(globalFrameClickListener);
        }
        currentCityDialog = dialog;
    }


    private void closeCityPopup() {
        if (currentCityDialog != null) {
            currentCityDialog.dispose();
            currentCityDialog = null;
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.removeMouseListener(globalFrameClickListener);
        }
    }

    private final MouseAdapter globalFrameClickListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) { closeCityPopup(); }
    };
}