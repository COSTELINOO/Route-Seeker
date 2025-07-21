package UI;

import UI.Pages.*;
import dtos.CityDTO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

 public class RouteSeekerUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private Map<String, LocalityPage> cityPages = new HashMap<>();

    public RouteSeekerUI() {
        setTitle("RouteSeeker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        LandingPage landingPage = new LandingPage(
                () -> showPage("login"),
                () -> showPage("register")
        );
        cardPanel.add(landingPage, "landing");

        LoginPage loginPage = new LoginPage(
                () -> showPage("romaniaMap"),
                () -> showPage("landing")
        );
        cardPanel.add(loginPage, "login");

        RegisterPage registerPage = new RegisterPage(
                () -> showPage("romaniaMap"),
                () -> showPage("landing")
        );
        cardPanel.add(registerPage, "register");


        RomaniaMapPage mapPanel = new RomaniaMapPage(

                () -> showPage("landing"),
                this::showLocalityPage
        );
        cardPanel.add(mapPanel, "romaniaMap");

        setContentPane(cardPanel);
        showPage("landing");
        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(cardPanel, pageName);
    }

    public  void showLocalityPage(CityDTO city) {
        String key = "localitate_" + city.getName();
        if (!cityPages.containsKey(key)) {
            LocalityPage locality = new LocalityPage(
                    city,
                    () -> showPage("romaniaMap")
            );
            cityPages.put(key, locality);
            cardPanel.add(locality, key);
        }
        showPage(key);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RouteSeekerUI::new);
    }
}