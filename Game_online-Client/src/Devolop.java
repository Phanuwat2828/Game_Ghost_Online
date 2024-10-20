import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.geom.*;

public class Devolop extends JPanel {
    private Image backgroundImage;

    public Devolop(CardLayout cardLayout, JPanel MainPanel) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1300, 750));

        // Load the background image
        backgroundImage = new ImageIcon(Devolop.class.getResource("/image/Background_FirstPage.gif")).getImage();

        // Create the top panel for the title using BackgroundPanel
        BackgroundPanel titlePanel = new BackgroundPanel(backgroundImage) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color_all().cl_bg_white); // กำหนดสีกรอบ
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1); // วาดกรอบสี่เหลี่ยม
            }
        };

        titlePanel.setLayout(new GridBagLayout()); // ใช้ GridBagLayout เพื่อจัดตำแหน่งให้กลาง
        JLabel titleLabel = new JLabel("DEVELOPER", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
        titleLabel.setForeground(new Color_all().cl_bg_white);
        titlePanel.add(titleLabel);
        titlePanel.setOpaque(false);
        titlePanel.setPreferredSize(new Dimension(300, 100)); // ปรับขนาดของ titlePanel ให้เหมาะสม

        // Create the card panel to hold student cards using BackgroundPanel
        BackgroundPanel cardPanel = new BackgroundPanel(backgroundImage);
        cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
        cardPanel.setPreferredSize(new Dimension(1300, 600));

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
        cardPanel.add(createStudentCard(Devolop.class.getResource("/image/Night.jpg"), "ภานุวัฒน์ คำทา", "66011212124", "ORGANIZER"));
        cardPanel.add(createStudentCard(Devolop.class.getResource("/image/lek.jpg"), "ชัยวรรณ วิเศษรัตน์", "66011212016", ""));
        cardPanel.add(createStudentCard(Devolop.class.getResource("/image/nate.jpg"), "จิรัชยา พันอุ่น", "66011212079", ""));
        cardPanel.add(createStudentCard(Devolop.class.getResource("/image/ju.jpg"), "กันยาพร รุ่งแสง", "66011212074", "ตำแหน่ง"));

        // Set the background color of the panels
        buttonPanel.setBackground(new Color_all().cl_bg);
        
        // Add the button to the button panel
        buttonPanel.add(backButton);

        // Add the title, cards, and button panel to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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


    public static void main(String[] args) {
        JFrame frame = new JFrame("Developer Info");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel MainPanel = new JPanel(new CardLayout());
        CardLayout cardLayout = (CardLayout) MainPanel.getLayout();

        MainPanel.add(new Devolop(cardLayout, MainPanel), "Devolop");

        frame.add(MainPanel);
        frame.setVisible(true);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcSize, arcSize);
        g2.setClip(shape);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.draw(shape);
        g2.dispose();
    }
}
