package UI.Pages;

import UI.Components.BackButton;
import UI.HttpJwtClient;
import dtos.UserDTO;
import staticData.Cities;
import dtos.CityDTO;
import dtos.InformationDTO;
import staticData.Informations;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RegisterPage extends JPanel {
    public RegisterPage(Runnable onRegister, Runnable onBack) {
        setLayout(new BorderLayout());

        // Buton back sus-stanga
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        BackButton backBtn = new BackButton(onBack);
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Formular centrat
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Titlul RouteSeeker cu spațiu deasupra și sub el, deasupra formularului
        JLabel logoLabel = new JLabel("RouteSeeker");
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 40));
        logoLabel.setForeground(new Color(38, 132, 255));

        JPanel verticalWrapper = new JPanel();
        verticalWrapper.setOpaque(false);
        verticalWrapper.setLayout(new BoxLayout(verticalWrapper, BoxLayout.Y_AXIS));
        verticalWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        verticalWrapper.add(Box.createVerticalStrut(20)); // Spațiu de 20px deasupra titlului
        verticalWrapper.add(logoLabel);
        verticalWrapper.add(Box.createVerticalStrut(20)); // Spațiu de 20px între titlu și formular

        // Card panel (formularul)
        JPanel cardPanel =
                new JPanel() {
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

        JLabel title = new JLabel("Înregistrare");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));
        cardPanel.add(title, c);

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
        JLabel confirmLabel = new JLabel("Confirmă parola");
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 15));
        cardPanel.add(confirmLabel, c);

        c.gridy++;
        JPasswordField confirmField = new JPasswordField();
        confirmField.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmField.setPreferredSize(new Dimension(330, 38));
        confirmField.setMinimumSize(new Dimension(250, 32));
        confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        cardPanel.add(confirmField, c);

        c.gridy++;
        c.insets = new Insets(18, 0, 0, 0);
        JButton registerBtn = new JButton("Înregistrare");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 20));
        registerBtn.setBackground(new Color(38, 132, 255));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        registerBtn.setPreferredSize(new Dimension(330, 42));
        cardPanel.add(registerBtn, c);

        // Metodă pentru curățarea field-urilor
        Runnable clearFields = () -> {
            userField.setText("");
            passField.setText("");
            confirmField.setText("");
        };

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirm = new String(confirmField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showTimedDialog(this, "Completează toate câmpurile!", 2500);
                clearFields.run();
            } else if (!password.equals(confirm)) {
                showTimedDialog(this, "Parolele nu coincid!", 2000);
                clearFields.run();
            } else {





                        clearFields.run();
                        if (onRegister != null)
                           {
                               UserDTO user=new UserDTO();
                               user.setUsername(username);
                               user.setPassword(password);
                               if(HttpJwtClient.sendAuthRequest(user,"/auth/register")==true){
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
                                   showTimedDialog(this, "Înregistrare cu succes!", 1000);
                               onRegister.run();}

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
        dialog.setSize(340, 120);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Timer timer = new Timer(millis, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }
}