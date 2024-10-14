import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

public class Server {

    public static void main(String[] args) {
        Server_Thread Server = new Server_Thread();
        Server.start();
    }
}

class theard_format extends Thread {
    private DB_ data;
    private int index;

    theard_format(DB_ data, int index) {
        this.data = data;
        this.index = index;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}

/**
 * InnerServer
 */
class Server_Thread extends Thread {
    private static final int Port = 3000;
    private static final int Size_Ob = 65507;
    private Data_Server data_Server = new Data_Server();

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(Port)) {

            byte[] buffer = new byte[Size_Ob];
            while (true) {
                DB_ response = new DB_();
                response.setSender("Server");
                response.setContent(" : 200");
                System.out.println("Server_Runing Now in Port " + Port);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object obj = ois.readObject();
                // แปลงวัตถุเป็นไบต์
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(response);
                oos.flush();
                byte[] responseData = baos.toByteArray();
                // if (obj instanceof DB_) {
                DB_ data = (DB_) obj;
                System.out.println("Call here : " + data);
                if (data.getContent().equals("Mouse")) {
                    String ip = packet.getAddress().toString();
                    ip.replace("/", "");
                    data_Server.setclientPositions(packet.getAddress().toString(), data.getXy());
                    // sendtoAll(data_Server, socket, Port, data.getSender(), response,
                    // responseData, packet);
                    System.out.println(data_Server.getMouse_p(packet.getAddress().toString())[0] + " : "
                            + data_Server.getMouse_p(packet.getAddress().toString())[1]);
                    sendtoAll(data_Server, socket, Port, packet.getAddress().toString());
                }

                // ส่งข้อความตอบกลับ

                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length,
                        packet.getAddress(), packet.getPort());
                socket.send(responsePacket);

                // }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {

                }

            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Server Stop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendtoAll(Data_Server data, DatagramSocket socket, int Port, String ipsender) {
        Map<String, int[]> data_send = data.getClientPositions();
        for (Map.Entry<String, int[]> entry : data_send.entrySet()) {
            String ip = entry.getKey();
            if (!ip.equals(ipsender)) {
                try {
                    DB_ response = new DB_();
                    response.setSender("Server");
                    response.setContent(ip + " : 200");
                    response.setX(data.getMouse_p(ip)[0], data.getMouse_p(ip)[1]);
                    // แปลงวัตถุเป็นไบต์
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(response);
                    oos.flush();
                    byte[] responseData = baos.toByteArray();
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseData, responseData.length,
                            InetAddress.getByName(ip), Port);
                    socket.send(responsePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
