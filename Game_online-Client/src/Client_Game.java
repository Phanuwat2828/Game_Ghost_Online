import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;

public class Client_Game extends JPanel {
    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "Image";
    Image image_bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background.png");
    String path_png = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "png";
    String path_gif = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "Gif";
    Image image_gif = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Zombie_walk.gif");
    Image item_Ammo = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Ammo_gif.gif");
    Image rare_item = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Ammo_1.gif");
    Image bullet = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Ammo_2.png");
    Image TextGameOver = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "GameOver.gif");
    Image wink = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Wink2.gif");
    Image Border2 = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Border2.PNG");
    Image Border3 = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Border3.PNG");

    String pathSound = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "sound";
    File audioFile_shoot = new File(pathSound + File.separator + "pistol-shot-233473.wav");
    File audioFile_Items = new File(pathSound + File.separator + "item-pick-up-38258.wav");

    ZombieThread[] zombieThreads = new ZombieThread[30];
    Random rand = new Random();
    int Count_Wave = 30;
    boolean ready = false;
    boolean exit_game = false;

    // Var
    int[] axisX = new int[Count_Wave];
    int[] axisY = new int[Count_Wave];
    int[] speedX = new int[Count_Wave];
    boolean[] Status_Zombie = new boolean[Count_Wave];
    Image[] zombie_action_walk = new Image[10];
    int[] Health = new int[Count_Wave];
    int[] Max_HP = new int[Count_Wave];
    int[] Percent_HP = new int[Count_Wave];
    int[] Damage = new int[Count_Wave];
    Timer timer;
    boolean[] Chance_Drop = new boolean[30];
    boolean[] Chance_Drop_rare = new boolean[30];
    boolean[] Dropped_item = new boolean[30];
    boolean GameOver = false;
    boolean GameWin = false;
    boolean AddBullet = false;
    boolean ready_ = false;
    int bullets = 20;
    int amountBullet;
    int CountDead = 0;
    int mouseX = 0;
    int mouseY = 0;

    // =========================== to_server
    private String SERVER_IP; // เปลี่ยนเป็น IP ของ Server ถ้าไม่ใช่ localhost
    private int SERVER_PORT1 = 8000;
    private int SERVER_PORT2 = 9090;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket2;
    private PrintWriter out2;
    private Socket socket3;
    private PrintWriter out3;
    private B_Mouse_Server server_01;
    private A_Zombie_Server server02;
    private Map<Integer, Point> remoteMousePositions = Collections.synchronizedMap(new HashMap<>());
    private int clientId = -1; // ใช้ในการระบุว่าเป็น Client ตัวไหน
    // ===========================

    MediaTracker tracker = new MediaTracker(this);

    public Client_Game(JPanel cardLayout, Client_setting_ setting) {
        if (setting.getCreator()) {
            server_01 = new B_Mouse_Server(setting);
            server_01.start();
            server02 = new A_Zombie_Server(setting);
            server02.start();
        }

        this.SERVER_IP = setting.getIp();
        setSize(1920, 1080);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        panel.setPreferredSize(new Dimension(1920, 100));
        panel.setOpaque(false);
        // panel.setBackground(Color.BLUE);
        JButton bt_s = new JButton("Start");

        JButton bt_e = new JButton("Exit");
        bt_s.setPreferredSize(new Dimension(100, 50));
        bt_e.setPreferredSize(new Dimension(100, 50));
        bt_e.setForeground(Color.WHITE);
        bt_s.setForeground(Color.WHITE);
        bt_s.setBackground(Color.green);
        bt_e.setBackground(Color.RED);

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT1);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    try {

                        mouseX = e.getX();
                        mouseY = e.getY();
                        if (clientId != -1) { // ตรวจสอบว่ามี clientId แล้ว
                            String msg = "MOVE," + e.getX() + "," + e.getY();
                            out.println(msg);
                            out.flush();
                            // ไม่ต้องอัพเดตตำแหน่งเมาส์ของตัวเองที่นี่ เพราะจะได้รับจาก Server
                        }
                    } catch (Exception ex) {
                        // TODO: handle exception
                    }

                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {

                    // System.out.println("test");
                    if (bullets > 0) {
                        Sound(audioFile_shoot);
                        if (ready_) {
                            socket2 = new Socket(SERVER_IP, SERVER_PORT2);
                            out2 = new PrintWriter(socket2.getOutputStream(), true);
                            out2.println(e.getX() + "," + e.getY());
                        }
                    }
                    if (ready_) {
                        getItem(e.getX(), e.getY());
                        Bullets_Manage(-1, null);
                    }

                    repaint();
                } catch (Exception ex) {
                    // TODO: handle exception
                }
            }
        });

        try {
            socket3 = new Socket(setting.getIp_setting(), 3000);
            out3 = new PrintWriter(socket3.getOutputStream(), true);
            bt_e.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (setting.getCreator()) {
                        try {
                            out3.println("Remove," + setting.getName() + "," + setting.getIp());
                            setting.setReady(false);
                            setting.setCreator(false);
                            server02.stopServer();
                            server_01.stopServer();
                        } catch (Exception ex) {
                            // TODO: handle exception
                        }
                    }
                    CardLayout cl = (CardLayout) (cardLayout.getLayout());
                    cl.show(cardLayout, "Room"); // สลับไปยัง Room

                }

            });
        } catch (Exception e) {
            // TODO: handle exception
        }

        bt_s.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setting.setReady(true);

                } catch (Exception ex) {

                }
            }

        });

        if (setting.getCreator()) {
            panel.add(bt_s);
        }
        panel.add(bt_e);

        add(panel);

        img_zombie_walk();

        System.out.println(setting.getIp());

        recive_data th = new recive_data(this, setting, cardLayout);
        th.start();

        new Thread(new IncomingReader(cardLayout)).start();

    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public void setAll_data(int index, int x, int y, int speed, boolean status, int hp, int max_hp, int percent_hp,
            boolean Chance_Drop_, boolean Chance_Drop_rare_, boolean dropped, boolean ready) {
        axisX[index] = x;
        axisY[index] = y;
        speedX[index] = speed;
        Status_Zombie[index] = status;
        Health[index] = hp;
        Max_HP[index] = max_hp;
        Percent_HP[index] = percent_hp;
        Chance_Drop[index] = Chance_Drop_;
        Chance_Drop_rare[index] = Chance_Drop_rare_;
        Dropped_item[index] = dropped;
        ready_ = ready;
        // System.out.println("=================== " + index +
        // "=======================");
        // System.out.println("Position: [" + position[0] + ", " + position[1] + "]");
        // System.out.println("Status: " + status);
        // System.out.println("Speed: " + speed);
        // System.out.println("HP: " + hp_ + "/" + hp_max + " (" + hp_percent + "%)");
        // System.out.println("Chance to Drop: " + chanceDrop);
        // System.out.println("Chance to Drop Rare: " + chanceDropRare);
        // System.out.println("=========================================================");
    }

    public void setAll_data02(int index, int x, int y, int speed, boolean status, int hp, int max_hp, int percent_hp,
            boolean ready) {
        axisX[index] = x;
        axisY[index] = y;
        speedX[index] = speed;
        Status_Zombie[index] = status;
        Health[index] = hp;
        Max_HP[index] = max_hp;
        Percent_HP[index] = percent_hp;
        ready_ = ready;
        // System.out.println("=================== " + index +
        // "=======================");
        // System.out.println("Position: [" + position[0] + ", " + position[1] + "]");
        // System.out.println("Status: " + status);
        // System.out.println("Speed: " + speed);
        // System.out.println("HP: " + hp_ + "/" + hp_max + " (" + hp_percent + "%)");
        // System.out.println("Chance to Drop: " + chanceDrop);
        // System.out.println("Chance to Drop Rare: " + chanceDropRare);
        // System.out.println("=========================================================");
    }

    class IncomingReader implements Runnable {
        private JPanel cardlayout;

        IncomingReader(JPanel card) {
            this.cardlayout = card;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // ประมวลผลข้อความที่ได้รับ
                    // คาดว่า message มีรูปแบบ "ID,clientId" หรือ "MOVE,clientId,x,y" หรือ
                    // "DISCONNECT,clientId"
                    String[] parts = message.split(",");
                    if (parts.length >= 2) {
                        switch (parts[0]) {
                            case "ID":
                                // กำหนด clientId ของตัวเอง
                                clientId = Integer.parseInt(parts[1]);
                                System.out.println("ได้รับ clientId ของตัวเอง: " + clientId);
                                break;
                            case "MOVE":
                                if (parts.length == 4) {
                                    int id = Integer.parseInt(parts[1]);
                                    int x = Integer.parseInt(parts[2]);
                                    int y = Integer.parseInt(parts[3]);
                                    updateRemoteMouse(id, x, y);
                                }
                                break;
                            case "DISCONNECT":
                                if (parts.length == 2) {
                                    int id = Integer.parseInt(parts[1]);
                                    removeRemoteMouse(id);
                                    CardLayout cl = (CardLayout) cardlayout.getLayout();
                                    cl.show(cardlayout, "Room"); // เปลี่ยนไปยังหน้า "Room"
                                }
                                break;
                            default:
                                System.out.println("ข้อความที่ไม่รู้จัก: " + message);
                        }
                    }
                }
            } catch (IOException e) {
                CardLayout cl = (CardLayout) (cardlayout.getLayout());
                cl.show(cardlayout, "Room"); // สลับไปยัง Room
            }
        }
    }

    private void updateRemoteMouse(int id, int x, int y) {
        remoteMousePositions.put(id, new Point(x, y));
        this.repaint();
    }

    // ลบตำแหน่งเมาส์เมื่อ Client อื่น Disconnect
    private void removeRemoteMouse(int id) {
        remoteMousePositions.remove(id);
        this.repaint();
    }

    public void img_zombie_walk() {
        for (int k = 0; k < 10; k++) {
            zombie_action_walk[k] = Toolkit.getDefaultToolkit()
                    .createImage(path_png + File.separator + "Zombie_walk" + (k + 1) + ".png");
            tracker.addImage(zombie_action_walk[k], k);
        }
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // public void startZombieThreads() {
    //     for (int i = 0; i < 30; i++) {
    //         zombieThreads[i] = new ZombieThread(i, this);
    //         zombieThreads[i].start();
    //     }
    // }

    public void stopAllZombies() {
        for (int i = 0; i < 30; i++) {
            if (zombieThreads[i] != null) {
                zombieThreads[i].stopZombie();
                try {
                    zombieThreads[i].join(); // รอให้ thread หยุดทำงาน
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public boolean getItem(int MouseAxisX, int MouseAxisY) {
        boolean status = true;
        for (int i = 0; i < 30; i++) {
            if (!Status_Zombie[i] && Dropped_item[i]) {
                if (MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 70 &&
                        MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 70) {
                    status = false;
                    Bullets_Manage(1, null);
                    if (Chance_Drop_rare[i]) {
                        Bullets_Manage(20, null);
                    } else if (Chance_Drop[i]) {
                        Bullets_Manage(10, null);
                    }
                    Dropped_item[i] = false;
                    break;
                }
            }
        }
        return status;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1920, 1080, this);
        BulletBar(g);
        synchronized (remoteMousePositions) {
            for (Map.Entry<Integer, Point> entry : remoteMousePositions.entrySet()) {
                int id = entry.getKey();
                Point p = entry.getValue();
                if (id == clientId) {
                    g.setColor(Color.WHITE); // สีสำหรับเมาส์ของตัวเอง
                } else {
                    g.setColor(Color.GREEN); // สีสำหรับเมาส์ของ Client อื่น
                }
                g.fillOval(p.x - 5, p.y - 5, 10, 10);
                g.drawString("Player : " + id, p.x + 5, p.y - 5);
            }
        }
        for (int i = 0; i < 30; i++) {
            if (!GameOver) {
                int frameDelay = (speedX[i] > 0) ? 500 / speedX[i] : 500;
                int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);
                if (Status_Zombie[i] && ready_) {
                    Font font = new Font("Arial", Font.BOLD, 16);
                    // g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
                    g.setColor(Color.RED);
                    g.setFont(font);
                    g.drawString("Drop item : " + Chance_Drop[i], axisX[i], axisY[i] - 10);
                    g.drawString("Drop item : " + Chance_Drop_rare[i], axisX[i], axisY[i] - 20);
                    g.drawRect(axisX[i], axisY[i], 100, 100);
                    g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);

                } else {
                    Drop_item(g, i);
                }

                if (Health[i] <= 0) {
                    Status_Zombie[i] = false;
                    continue;
                }
                if (Status_Zombie[i] && ready_) {
                    if (Percent_HP[i] >= 80) {
                        g.setColor(Color.GREEN);
                    } else if (Percent_HP[i] >= 60) {
                        g.setColor(Color.YELLOW);
                    } else if (Percent_HP[i] >= 40) {
                        g.setColor(Color.ORANGE);
                    } else {
                        g.setColor(Color.RED);
                    }
                    g.fillRect(axisX[i], axisY[i] + 120, Percent_HP[i], 5);
                }

            } else if (GameOver) {
                Game_Over(g);
            } else if (checkdead() == 30) {
                GameWin = true;
                Game_Win(g);
            }
            g.setFont(new Font("Tahoma", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("Zombie Dead : " + checkdead() + " / 30", 50, 50);
        }
    }

    public void Drop_item(Graphics g, int i) {
        if (Dropped_item[i] && ready_) {
            if (Chance_Drop[i]) {
                g.drawImage(item_Ammo, axisX[i] + 20, axisY[i] + 20, 50, 50, this);
            } else if (Chance_Drop_rare[i] == true) {
                g.drawImage(rare_item, axisX[i] + 20, axisY[i] + 20, 50, 50, this);
            }
        }
    }

    // ฟังก์ชันเพิ่มตำแหน่ง X ของ Zombie
    public void addZombieX(int i, int num) {
        axisX[i] += num;
    }

    // ฟังก์ชันเพิ่มตำแหน่ง Y ของ Zombie
    public void addZombieY(int i, int num) {
        axisY[i] += num;
    }

    // ฟังก์ชันรับค่า X ของ Zombie
    public int getZombieX(int i) {
        return this.axisX[i];
    }

    // ฟังก์ชันรับค่า Y ของ Zombie
    public int getZombieY(int i) {
        return this.axisY[i];
    }

    public void Bullets_Manage(int amountBullet, Graphics g) {
        this.amountBullet = amountBullet;
        if (bullets == 0) {
            if (amountBullet > 1) {
                bullets += amountBullet;
            }
        } else {
            bullets += amountBullet;
        }
        if (amountBullet > 1) {
            AddBullet = true;
            Sound(audioFile_Items);
            repaint();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    AddBullet = false;
                    repaint();
                }
            }, 5000);
        }
    }

    public void BulletBar(Graphics g) {
        Font f = new Font("Tahoma", Font.BOLD, 20);
        Font add = new Font("Tahoma", Font.BOLD, 20);
        g.setColor(Color.black);
        // g.fillRect(20,20, 150, 150);
        g.drawImage(Border2, 0, 0, 250, 250, this);
        g.drawImage(Border3, 27, 127, 200, 100, this);
        g.setColor(Color.WHITE);
        g.setFont(f);
        g.drawString("bullet", 95, 85);
        g.drawString(bullets + "", 115, 185);
        g.drawImage(bullet, 75, 77, 100, 100, this);
        if (AddBullet) {
            g.setFont(add);
            g.drawImage(rare_item, 75, 77, 100, 100, this);
            g.drawImage(wink, 120, 75, 50, 50, this);
            g.drawImage(wink, 25, 100, 50, 50, this);
        }

    }

    public void Game_Over(Graphics g) {
        g.setColor(new Color(0, 0, 0, 10));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(TextGameOver, 500, 150, 500, 500, this);
        g.drawImage(image_gif, 450, 300, 150, 200, this);
        g.drawImage(image_gif, 900, 300, 150, 200, this);
        stopAllZombies();
        repaint();
    }

    public void Game_Win(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Tahoma", Font.BOLD, 70));
        g.setColor(Color.YELLOW);
        g.drawString("YOU WIN!", 600, 300);
    }

    public int checkdead() {
        int count = 0;
        for (int i = 0; i < 30; i++) {
            if (Status_Zombie[i] == false) {
                count++;
            }
        }
        return count;
    }

    public void Sound(File audioFile) {
        new Thread(() -> {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public boolean isZombieAlive(int i) {
        return Status_Zombie[i];
    }
}

class recive_data extends Thread {
    private Client_Game panel;
    private Client_setting_ setting;
    private JPanel cardlayout;

    recive_data(Client_Game panel, Client_setting_ setting, JPanel card) {
        this.panel = panel;
        this.setting = setting;
        this.cardlayout = card;
    }

    public void run() {
        boolean first = true;
        while (true) {
            try (Socket socket = new Socket(setting.getIp(), 9090);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                // รับข้อมูล Map ผ่าน ObjectInputStream
                Map<String, Map<String, Object>> monsterData = (Map<String, Map<String, Object>>) in.readObject();
                int index = 0;

                // แสดงข้อมูลมอนสเตอร์แต่ละตัว
                for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
                    Map<String, Object> data = entry.getValue();
                    Boolean chanceDrop = false;
                    Boolean chanceDropRare = false;
                    Boolean dropped = false;
                    int[] position = (int[]) data.get("position");
                    boolean status = (boolean) data.get("status");
                    int speed = (int) data.get("Speed");
                    int hp_ = (int) data.get("Hp_");
                    int hp_percent = (int) data.get("Hp_percent");
                    int hp_max = (int) data.get("Hp_max");
                    Boolean ready = (Boolean) data.get("Ready");

                    if (first) {
                        chanceDrop = (Boolean) data.get("Chance_Drop");
                        chanceDropRare = (Boolean) data.get("Chance_Drop_rare");
                        dropped = (Boolean) data.get("dropped");
                        this.panel.setAll_data(index, position[0], position[1], speed, status, hp_, hp_max,
                                hp_percent,
                                chanceDrop, chanceDropRare, dropped, ready);
                    } else {
                        this.panel.setAll_data02(index, position[0], position[1], speed, status, hp_, hp_max,
                                hp_percent, ready);
                    }

                    this.panel.repaint();

                    index++;
                    // System.out.println("Position: [" + position[0] + ", " + position[1] + "]");
                    // System.out.println("Status: " + status);
                    // System.out.println("Speed: " + speed);
                    // System.out.println("HP: " + hp_ + "/" + hp_max + " (" + hp_percent + "%)");
                    // System.out.println("Chance to Drop: " + chanceDrop);
                    // System.out.println("Chance to Drop Rare: " + chanceDropRare);
                    // System.out.println("=========================================================");
                }
                first = false;
                in.close();
                socket.close();

            } catch (Exception e) {
                CardLayout cl = (CardLayout) (cardlayout.getLayout());
                // แสดงหน้า "Room"
                cl.show(cardlayout, "Room");
                break;
            }
            try {

                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // รอการอัพเดตครั้งต่อไป

        }
    }
}
