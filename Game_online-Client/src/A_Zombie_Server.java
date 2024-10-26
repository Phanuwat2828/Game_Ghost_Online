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

public class A_Zombie_Server extends Thread {
    private Client_setting_ setting;
    private Data data;
    private Create_Data cr;
    private boolean running = true;

    A_Zombie_Server(Client_setting_ setting) {
        this.setting = setting;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(9090)) { // เปิด port 9090 สำหรับ server
            System.out.println("Server Zombie Port 9090");
            data = new Data(setting);
            cr = new Create_Data(data, setting);
            cr.Zombie_Movement();

            while (running) {
                try {
                    // รอการเชื่อมต่อจาก client
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket, data);
                    new Thread(clientHandler).start(); // เริ่ม Thread ใหม่

                } catch (Exception e) {

                }
                Thread.sleep(10);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        try {
            // close resources if necessary
            this.interrupt(); // interrupt the thread if it's waiting
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
            // data.setlevel_now();
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

        }
    }

}

// ส่วนของ Create_Data และ Data จะยังคงเหมือนเดิม
class Create_Data {
    private Data data;
    private Timer timer = new Timer();
    private Client_setting_ setting;

    Create_Data(Data data, Client_setting_ setting) {
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
    private Map<String, Map<String, Object>> monsterData1 = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> monsterData2 = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> monsterData3 = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> monsterData4 = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> monsterData5 = new LinkedHashMap<>();

    private static Random random = new Random();
    private int[] X;
    private int[] Y;
    private int level = 3;
    private Client_setting_ setting;


    Data(Client_setting_ setting) {
        this.setting = setting;
        for(int i=0 ;i<5;i++){
            int count_zombie=0;
            if(i==0){
                count_zombie=25;
            }else if(i==1){
                count_zombie=30;
            }else if(i==2){
                count_zombie=40;
            }else if(i==3){
                count_zombie=45;
            }else if(i==4){
                count_zombie=50;
            }
            X = new int[count_zombie];
            Y = new int[count_zombie];
            for (int k = 0; k < count_zombie; k++) {
                X[k] = random.nextInt(20, 419);
                Y[k] = random.nextInt(250, 650);
            }
            Arrays.sort(Y);
            for(int j=0;j<count_zombie;j++){
                Map<String, Object> data_monster = new HashMap<>();
                Boolean Chance_Drop = Chance_To_Drop();
                Boolean rare = Chance_To_Drop_rare(Chance_Drop);
    
                data_monster.put("position", new int[] { X[j], Y[j] });
                data_monster.put("status", true);
                data_monster.put("Speed", random.nextInt(1, 5));
                data_monster.put("dropped", true);
                data_monster.put("Hp_", 100);
                data_monster.put("Hp_max", 100);
                data_monster.put("Hp_percent", 100);
                data_monster.put("Chance_Drop_rare", rare);
                data_monster.put("Chance_Drop", Chance_Drop);
                data_monster.put("Ready", false);
             
                if(i==0){
                    data_monster.put("position_level", 1);
                    data_monster.put("level","common");
                    monsterData1.put("monster" + (j + 1), data_monster);
                }else if(i==1){
                    data_monster.put("position_level", 2);
                    data_monster.put("level","common");
                    monsterData2.put("monster" + (j + 1), data_monster);
                }else if(i==2){
                    data_monster.put("position_level", 3);
                    data_monster.put("level","common");
                    monsterData3.put("monster" + (j + 1), data_monster);
                }else if(i==3){
                    data_monster.put("position_level", 4);
                    if(j==0){
                        data_monster.put("level","Boss");
                        data_monster.put("Hp_", 3000);
                        data_monster.put("Hp_max", 3000);
                    }else{
                        data_monster.put("level","common");
                    }
                    monsterData4.put("monster" + (j + 1), data_monster);
                }else if(i==4){
                    data_monster.put("position_level", 5);
                    if(j<=1){
                        data_monster.put("level","Boss");
                        data_monster.put("Hp_", 3000);
                        data_monster.put("Hp_max", 3000);
                    }else{
                        data_monster.put("level","common");
                    }
                    monsterData5.put("monster" + (j + 1), data_monster);
                }
            }
            
        }
    }

    public void Zombie_Mange(int Mousex, int Mousey) {
        Map<String, Map<String, Object>> monsterData = getMonsterData();
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
        if(level==1){
            return monsterData1;
        }else if(level==2){
            return monsterData2;
        }else if(level==3){
            return monsterData3;
        }else if(level==4){
            return monsterData4;
        }else{
            return monsterData5;
        }
    }

    public void setPosition(int[] value, String name_monster) {
        if(level==1){
            this.monsterData1.get(name_monster).put("position", value);
        }else if(level==2){
            this.monsterData2.get(name_monster).put("position", value);
        }else if(level==3){
            this.monsterData3.get(name_monster).put("position", value);
        }else if(level==4){
            this.monsterData4.get(name_monster).put("position", value);
        }else{
            this.monsterData5.get(name_monster).put("position", value);
        }
    }

    public void setReady(String name_monster) {
        if(level==1){
            this.monsterData1.get(name_monster).put("Ready", true);
        }else if(level==2){
            this.monsterData2.get(name_monster).put("Ready", true);
        }else if(level==3){
            this.monsterData3.get(name_monster).put("Ready", true);
        }else if(level==4){
            this.monsterData4.get(name_monster).put("Ready", true);
        }else{
            this.monsterData5.get(name_monster).put("Ready", true);
        }
    }

    public void setStatus(String name_monster) {
        if(level==1){
            this.monsterData1.get(name_monster).put("status", true);
        }else if(level==2){
            this.monsterData2.get(name_monster).put("status", true);
        }else if(level==3){
            this.monsterData3.get(name_monster).put("status", true);
        }else if(level==4){
            this.monsterData4.get(name_monster).put("status", true);
        }else{
            this.monsterData5.get(name_monster).put("status", true);
        }

    }

    public void  setlevel_now(){
        if (setting.getReady()) {
            int count =0;
            Map<String, Map<String, Object>> monsterMap = getMonsterData();
            for (Map.Entry<String, Map<String, Object>> entry : monsterMap.entrySet()) {
                Map<String, Object> data_now = entry.getValue();
                Boolean status = (Boolean) data_now.get("status");
                if(!status){
                    count++;
                }
            }
            if(count==25 && level==1){
                level=2;
            }else if(count==30 && level==2){
                level=3;
            }else if(count==40 && level==3){
                level=4;
            }else if(count==45 && level==4){
                level=5;
            }
        }
        
    }
    public int getLevel_now(){
        return level;
    }

}
