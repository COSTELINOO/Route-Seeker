package UI.Pages;

import javax.swing.*;
import java.awt.*;

public class LandingPage extends JPanel {
    public LandingPage(Runnable onLogin, Runnable onRegister) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Titlu aplicație
        JLabel title = new JLabel("RouteSeeker");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(38, 132, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(60, 0, 40, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        JLabel subtitle = new JLabel("Bine ai venit! Alege o opțiune:");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 22));
        subtitle.setForeground(new Color(60, 60, 60));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 30, 0);
        add(subtitle, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 40, 0));
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 22));
        loginBtn.setBackground(new Color(38, 132, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        loginBtn.addActionListener(e -> onLogin.run());

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 22));
        registerBtn.setBackground(new Color(60, 180, 112));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        registerBtn.addActionListener(e -> onRegister.run());

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(buttonPanel, gbc);

        setBackground(new Color(241, 245, 249));
    }

    // Adaugă o metodă main pentru a putea rula standalone această pagină!
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("LandingPage Preview");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(600, 400);
            f.setLocationRelativeTo(null);
            LandingPage page = new LandingPage(
                    () -> JOptionPane.showMessageDialog(f, "Login apăsat!"),
                    () -> JOptionPane.showMessageDialog(f, "Register apăsat!")
            );
            f.setContentPane(page);
            f.setVisible(true);
        });
    }
}