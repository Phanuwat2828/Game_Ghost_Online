import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import java.util.Timer;
import java.util.TimerTask;

public class Client_Bg extends JFrame{
    Timer checkTime;
    Client_Jpanel [] wave = new Client_Jpanel[6];
    static int WaveNow = 1;

    public static void main(String[] args) {
        Client_Bg bg = new Client_Bg();
        bg.startGame();
        //wave[1] = new Client_Jpanel(2,40,0);
        bg.setVisible(true);
    } 
    
    
    Client_Bg(){
        setSize(1920, 1080 );
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
              setTitle("x :"+e.getX()+" "+"Y :"+e.getY());
            }
            
        });
    }

    public void startGame() {
        if (wave[WaveNow] != null) {
            remove(wave[WaveNow]);
            wave[WaveNow] = null;
            revalidate(); 
            repaint(); 
            WaveNow ++;
        }
        if(WaveNow ==1){
            wave[WaveNow] = new Client_Jpanel(1, 15, 3 );
            add(wave[WaveNow]);
            checkTime(wave[WaveNow],WaveNow);
        }else if(WaveNow ==2){
            wave[WaveNow] = new Client_Jpanel(2, 20, 3);
            add(wave[WaveNow]);
            checkTime(wave[WaveNow],WaveNow);
        }else if(WaveNow ==3){
            wave[WaveNow] = new Client_Jpanel(3, 25, 0);
            add(wave[WaveNow]);
            checkTime(wave[WaveNow],WaveNow);
        }else if(WaveNow ==4){
            wave[WaveNow] = new Client_Jpanel(4, 30, 1);
            add(wave[WaveNow]);
            checkTime(wave[WaveNow],WaveNow);
        }else if(WaveNow ==5){
            wave[WaveNow] = new Client_Jpanel(5, 35, 2);
            add(wave[WaveNow]);
            checkTime(wave[WaveNow],WaveNow);
        }
    }

    public  void checkTime(Client_Jpanel wave,int i){
        checkTime = new Timer();
        checkTime.schedule(new TimerTask() {
            @Override
            public void run() {
                if (wave.check_win()) {
                    wave.setWin();
                    System.out.println("You win");
                    checkTime.cancel();  // หยุดการตรวจสอบหลังจากเกมจบ
                    startGame();
                }
            }
        }, 0, 1000); 
    }
    

}
