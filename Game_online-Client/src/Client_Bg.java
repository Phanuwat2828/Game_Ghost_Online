import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;

public class Client_Bg extends JFrame{
    public static void main(String[] args) {
        Client_Bg bg = new Client_Bg();
        Client_Jpanel panel = new Client_Jpanel();
        bg.add(panel);
        bg.setVisible(true);
    }
    
    
    Client_Bg(){
        setSize(1920, 1080 );
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
              setTitle("x :"+e.getX()+" "+"Y :"+e.getY());
            }
            
        });
    }
    
}
