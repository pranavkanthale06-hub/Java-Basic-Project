import gui.LoginFrame;
import services.DataService;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataService dataService = new DataService();
            new LoginFrame(dataService).setVisible(true);
        });
    }
}
