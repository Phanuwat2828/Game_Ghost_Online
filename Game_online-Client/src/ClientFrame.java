import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientFrame extends JFrame {
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;

    public ClientFrame() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sendDataToServer("Hello");
            }
        });

        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // เชื่อมต่อไปยังเซิร์ฟเวอร์
        try {
            socket = new Socket("localhost", 12345); // แก้ไขให้ตรงกับที่อยู่และพอร์ตของเซิร์ฟเวอร์
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ส่งข้อมูลไปยังเซิร์ฟเวอร์
    private void sendDataToServer(String message) {
        if (out != null) {
            out.println(message); // ส่งข้อความไปยังเซิร์ฟเวอร์
        }
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
