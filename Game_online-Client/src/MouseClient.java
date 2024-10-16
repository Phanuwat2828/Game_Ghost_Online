import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class MouseClient extends JFrame {
    private static final String SERVER_IP = "localhost"; // เปลี่ยนเป็น IP ของ Server ถ้าไม่ใช่ localhost
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JLabel statusLabel;
    private Map<Integer, Point> remoteMousePositions = Collections.synchronizedMap(new HashMap<>());
    private int clientId = -1; // ใช้ในการระบุว่าเป็น Client ตัวไหน

    private DrawingPanel drawingPanel;

    public MouseClient() {
        setTitle("Mouse Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusLabel = new JLabel("เชื่อมต่อกับ Server...");
        add(statusLabel, BorderLayout.SOUTH);

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // เริ่ม Thread สำหรับรับข้อมูลจาก Server
            new Thread(new IncomingReader()).start();

            // เพิ่ม Mouse Motion Listener เพื่อจับการเคลื่อนไหวของเมาส์
            drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (clientId != -1) { // ตรวจสอบว่ามี clientId แล้ว
                        String msg = "MOVE," + e.getX() + "," + e.getY();
                        out.println(msg);
                        // ไม่ต้องอัพเดตตำแหน่งเมาส์ของตัวเองที่นี่ เพราะจะได้รับจาก Server
                    }
                }
            });

            statusLabel.setText("เชื่อมต่อกับ Server สำเร็จ");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("ไม่สามารถเชื่อมต่อกับ Server ได้");
        }
    }

    // แสดงตำแหน่งเมาส์ของ Client อื่น
    private void updateRemoteMouse(int id, int x, int y) {
        remoteMousePositions.put(id, new Point(x, y));
        drawingPanel.repaint();
    }

    // ลบตำแหน่งเมาส์เมื่อ Client อื่น Disconnect
    private void removeRemoteMouse(int id) {
        remoteMousePositions.remove(id);
        drawingPanel.repaint();
    }

    class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // ประมวลผลข้อความที่ได้รับ
                    // คาดว่า message มีรูปแบบ "ID,clientId" หรือ "MOVE,clientId,x,y" หรือ
                    // "DISCONNECT,clientId"
                    String[] parts = message.split(",");
                    if (parts.length >= 2) {
                        switch (parts[0]) {
                            case "ID":
                                // กำหนด clientId ของตัวเอง
                                clientId = Integer.parseInt(parts[1]);
                                System.out.println("ได้รับ clientId ของตัวเอง: " + clientId);
                                break;
                            case "MOVE":
                                if (parts.length == 4) {
                                    int id = Integer.parseInt(parts[1]);
                                    int x = Integer.parseInt(parts[2]);
                                    int y = Integer.parseInt(parts[3]);
                                    updateRemoteMouse(id, x, y);
                                }
                                break;
                            case "DISCONNECT":
                                if (parts.length == 2) {
                                    int id = Integer.parseInt(parts[1]);
                                    removeRemoteMouse(id);
                                }
                                break;
                            default:
                                System.out.println("ข้อความที่ไม่รู้จัก: " + message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class DrawingPanel extends JPanel {
        public DrawingPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            synchronized (remoteMousePositions) {
                for (Map.Entry<Integer, Point> entry : remoteMousePositions.entrySet()) {
                    int id = entry.getKey();
                    Point p = entry.getValue();
                    if (id == clientId) {
                        g.setColor(Color.BLUE); // สีสำหรับเมาส์ของตัวเอง
                    } else {
                        g.setColor(Color.RED); // สีสำหรับเมาส์ของ Client อื่น
                    }
                    g.fillOval(p.x - 5, p.y - 5, 10, 10);
                    g.drawString("Client #" + id, p.x + 5, p.y - 5);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MouseClient client = new MouseClient();
            client.setVisible(true);
        });
    }
}
