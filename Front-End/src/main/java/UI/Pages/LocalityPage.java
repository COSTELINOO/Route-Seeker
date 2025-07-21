package UI.Pages;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import UI.HttpJwtClient;
import dtos.*;
import staticData.*;

import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class LocalityPage extends JPanel {

    private CityDTO city;
    private JLabel messageLabel;
    private JComboBox<LocationDTO> startCombo, finishCombo;
    private JButton regenBtn;
    private Boolean cycle=false,minPath=false,maxPath=false;
    private BufferedImage img;
    private int imageWidth, imageHeight;
    private int imageX, imageY;
    private JDialog currentLocationDialog = null; // Pentru pop-up-uri de locații

    private static final double LAT_MIN = 47.11, LAT_MAX = 47.19;
    private static final double LON_MIN = 27.54, LON_MAX = 27.68;

    public LocalityPage(CityDTO city, Runnable onBack) {
        this.city = city;

        loadImage();

        if (Cities.existaRandom.get(city.getName())==false) {
            try {
                Long cityId;
                try {
                    // Încercăm să luăm un ID din apropierea orașului
                    if ("Iași".equals(city.getName())) {
                        cityId = 25L;
                    } else if ("București".equals(city.getName())) {
                        cityId = 10L;
                    } else if ("Cluj-Napoca".equals(city.getName())) {
                        cityId = 15L;
                    } else if ("Timișoara".equals(city.getName())) {
                        cityId = 20L;
                    } else {
                        // ID default pentru alte orașe
                        cityId = 1L;
                    }
                } catch (Exception e) {
                    // Dacă nu reușim, folosim un ID implicit
                    cityId = 1L;
                }

            } catch (Exception e) {
                // Dacă avem erori, inițializăm o listă goală
                Locations.list = new ArrayList<>();
                System.err.println("Eroare la încărcarea locațiilor: " + e.getMessage());
            }
        } else {
            HttpJwtClient.sendToServer("","/operations/random/"+Cities.currentCityId,"POST",false,false,boolean.class);
            Locations.list=HttpJwtClient.sendToServerForList(null,"/locations","GET",false,true,LocationDTO.class);
            Connections.list=HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header cu buton Înapoi
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel label = new JLabel("Harta localității: " + city.getName(), SwingConstants.CENTER);
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

        // Split pane principal: stânga (imagine), dreapta (controale)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7); // 70% imagine, 30% controale
        split.setContinuousLayout(true);
        split.setDividerSize(3);
        split.setBorder(null);

        // Panel stânga: imagine cu puncte
        JPanel mapPanel = new JPanel() {
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
                    imageX = (panelW - drawW) / 2;
                    imageY = (panelH - drawH) / 2;
                    imageWidth = drawW;
                    imageHeight = drawH;

                    // Desenează imaginea
                    g.drawImage(img, imageX, imageY, imageWidth, imageHeight, null);

                    // Desenează punctele pe hartă
                    drawLocations((Graphics2D)g);
                    g.setColor(Color.blue);

                    if(cycle==true) {
                        g.setColor(Color.red);
                        drawLines((Graphics2D) g);
                        cycle=false;
                    }
                    else if(minPath==true)
                    {
                        g.setColor(new Color(36, 161, 115));
                        drawLines((Graphics2D) g);
                        minPath=false;
                    }
                    else if (maxPath==true)
                    {
                        g.setColor( new Color(225, 170, 0));
                        drawLines((Graphics2D) g);
                        maxPath=false;
                    }
                    else
                    {
                        Connections.list=HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
                        g.setColor(Color.blue);
                        drawLines((Graphics2D) g);
                    }
                } else {
                    g.setColor(Color.RED);
                    g.setFont(getFont().deriveFont(Font.BOLD, 24f));
                    g.drawString("Harta nu este disponibilă!", 30, 60);
                }
            }
        };

        // Adaugă mouse listener pentru interacțiunea cu punctele de pe hartă
        mapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Verifică dacă click-ul a fost pe un punct
                checkLocationClick(e.getX(), e.getY(), mapPanel);
            }
        });

        mapPanel.setBackground(Color.WHITE);
        split.setLeftComponent(mapPanel);


        // Panel dreapta: controale (selectoare și butoane)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(30, 18, 20, 18));

        controlsPanel.add(Box.createVerticalStrut(10));
        JLabel startLbl = new JLabel("Selectează punctul de start:");
        startLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsPanel.add(startLbl);

        // Combo box pentru puncte de start, va fi populat când avem locațiile
        startCombo = new JComboBox<>();
        startCombo.setMaximumSize(new Dimension(260, 38));
        startCombo.setAlignmentX(Component.CENTER_ALIGNMENT);


        controlsPanel.add(startCombo);
        controlsPanel.add(Box.createVerticalStrut(16));


        JLabel finishLbl = new JLabel("Selectează punctul de finish:");
        finishLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsPanel.add(finishLbl);

        // Combo box pentru puncte de finish, va fi populat când avem locațiile
        finishCombo = new JComboBox<>();
        finishCombo.setMaximumSize(new Dimension(260, 38));
        finishCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsPanel.add(finishCombo);
        controlsPanel.add(Box.createVerticalStrut(30));

        // Dimensiune butoane uniformă
        Dimension btnDim = new Dimension(220, 42);

        JButton minBtn = new JButton("Drumul minim");
        styleNiceButton(minBtn, new Color(44, 201, 144), new Color(36, 161, 115));
        minBtn.setPreferredSize(btnDim);
        minBtn.setMaximumSize(btnDim);
        minBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        minBtn.addActionListener(e -> minimPath()); // true pentru drum minim

        JButton maxBtn = new JButton("Drumul maxim");
        styleNiceButton(maxBtn, new Color(255, 193, 7), new Color(225, 170, 0));
        maxBtn.setPreferredSize(btnDim);
        maxBtn.setMaximumSize(btnDim);
        maxBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        maxBtn.addActionListener(e -> maximPath()); // false pentru drum maxim

        JButton cycleBtn = new JButton("Ciclu");
        styleNiceButton(cycleBtn, new Color(220, 53, 69), new Color(185, 39, 52));
        cycleBtn.setPreferredSize(btnDim);
        cycleBtn.setMaximumSize(btnDim);
        cycleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cycleBtn.addActionListener(e -> {
            showCycle();
        });

        regenBtn = new JButton("Regenerează");
        styleNiceButton(regenBtn, new Color(38, 132, 255), new Color(0, 96, 192));
        regenBtn.setPreferredSize(btnDim);
        regenBtn.setMaximumSize(btnDim);
        regenBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regenBtn.addActionListener(e ->
        {
            // Corectare: adăugare slash între "random" și ID
            HttpJwtClient.sendToServer("","/operations/random/" + Cities.currentCityId, "POST", false, false, boolean.class);
            Locations.list=HttpJwtClient.sendToServerForList(null,"/locations","GET",false,true,LocationDTO.class);
            Connections.list=HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
            regeneratePoints();
        });

        // Butoane export
        JButton exportLocationsBtn = new JButton("Exporta locatii");
        styleExportButton(exportLocationsBtn, new Color(40, 167, 69), new Color(33, 136, 56));
        exportLocationsBtn.setPreferredSize(btnDim);
        exportLocationsBtn.setMaximumSize(btnDim);
        exportLocationsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportLocationsBtn.addActionListener(e -> {
            // Implementare pentru endpoint-ul "POST","/files/locations"
            if (HttpJwtClient.sendFileRequest("/files/locations")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informațiile au fost exportate cu succes!",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE
                );

                Locations.list=HttpJwtClient.sendToServerForList(null,"/locations","GET",false,true,LocationDTO.class);
                Locations.list=Locations.list.stream().filter(es->(es.getIdCity()==city.getId())).collect(Collectors.toList());
                Connections.list=HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
                updateComboBoxes();
                repaint();
            }
        });

        JButton exportConnectionBtn = new JButton("Exporta conexiuni");
        styleExportButton(exportConnectionBtn, new Color(108, 117, 125), new Color(90, 98, 104));
        exportConnectionBtn.setPreferredSize(btnDim);
        exportConnectionBtn.setMaximumSize(btnDim);
        exportConnectionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportConnectionBtn.addActionListener(e -> {
            // Implementare pentru endpoint-ul "POST","/files/connections"
            if (HttpJwtClient.sendFileRequest("/files/connections")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informațiile au fost importate cu succes!",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE
                );

                Locations.list=HttpJwtClient.sendToServerForList(null,"/locations","GET",false,true,LocationDTO.class);
                Locations.list=Locations.list.stream().filter(ec->(ec.getIdCity().equals(city.getId()))).collect(Collectors.toList());
                Connections.list=HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);
                updateComboBoxes();
                repaint();
            }
        });

        controlsPanel.add(minBtn);
        controlsPanel.add(Box.createVerticalStrut(14));
        controlsPanel.add(maxBtn);
        controlsPanel.add(Box.createVerticalStrut(14));
        controlsPanel.add(cycleBtn);
        controlsPanel.add(Box.createVerticalStrut(14));
        controlsPanel.add(regenBtn);
        controlsPanel.add(Box.createVerticalStrut(14));
        controlsPanel.add(exportLocationsBtn);
        controlsPanel.add(Box.createVerticalStrut(14));
        controlsPanel.add(exportConnectionBtn);
        controlsPanel.add(Box.createVerticalStrut(18));

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(new Color(220, 53, 69));
        controlsPanel.add(messageLabel);

        split.setRightComponent(controlsPanel);
        add(split, BorderLayout.CENTER);

        // Ajustare automată a split-ului la resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int w = getWidth();
                split.setDividerLocation((int)(w * 0.7));
            }
        });

        // Adăugăm mouse listener pentru a închide popup-ul când se face click în altă parte
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                closeLocationPopup();
            }
        });

        // Inițializează interfața în funcție de tipul orașului
        initializeByCity();
    }

    /**
     * Verifică dacă utilizatorul a făcut click pe un punct de locație
     */
    private void checkLocationClick(int mouseX, int mouseY, Component parent) {
        if (Locations.list == null || Locations.list.isEmpty()) {
            return;
        }

        // Închide orice popup existent
        closeLocationPopup();

        // Verifică dacă click-ul a fost pe vreun punct
        for (int i = 0; i < Locations.list.size(); i++) {
            LocationDTO loc = Locations.list.get(i);
            Point p = latLonToPoint(loc.getPozX(), loc.getPozY());

            // Verifică dacă click-ul a fost în aria punctului (raza de 10 pixeli)
            int size = 16;
            int radius = size / 2;
            double distance = Math.sqrt(Math.pow(mouseX - p.x, 2) + Math.pow(mouseY - p.y, 2));

            if (distance <= radius) {
                // A fost găsit un punct - afișează popup
                showLocationPopup(loc, p.x, p.y, parent);
                return;
            }
        }
    }

    /**
     * Afișează un popup cu informații despre locația selectată
     */
    private void showLocationPopup(LocationDTO location, int px, int py, Component parent) {
        closeLocationPopup();
        JDialog popup = new JDialog(SwingUtilities.getWindowAncestor(this));
        popup.setUndecorated(true);

        // Panel principal pentru conținut
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(38, 132, 255), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Titlu locație
        JLabel titleLabel = new JLabel(location.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(titleLabel, BorderLayout.NORTH);

        // Informații despre locație
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Coordonate
        JLabel coordLabel = new JLabel("<html><b>Coordonate:</b> " +
                String.format("%.4f", location.getPozX()) + ", " +
                String.format("%.4f", location.getPozY()) + "</html>");
        coordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(coordLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        // ID
        JLabel idLabel = new JLabel("<html><b>ID:</b> " + location.getId() + "</html>");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        // Obține informațiile locației de la server
        String description = "Informație nedisponibilă";
        String locationType = "Necunoscut";
        boolean isDangerZone = false;

        try {
            // Obține toate informațiile de la server
            List<InformationDTO> allInfos = HttpJwtClient.sendToServerForList(null, "/informations", "GET", false, true, InformationDTO.class);

            if (allInfos != null) {
                // Filtrează pentru a găsi informația asociată cu această locație
                InformationDTO locationInfo = allInfos.stream()
                        .filter(info -> info.getIdLocation() != null && info.getIdLocation().equals(location.getId()))
                        .findFirst()
                        .orElse(null);

                if (locationInfo != null) {
                    // Folosește informațiile obținute de la server
                    description = locationInfo.getDescription();
                    locationType = locationInfo.getType();
                    isDangerZone = locationInfo.getDangerZone();
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la obținerea informațiilor: " + e.getMessage());
        }

        // Adaugă descrierea la popup - versiunea îmbunătățită pentru text lung
        if (description != null && !description.equals("No Description")) {
            // Folosim HTML pentru a formata descrierea pe mai multe rânduri, cu o lățime maximă
            JLabel descLabel = new JLabel("<html><b>Descriere:</b><div width='250'>" + description + "</div></html>");
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(descLabel);
            infoPanel.add(Box.createVerticalStrut(5));
        }

        // Adaugă tipul locației - același model de formatare
        if (locationType != null && !locationType.equals("Unknown")) {
            JLabel typeLocationLabel = new JLabel("<html><b>Categoria locației:</b><div width='250'>" + locationType + "</div></html>");
            typeLocationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(typeLocationLabel);
            infoPanel.add(Box.createVerticalStrut(5));
        }

        // Tip punct (Start/End)
        String typeTxt = "";
        if (location.getStart() != null && location.getStart()) {
            typeTxt = "Punct de start";
        } else if (location.getEnd() != null && location.getEnd()) {
            typeTxt = "Punct de finish";
        } else {
            typeTxt = "Punct intermediar";
        }

        JLabel typeLabel = new JLabel("<html><b>Tip punct:</b> " + typeTxt + "</html>");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        // Zona de pericol (obținută de la server)
        JLabel dangerLabel = new JLabel("<html><b>Zonă de risc:</b> " + (isDangerZone ? "DA" : "NU") + "</html>");
        dangerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dangerLabel.setForeground(isDangerZone ? new Color(220, 53, 69) : new Color(40, 167, 69));
        infoPanel.add(dangerLabel);

        content.add(infoPanel, BorderLayout.CENTER);

        // Butoane pentru setare ca start/finish
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        // Buton pentru setare ca start
        JButton setStartBtn = new JButton("Setează ca start");
        setStartBtn.setBackground(new Color(40, 167, 69));
        setStartBtn.setForeground(Color.WHITE);
        setStartBtn.setFocusPainted(false);
        setStartBtn.addActionListener(e -> {
            try {
                // Resetează toate punctele de start existente
                for (LocationDTO existingLocation : Locations.list) {
                    if (existingLocation.getStart() != null && existingLocation.getStart()) {
                        existingLocation.setStart(false);
                        HttpJwtClient.sendToServer(
                                existingLocation,
                                "/locations/" + existingLocation.getId(),
                                "PUT",
                                true,
                                false,
                                LocationDTO.class
                        );
                    }
                }

                // Setează noul punct de start
                location.setStart(true);
                location.setEnd(false);

                LocationDTO result = HttpJwtClient.sendToServer(
                        location,
                        "/locations/" + location.getId(),
                        "PUT",
                        true,
                        true,
                        LocationDTO.class
                );

                if (result != null) {
                    // Actualizează lista locală
                    Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
                    Locations.list = Locations.list.stream()
                            .filter(es-> es.getIdCity().equals(city.getId()))
                            .collect(Collectors.toList());

                    // Feedback pentru utilizator
                    JOptionPane.showMessageDialog(
                            popup,
                            "Locația a fost setată ca punct de start.",
                            "Operație reușită",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Actualizează combobox-urile și interfața
                    updateComboBoxes();
                    repaint();
                }
            } catch (Exception ex) {
                System.err.println("Eroare la setarea punctului de start: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        popup,
                        "Eroare la setarea punctului de start.",
                        "Eroare",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            popup.dispose();
        });

        // Buton pentru setare ca finish
        JButton setFinishBtn = new JButton("Setează ca finish");
        setFinishBtn.setBackground(new Color(220, 53, 69));
        setFinishBtn.setForeground(Color.WHITE);
        setFinishBtn.setFocusPainted(false);
        setFinishBtn.addActionListener(e -> {
            try {
                // Resetează toate punctele de finish existente
                for (LocationDTO existingLocation : Locations.list) {
                    if (existingLocation.getEnd() != null && existingLocation.getEnd()) {
                        existingLocation.setEnd(false);
                        HttpJwtClient.sendToServer(
                                existingLocation,
                                "/locations/" + existingLocation.getId(),
                                "PUT",
                                true,
                                false,
                                LocationDTO.class
                        );
                    }
                }

                // Setează noul punct de finish
                location.setStart(false);
                location.setEnd(true);

                LocationDTO result = HttpJwtClient.sendToServer(
                        location,
                        "/locations/" + location.getId(),
                        "PUT",
                        true,
                        true,
                        LocationDTO.class
                );

                if (result != null) {
                    // Actualizează lista locală
                    Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
                    Locations.list = Locations.list.stream()
                            .filter(em -> em.getIdCity().equals(city.getId()))
                            .collect(Collectors.toList());

                    // Feedback pentru utilizator
                    JOptionPane.showMessageDialog(
                            popup,
                            "Locația a fost setată ca punct de finish.",
                            "Operație reușită",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Actualizează combobox-urile și interfața
                    updateComboBoxes();
                    repaint();
                }
            } catch (Exception ex) {
                System.err.println("Eroare la setarea punctului de finish: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        popup,
                        "Eroare la setarea punctului de finish.",
                        "Eroare",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            popup.dispose();
        });

        JButton closeBtn = new JButton("Închide");
        closeBtn.setBackground(new Color(108, 117, 125));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> popup.dispose());

        buttonPanel.add(setStartBtn);
        buttonPanel.add(setFinishBtn);
        buttonPanel.add(closeBtn);

        content.add(buttonPanel, BorderLayout.SOUTH);

        popup.setContentPane(content);
        popup.pack();

        // Obține dimensiunile ecranului
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Point parentLocation = parent.getLocationOnScreen();

        // Calculează poziția inițială propusă
        int proposedX = parentLocation.x + px + 10;
        int proposedY = parentLocation.y + py - popup.getHeight()/2;

        // Asigură-te că fereastra nu iese în afara marginilor ecranului
        // Verifică marginea din stânga
        if (proposedX < screenBounds.x) {
            proposedX = screenBounds.x + 5;
        }

        // Verifică marginea din dreapta
        if (proposedX + popup.getWidth() > screenBounds.x + screenBounds.width) {
            // Încearcă să poziționezi la stânga punctului dacă există spațiu
            if (parentLocation.x + px - popup.getWidth() - 10 > screenBounds.x) {
                proposedX = parentLocation.x + px - popup.getWidth() - 10;
            } else {
                // Altfel, aliniază cu marginea dreaptă a ecranului
                proposedX = screenBounds.x + screenBounds.width - popup.getWidth() - 5;
            }
        }

        // Verifică marginea de sus
        if (proposedY < screenBounds.y) {
            proposedY = screenBounds.y + 5;
        }

        // Verifică marginea de jos
        if (proposedY + popup.getHeight() > screenBounds.y + screenBounds.height) {
            proposedY = screenBounds.y + screenBounds.height - popup.getHeight() - 5;
        }

        // Aplică poziția finală calculată
        popup.setLocation(proposedX, proposedY);
        popup.setVisible(true);
        currentLocationDialog = popup;
    }

    private void closeLocationPopup() {
        if (currentLocationDialog != null && currentLocationDialog.isVisible()) {
            currentLocationDialog.dispose();
            currentLocationDialog = null;
        }
    }

    /**
     * Metoda pentru încărcarea imaginii din Base64 string
     */
    private void loadImage() {
        String base64 = city.getImage();
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                this.img = ImageIO.read(bais);

                if (this.img == null) {
                    System.err.println("Nu s-a putut crea imaginea din datele Base64");
                }
            } catch (Exception e) {
                System.err.println("Eroare la decodarea imaginii: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("String-ul Base64 pentru imagine este null sau gol");
        }
    }

    /**
     * Metoda pentru desenarea liniilor între locații
     */
    private void drawLines(Graphics2D g) {
        for(ConnectionDTO c: Connections.list) {
            LocationDTO i1= Locations.list.stream().filter(entry->entry.getId().equals(c.getIdInt())).findFirst().orElse(null);
            LocationDTO i2=Locations.list.stream().filter(entry->entry.getId().equals(c.getIdExt())).findFirst().orElse(null);
            if(i1!=null&& i2!=null) {
                Point p1=latLonToPoint(i1.getPozX(), i1.getPozY());
                Point p2=latLonToPoint(i2.getPozX(), i2.getPozY());
                g.setStroke(new BasicStroke(3));
                g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                drawArrowHead(g, (int) p2.x, (int) p2.y, (int) p1.x, (int) p1.y);
            }
        }
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(30);  // unghiul săgeții
        int barb = 15;                    // lungimea "vârfurilor"

        double dx = x2 - x1, dy = y2 - y1;
        double theta = Math.atan2(dy, dx);
        double x, y;

        for (int i = -1; i <= 1; i += 2) {
            x = x2 - barb * Math.cos(theta + i * phi);
            y = y2 - barb * Math.sin(theta + i * phi);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x2, y2, (int) x, (int) y);
        }
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Convertește coordonatele lat/lon în puncte pe imagine
     */
    private Point latLonToPoint(double lat, double lon) {
        double x = (lon - LON_MIN) / (LON_MAX - LON_MIN) * imageWidth;
        if(x<0) {
            x=x*(-1);
        }
        double y = (LAT_MAX - lat) / (LAT_MAX - LAT_MIN) * imageHeight;
        if(y<0) {
            y=y*(-1);
        }
        if(x>imageWidth)
            x=imageWidth-10;
        if(y>imageHeight)
            y=imageHeight-10;
        return new Point((int) x + imageX, (int) y + imageY);
    }

    /**
     * Desenează toate punctele de locație pe hartă
     */
    private void drawLocations(Graphics2D g) {
        if (Locations.list == null || Locations.list.isEmpty()) {
            return;
        }

        // Adaugă antialiasing pentru text și forme
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pentru fiecare punct
        for (int i = 0; i < Locations.list.size(); i++) {
            LocationDTO loc = Locations.list.get(i);
            Point p = latLonToPoint(loc.getPozX(), loc.getPozY());

            // Desenează cadru special pentru punctele de start/finish
            if (loc.getStart() != null && loc.getStart()) {
                // Cadru pentru punct de start (verde)
                g.setColor(new Color(40, 167, 69));
                g.fillOval(p.x - 10, p.y - 10, 20, 20);
            } else if (loc.getEnd() != null && loc.getEnd()) {
                // Cadru pentru punct de finish (roșu)
                g.setColor(new Color(220, 53, 69));
                g.fillOval(p.x - 10, p.y - 10, 20, 20);
            }

            // Desenează buline pentru puncte
            int size = 16;
            g.setColor(new Color(38, 132, 255));
            g.fillOval(p.x - size/2, p.y - size/2, size, size);
            g.setColor(Color.WHITE);
            g.drawOval(p.x - size/2, p.y - size/2, size, size);

            // Adaugă numerele punctelor
            g.setColor(Color.WHITE);
            g.setFont(getFont().deriveFont(Font.BOLD, 10f));
            String nr = String.valueOf(i+1);
            FontMetrics fm = g.getFontMetrics();
            int strW = fm.stringWidth(nr);
            int strH = fm.getAscent();
            g.drawString(nr, p.x - strW/2, p.y + strH/2 - 1);

            // Adaugă numele locației sub punct (opțional)
            g.setColor(new Color(33, 37, 41));
            g.setFont(getFont().deriveFont(Font.BOLD, 11f));
            int nameW = fm.stringWidth(loc.getName());
            g.drawString(loc.getName(), p.x - nameW/2, p.y + size + 12);
        }
    }

    /**
     * Inițializează interfața în funcție de tipul orașului (predefinit sau random)
     */
    private void initializeByCity() {
        // Verifică dacă orașul este predefinit (nu este random)
        if (!Cities.existaRandom.get(city.getName())) {
            // Dezactivează butonul de regenerare pentru orașe predefinite
            regenBtn.setEnabled(false);
            messageLabel.setText("Acest oraș are locații predefinite.");

            // Obține locațiile pentru oraș
            try {
                // Asigură-te că avem locații pentru acest oraș
                if (Locations.list.isEmpty()) {
                    Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
                    Locations.list = Locations.list.stream()
                            .filter(e -> e.getIdCity().equals(city.getId()))
                            .collect(Collectors.toList());
                }

                LocationDTO al = (LocationDTO) startCombo.getSelectedItem();
                if (al != null && al.getId() != null) {
                    al.setStart(true);
                    HttpJwtClient.sendToServer(al, "/locations/" + al.getId(), "PUT", true, false, LocationDTO.class);
                }
            } catch (Exception e) {
                System.err.println("Eroare la inițializarea locațiilor predefinite: " + e.getMessage());
                e.printStackTrace();
                messageLabel.setText("Eroare la încărcarea locațiilor. Încearcă din nou.");
            }
            // Inițializează punctele și combo box-urile pentru start/finish
            updateComboBoxes();
        } else {
            // Activează butonul de regenerare pentru orașe random
            regenBtn.setEnabled(true);
            messageLabel.setText("Acest oraș are locații generate aleatoriu.");

            // Generează puncte inițiale pentru orașele random
            try {
                // Verifică dacă există deja locații pentru acest oraș
                if (Locations.list.isEmpty() || Locations.list.stream()
                        .noneMatch(e -> e.getIdCity().equals(city.getId()))) {

                    // Solicită generarea de locații aleatorii
                    Boolean result = HttpJwtClient.sendToServer("", "/operations/random/"+Cities.currentCityId, "POST", false, false, Boolean.class);

                    if (result != null && result) {
                        // Obține locațiile generate
                        Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);

                        // Filtrează locațiile pentru orașul curent
                        Locations.list = Locations.list.stream()
                                .filter(e -> e.getIdCity().equals(city.getId()))
                                .collect(Collectors.toList());

                        // Obține conexiunile generate
                        Connections.list = HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);

                        // Afișează mesaj de succes
                        messageLabel.setText("Locații generate cu succes pentru: " + city.getName());
                        System.out.println("Locații generate: " + Locations.list.size());
                    } else {
                        messageLabel.setText("Eroare la generarea locațiilor aleatorii!");
                    }
                } else {
                    // Folosește locațiile existente dar actualizează conexiunile
                    Connections.list = HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);

                    // Verifică dacă există un punct de start setat
                    if (startCombo.getSelectedItem() != null && startCombo.getSelectedItem() instanceof LocationDTO) {
                        LocationDTO selected = (LocationDTO) startCombo.getSelectedItem();
                        if (selected.getId() != null) {
                            // Găsește locația în lista actualizată
                            LocationDTO locationToUpdate = Locations.list.stream()
                                    .filter(l -> l.getId().equals(selected.getId()))
                                    .findFirst()
                                    .orElse(null);

                            if (locationToUpdate != null) {
                                locationToUpdate.setStart(true);
                                HttpJwtClient.sendToServer(
                                        locationToUpdate,
                                        "/locations/" + locationToUpdate.getId(),
                                        "PUT",
                                        true,
                                        false,
                                        LocationDTO.class
                                );
                            }
                        }
                    }
                }

                // Actualizează interfața
                regeneratePoints();
            } catch (Exception e) {
                System.err.println("Eroare la inițializarea locațiilor random: " + e.getMessage());
                e.printStackTrace();
                messageLabel.setText("Eroare la generarea locațiilor. Încearcă din nou.");
            }
        }
    }

    /**
     * Regenerează puncte aleatorii pentru orașul curent (dacă este de tip random)
     */
    private void regeneratePoints() {
        try {
            Cities.currentCityId = city.getId();

            // Solicită regenerarea punctelor aleatorii
            Boolean result = HttpJwtClient.sendToServer("", "/operations/random/"+Cities.currentCityId, "POST", false, false, Boolean.class);

            if (result != null && result) {
                // Obține locațiile actualizate
                List<LocationDTO> allLocations = HttpJwtClient.sendToServerForList(
                        null,
                        "/locations",
                        "GET",
                        false,
                        true,
                        LocationDTO.class
                );

                // Filtrează pentru orașul curent
                List<LocationDTO> newLocations = allLocations.stream()
                        .filter(entry -> entry.getIdCity().equals(Cities.currentCityId))
                        .collect(Collectors.toList());

                // Actualizează lista locală
                Locations.list = new ArrayList<>();
                for (LocationDTO loc : newLocations) {
                    Locations.list.add(loc);
                }

                // Actualizează conexiunile
                Connections.list = HttpJwtClient.sendToServerForList(null, "/connections", "GET", false, true, ConnectionDTO.class);

                // Mesaj de confirmare
                messageLabel.setText("Locații regenerate cu succes!");
            } else {
                messageLabel.setText("Eroare la regenerarea locațiilor!");
            }

            // Actualizează datele în combo box-uri
            updateComboBoxes();

            // Actualizează vizual
            repaint();
        } catch (Exception ex) {
            System.err.println("Eroare la regenerarea punctelor: " + ex.getMessage());
            ex.printStackTrace();
            messageLabel.setText("Eroare la regenerarea locațiilor. Încearcă din nou.");
        }
    }

    /**
     * Actualizează combo box-urile cu punctele disponibile
     */
    private void updateComboBoxes() {
        try {
            startCombo.removeAllItems();
            finishCombo.removeAllItems();

            // Verifică dacă lista de locații există și nu este goală
            if (Locations.list == null || Locations.list.isEmpty()) {
                // Adaugă un item gol în combo box-uri pentru a evita NullPointerException
                LocationDTO empty = new LocationDTO(null, null, "Selectați o locație", 0.0, 0.0, false, false);
                startCombo.addItem(empty);
                finishCombo.addItem(empty);
                return;
            }

            // Găsește punctul de start și finish
            LocationDTO startLocation = Locations.list.stream()
                    .filter(entry -> entry.getStart() != null && entry.getStart().equals(true))
                    .findFirst()
                    .orElse(null);

            LocationDTO endLocation = Locations.list.stream()
                    .filter(entry -> entry.getEnd() != null && entry.getEnd().equals(true))
                    .findFirst()
                    .orElse(null);

            // Adaugă la combobox-uri
            if (startLocation != null) {
                startCombo.addItem(startLocation);
            } else {
                LocationDTO empty = new LocationDTO(null, null, "Selectați punctul de start", 0.0, 0.0, false, false);
                startCombo.addItem(empty);
            }

            if (endLocation != null) {
                finishCombo.addItem(endLocation);
            } else {
                LocationDTO empty = new LocationDTO(null, null, "Selectați punctul de finish", 0.0, 0.0, false, false);
                finishCombo.addItem(empty);
            }

            // Adaugă restul locațiilor
            for (LocationDTO loc : Locations.list) {
                // Verifică dacă locația nu este deja punct de start
                if (loc.getStart() == null || !loc.getStart()) {
                    startCombo.addItem(loc);
                }

                // Verifică dacă locația nu este deja punct de finish
                if (loc.getEnd() == null || !loc.getEnd()) {
                    finishCombo.addItem(loc);
                }
            }
        } catch (Exception ex) {
            System.err.println("Eroare la actualizarea combo box-urilor: " + ex.getMessage());
            ex.printStackTrace();
            messageLabel.setText("Eroare la actualizarea interfeței. Încearcă din nou.");
        }
    }

    private void showCycle() {
        try {
            Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
            Locations.list = Locations.list.stream().filter(e -> (e.getIdCity().equals(city.getId()))).collect(Collectors.toList());

            CityDTO currentCity = Cities.data.get(city.getName());
            if (currentCity == null) {
                messageLabel.setText("Eroare: Nu s-a putut identifica orașul curent");
                return;
            }

            String cityName = currentCity.getName();
            messageLabel.setText("Se caută cicluri în orașul " + cityName + "...");
            System.out.println("------" + currentCity.getName());

            LocationDTO locationDTO = startCombo.getItemAt(startCombo.getSelectedIndex());
            System.out.println("------" + locationDTO.getName() + "    " + locationDTO.getId());

            if (locationDTO != null) {
                locationDTO.setStart(true);
                HttpJwtClient.sendToServer(locationDTO, "/locations/" + locationDTO.getId(), "PUT", true, true, LocationDTO.class);
                System.out.println(locationDTO.getStart() + "-----");
            }

            // Folosim URL-ul corect cu ID-ul orașului în loc de nume
            PathDTO response = HttpJwtClient.sendToServer(
                    null,
                    "/operations/cycle/" + Cities.list.stream()
                            .filter(e -> e.getId().equals(Cities.currentCityId))
                            .findFirst()
                            .orElse(null), // Folosim ID-ul în loc de nume
                    "GET",
                    false,
                    true,
                    PathDTO.class
            );

            System.out.println("->>>>" + cityName);

            if (response == null || response.getNames() == null || response.getNames().isEmpty()) {
                messageLabel.setText("Nu s-a găsit niciun ciclu în acest oraș");
                return;
            }

            List<String> path = response.getNames();
            System.out.println("CICLU GĂSIT: " + String.join(" -> ", path));

            // Afișează un dialog cu informații despre ciclu
            JOptionPane.showMessageDialog(
                    this,
                    "Ciclu găsit: " + String.join(" -> ", path),
                    "Informații ciclu",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Creăm o nouă listă de conexiuni pentru ciclu
            List<ConnectionDTO> cycleConnections = new ArrayList<>();
            Long id = 1L;

            // Verifică dacă putem construi ciclul din locațiile existente
            List<LocationDTO> locations = new ArrayList<>();
            boolean canBuildCycle = true;
            for (String locationName : path) {
                LocationDTO aux = Locations.list.stream().filter(entry -> entry.getName().equals(locationName)).findFirst().orElse(null);
                if (aux == null) {
                    canBuildCycle = false;
                    messageLabel.setText("Eroare: Nu toate locațiile din ciclu există în listă");
                    break;
                }
                locations.add(aux);
            }

            if (canBuildCycle) {
                // Construim conexiunile pentru ciclu
                for (int i = 0; i < path.size() - 1; i++) {
                    String currentLocation = path.get(i);
                    String nextLocation = path.get(i + 1);

                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(currentLocation))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(nextLocation))
                            .findFirst().orElse(null);

                    if (loc1 != null && loc2 != null) {
                        cycleConnections.add(new ConnectionDTO(id++, loc2.getId(), loc1.getId()));
                    }
                }

                Connections.list = cycleConnections;
                if (path.size() >= 2) {
                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 2)))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 1)))
                            .findFirst().orElse(null);

                    Connections.list.add(new ConnectionDTO(id, loc2.getId(), loc1.getId()));
                }

                this.cycle = true;
                repaint();
            }
        } catch (Exception ex) {
            messageLabel.setText("Eroare la afișarea ciclului: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void maximPath() {
        try {
            Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
            Locations.list = Locations.list.stream().filter(e -> (e.getIdCity().equals(city.getId()))).collect(Collectors.toList());

            CityDTO currentCity = Cities.data.get(city.getName());
            if (currentCity == null) {
                messageLabel.setText("Eroare: Nu s-a putut identifica orașul curent");
                return;
            }

            String cityName = currentCity.getName();
            messageLabel.setText("Se caută drumul maxim în orașul " + cityName + "...");

            LocationDTO locationSt = startCombo.getItemAt(startCombo.getSelectedIndex());
            LocationDTO locationFi = finishCombo.getItemAt(finishCombo.getSelectedIndex());

            if (locationSt != null && locationFi != null) {
                locationSt.setStart(true);
                locationSt.setEnd(false);
                locationFi.setStart(false);
                locationFi.setEnd(true);
                HttpJwtClient.sendToServer(locationSt, "/locations/" + locationSt.getId(), "PUT", true, true, LocationDTO.class);
                HttpJwtClient.sendToServer(locationFi, "/locations/" + locationFi.getId(), "PUT", true, true, LocationDTO.class);
            }

            // Folosim URL-ul corect cu ID-ul orașului în loc de nume
            PathDTO response = HttpJwtClient.sendToServer(
                    null,
                    "/operations/longest/" + Cities.list.stream()
                            .filter(e -> e.getId().equals(Cities.currentCityId))
                            .findFirst()
                            .orElse(null), // Folosim ID-ul în loc de nume
                    "GET",
                    false,
                    true,
                    PathDTO.class
            );

            if (response == null || response.getNames() == null || response.getNames().isEmpty()) {
                messageLabel.setText("Nu s-a găsit niciun drum în acest oraș");
                return;
            }

            List<String> path = response.getNames();

            // Afișează un dialog cu informații despre drumul maxim
            JOptionPane.showMessageDialog(
                    this,
                    "Drum maxim găsit: " + String.join(" -> ", path),
                    "Informații drum maxim",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Creăm o nouă listă de conexiuni pentru drumul maxim
            List<ConnectionDTO> pathConnections = new ArrayList<>();
            Long id = 1L;

            List<LocationDTO> locations = new ArrayList<>();
            boolean havePath = true;
            for (String locationName : path) {
                LocationDTO aux = Locations.list.stream().filter(entry -> entry.getName().equals(locationName)).findFirst().orElse(null);
                if (aux == null) {
                    havePath = false;
                    messageLabel.setText("Eroare: Nu toate locațiile din path există în listă");
                    break;
                }
                locations.add(aux);
            }

            if (havePath) {
                // Construim conexiunile pentru drumul maxim
                for (int i = 0; i < path.size() - 1; i++) {
                    String currentLocation = path.get(i);
                    String nextLocation = path.get(i + 1);

                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(currentLocation))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(nextLocation))
                            .findFirst().orElse(null);

                    if (loc1 != null && loc2 != null) {
                        pathConnections.add(new ConnectionDTO(id++, loc2.getId(), loc1.getId()));
                    }
                }
                Connections.list = pathConnections;
                if (path.size() >= 2) {
                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 2)))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 1)))
                            .findFirst().orElse(null);

                    Connections.list.add(new ConnectionDTO(id, loc2.getId(), loc1.getId()));
                }
                this.maxPath = true;
                repaint();
            }
        } catch (Exception ex) {
            messageLabel.setText("Eroare la afișarea drumului: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void minimPath() {
        try {
            Locations.list = HttpJwtClient.sendToServerForList(null, "/locations", "GET", false, true, LocationDTO.class);
            Locations.list = Locations.list.stream().filter(e -> (e.getIdCity().equals(city.getId()))).collect(Collectors.toList());

            CityDTO currentCity = Cities.data.get(city.getName());
            if (currentCity == null) {
                messageLabel.setText("Eroare: Nu s-a putut identifica orașul curent");
                return;
            }

            String cityName = currentCity.getName();
            messageLabel.setText("Se caută drumul minim în orașul " + cityName + "...");
            System.out.println("------" + currentCity.getName());

            LocationDTO locationSt = startCombo.getItemAt(startCombo.getSelectedIndex());
            LocationDTO locationFi = finishCombo.getItemAt(finishCombo.getSelectedIndex());

            if (locationSt != null && locationFi != null) {
                locationSt.setStart(true);
                locationSt.setEnd(false);
                locationFi.setStart(false);
                locationFi.setEnd(true);
                HttpJwtClient.sendToServer(locationSt, "/locations/" + locationSt.getId(), "PUT", true, true, LocationDTO.class);
                HttpJwtClient.sendToServer(locationFi, "/locations/" + locationFi.getId(), "PUT", true, true, LocationDTO.class);
            }

            // Folosim URL-ul corect cu ID-ul orașului în loc de nume
            PathDTO response = HttpJwtClient.sendToServer(
                    null,
                    "/operations/shortest/" + Cities.list.stream()
                            .filter(e -> e.getId().equals(Cities.currentCityId))
                            .findFirst()
                            .orElse(null), // Folosim ID-ul în loc de nume
                    "GET",
                    false,
                    true,
                    PathDTO.class
            );

            if (response == null || response.getNames() == null || response.getNames().isEmpty()) {
                messageLabel.setText("Nu s-a găsit niciun drum în acest oraș");
                return;
            }

            List<String> path = response.getNames();

            // Afișează un dialog cu informații despre drumul minim
            JOptionPane.showMessageDialog(
                    this,
                    "Drum minim găsit: " + String.join(" -> ", path),
                    "Informații drum minim",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Creăm o nouă listă de conexiuni pentru drum
            List<ConnectionDTO> pathConnections = new ArrayList<>();
            Long id = 1L;

            List<LocationDTO> locations = new ArrayList<>();
            boolean havePath = true;
            for (String locationName : path) {
                LocationDTO aux = Locations.list.stream().filter(entry -> entry.getName().equals(locationName)).findFirst().orElse(null);
                if (aux == null) {
                    havePath = false;
                    messageLabel.setText("Eroare: Nu toate locațiile din path există în listă");
                    break;
                }
                locations.add(aux);
            }

            if (havePath) {
                // Construim conexiunile pentru drum
                for (int i = 0; i < path.size() - 1; i++) {
                    String currentLocation = path.get(i);
                    String nextLocation = path.get(i + 1);

                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(currentLocation))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(nextLocation))
                            .findFirst().orElse(null);

                    if (loc1 != null && loc2 != null) {
                        pathConnections.add(new ConnectionDTO(id++, loc2.getId(), loc1.getId()));
                    }
                }
                Connections.list = pathConnections;
                if (path.size() >= 2) {
                    LocationDTO loc1 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 2)))
                            .findFirst().orElse(null);

                    LocationDTO loc2 = Locations.list.stream()
                            .filter(entry -> entry.getName().equals(path.get(path.size() - 1)))
                            .findFirst().orElse(null);

                    Connections.list.add(new ConnectionDTO(id, loc2.getId(), loc1.getId()));
                }

                this.minPath = true;
                repaint();
            }
        } catch (Exception ex) {
            messageLabel.setText("Eroare la afișarea drumului: " + ex.getMessage());
            ex.printStackTrace();
        }
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

    // Metodă utilitară pentru stilizarea butoanelor de export
    private void styleExportButton(JButton btn, Color main, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(main);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(main.darker(), 2, true),
                BorderFactory.createEmptyBorder(6, 0, 6, 0))
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
}