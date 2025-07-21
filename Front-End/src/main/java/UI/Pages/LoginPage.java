package UI.Pages;

import UI.Components.BackButton;
import UI.HttpJwtClient;
import staticData.Cities;
import dtos.CityDTO;
import dtos.InformationDTO;
import dtos.UserDTO;
import staticData.Informations;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginPage extends JPanel {
    public LoginPage(Runnable onLogin, Runnable onBack) {
        setLayout(new BorderLayout());

        // Panel pentru săgeată stânga sus
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);

        // Buton de back foarte sus și foarte în stânga, cu padding
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        BackButton backBtn = new BackButton(onBack);
        leftPanel.add(backBtn);
        northPanel.add(leftPanel, BorderLayout.WEST);

        add(northPanel, BorderLayout.PAGE_START);

        // Formularul centrat perfect
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Titlul (logo) RouteSeeker cu spatiu generos deasupra formularului
        JLabel logoLabel = new JLabel("RouteSeeker");
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 40));
        logoLabel.setForeground(new Color(38, 132, 255));

        // Wrapper vertical pentru logo + spatiu + formular
        JPanel verticalWrapper = new JPanel();
        verticalWrapper.setOpaque(false);
        verticalWrapper.setLayout(new BoxLayout(verticalWrapper, BoxLayout.Y_AXIS));
        verticalWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        verticalWrapper.add(Box.createVerticalStrut(20)); // Spatiu de 20px deasupra titlului
        verticalWrapper.add(logoLabel);
        verticalWrapper.add(Box.createVerticalStrut(20)); // Spatiu de 20px intre titlu si formular

        // Card panel (formularul)
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(430, 430));
        cardPanel.setMinimumSize(new Dimension(350, 430));
        cardPanel.setMaximumSize(new Dimension(530, 600));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 18, 0);

        JLabel subtitle = new JLabel("Autentificare");
        subtitle.setFont(new Font("Arial", Font.BOLD, 26));
        subtitle.setForeground(new Color(33, 37, 41));
        cardPanel.add(subtitle, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 15));
        cardPanel.add(userLabel, c);

        c.gridy++;
        JTextField userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 16));
        userField.setPreferredSize(new Dimension(330, 38));
        userField.setMinimumSize(new Dimension(250, 32));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        cardPanel.add(userField, c);

        c.gridy++;
        JLabel passLabel = new JLabel("Parolă");
        passLabel.setFont(new Font("Arial", Font.BOLD, 15));
        cardPanel.add(passLabel, c);

        c.gridy++;
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 16));
        passField.setPreferredSize(new Dimension(330, 38));
        passField.setMinimumSize(new Dimension(250, 32));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        cardPanel.add(passField, c);

        c.gridy++;
        c.insets = new Insets(18, 0, 0, 0);
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.setBackground(new Color(38, 132, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginBtn.setPreferredSize(new Dimension(330, 42));
        cardPanel.add(loginBtn, c);

        // Metodă pentru curățarea field-urilor
        Runnable clearFields = () -> {
            userField.setText("");
            passField.setText("");
        };

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                showTimedDialog(this, "Completează ambele câmpuri pentru a te loga!", 2500);
                clearFields.run();
            } else {
                clearFields.run();
                if (onLogin != null) {
                    UserDTO user=new UserDTO();
                    user.setUsername(username);
                    user.setPassword(password);
if(HttpJwtClient.sendAuthRequest(user,"/auth/login")==true){
   Cities.list=HttpJwtClient.sendToServerForList(null,"/cities","GET",false,true,CityDTO.class);
    Informations.list=HttpJwtClient.sendToServerForList(null,"/informations","GET",false,true,InformationDTO.class);
    for(CityDTO citydto:Cities.list)
    {
        Cities.data.put(citydto.getName(),citydto);
        Cities.existaTraseu.put(citydto.getName(),citydto.getExist());
        Cities.existaRandom.put(citydto.getName(),citydto.getRandom());

        System.out.println(citydto.getName());
    }
    for(InformationDTO infodto:Informations.list)
    {
        String cityName=null;

        if(infodto.getCity()!=false&&infodto.getIdCity()!=null)
        {  CityDTO city=Cities.data.entrySet().stream().filter(entry -> entry.getValue().getId().equals(infodto.getIdCity())).map(Map.Entry::getValue).findFirst().get();
        cityName=city.getName();
        if(cityName!=null) {
          Informations.cityData.put(cityName, infodto);
          Cities.descriptions.put(cityName, infodto.getDescription());
      }}
       System.out.println(infodto.getId());
    }
    showTimedDialog(this, "Te-ai logat cu succes!", 1000);

                    onLogin.run();}
                }
            }
        });

        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalWrapper.add(cardPanel);

        centerWrapper.add(verticalWrapper, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        setBackground(new Color(241, 245, 249));
    }

    public static void showTimedDialog(Component parent, String message, int millis) {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Confirmare", Dialog.ModalityType.APPLICATION_MODAL);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        dialog.getContentPane().add(label);
        dialog.setSize(320, 120);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Timer timer = new Timer(millis, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }
}