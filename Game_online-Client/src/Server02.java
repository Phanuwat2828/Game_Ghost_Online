import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Server02 {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9090)) { // เปิด port 9090 สำหรับ server
            System.out.println("Server is running...");

            while (true) {
                try {
                    // รอการเชื่อมต่อจาก client
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected.");

                    // สร้าง LinkedHashMap เพื่อเก็บข้อมูลมอนสเตอร์
                    Map<String, Map<String, Object>> monsterData = new LinkedHashMap<>();

                    for (int i = 0; i < 20; i++) {
                        Map<String, Object> data_monster = new HashMap<>();
                        data_monster.put("position", new int[] { 100, 200 });
                        data_monster.put("status", true);
                        monsterData.put("monster" + (i + 1), data_monster);
                    }

                    // ส่งข้อมูล Map ผ่าน ObjectOutputStream
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(monsterData);
                    out.flush(); // ควรเรียก flush เพื่อให้มั่นใจว่าข้อมูลถูกส่งออกไป
                    System.out.println("Monster data sent to the client.");

                    // ปิดการเชื่อมต่อ
                    out.close();
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Error while handling client connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
