import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class RoomZombieGUI extends JFrame {
    private JTextField nameRoomField;
    private JLabel yourIPLabel;
    private JPanel roomsPanel;
    private List<Room> rooms;
    private String localIP;

    public RoomZombieGUI() {
        // ตั้งค่าหน้าต่างหลัก
        setTitle("Room Zombie");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        rooms = new ArrayList<>();
        localIP = getLocalIP();

        // สร้างพาเนลหลักพร้อมพื้นหลังสีเขียวเข้ม
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 50, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
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
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(titleLabel, gbc);

        // สร้างพาเนลสำหรับแสดงรายการห้อง
        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.setPreferredSize(new Dimension(450, 500));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
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
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(nameRoomLabel, gbc);

        nameRoomField = new JTextField(20);
        nameRoomField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(nameRoomField, gbc);

        // เพิ่มปุ่มสร้างห้อง
        JButton createButton = createStyledButton("Create");
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.addActionListener(e -> createRoom());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(createButton, gbc);

        // แสดง IP ของผู้ใช้
        yourIPLabel = new JLabel("Your IP : " + localIP);
        yourIPLabel.setForeground(Color.WHITE);
        yourIPLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 3;
        rightPanel.add(yourIPLabel, gbc);

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

        // เพิ่มพาเนลหลักเข้าไปในหน้าต่าง
        add(mainPanel);

        // จัดตำแหน่งหน้าต่างให้อยู่กลางจอ
        setLocationRelativeTo(null);

        // เพิ่มห้องตัวอย่าง
        // addRoom(new Room("#Leknaja", "192.168.2.3"));
        // addRoom(new Room("#KaiJa", "192.168.4.3"));
    }

    // เมธอดสำหรับสร้างห้องใหม่
    private void createRoom() {
        String roomName = nameRoomField.getText();
        if (!roomName.isEmpty()) {
            Room newRoom = new Room(roomName, localIP);
            addRoom(newRoom);
            nameRoomField.setText("");
        }
    }

    // เมธอดสำหรับเพิ่มห้องใหม่เข้าไปในรายการ
    private void addRoom(Room room) {
        rooms.add(room);
        updateRoomsList();
    }

    // เมธอดสำหรับอัพเดทรายการห้องใน GUI
    private void updateRoomsList() {
        roomsPanel.removeAll();
        for (Room room : rooms) {
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(new BorderLayout());
            roomPanel.setOpaque(false);
            roomPanel.setMaximumSize(new Dimension(400, 80));

            JLabel nameLabel = new JLabel(room.getName());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            roomPanel.add(nameLabel, BorderLayout.NORTH);

            JLabel ipLabel = new JLabel("IP: " + room.getIp());
            ipLabel.setForeground(Color.YELLOW);
            ipLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            roomPanel.add(ipLabel, BorderLayout.CENTER);

            JButton joinButton = createStyledButton("Join");
            joinButton.setPreferredSize(new Dimension(100, 40));
            joinButton.setFont(new Font("Arial", Font.BOLD, 16));
            roomPanel.add(joinButton, BorderLayout.EAST);

            roomsPanel.add(roomPanel);
            roomsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
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

    // คลาสภายในสำหรับเก็บข้อมูลของแต่ละห้อง
    private static class Room {
        private String name;
        private String ip;

        public Room(String name, String ip) {
            this.name = name;
            this.ip = ip;
        }

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }
    }

    // เมธอดหลักสำหรับเริ่มต้นโปรแกรม
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoomZombieGUI gui = new RoomZombieGUI();
            gui.setVisible(true);
        });
    }
}