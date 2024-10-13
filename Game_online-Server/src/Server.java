import java.net.DatagramSocket;
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
            data.settest(index, new Random().nextInt(1, 11));
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

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(Port)) {
            DB_ response = new DB_("Server", "Hello Client ");
            for (int i = 0; i < 5; i++) {
                theard_format th = new theard_format(response, i);
                th.start();
            }
            System.out.println("Server_Runing Now in Port " + Port);
            byte[] buffer = new byte[Size_Ob];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object obj = ois.readObject();
                // if (obj instanceof DB_) {
                DB_ data = (DB_) obj;
                System.out.println("Call here : " + data);
                // แปลงวัตถุเป็นไบต์
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(response);
                oos.flush();
                byte[] responseData = baos.toByteArray();

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

        } catch (IOException |

                ClassNotFoundException e) {
            System.out.println("Server Stop: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
