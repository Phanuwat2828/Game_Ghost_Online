import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.swing.JPanel;
import java.awt.event.MouseMotionAdapter;

public class Client_Jpanel extends JPanel {
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
    int bullets = 20;
    int amountBullet;
    int CountDead = 0;

    // =========================== to_server
    private static final String SERVER_IP = "26.245.160.254"; // เปลี่ยนเป็น IP ของ Server ถ้าไม่ใช่ localhost
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Map<Integer, Point> remoteMousePositions = Collections.synchronizedMap(new HashMap<>());
    private int clientId = -1; // ใช้ในการระบุว่าเป็น Client ตัวไหน
    // ===========================

    MediaTracker tracker = new MediaTracker(this);

    public Client_Jpanel() {
        setSize(1920, 1080);
        Defualt_Zombie();
        img_zombie_walk();
        Zombie_Movement();
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // เริ่ม Thread สำหรับรับข้อมูลจาก Server
            new Thread(new IncomingReader()).start();

            // เพิ่ม Mouse Motion Listener เพื่อจับการเคลื่อนไหวของเมาส์
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (bullets > 0) {
                        Sound(audioFile_shoot);
                    }

                    Bullets_Manage(-1, null);
                    Zombie_Mange(e.getX(), e.getY());
                    getItem(e.getX(), e.getY());
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (clientId != -1) { // ตรวจสอบว่ามี clientId แล้ว
                        String msg = "MOVE," + e.getX() + "," + e.getY();
                        out.println(msg);
                        // ไม่ต้องอัพเดตตำแหน่งเมาส์ของตัวเองที่นี่ เพราะจะได้รับจาก Server
                    }
                }
            });

            System.out.println("เชื่อมต่อกับ Server สำเร็จ");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ไม่สามารถเชื่อมต่อกับ Server ได้");
        }

    }

    class IncomingReader implements Runnable {
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
                                }
                                break;
                            default:
                                System.out.println("ข้อความที่ไม่รู้จัก: " + message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    public void Defualt_Zombie() {
        for (int i = 0; i < 30; i++) {
            axisX[i] = rand.nextInt(20, 419);
            axisY[i] = rand.nextInt(250, 650);
            speedX[i] = rand.nextInt(1, 5);
            Status_Zombie[i] = true;
            Max_HP[i] = 100;
            Health[i] = Max_HP[i];
            Percent_HP[i] = 100;
            Chance_Drop[i] = Chance_To_Drop(i);
        }
    }

    public void startZombieThreads() {
        for (int i = 0; i < 30; i++) {
            zombieThreads[i] = new ZombieThread(i, this);
            zombieThreads[i].start();
        }
    }

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

    public void Zombie_Movement() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveZombies();
                repaint();
            }
        }, 0, 50);
    }

    public void moveZombies() {
        for (int i = 0; i < 30; i++) {
            if (axisX[i] > getWidth() - 230) {
                GameOver = true;
                repaint();
            } else if (GameOver) {
            } else if (Status_Zombie[i]) {
                axisX[i] += speedX[i];
            }

        }
    }

    public void Zombie_Mange(int MouseAxisX, int MouseAxisY) {
        for (int i = 0; i < 30; i++) {
            if (Status_Zombie[i]) {
                if (MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 100 &&
                        MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 100) {
                    if (bullets > 0) {
                        Dropped_item[i] = true;
                        Damage[i] = 20;
                        Health[i] -= Damage[i];
                        Percent_HP[i] = (Health[i] * 100) / Max_HP[i];
                        repaint();
                    }
                }
            }
        }
    }

    public void getItem(int MouseAxisX, int MouseAxisY) {
        for (int i = 0; i < 30; i++) {
            if (!Status_Zombie[i] && Dropped_item[i]) {
                if (MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 70 &&
                        MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 70) {

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1555, 855, this);
        BulletBar(g);
        synchronized (remoteMousePositions) {
            for (Map.Entry<Integer, Point> entry : remoteMousePositions.entrySet()) {
                int id = entry.getKey();
                Point p = entry.getValue();
                if (id == clientId) {
                    g.setColor(Color.BLUE); // สีสำหรับเมาส์ของตัวเอง
                } else {
                    g.setColor(Color.RED); // สีสำหรับเมาส์ของ Client อื่น
                }
                g.fillOval(p.x - 5, p.y - 5, 10, 10);
                g.drawString("Client #" + id, p.x + 5, p.y - 5);
            }
        }
        for (int i = 0; i < 30; i++) {
            if (!GameOver) {
                int frameDelay = (speedX[i] > 0) ? 500 / speedX[i] : 500;
                int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);
                if (Status_Zombie[i]) {
                    // g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
                    g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
                } else {
                    Drop_item(g, i);
                }

                if (Health[i] <= 0) {
                    Status_Zombie[i] = false;
                    continue;
                }
                if (Status_Zombie[i]) {
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
        if (Dropped_item[i]) {
            if (Chance_Drop[i]) {
                g.drawImage(item_Ammo, axisX[i] + 20, axisY[i] + 20, 50, 50, this);
            } else if (Chance_Drop_rare[i] == true) {
                g.drawImage(rare_item, axisX[i] + 20, axisY[i] + 20, 50, 50, this);
            }
        }
    }

    public boolean Chance_To_Drop(int i) {
        int chance = rand.nextInt(100);
        if (chance <= 20) {
            return true;
        } else {
            chance = rand.nextInt(100);
            if (chance <= 10) {
                Chance_Drop_rare[i] = true;
            } else {
            }
            return false;
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
