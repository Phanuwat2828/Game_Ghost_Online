
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class App extends JFrame {
    App() {
        setTitle("My Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setting_ setting = new setting_();

        JPanel cardPanel = new JPanel(new CardLayout());
        first_page firstPage = new first_page(cardPanel);
        RoomZombieGUI cardRoom = new RoomZombieGUI(cardPanel, setting);
        cardPanel.add(firstPage, "First");
        cardPanel.add(cardRoom, "Room");

        add(cardPanel);

    }

    public static void main(String[] args) {
        App app = new App();
        app.setVisible(true);
    }
}
