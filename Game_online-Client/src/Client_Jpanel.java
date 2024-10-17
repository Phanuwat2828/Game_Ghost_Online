import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.swing.JPanel;
import java.util.Arrays;


public class Client_Jpanel extends JPanel {
    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src" + File.separator + "Image";
    Image image_bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background.png");
    String path_png = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src" + File.separator + "png";
    String path_gif = System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Gif";
    Image image_gif =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Zombie_walk.gif");
    Image item_Ammo =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_gif.gif");
    Image rare_item =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_1.gif");
    Image bullet =Toolkit.getDefaultToolkit().createImage(path_png+ File.separator + "Ammo_2.png");
    Image TextGameOver =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "GameOver.gif");
    Image wink =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Wink2.gif");
    Image Border2 =Toolkit.getDefaultToolkit().createImage(path_png+ File.separator + "Border2.PNG");
    Image Border3 =Toolkit.getDefaultToolkit().createImage(path_png+ File.separator + "Border3.PNG");
    Image frame1 =Toolkit.getDefaultToolkit().createImage(path_png+ File.separator + "frame1.PNG");


    String pathSound = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src" + File.separator + "sound";
    File audioFile_shoot = new File(pathSound + File.separator + "pistol-shot-233473.wav");
    File audioFile_Items = new File(pathSound + File.separator + "item-pick-up-38258.wav");
    
    ZombieThread[] zombieThreads = new ZombieThread[30];
    Random rand = new Random();
    int[] axisX = new int[30];
    int[] axisY = new int[30];
    int[] speedX = new int[30];
    boolean[] Status_Zombie = new boolean[30];
    Image[] zombie_action_walk = new Image[10];
    int[] Health = new int[30];
    int[] Max_HP = new int[30];
    int[] Percent_HP = new int[30];
    int[] Damage = new int[30];
    Timer timer;
    boolean[] Chance_Drop = new boolean[30];
    boolean[] Chance_Drop_rare = new boolean[30];
    boolean[] Dropped_item = new boolean[30];
    boolean GameOver = false;
    boolean GameWin = false;
    boolean AddBullet = false;
    int bullets = 500;
    int amountBullet;
    int CountDead = 0;
    boolean retry_Game=false;
    MediaTracker tracker = new MediaTracker(this);
    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
        img_zombie_walk();
        Zombie_Movement();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(GameOver || GameWin){
                    Check_Click_Retry(e.getX(),e.getY());
                }else{
                    //เพิ่มเสียงตอนยิงเฉยๆ
                    if(bullets > 0){
                        Sound(audioFile_shoot);
                    }
                    //คำนวณจัดการกระสุนหลังจากยิง
                    Bullets_Manage(-1,null);
    
                    //จัดการเรื่องคลิก zombie
                    Zombie_Mange(e.getX(),e.getY());
                    
                    //จัดการเรื่องการดรอป ไอเท็มและ คลิกเก็บไอเท็ม
                    getItem(e.getX(),e.getY());
                }
            }
        });
        
        
    }

    public void img_zombie_walk() {
        for (int k = 0; k < 10; k++) {
            zombie_action_walk[k] = Toolkit.getDefaultToolkit().createImage(path_png + File.separator + "Zombie_walk" + (k + 1) + ".png");
            tracker.addImage(zombie_action_walk[k], k); 
        }
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Defualt_Zombie(){
        for(int i = 0; i < 30; i++){
            axisX[i] = rand.nextInt(20, 419);
            axisY[i] = rand.nextInt(250, 650); 
            speedX[i] = rand.nextInt(1, 5);
            Status_Zombie[i] = true;
            Max_HP[i] = 100;
            Health[i] = Max_HP[i]; 
            Percent_HP[i] = 100;
            Chance_Drop[i] = Chance_To_Drop(i);
        }
        Arrays.sort(axisY);
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
                    zombieThreads[i].join();  // รอให้ thread หยุดทำงาน
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
            if(axisX[i]> getWidth()-230){
                GameOver = true;
                repaint();
            }else if(GameOver){
            }
            else if(Status_Zombie[i]){
                axisX[i] += speedX[i];
            }

        }
    }
    
    public void Zombie_Mange(int MouseAxisX, int MouseAxisY){
        for(int i = 0; i < 30; i++){
            if(Status_Zombie[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 100 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 100){
                    if(bullets>0){
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
    public void getItem(int MouseAxisX, int MouseAxisY){
        for(int i=0; i<30; i++){
            if(!Status_Zombie[i] && Dropped_item[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 70 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 70){
                    
                    Bullets_Manage(1,null);
                    if(Chance_Drop_rare[i]){
                        Bullets_Manage(20,null);
                    }else if(Chance_Drop[i]){
                        Bullets_Manage(10,null);
                    }
                    Dropped_item[i] = false;
                    break;
                }
            }
        }
    }

  
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1555, 855, this);
        BulletBar(g);
        for(int i = 0; i < 30; i++){
            if(GameOver){
                Game_Over(g);
            }else if(GameWin){
                Game_Win(g);
            }else {
                PaintZombie(g,i);
               
                if(Health[i] <= 0){
                    Status_Zombie[i] = false;
                    continue;
                }
                if(Status_Zombie[i]){
                    if(Percent_HP[i] >= 80){
                        g.setColor(Color.GREEN);
                    } else if (Percent_HP[i] >= 60) {
                        g.setColor(Color.YELLOW);   
                    } else if (Percent_HP[i] >= 40) {
                        g.setColor(Color.ORANGE);
                    } else{
                    g.setColor(Color.RED);
                    }
                    g.fillRect(axisX[i], axisY[i] + 120, Percent_HP[i], 5);
                } 
            }
        
            g.setFont(new Font("Tahoma", Font.BOLD, 25));
            g.setColor(Color.WHITE);
            g.drawString("Zombie Dead : " + Check_Amount_Dead()+" / 30", 50, 30);

        }
            if (Check_Amount_Dead() == 30) {
                GameWin = true;
            }
    }

    public void PaintZombie(Graphics g, int i){
        int frameDelay = (speedX[i] > 0) ? 500 / speedX[i] : 500; 
                int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);
                
                if(Status_Zombie[i]){
                    g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
                }else{
                    Drop_item(g,i);
        }
    }


    public void Drop_item(Graphics g,int i){
        if(Dropped_item[i]){
            if(Chance_Drop[i]){
                g.drawImage(item_Ammo, axisX[i]+20, axisY[i]+20, 50, 50, this);
            }else if (Chance_Drop_rare[i] == true){
                g.drawImage(rare_item, axisX[i]+20, axisY[i]+20, 50, 50, this);
            }
        }
    }


    public boolean Chance_To_Drop(int i){
        int chance = rand.nextInt(100);
        if(chance <= 20){
            return true;
        }
        else{
            chance = rand.nextInt(100);
            if(chance <=10){
                Chance_Drop_rare[i] =true;
            }else{
            }
            return false;
        }
    }
    
    // ฟังก์ชันเพิ่มตำแหน่ง X ของ Zombie
    public void addZombieX(int i, int num){
        axisX[i] += num;
    }
    
    // ฟังก์ชันเพิ่มตำแหน่ง Y ของ Zombie
    public void addZombieY(int i, int num){
        axisY[i] += num;
    }
    
    // ฟังก์ชันรับค่า X ของ Zombie
    public int getZombieX(int i){
        return this.axisX[i];
    }
    
    // ฟังก์ชันรับค่า Y ของ Zombie
    public int getZombieY(int i){
        return this.axisY[i];
    }

    public void Bullets_Manage(int amountBullet,Graphics g){
        this.amountBullet = amountBullet;
        if(bullets ==0){
            if(amountBullet>1){
                bullets += amountBullet;
            }
        }else{
            bullets += amountBullet;
        }
        if(amountBullet>1){
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
    public void BulletBar(Graphics g){
        Font f = new Font("Tahoma",Font.BOLD,20);
        Font add = new Font("Tahoma",Font.BOLD,20);
        g.setColor(Color.black);
        //g.fillRect(20,20, 150, 150);
        g.drawImage(Border2, 0, 0, 250, 250, this);
        g.drawImage(Border3, 27, 127, 200, 100, this);
        g.setColor(Color.WHITE);
        g.setFont(f);
        g.drawString("bullet", 95, 85);
        g.drawString(bullets+"", 115, 185);
        g.drawImage(bullet, 75, 77, 100, 100, this);
        if(AddBullet){
            g.setFont(add);
            g.drawImage(rare_item,75 , 77, 100, 100, this);
            g.drawImage(wink, 150,75, 50, 50, this);
            g.drawImage(wink, 60,100, 50, 50, this);
        }

    }

    public void Game_Over(Graphics g){
        g.setColor(new Color(0, 0, 0, 10)); 
        g.fillRect(0, 0, getWidth(), getHeight()); 
        g.drawImage(TextGameOver, 500, 150, 500, 500, this);
        g.drawImage(image_gif, 450, 300, 150, 200, this);
        g.drawImage(image_gif, 900, 300, 150, 200, this);
        g.drawImage(frame1, 590, 450, 300, 220, this);
        g.setColor(Color.WHITE);
        g.drawString("try agin", 700, 570);


        //g.add(retry);

        stopAllZombies();
        repaint();
    }

    public void Game_Win(Graphics g){
        g.setColor(new Color(0, 0, 0, 10)); 
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Tahoma", Font.BOLD, 70)); 
        g.setColor(Color.YELLOW); 
        g.drawString("WAVE  1  ", 600, 400); 
        g.drawString("CLEAR", 630, 500); 
        repaint();
    }

    public int Check_Amount_Dead(){
        int count=0;
        for(int i = 0; i <30;i++){
            if(Status_Zombie[i] == false){
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

    public void Check_Click_Retry(int MouseAxisX, int MouseAxisY){
        if(MouseAxisX >= 590 && MouseAxisX <= 590 + 890 && 
                MouseAxisY >= 450 && MouseAxisY <= 450 + 670){
            retry_Game = true;
        }
    }
    public boolean check_retry() {
        return retry_Game;
    }
}   