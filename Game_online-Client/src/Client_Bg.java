import javax.swing.JFrame;

public class Client_Bg extends JFrame{
    public static void main(String[] args) {
        Client_Bg bg = new Client_Bg();
        Client_Jpanel panel = new Client_Jpanel();
        bg.add(panel);
        panel.test();
        bg.setVisible(true);
    }
    
    
    Client_Bg(){
        setSize(1920, 1080 );
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
}
