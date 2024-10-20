import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class first_page extends JFrame {
    public static void main(String[] args) {
        first_page window = new first_page();
        MyPanel_background bg = new MyPanel_background();
        window.add(bg);

        JButton start = new JButton("Start");
        JButton developer = new JButton("Developer");
        JButton exit = new JButton("Exit");

        setButtonStyle(start);
        setButtonStyle(developer);
        setButtonStyle(exit);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Close the application
            }
        });

        bg.setLayout(null);
        start.setBounds(670, 350, 200, 50);
        developer.setBounds(670, 450, 200, 50);
        exit.setBounds(670, 550, 200, 50);

        bg.add(start);
        bg.add(developer);
        bg.add(exit);
        window.setVisible(true);
    }

    static void setButtonStyle(JButton button) {
        Font f = new Font("Tahoma", Font.PLAIN, 35);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(f);
    }

    first_page() {
        setTitle("My Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
    }
}

class MyPanel_background extends JPanel {
    // Image bg = new ImageIcon(System.getProperty("user.dir") + File.separator +
    // "Background_firstPage.gif").getImage();
    Font f = new Font("Tahoma", Font.PLAIN, 75);

    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "Image";
    Image bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background_FirstPage.gif");

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.WHITE);
        g.setFont(f);
        g.drawString("Zombie Remake", 520, 220);
    }

}