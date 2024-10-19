import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class Server02 {

    private static final Random random = new Random(); // สร้าง Random instance เดียวเพื่อใช้งาน
    private static final int MONSTER_COUNT = 30; // จำนวนมอนสเตอร์

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9090)) { // เปิด port 9090 สำหรับ server
            System.out.println("Server is running...");
            Data data = new Data(MONSTER_COUNT);
            Create_Data cr = new Create_Data(data);
            cr.Zombie_Movement();

            while (true) {
                try {
                    // รอการเชื่อมต่อจาก client
                    Socket socket = serverSocket.accept();

                    // สร้าง Thread ใหม่สำหรับการจัดการ client
                    ClientHandler clientHandler = new ClientHandler(socket, data);
                    new Thread(clientHandler).start(); // เริ่ม Thread ใหม่
                } catch (Exception e) {
                    System.err.println("Error while handling client connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// คลาสสำหรับจัดการ client
class ClientHandler implements Runnable {
    private Socket socket;
    private Data data;

    public ClientHandler(Socket socket, Data data) {
        this.socket = socket;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            // ใช้ BufferedOutputStream เพื่อเพิ่มประสิทธิภาพ
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(data.getMonsterData());
            out.flush(); // ควรเรียก flush เพื่อให้มั่นใจว่าข้อมูลถูกส่งออกไป

            // รับข้อมูลจาก client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Received from client: " + clientMessage);
            }

            // ปิดการเชื่อมต่อ
            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            System.err.println("Error in client handler: " + e.getMessage());
        }
    }
}

// ส่วนของ Create_Data และ Data จะยังคงเหมือนเดิม
class Create_Data {
    private Data data;

    Create_Data(Data data) {
        this.data = data;
    }

    public void Zombie_Movement() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveZombies();
            }
        }, 0, 50);
    }

    public void moveZombies() {
        Map<String, Map<String, Object>> monsterMap = this.data.getMonsterData();
        for (Map.Entry<String, Map<String, Object>> entry : monsterMap.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> data_now = entry.getValue();
            int[] position = (int[]) data_now.get("position");
            Boolean status = (Boolean) data_now.get("status");
            int speed = (int) data_now.get("Speed");
            int[] value = new int[2];
            value[1] = position[1];
            if (status) {
                value[0] = position[0] + speed;
                this.data.setPosition(value, name);
            }
        }
    }
}

class Data {
    private Map<String, Map<String, Object>> monsterData = new LinkedHashMap<>();
    private static Random random = new Random();

    Data(int MONSTER_COUNT) {
        for (int i = 0; i < MONSTER_COUNT; i++) {
            Map<String, Object> data_monster = new HashMap<>();
            Boolean Chance_Drop = Chance_To_Drop();
            Boolean rare = Chance_To_Drop_rare(Chance_Drop);

            data_monster.put("position", new int[] { random.nextInt(20, 419), random.nextInt(250, 650) });
            data_monster.put("status", true);
            data_monster.put("Speed", random.nextInt(1, 5));
            data_monster.put("Hp_", 100);
            data_monster.put("Hp_max", 100);
            data_monster.put("Hp_percent", 100);
            data_monster.put("Chance_Drop_rare", rare);
            data_monster.put("Chance_Drop", Chance_Drop);
            monsterData.put("monster" + (i + 1), data_monster);
        }
    }

    public static Boolean Chance_To_Drop() {
        int chance = random.nextInt(100);
        return chance <= 20;
    }

    public static Boolean Chance_To_Drop_rare(boolean chance_) {
        int chance = random.nextInt(100);
        return chance <= 10 && !chance_;
    }

    public Map<String, Map<String, Object>> getMonsterData() {
        return monsterData;
    }

    public void setPosition(int[] value, String name_monster) {
        this.monsterData.get(name_monster).put("position", value);
    }
}
