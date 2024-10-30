import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.geom.*;
import java.io.File;

public class Devolop extends JPanel {
    private Image backgroundImage;
    String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator + "src"
            + File.separator + "Image";
    Image bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background_FirstPage.gif");

    public Devolop(JPanel cardLayout) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setSize(1920, 1080);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        top.setPreferredSize(new Dimension(1920, 200));
        // top.setOpaque(false);
        JPanel bootom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        bootom.setPreferredSize(new Dimension(1920, 600));
        JPanel last = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 50));
        JButton bt_left = new JButton("Back");
        bt_left.setBackground(Color.BLACK);
        bt_left.setForeground(Color.WHITE);
        bt_left.setFont(new Font("Tahoma", Font.PLAIN, 20));
        bt_left.setPreferredSize(new Dimension(200, 70));
        bt_left.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardLayout.getLayout());
                cl.show(cardLayout, "First");
            }

        });
        last.add(bt_left);
        last.setPreferredSize(new Dimension(1920, 200));
        top.setOpaque(false);
        bootom.setOpaque(false);
        last.setOpaque(false);

        // bootom.setOpaque(false);
        JLabel titleLabel = new JLabel("Developer", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 75));
        titleLabel.setForeground(new Color_all().cl_bg_white);
        top.add(titleLabel);
        top.setBackground(Color.BLUE);
        bootom.setBackground(Color.RED);

        // Create the card panel to hold student cards using BackgroundPanel
        BackgroundPanel cardPanel = new BackgroundPanel(backgroundImage);
        cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
        cardPanel.setPreferredSize(new Dimension(1500, 600));
        cardPanel.setOpaque(false);

        // Create a button panel for the 'Back' button using BackgroundPanel
        BackgroundPanel buttonPanel = new BackgroundPanel(backgroundImage);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 50, 0));
        buttonPanel.setPreferredSize(new Dimension(1300, 100));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.setBackground(new Color_all().cl_b);
        backButton.setForeground(new Color_all().cl_bg_white);

        // Add the student cards
        cardPanel.add(createStudentCard(Devolop.class.getResource("/Image/Night.jpg"), "ภานุวัฒน์ คำทา", "66011212124",
                "ORGANIZER"));
        cardPanel.add(createStudentCard(Devolop.class.getResource("/Image/lek.jpg"), "ชัยวรรณ วิเศษรัตน์",
                "66011212016", ""));
        cardPanel.add(
                createStudentCard(Devolop.class.getResource("/Image/nate.jpg"), "จิรัชยา พันอุ่น", "66011212079", ""));
        cardPanel.add(createStudentCard(Devolop.class.getResource("/Image/ju.jpg"), "กันยาพร รุ่งแสง", "66011212074",
                "ตำแหน่ง"));

        // Set the background color of the panels
        buttonPanel.setBackground(new Color_all().cl_bg);

        // Add the button to the button panel
        buttonPanel.add(backButton);
        bootom.add(cardPanel);
        add(top);
        add(bootom);
        add(last);
        // add(buttonPanel, BorderLayout.SOUTH);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // อย่าลืมเรียก super.paintComponent(g) ด้วย
        g.drawImage(bg, 0, 0, 1920, 1080, this);
    }

    private JPanel createStudentCard(URL imageUrl, String name, String studentId, String title_) {
        JPanel card = new JPanel(null);
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 500));

        ImageIcon imageIcon = new ImageIcon(imageUrl);
        Image image = imageIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        RoundedImageLabel imageLabel = new RoundedImageLabel(new ImageIcon(image), 30);
        imageLabel.setBounds(25, 0, 250, 250);

        if (imageUrl == null || imageIcon.getIconWidth() == -1) {
            imageLabel.setText("Image not found");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);
            imageLabel.setForeground(Color.RED);
            imageLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        }

        // สร้าง panel สำหรับชื่อและรหัสนักศึกษา
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setBackground(new Color(0, 0, 0, 150)); // สีพื้นหลังสีดำโปร่งแสง
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color_all().cl_bg_white, 2, true));

        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        JLabel idLabel = new JLabel(studentId, JLabel.CENTER);
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
        nameLabel.setForeground(new Color_all().cl_bg_white);
        idLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
        idLabel.setForeground(new Color_all().cl_bg_white);

        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.setBounds(25, 340, 250, 80);

        card.add(imageLabel);
        card.add(infoPanel);

        return card;
    }

}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image image) {
        this.backgroundImage = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

class Color_all {
    public Color cl_bg = new Color(240, 240, 240);
    public Color cl_bg_bu = new Color(30, 144, 255);
    public Color cl_bg_p = new Color(128, 0, 128);
    public Color cl_bg_white = Color.WHITE;
    public Color cl_b = Color.BLACK;
}

class RoundedImageLabel extends JLabel {
    private Shape shape;
    private int arcSize;

    public RoundedImageLabel(Icon image, int arcSize) {
        super(image);
        this.arcSize = arcSize;
        setOpaque(false);
    }

}