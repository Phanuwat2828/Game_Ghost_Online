import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) {
        body body = new body();
        body.setVisible(true);
    }
}

class body extends JFrame {
    body() {
        container container = new container();
        setSize(500, 500);
        setLayout(null);
        // [======== add =============]
        add(container);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class container extends JPanel {

    container() {
        setBounds(0, 0, 500, 500);
        setBackground(Color.GREEN);
        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                DB_ data = new DB_();
                data.setX(e.getX(), e.getY());
                Send_Class send = new Send_Class("Mouse", data);
            }

        });
    }
}

class Send_Class {
    private static final String SERVER = "26.12.207.51";
    private static final int PORT = 3000;
    private static final int BUFFER_SIZE = 65507;

    Send_Class(String content, DB_ message) {
        try (
                DatagramSocket socket = new DatagramSocket();
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress address = InetAddress.getByName(SERVER);
            byte[] buffer = new byte[BUFFER_SIZE];
            InetAddress localHost = InetAddress.getLocalHost();
            String sender = localHost + "";
            String content_ = content;

            System.out.println("Connect Server");
            message.setContent(content_);
            message.setSender(sender);

            // แปลงวัตถุเป็นไบต์
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            byte[] data = baos.toByteArray();

            // ส่ง DatagramPacket ไปยังเซิร์ฟเวอร์
            DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
            socket.send(packet);

            // // รอรับการตอบกลับจากเซิร์ฟเวอร์
            // DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            // socket.receive(responsePacket);

            // // แปลงไบต์กลับเป็นวัตถุ
            // ByteArrayInputStream bais = new
            // ByteArrayInputStream(responsePacket.getData(), 0,
            // responsePacket.getLength());
            // ObjectInputStream ois = new ObjectInputStream(bais);
            // Object responseObj = ois.readObject();

            // // if (responseObj instanceof DB_) {
            // DB_ response = (DB_) responseObj;
            // System.out.println("Send to Server: ");
            // System.out.println("Call Back From Server: " + response);

        } catch (UnknownHostException e) {
            System.out.println("Server Not Fond : " + e.getMessage());
        } catch (IOException ex) {
            System.out.println("Server Not Fond : " + ex.getMessage());
        }
    }

}