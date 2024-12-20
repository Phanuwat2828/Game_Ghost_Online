
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.Socket;

public class C_App extends JFrame {
    private Socket socket;
    private PrintWriter out;

    C_App() {
        Client_setting_ setting = new Client_setting_();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (setting.getCreator()) {

                    try {
                        socket = new Socket(setting.getIp_setting(), 3000);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Remove," + setting.getName() + "," + setting.getIp());
                    } catch (Exception ex) {
                    }
                }
                System.exit(0);
            }
        });
        setTitle("My Window");
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        JPanel cardPanel = new JPanel(new CardLayout());
        Client_Landing firstPage = new Client_Landing(cardPanel);
        Client_Room cardRoom = new Client_Room(cardPanel, setting);
        Devolop dev = new Devolop(cardPanel);
        cardPanel.add(firstPage, "First");
        cardPanel.add(cardRoom, "Room");
        cardPanel.add(dev, "dev");
        add(cardPanel);

    }

    public static void main(String[] args) {
        C_App app = new C_App();
        app.setVisible(true);
    }
}
