import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

class MyThread implements Runnable {
    private int[] axisX;
    private int []axisY;
    private int []speedX;
    private int []health;
    private boolean []Alive;
    private JPanel panel;
    private Image[] zombieFrames;

    public MyThread(int[] axisX, int[] axisY, int[] speed, JPanel panel, Image[] zombieFrames) {
        axisX = new int [30];
        axisY = new int [30];
        this.panel = panel;
        this.Alive = new boolean[30];
        this.speedX = new int[30];
        this.health = new int[30];
        this.zombieFrames = new Image[30];
    }

    @Override
    public void run() {
        for(int i=0; i<30; i++) {
            if (Alive[i]) {
                moveZombie();
                panel.repaint();

            }
            if (health[i] <= 0) {
                Alive[i] = false;

            }
        }
    }

    public void moveZombie() {
        for(int i=0; i<30; i++) {
        axisX[i] += speedX[i];
        if (axisX[i] > panel.getWidth()) {
            }
        }
    }

    public void damageZombie(int damage) {
    }

    public boolean isAlive() {
        for(int i=0; i < 30 ; i++){
            
        }
    }

    public void draw(Graphics g, int frame) {
        for(int i=0; i<30; i++){
            g.drawImage(zombieFrames[frame], axisX[i], axisY[i], 100, 100, panel);
        }
    }
}
