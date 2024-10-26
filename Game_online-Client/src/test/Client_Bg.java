package test;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Client_Bg extends JFrame {
    public static void main(String[] args) {
        Client_Bg bg = new Client_Bg();
        // Client_Jpanel panel = new Client_Jpanel();

        // bg.add(panel);
        bg.setVisible(true);
    }

    Client_Bg() {
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
