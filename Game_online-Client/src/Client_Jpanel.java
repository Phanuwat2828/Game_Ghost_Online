import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class Client_Jpanel extends JPanel {
    String path_Bg =  System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Image";
    Image image_bg =Toolkit.getDefaultToolkit().createImage(path_Bg+ File.separator + "Background.png");

    String path_gif = System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Gif";
    Image image_gif =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Zombie_walk.gif");
    Image item_Ammo =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_gif.gif");
    Image rare_item =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Ammo_1.gif");


    Random rand = new Random();
    int [] axisX = new int[30];
    int [] axisY = new int[30];
    int [] speedX = new int[30];
    Image [] zombie_action_walk = new Image[10];
    boolean [] set_Visible = new boolean [30];
    boolean[] Chance_Drop = new boolean[30];
    boolean[] Chance_Drop_rare = new boolean[30];
    boolean[] setVisible_item = new boolean[30];
    // boolean[] item_axisX = new boolean[30];
    // boolean[] item_axisY = new boolean[30];

    Timer timer;

    MediaTracker tracker = new MediaTracker(this);

    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
        img_zombie_walk();
        Zombie_Movement();
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
        Zombie_Movement();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                Zombie_Mange(e.getX(),e.getY());
                getItem(e.getX(),e.getY());
            }
        });
    }

    public void Defualt_Zombie(){
        for(int i=0; i<30;i++){
            axisX[i] = rand.nextInt(20,419);
            axisY[i] = rand.nextInt(250,650); 
            speedX[i] = rand.nextInt(1, 5);
            set_Visible[i] = true;
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
            }else if(set_Visible[i]){
                axisX[i] += speedX[i];
            }
        }
    }
    
    public void Zombie_Mange(int MouseAxisX, int MouseAxisY){
        for(int i=0; i<30; i++){
            if(set_Visible[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 100 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 100){
                    set_Visible[i] = false;
                    repaint();
                    break;
                }
            }
        }
    }
    public void getItem(int MouseAxisX, int MouseAxisY){
        for(int i=0; i<30; i++){
            if(!set_Visible[i] && setVisible_item[i]){
                if(MouseAxisX >= axisX[i] && MouseAxisX <= axisX[i] + 70 && 
                MouseAxisY >= axisY[i] && MouseAxisY <= axisY[i] + 70){
                    setVisible_item[i] = false;
                    repaint();
                    break;
                }
            }
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image_bg, 0, 0, 1555, 855, this);
    
        for(int i = 0; i < 30; i++){
            int frameDelay = 500 / speedX[i];
            int frame = (int) ((System.currentTimeMillis() / frameDelay) % 10);
            if(set_Visible[i]){
                g.drawImage(zombie_action_walk[frame], axisX[i], axisY[i], 100, 100, this);
            }else{
                Drop_item(g,i);
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
        setVisible_item[i] = true;
        int chance = rand.nextInt(100);
        if(chance <= 10){
            return true;
        }
        else{
            chance = rand.nextInt(100);
            if(chance <=5){
                Chance_Drop_rare[i] =true;
            }else{
            }
            return false;
        }
    }

}