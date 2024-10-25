import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.util.Arrays;

public class Server02 extends Thread {
    private static final int MONSTER_COUNT = 30; // จำนวนมอนสเตอร์
    private setting_ setting;
    private Data data;
    private Create_Data cr;

    Server02(setting_ setting) {
        this.setting = setting;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(9090)) { // เปิด port 9090 สำหรับ server
            System.out.println("Server is running...");
            data = new Data(MONSTER_COUNT);
            cr = new Create_Data(data, setting);
            cr.Zombie_Movement();

            while (true) {
                try {
                    // รอการเชื่อมต่อจาก client
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket, data);
                    new Thread(clientHandler).start(); // เริ่ม Thread ใหม่

                } catch (Exception e) {
                    System.err.println("Error while handling client connection: " + e.getMessage());
                }
                Thread.sleep(10);
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
                String[] xy = clientMessage.split("[,]");
                data.Zombie_Mange(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));

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
    private Timer timer = new Timer();
    private setting_ setting;

    Create_Data(Data data, setting_ setting) {
        this.data = data;
        this.setting = setting;
    }

    public void Zombie_Movement() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveZombies();
            }
        }, 0, 50);

    }

    public void zombie_stop() {

        timer.cancel();
    }

    public void moveZombies() {
        if (setting.getReady()) {
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
                this.data.setReady(name);
            }
        }

    }
}

class Data {
    private Map<String, Map<String, Object>> monsterData = new LinkedHashMap<>();
    private static Random random = new Random();
    private int[] X;
    private int[] Y;

    Data(int MONSTER_COUNT) {
        X = new int[MONSTER_COUNT];
        Y = new int[MONSTER_COUNT];
        for (int i = 0; i < MONSTER_COUNT; i++) {
            X[i] = random.nextInt(20, 419);
            Y[i] = random.nextInt(250, 650);
        }
        Arrays.sort(Y);
        for (int i = 0; i < MONSTER_COUNT; i++) {
            Map<String, Object> data_monster = new HashMap<>();
            Boolean Chance_Drop = Chance_To_Drop();
            Boolean rare = Chance_To_Drop_rare(Chance_Drop);

            data_monster.put("position", new int[] { X[i], Y[i] });
            data_monster.put("status", true);
            data_monster.put("Speed", random.nextInt(1, 5));
            data_monster.put("dropped", true);
            data_monster.put("Hp_", 100);
            data_monster.put("Hp_max", 100);
            data_monster.put("Hp_percent", 100);
            data_monster.put("Chance_Drop_rare", rare);
            data_monster.put("Chance_Drop", Chance_Drop);
            data_monster.put("Ready", false);
            monsterData.put("monster" + (i + 1), data_monster);

        }
    }

    public void Zombie_Mange(int Mousex, int Mousey) {
        for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> data_now = entry.getValue();
            Boolean status = (Boolean) data_now.get("status");
            int[] position = (int[]) data_now.get("position");
            int Hp = (int) data_now.get("Hp_");
            int max = (int) data_now.get("Hp_max");

            int x = position[0];
            int y = position[1];

            if (status) {
                if (Mousex >= x && Mousex <= x + 100 &&
                        Mousey >= y && Mousey <= y + 100) {
                    // Dropped_item[i] = true;
                    monsterData.get(name).put("Hp_", Hp - 20);
                    monsterData.get(name).put("Hp_percent", (Hp * 100) / max);
                    // Percent_HP[i] = (Health[i] * 100) / Max_HP[i];
                    if (Hp - 20 <= 0) {
                        monsterData.get(name).put("status", false);
                    }
                }

            }
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

    public void setReady(String name_monster) {
        this.monsterData.get(name_monster).put("Ready", true);
    }

    public void setStatus(String name_monster) {
        this.monsterData.get(name_monster).put("status", true);
    }

}
