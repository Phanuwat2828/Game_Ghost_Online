import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.CardLayout;

public class Client_Landing extends JPanel {
    boolean bg_layout = false;
    Font f = new Font("Tahoma", Font.PLAIN, 75);

    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "Image";
    Image bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background_FirstPage.gif");

    Client_Landing(JPanel cardPanel) {
        JPanel Center = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel name_game = new JPanel();
        JPanel space = new JPanel();
        JPanel start_bt = new JPanel();
        JPanel dev = new JPanel();
        JPanel Exit = new JPanel();

        space.setPreferredSize(new Dimension(700, 150));
        name_game.setPreferredSize(new Dimension(700, 200));
        start_bt.setPreferredSize(new Dimension(700, 75));
        dev.setPreferredSize(new Dimension(700, 75));
        Exit.setPreferredSize(new Dimension(700, 75));

        if (bg_layout) {
            Center.setBackground(Color.RED);
            space.setBackground(Color.BLUE);
            name_game.setBackground(Color.CYAN);
            start_bt.setBackground(Color.GREEN);
            dev.setBackground(Color.ORANGE);
            Exit.setBackground(Color.MAGENTA);
        } else {
            Center.setOpaque(false);
            space.setOpaque(false);
            name_game.setOpaque(false);
            start_bt.setOpaque(false);
            dev.setOpaque(false);
            Exit.setOpaque(false);
        }

        Center.setPreferredSize(new Dimension(700, 1080));
        setPreferredSize(new Dimension(1920, 1080));
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel game_name = new JLabel("Zombie Remake");
        game_name.setFont(f);
        game_name.setForeground(Color.white);
        JButton start = new JButton("Start");

        // ใช้การเพิ่มการเชื่อมโยงกับ RoomZombieGUI ที่สร้างไว้ก่อนหน้า
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, "Room"); // สลับไปยัง Room
            }
        });

        JButton developer = new JButton("Developer");
        developer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, "dev"); // สลับไปยัง Room
            }

        });
        JButton exit = new JButton("Exit");

        name_game.add(game_name);
        start_bt.add(start);
        dev.add(developer);
        Exit.add(exit);

        setButtonStyle(start);
        setButtonStyle(developer);
        setButtonStyle(exit);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // ปิดแอปพลิเคชัน
            }
        });

        Center.add(space);
        Center.add(name_game);
        Center.add(start_bt);
        Center.add(dev);
        Center.add(Exit);
        add(Center);

    }

    public void setButtonStyle(JButton button) {
        Font f = new Font("Tahoma", Font.PLAIN, 35);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // อย่าลืมเรียก super.paintComponent(g) ด้วย
        g.drawImage(bg, 0, 0, 1920, 1080, this);
    }
}

// การสร้าง JFrame และ cardPa

// class MyPanel_background extends JPanel {
// // Image bg = new ImageIcon(System.getProperty("user.dir") + File.separator +
// // "Background_firstPage.gif").getImage();
// Font f = new Font("Tahoma", Font.PLAIN, 75);

// String path_Bg = System.getProperty("user.dir") + File.separator +
// "Game_online-Client" + File.separator + "src"
// + File.separator + "Image";
// Image bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator +
// "Background_FirstPage.gif");

// @Override
// protected void paintComponent(Graphics g) {
// g.drawImage(bg, 0, 0, 1920, 1080, this);
// g.setColor(Color.WHITE);
// g.setFont(f);
// g.drawString("Zombie Remake", 520, 220);
// }

// }