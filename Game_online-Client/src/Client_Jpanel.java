import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
    boolean [] set_Visible = new boolean [30];
    Timer timer;

    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
        Zombie_Movement();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                Zombie_Mange(e.getX(),e.getY());
            }
        });
    }

    public void Defualt_Zombie(){
        for(int i=0; i<30;i++){
            axisX[i] = rand.nextInt(20,419);
            axisY[i] = rand.nextInt(250,650); 
            speedX[i] = rand.nextInt(1, 5);
            set_Visible[i] = true;
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
            axisX[i] += speedX[i];
            if(axisX[i]> getWidth()){
                set_Visible[i] = false;
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

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image_bg,0,0,1555,855,this);
        for(int i=0; i<30;i++){
            if(set_Visible[i]){
                g.drawImage(image_gif,axisX[i],axisY[i],100,100,this);
            }
        }
    }
}