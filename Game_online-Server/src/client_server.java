import java.net.DatagramSocket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;


public class client_server{
    private static final int Port = 3000;
    private static final int Size_Ob = 65507;
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(Port)) {
            System.out.println("Server_Runing Now in Port "+Port);
            byte[] buffer = new byte[Size_Ob];
            while(true){
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                socket.receive(packet);
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object obj = ois.readObject();
                if(obj instanceof DB_){
                    DB_ data = (DB_) obj ;
                    System.out.println("Call here : " +data);
                    DB_ response = new DB_("Server", "Hello Client ");

                     // แปลงวัตถุเป็นไบต์
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(response);
                    oos.flush();
                    byte[] responseData = baos.toByteArray();

                    // ส่งข้อความตอบกลับ
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length,
                        packet.getAddress(), packet.getPort()
                    );
                    socket.send(responsePacket);
                    
                }
                
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Server Stop: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}