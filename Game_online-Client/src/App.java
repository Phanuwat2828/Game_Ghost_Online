
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.Socket;

public class App extends JFrame {
    private Socket socket;
    private PrintWriter out;

    App() {
        setting_ setting = new setting_();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (setting.getCreator()) {

                    try {
                        socket = new Socket("26.12.207.51", 3000);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Remove," + setting.getName() + "," + setting.getIp());
                    } catch (Exception ex) {
                        // TODO: handle exception
                    }
                }
                System.exit(0);
            }
        });
        setTitle("My Window");
        setSize(1920, 1080);
        setLocationRelativeTo(null);

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
