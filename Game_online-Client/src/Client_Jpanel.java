import java.io.File;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class Client_Jpanel extends JPanel {
    String path =  System.getProperty("user.dir")+File.separator +"Game_online-Client"+ File.separator + "src"+ File.separator + "Image";
    Image image_bg =Toolkit.getDefaultToolkit().createImage(path+ File.separator + "Background.png");
    public Client_Jpanel(){
        setSize(1920, 1080);
    }

    protected void paintComponent(Graphics g){
        g.drawImage(image_bg,0,0,1555,855,this);
    }

    public void test(){
        System.out.println("Client_Jpanel");
         System.out.println(path);

    }
}