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

public class VS_Client {
    private static final String SERVER = "26.12.207.51";
    private static final int PORT = 3000;
    private static final int BUFFER_SIZE = 65507; // ขนาดสูงสุดของ UDP packet

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {
            InetAddress address = InetAddress.getByName(SERVER);
            byte[] buffer = new byte[BUFFER_SIZE];
            InetAddress localHost = InetAddress.getLocalHost();
            String sender = localHost+"";
            String content;

            System.out.println("Connect UDP Server");

            while ((content = console.readLine()) != null) {
                if (content.equals("stop")){
                    break;
                }
                // สร้างวัตถุ Message
                DB_ message = new DB_(sender, content);

                // แปลงวัตถุเป็นไบต์
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                oos.flush();
                byte[] data = baos.toByteArray();

                // ส่ง DatagramPacket ไปยังเซิร์ฟเวอร์
                DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
                socket.send(packet);

                // รอรับการตอบกลับจากเซิร์ฟเวอร์
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);

                // แปลงไบต์กลับเป็นวัตถุ
                ByteArrayInputStream bais = new ByteArrayInputStream(responsePacket.getData(), 0, responsePacket.getLength());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object responseObj = ois.readObject();

                if (responseObj instanceof DB_) {
                    DB_ response = (DB_) responseObj;
                System.out.println("Send to Server: "+content);
                    System.out.println("Call Back From Server: " + response);
                }
            
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server Not Fond : " + ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error Connection : " + ex.getMessage());
        }
    }
}
