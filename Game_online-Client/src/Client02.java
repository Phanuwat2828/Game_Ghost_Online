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
                int Speed = (int) data.get("Speed");
                int Hp_ = (int) data.get("Hp_");
                int Hp_percent = (int) data.get("Hp_percent");
                int Hp_max = (int) data.get("Hp_max");
                Boolean Chance_Drop = (Boolean) data.get("Chance_Drop");
                Boolean Chance_Drop_rare = (Boolean) data.get("Chance_Drop_rare");
                System.out.println("=================== " + name + " =======================");
                System.out.println("Position: [" + position[0] + ", " + position[1] + "]");
                System.out.println("Status: " + status);
                System.out.println("Speed: " + Speed);
                System.out.println("HP: " + Hp_ + "/" + Hp_max + " (" + Hp_percent + "%)");
                System.out.println("Chance to Drop: " + Chance_Drop);
                System.out.println("Chance to Drop Rare: " + Chance_Drop_rare);
                System.out.println("=========================================================");
            }

            // ปิดการเชื่อมต่อ
            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
