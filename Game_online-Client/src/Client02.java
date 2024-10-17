import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;

public class Client02 {
    public static void main(String[] args) {
        try {
            // เชื่อมต่อไปยัง server ที่ port 12345
            Socket socket = new Socket("localhost", 9090);
            System.out.println("Connected to the server.");

            // รับข้อมูล Map ผ่าน ObjectInputStream
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Map<String, Map<String, Object>> monsterData = (Map<String, Map<String, Object>>) in.readObject();

            // แสดงข้อมูลมอนสเตอร์แต่ละตัว
            for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
                String name = entry.getKey();
                Map<String, Object> data = entry.getValue();
                int[] position = (int[]) data.get("position");
                boolean status = (boolean) data.get("status");

                System.out.println(
                        "Name: " + name + ", X: " + position[0] + ", Y: " + position[1] + ", Status: " + status);
            }

            // ปิดการเชื่อมต่อ
            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
