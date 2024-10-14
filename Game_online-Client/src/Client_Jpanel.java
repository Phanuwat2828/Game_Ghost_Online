import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.MediaTracker;
import javax.swing.JPanel;

public class Client_Jpanel extends JPanel {
    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src" + File.separator + "Image";
    Image image_bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background.png");

    String path_gif = System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Gif";
    Image image_gif =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Zombie_walk.gif");
    Image item_Ammo =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_gif.gif");
    Image rare_item =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_1.gif");
    
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
    boolean[] setVisible_item = new boolean[30];

    MediaTracker tracker = new MediaTracker(this);

    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
        img_zombie_walk();
        Zombie_Movement(); // เรียกเพียงครั้งเดียว
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                Zombie_Mange(e.getX(),e.getY());
                getItem(e.getX(),e.getY());
            }
        });
    }

    public void img_zombie_walk() {
        for (int k = 0; k < 10; k++) {
            zombie_action_walk[k] = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Zombie_walk" + (k + 1) + ".png");
            tracker.addImage(zombie_action_walk[k], k); 
        }
        
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (int k = 0; k < 10; k++) {
            if (zombie_action_walk[k] == null) {
                System.out.println("Zombie_walk" + (k + 1) + ".png");
            }
        }
        
        if (image_bg == null) {
            System.out.println("Background.png");
        }
    }

    public void Defualt_Zombie(){
        for(int i = 0; i < 30; i++){
            axisX[i] = rand.nextInt(20, 419);
            axisY[i] = rand.nextInt(250, 650); 
            speedX[i] = rand.nextInt(1, 5);
            Status_Zombie[i] = true;
            Max_HP[i] = 100;
            Health[i] = Max_HP[i]; // กำหนด Health ให้เท่ากับ Max_HP
            Percent_HP[i] = 100;
            Chance_Drop[i] = Chance_To_Drop(i);
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
            }else if(Status_Zombie[i]){
                axisX[i] += speedX[i];
            }
        }
    }
    
    public void Zombie_Mange(int MouseAxisX, int MouseAxisY){
        for(int i = 0; i < 30; i++){
            if(Status_Zombie[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 100 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 100){
                    setVisible_item[i] = true;
                    Damage[i] = 20;
                    Health[i] -= Damage[i];
                    Percent_HP[i] = (Health[i] * 100) / Max_HP[i]; // แก้ไขการคำนวณ Percent_HP
                    repaint();
                }
            }
        }
    }
    public void getItem(int MouseAxisX, int MouseAxisY){
        for(int i=0; i<30; i++){
            if(!Status_Zombie[i] && setVisible_item[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 70 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 70){
                    setVisible_item[i] = false;
                    break;
                }
            }
        }
    }

  
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1555, 855, this);
    
        for(int i = 0; i < 30; i++){
            int frameDelay = (speedX[i] > 0) ? 500 / speedX[i] : 500; 
            int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);
            if(Status_Zombie[i]){
                g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
            }else{
                Drop_item(g,i);
            }
            
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
                // g.drawRect(axisX[i], axisY[i], 100, 100);
                g.fillRect(axisX[i], axisY[i] + 120, Percent_HP[i], 5);
                g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
            }
        }
    }
    public void Drop_item(Graphics g,int i){
        if(setVisible_item[i]){
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
}
