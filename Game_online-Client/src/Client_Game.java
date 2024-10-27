import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    Image rare_item1 = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Ammo_1.gif");
    Image rare_item2 = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "gold_ammo.gif");
    Image bullet = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Ammo_2.png");
    Image TextGameOver = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "GameOver.gif");
    Image wink = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Wink2.gif");
    Image Border2 = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Border2.PNG");
    Image Border3 = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Border3.PNG");
    Image CountDown = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "countdown.gif");
    String pathSound = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "sound";
    File audioFile_shoot = new File(pathSound + File.separator + "pistol-shot-233473.wav");
    File audioFile_Items = new File(pathSound + File.separator + "item-pick-up-38258.wav");

    Random rand = new Random();
    boolean ready = false;
    boolean exit_game = false;

    // Var
    private Map<String, Map<String, Object>> monsterData = new LinkedHashMap<>();

    Timer timer;
    Image zombie_action_walk[] = new Image[10];
    Image boss_action_walk[] = new Image[6];

    boolean GameOver = false;
    boolean GameWin = false;
    boolean AddBullet = false;
    boolean ready_ = false;
    boolean countdown = false;
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
    private Client_setting_ setting;
    private int count_monster = 0;
    private Map<Integer, Point> remoteMousePositions = Collections.synchronizedMap(new HashMap<>());
    private int clientId = -1; // ใช้ในการระบุว่าเป็น Client ตัวไหน
    // ===========================

    MediaTracker tracker = new MediaTracker(this);

    public Client_Game(JPanel cardLayout, Client_setting_ setting) {
        this.setting = setting;
        if (setting.getCreator()) {
            server_01 = new B_Mouse_Server(setting);
            server_01.start();
            server02 = new A_Zombie_Server(setting);
            server02.start();
        }
        recive_data th = new recive_data(this, setting, cardLayout, this);
        th.start();

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
                    boolean getitem = getItem(e.getX(), e.getY());
                    // System.out.println("test");
                    if (bullets > 0 && !getitem) {
                        Sound(audioFile_shoot);
                        if (ready_) {
                            socket2 = new Socket(SERVER_IP, SERVER_PORT2);
                            out2 = new PrintWriter(socket2.getOutputStream(), true);
                            out2.println(e.getX() + "," + e.getY());
                        }
                    }
                    if (ready_ && !getitem) {

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
        img_boss_walk();

        System.out.println(setting.getIp());

        new Thread(new IncomingReader(cardLayout)).start();

    }

    public int getMouseX() {
        return this.mouseX;
    }

    public void setCount_monster(int count_monster) {
        this.count_monster = count_monster;
    }

    public void img_boss_walk() {
        for (int i = 0; i < 6; i++) {
            boss_action_walk[i] = Toolkit.getDefaultToolkit()
                    .createImage(path_png + File.separator + "boss" + (i + 1) + ".png");
            tracker.addImage(boss_action_walk[i], i);
        }
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public void setAll_data(String index, int x, int y, int speed, boolean status, int hp, int max_hp, int percent_hp,
            boolean Chance_Drop_, boolean Chance_Drop_rare_, boolean dropped, boolean ready, String type, int wave) {
        Map<String, Object> data_monster = new HashMap<>();
        data_monster.put("position", new int[] { x, y });
        data_monster.put("status", status);
        data_monster.put("Speed", speed);
        data_monster.put("dropped", dropped);
        data_monster.put("Hp_", hp);
        data_monster.put("Hp_max", max_hp);
        data_monster.put("Hp_percent", percent_hp);
        data_monster.put("Chance_Drop_rare", Chance_Drop_rare_);
        data_monster.put("Chance_Drop", Chance_Drop_);
        data_monster.put("position_level", wave);
        data_monster.put("level", type);

        monsterData.put(index, data_monster);
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

    public void setAll_data02(String index, int x, int y, int speed, boolean status, int hp, int max_hp, int percent_hp,
            boolean ready) {
        monsterData.get(index).put("position", new int[] { x, y });
        monsterData.get(index).put("status", status);
        monsterData.get(index).put("Speed", speed);
        monsterData.get(index).put("Hp_", hp);
        monsterData.get(index).put("Hp_max", max_hp);
        monsterData.get(index).put("Hp_percent", percent_hp);

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

    public void setGameWin(boolean gameWin) {
        GameWin = gameWin;
    }

    public void setGameOver(boolean gameOver) {
        GameOver = gameOver;
    }

    // public void startZombieThreads() {
    // for (int i = 0; i < 30; i++) {
    // zombieThreads[i] = new ZombieThread(i, this);
    // zombieThreads[i].start();
    // }
    // }

    public boolean getItem(int MouseAxisX, int MouseAxisY) {
        boolean status = false;
        for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> data_now = entry.getValue();
            int[] position = (int[]) data_now.get("position");
            Boolean status_ = (Boolean) data_now.get("status");
            Boolean drop = (Boolean) data_now.get("dropped");
            Boolean chance_drop = (Boolean) data_now.get("Chance_Drop");
            Boolean chance_drop_rare = (Boolean) data_now.get("Chance_Drop_rare");
            if (!status_ && drop) {
                if (MouseAxisX >= position[0] && MouseAxisX <= position[0] + 70 &&
                        MouseAxisY >= position[1] && MouseAxisY <= position[1] + 70) {
                    status = true;
                    Bullets_Manage(1, null);
                    if (chance_drop_rare) {
                        Bullets_Manage(20, null);
                    } else if (chance_drop) {
                        Bullets_Manage(10, null);
                    }
                    monsterData.get(name).put("dropped", false);
                    break;
                }
            }

        }
        return status;
    }

    public void Game_Win(Graphics g, boolean win, int wave) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        if (win) {
            g.setFont(new Font("Tahoma", Font.BOLD, 75));
            g.setColor(Color.YELLOW);
            g.drawString("YOU WIN !!", 550, 400);
            Font add = new Font("Tahoma", Font.BOLD, 20);
            g.setFont(add);
            g.drawString("you are team work", 650, 500);

        } else {
            g.setFont(new Font("Tahoma", Font.BOLD, 70));
            g.setColor(Color.YELLOW);
            g.drawString("WAVE  " + wave, 600, 360);
            g.drawString("CLEAR", 630, 460);

            // Font for the countdown
            Font add = new Font("Tahoma", Font.BOLD, 20);
            g.setFont(add);
            g.drawString("next wave in ...  ", 670, 600);
            countdown = true;
            paintCountDown(g);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    countdown = false;
                    setting.setReadychange(true);
                }
            }, 4500);

        }

    }

    public void paintCountDown(Graphics g) {
        if (countdown) {
            g.drawImage(CountDown, 660, 650, 150, 100, this);
        } else {

        }
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
        // for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
        // String name = entry.getKey();
        // Map<String, Object> data_now = entry.getValue();
        // int[] position = (int[]) data_now.get("position");
        // Boolean status_ = (Boolean) data_now.get("status");
        // int speed = (int) data_now.get("Speed");
        // Boolean drop = (Boolean) data_now.get("dropped");
        // int hp = (int) data_now.get("Hp_");
        // int hp_max = (int) data_now.get("Hp_max");
        // int hp_percent = (int) data_now.get("Hp_percent");
        // Boolean chance_drop = (Boolean) data_now.get("Chance_Drop");
        // Boolean chance_drop_rare = (Boolean) data_now.get("Chance_Drop_rare");

        // }
        Font font = new Font("Arial", Font.BOLD, 13);
        int y_text = 100;
        int die = 0;
        int Wave_ = 0;
        die = checkdead();
        if (die == count_monster) {
            Wave_ += 1;
            Game_Win(g, GameWin, Wave_);
        }

        for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> data_now = entry.getValue();
            int[] position = (int[]) data_now.get("position");
            Boolean status_ = (Boolean) data_now.get("status");
            int speed = (int) data_now.get("Speed");
            Boolean drop = (Boolean) data_now.get("dropped");
            int hp = (int) data_now.get("Hp_");
            int hp_max = (int) data_now.get("Hp_max");
            int hp_percent = (int) data_now.get("Hp_percent");
            Boolean chance_drop = (Boolean) data_now.get("Chance_Drop");
            Boolean chance_drop_rare = (Boolean) data_now.get("Chance_Drop_rare");
            String type = (String) data_now.get("level");
            int wave = (Integer) data_now.get("position_level");
            Wave_ = wave;
            if (!GameOver) {
                int frameDelay = (speed > 0) ? 500 / speed : 500;
                int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);

                if (status_ && ready_) {

                    if (type.equals("common")) {

                        g.setColor(Color.RED);
                        g.setFont(font);
                        g.drawRect(position[0], position[1], 100, 100);
                        g.drawImage(zombie_action_walk[frame], position[0], position[1], 100, 100, this);
                        if (status_ && ready_) {
                            if (hp_percent >= 80) {
                                g.setColor(Color.GREEN);
                            } else if (hp_percent >= 60) {
                                g.setColor(Color.YELLOW);
                            } else if (hp_percent >= 40) {
                                g.setColor(Color.ORANGE);
                            } else {
                                g.setColor(Color.RED);
                            }
                            g.fillRect(position[0], position[1] + 120, hp_percent, 5);
                        }
                    } else if (type.equals("Boss")) {
                        g.setColor(Color.RED);
                        g.drawRect(position[0], position[1], 250, 250);
                        PaintBoss(g, status_, hp_percent, position[0], position[1]);
                    }

                } else {
                    Drop_item(g, name, position[0], position[1], chance_drop, chance_drop_rare);
                }

                if (hp <= 0) {
                    monsterData.get(name).put("status", false);
                    continue;
                }

            }
            // } else if (GameOver) {
            // Game_Over(g);
            // } else if (checkdead() == 30) {
            // GameWin = true;
            // Game_Win(g);
            // }

            g.setFont(font);
            g.setColor(Color.GREEN);

            if (true) {
                y_text += 17;
                g.drawString(
                        name + " : xy[" + position[0] + "," + position[1] + "] status[" + status_ + "] type[" + type
                                + "] hp[" + hp + "] hp_%[" + hp_percent + "] item[" + chance_drop + "] item_rare["
                                + chance_drop_rare + "] Speed[" + speed + "]",
                        1210,
                        y_text);

                g.drawRect(1200, 100, 700, 875);
            }

            g.setFont(new Font("Tahoma", Font.BOLD, 35));
            g.setColor(Color.WHITE);
            g.drawString("Zombie Dead : " + die + " / " + count_monster + "  Wave Monster : " + wave, 650, 50);

        }
    }

    public void PaintBoss(Graphics g, boolean status, int hp_p, int x, int y) {
        final int frameDelay = 500;
        final int frameCount = 6;
        final int bossWidth = 250;
        final int healthBarWidth = 150;
        final int healthBarHeight = 5;

        if (status) {
            final int frame = (int) ((System.currentTimeMillis() / frameDelay) % frameCount);
            final int healthBarX = x + (bossWidth / 2) - (healthBarWidth / 2);
            final int healthBarY = y - healthBarHeight - 5;
            final int filledWidth = (hp_p * healthBarWidth) / 100;
            final int maxFilledWidth = Math.min(filledWidth, healthBarWidth);
            g.setColor(hp_p >= 80 ? Color.GREEN
                    : hp_p >= 60 ? Color.YELLOW
                            : hp_p >= 40 ? new Color(255, 165, 0)
                                    : hp_p >= 30 ? new Color(255, 69, 0)
                                            : hp_p >= 20 ? Color.RED : new Color(139, 0, 0));
            g.fillRect(healthBarX, healthBarY, maxFilledWidth, healthBarHeight);
            g.drawImage(boss_action_walk[frame], x, y, bossWidth, bossWidth, this);
        }
    }

    public void Drop_item(Graphics g, String index, int x, int y, boolean chance_drop, boolean chance_drop_rare) {
        Boolean drop = (Boolean) monsterData.get(index).get("dropped");
        if (drop && ready_) {
            if (chance_drop) {
                g.drawImage(item_Ammo, x + 20, y + 20, 50, 50, this);
            } else if (chance_drop_rare) {
                g.drawImage(rare_item2, x + 20, y + 20, 50, 50, this);
            }
        }
    }

    // ฟังก์ชันเพิ่มตำแหน่ง X ของ Zombie
    // public void addZombieX(int i, int num) {
    // axisX.set(i, axisX.get(i) + num);

    // }

    // // Function to increase the Y position of a Zombie
    // public void addZombieY(int i, int num) {
    // axisY.set(i, axisY.get(i) + num);
    // }

    // // Function to get the X position of a Zombie
    // public int getZombieX(int i) {
    // return axisX.get(i);
    // }

    // // Function to get the Y position of a Zombie
    // public int getZombieY(int i) {
    // return axisY.get(i);
    // }

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
            g.drawImage(rare_item1, 75, 77, 100, 100, this);
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

        for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
            Map<String, Object> data_now = entry.getValue();
            int hp = (int) data_now.get("Hp_");
            Boolean status_ = (Boolean) data_now.get("status");
            if (!status_ && hp <= 0) {
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

    // public boolean isZombieAlive(int i) {
    // return Status_Zombie.get(i);
    // }
}

class recive_data extends Thread {
    private Client_Game panel;
    private Client_setting_ setting;
    private JPanel cardlayout;
    private Client_Game data;

    recive_data(Client_Game panel, Client_setting_ setting, JPanel card, Client_Game data) {
        this.panel = panel;
        this.setting = setting;
        this.cardlayout = card;
        this.data = data;
    }

    public void run() {
        boolean first = true;
        int waved = 1;
        boolean win = false;
        boolean lose = false;
        while (true) {
            try (Socket socket = new Socket(setting.getIp(), 9090);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                // รับข้อมูล Map ผ่าน ObjectInputStream
                Map<String, Map<String, Object>> monsterData = (Map<String, Map<String, Object>>) in.readObject();
                int index = 0;
                int count_monstaer = 0;

                // แสดงข้อมูลมอนสเตอร์แต่ละตัว
                for (Map.Entry<String, Map<String, Object>> entry : monsterData.entrySet()) {
                    String name = entry.getKey();
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
                    String type = (String) data.get("level");
                    int wave = (Integer) data.get("position_level");
                    if (count_monstaer == 0) {
                        win = (Boolean) data.get("win");
                        lose = (Boolean) data.get("lose");
                        this.data.setGameWin(win);
                        this.data.setGameOver(lose);
                    }
                    count_monstaer++;
                    if (waved != wave) {
                        first = true;
                        waved = wave;
                    }
                    if (first) {
                        chanceDrop = (Boolean) data.get("Chance_Drop");
                        chanceDropRare = (Boolean) data.get("Chance_Drop_rare");
                        dropped = (Boolean) data.get("dropped");
                        this.panel.setAll_data(name, position[0], position[1], speed, status, hp_, hp_max,
                                hp_percent,
                                chanceDrop, chanceDropRare, dropped, ready, type, wave);
                    } else {
                        this.panel.setAll_data02(name, position[0], position[1], speed, status, hp_, hp_max,
                                hp_percent, ready);
                    }

                    this.panel.repaint();

                    index++;
                    System.out.println(name);
                    System.out.println("Position: [" + position[0] + ", " + position[1] + "]");
                    System.out.println("Status: " + status);
                    System.out.println("Speed: " + speed);
                    System.out.println("HP: " + hp_ + "/" + hp_max + " (" + hp_percent + "%)");
                    System.out.println("Chance to Drop: " + chanceDrop);
                    System.out.println("Chance to Drop Rare: " + chanceDropRare);
                    System.out.println("Level: " + wave);
                    System.out.println("win: " + win);
                    System.out.println("lose: " + lose);
                    System.out.println("=========================================================");
                }
                data.setCount_monster(count_monstaer);
                first = false;
                in.close();
                socket.close();

            } catch (Exception e) {
                System.out.println(e);
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
