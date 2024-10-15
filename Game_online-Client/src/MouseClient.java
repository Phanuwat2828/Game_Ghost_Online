import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class MouseClient extends JFrame {
    private static final String SERVER_IP = "26.12.207.51"; // เปลี่ยนเป็น IP ของ Server ถ้าไม่ใช่ localhost
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
                    String msg = "MOVE," + e.getX() + "," + e.getY();
                    out.println(msg);
                    // เราจะไม่อัพเดตตำแหน่งเมาส์ของตัวเอง เพราะ Server
                    // จะกระจายข้อมูลให้ทุกคนรวมถึงเรา
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
                    // คาดว่า message มีรูปแบบ "MOVE,clientId,x,y"
                    String[] parts = message.split(",");
                    if (parts.length == 4 && parts[0].equals("MOVE")) {
                        int id = Integer.parseInt(parts[1]);
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);

                        // ถ้า clientId ยังไม่ถูกกำหนด, กำหนดมันจากข้อความแรกที่รับมา
                        if (clientId == -1) {
                            clientId = id; // สมมติว่าข้อมูลแรกที่รับมาเป็นตัวเรา
                        }

                        // ถ้าไม่ใช่ตัวเราเอง, อัพเดตตำแหน่งเมาส์
                        if (id != clientId) {
                            updateRemoteMouse(id, x, y);
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
                    g.setColor(Color.RED);
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
