import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.MediaTracker;
import javax.swing.JPanel;


public class Client_Jpanel extends JPanel {
    String path_Bg =  System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Image";
    Image image_bg =Toolkit.getDefaultToolkit().createImage(path_Bg+ File.separator + "Background.png");

    String path_gif = System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Gif";
    Image image_gif =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Zombie_walk.gif");

    Random rand = new Random();
    int [] axisX = new int[30];
    int [] axisY = new int[30];
    int [] speedX = new int[30];
    Image [] zombie_action_walk = new Image[10];
    Timer timer;

// เพิ่มตัวแปร MediaTracker
    MediaTracker tracker = new MediaTracker(this);

    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
        img_zombie_walk();
        startZombieMovement();
    }

    public void img_zombie_walk() {
        for (int k = 0; k < 10; k++) {
            zombie_action_walk[k] = Toolkit.getDefaultToolkit().createImage(path_gif + File.separator + "Zombie_walk" + (k + 1) + ".png");
            tracker.addImage(zombie_action_walk[k], k);  // เพิ่มภาพเข้าไปใน tracker
        }
        
        try {
            tracker.waitForAll();  // รอให้โหลดภาพทั้งหมดก่อน
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Defualt_Zombie(){
        for(int i=0; i<30;i++){
            axisX[i] = rand.nextInt(20,419);
            axisY[i] = rand.nextInt(250,650); 
            speedX[i] = rand.nextInt(1, 5);
        }
    }


    public void startZombieMovement() {
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
            axisX[i] += speedX[i];

            if (axisX[i] > getWidth()) {
                axisX[i] = -100; 
            }
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1555, 855, this);
    
        // วาดภาพซอมบี้
        for(int i = 0; i < 30; i++){
            // ยิ่งซอมบี้ช้ามาก ตัวคูณของ frameDelay จะยิ่งมาก ทำให้เปลี่ยนเฟรมช้าลง
            int frameDelay = 500 / speedX[i]; // กำหนดความเร็วในการเปลี่ยนเฟรมตามความเร็วของซอมบี้
            int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10); // คำนวณเฟรมตามความเร็วที่ปรับแล้ว
            
            g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
        }
    }
}