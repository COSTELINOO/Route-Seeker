package UI.Components;

import javax.swing.*;
import java.awt.*;

public class BackButton extends JButton {
    public BackButton(Runnable onBack) {
        super();
        setPreferredSize(new Dimension(48, 48));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setToolTipText("Înapoi");
        setMargin(new Insets(0,0,0,0));
        addActionListener(e -> { if (onBack != null) onBack.run(); });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundal rotund
        int pad = 0; // desenăm de la 0, dar paddingul e dat de container
        int diam = Math.min(getWidth(), getHeight());
        g2.setColor(new Color(230, 230, 230));
        g2.fillOval(pad, pad, diam, diam);

        // Săgeată spre stânga, centrată cu padding intern
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int len = 14;
        int arrowPad = 3; // padding intern față de cerc

        // corp săgeată
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(80, 80, 80));
        g2.drawLine(cx + len/2 - arrowPad, cy, cx - len/2 + arrowPad, cy);

        // vârf săgeată
        int[] xPoints = {cx - len/2 + arrowPad + 5, cx - len/2 + arrowPad, cx - len/2 + arrowPad + 5};
        int[] yPoints = {cy - 5, cy, cy + 5};
        g2.drawPolyline(xPoints, yPoints, 3);

        // Contur cerc
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(pad, pad, diam - 1, diam - 1);

        g2.dispose();
    }
}