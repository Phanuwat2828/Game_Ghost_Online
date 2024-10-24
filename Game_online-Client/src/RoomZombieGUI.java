import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.io.ObjectInputStream;

public class RoomZombieGUI extends JPanel {
    private JTextField nameRoomField;
    private JLabel yourIPLabel;
    private JPanel roomsPanel;
    // private List<Room> rooms;
    private String localIP;
    private PrintWriter out;
    private Map<String, String> ip_all;
    private Socket socket;
    private setting_ setting;
    private JPanel cardLayout;

    public RoomZombieGUI(JPanel cardLayout, setting_ setting) {
        setSize(1920, 1080);
        setLayout(new BorderLayout());
        this.setting = setting;
        this.cardLayout = cardLayout;

        // rooms = new ArrayList<>();
        localIP = getLocalIP();
        ReceiveIP rp_ip = new ReceiveIP(this);
        rp_ip.start();

        // สร้างพาเนลหลักพร้อมพื้นหลังสีเขียวเข้ม
        JPanel mainPanel = new JPanel() {
            String path_Bg = System.getProperty("user.dir") + File.separator + "Game_online-Client" + File.separator
                    + "src"
                    + File.separator + "Image";
            Image bg = Toolkit.getDefaultToolkit().createImage(path_Bg + File.separator + "Background_FirstPage.gif");

            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                // g.setColor(new Color(0, 50, 0));
                // g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        // ใช้ GridBagLayout สำหรับจัดวางองค์ประกอบให้อยู่กึ่งกลาง
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // สร้างพาเนลสำหรับเนื้อหาทั้งหมด
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        // เพิ่มชื่อเกมที่ด้านบน
        JLabel titleLabel = new JLabel("Room Zombie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 75));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        contentPanel.add(titleLabel, gbc);

        // สร้างพาเนลสำหรับแสดงรายการห้อง
        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.setPreferredSize(new Dimension(450, 500));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 20);
        contentPanel.add(scrollPane, gbc);

        // สร้างพาเนลด้านขวาสำหรับสร้างห้องใหม่
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        // เพิ่มฉลากและฟิลด์สำหรับชื่อห้อง
        JLabel nameRoomLabel = new JLabel("Name Room :");
        nameRoomLabel.setForeground(Color.WHITE);
        nameRoomLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 170);
        rightPanel.add(nameRoomLabel, gbc);

        nameRoomField = new JTextField(20);
        nameRoomField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(nameRoomField, gbc);

        // เพิ่มปุ่มสร้างห้อง
        JButton createButton = createStyledButton("Create");
        createButton.setFont(new Font("Arial", Font.BOLD, 16));

        JButton Back_first = createStyledButton("Back Menu");
        Back_first.setBackground(Color.RED);
        Back_first.setSize(50, 25);
        Back_first.setFont(new Font("Arial", Font.BOLD, 16));
        Back_first.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardLayout.getLayout());
                cl.show(cardLayout, "First"); // สลับไปยัง Room
            }

        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendtoserver();
                nameRoomField.setText(null);

            }

        });
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 170, 20, 0);
        rightPanel.add(createButton, gbc);

        yourIPLabel = new JLabel("Your IP : " + localIP);
        yourIPLabel.setForeground(Color.WHITE);
        yourIPLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 100);
        rightPanel.add(yourIPLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(Back_first, gbc);

        // แสดง IP ของผู้ใช้

        // เพิ่มพาเนลด้านขวาเข้าไปในพาเนลเนื้อหา
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 0);
        contentPanel.add(rightPanel, gbc);

        // เพิ่มพาเนลเนื้อหาเข้าไปในพาเนลหลัก
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(contentPanel, gbc);
        add(mainPanel);

    }

    public void setIp_all(Map<String, String> ip_all) {
        this.ip_all = ip_all;
        updateRoomsList();
    }

    private void sendtoserver() {
        String roomName = nameRoomField.getText();
        if (!roomName.isEmpty()) {
            try {

                setting.setIp(localIP);
                setting.setName(roomName);
                setting.setCreator(true);
                Server_01 server_01 = new Server_01(setting);
                server_01.start();
                Server02 server02 = new Server02(setting);
                server02.start();
               
                Client_Jpanel in_game = new Client_Jpanel(cardLayout, setting);
                cardLayout.add(in_game, "in_game");
                CardLayout cl = (CardLayout) (cardLayout.getLayout());
                cl.show(cardLayout, "in_game"); // สลับไปยัง Room

                socket = new Socket("localhost", 3000);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("set," + roomName + "," + localIP);
                System.out.println(roomName + localIP);

            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }
    }

    private void updateRoomsList() {
        roomsPanel.removeAll();

        for (Map.Entry<String, String> value : ip_all.entrySet()) {
            String name = value.getKey();
            String ip = value.getValue();
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(new BorderLayout());
            roomPanel.setOpaque(false);
            roomPanel.setMaximumSize(new Dimension(400, 80));

            JLabel nameLabel = new JLabel("#" + name);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            roomPanel.add(nameLabel, BorderLayout.NORTH);

            JLabel ipLabel = new JLabel("IP: " + ip);
            ipLabel.setForeground(Color.YELLOW);
            ipLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            roomPanel.add(ipLabel, BorderLayout.CENTER);

            JButton joinButton = createStyledButton("Join");
            joinButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setting.setIp(ip);
                    setting.setName(name);
                    Client_Jpanel in_game = new Client_Jpanel(cardLayout, setting);
                    cardLayout.add(in_game, "in_game");
                    CardLayout cl = (CardLayout) (cardLayout.getLayout());
                    cl.show(cardLayout, "in_game"); // สลับไปยัง Room
                }

            });
            joinButton.setPreferredSize(new Dimension(100, 40));
            joinButton.setFont(new Font("Arial", Font.BOLD, 16));
            roomPanel.add(joinButton, BorderLayout.EAST);

            roomsPanel.add(roomPanel);

        }
        roomsPanel.revalidate();
        roomsPanel.repaint();
    }

    // เมธอดสำหรับสร้างปุ่มที่มีสไตล์เฉพาะ
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x2B773F));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    // เมธอดสำหรับดึง IP ของเครื่องที่เปิดโปรแกรม
    private String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Unable to get IP";
        }
    }

}

class ReceiveIP extends Thread {
    private RoomZombieGUI panel;
    private boolean running = true;

    ReceiveIP(RoomZombieGUI panel) {
        this.panel = panel;
    }

    public void run() {
        while (running) {
            try (Socket socket = new Socket("localhost", 3000);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // รับข้อมูล Map<String, String> จากเซิร์ฟเวอร์
                Map<String, String> data = (Map<String, String>) in.readObject();
                panel.setIp_all(data);

                // รอ 50 มิลลิวินาทีเพื่อรับข้อมูลใหม่
                Thread.sleep(50);

            } catch (Exception e) {
                e.printStackTrace();
                // อาจเพิ่ม break หยุดการทำงานของ thread ในกรณีที่เกิดข้อผิดพลาด
            }
        }
    }

    // ฟังก์ชันหยุด thread
    public void stopReceiving() {
        running = false;
    }
}
