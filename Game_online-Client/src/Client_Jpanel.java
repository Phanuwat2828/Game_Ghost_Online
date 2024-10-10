import java.io.File;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class Client_Jpanel extends JPanel {
    String path_Bg =  System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Image";
    Image image_bg =Toolkit.getDefaultToolkit().createImage(path_Bg+ File.separator + "Background.png");

    String path_gif = System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Gif";
    Image image_gif =Toolkit.getDefaultToolkit().createImage(path_gif+ File.separator + "Zombie_walk.gif");
    int [] axisX = new int[5];
    int [] axisY = new int[5];

    Random rand = new Random();

    public Client_Jpanel(){
        setSize(1920, 1080);
        Defualt_Zombie();
    }

    public void Defualt_Zombie(){
        for(int i=0; i<5;i++){
            axisX[i] = rand.nextInt(60,400);
            axisY[i] = rand.nextInt(200,400); 
        }
    }

    protected void paintComponent(Graphics g){
        g.drawImage(image_bg,0,0,1555,855,this);
        for(int i=0; i<5;i++){
            g.drawImage(image_gif,axisX[i],axisY[i],100,100,this);
        }
    }
}