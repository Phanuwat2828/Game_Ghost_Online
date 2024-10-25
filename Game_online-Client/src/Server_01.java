import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.Thread;

public class Server_01 extends Thread {
    private static final int PORT = 8000;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    private static AtomicInteger clientIdCounter = new AtomicInteger(0);
    private setting_ setting;
    private boolean running = true;
    private Map<String, Integer> ip_all = new LinkedHashMap<>();
    private data_Mouse data = new data_Mouse();
    private ServerSocket serverSocket;
    private Socket socket;

    Server_01(setting_ setting) {
        this.setting = setting;
    }

    public void run() {

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server เริ่มต้นแล้ว รอการเชื่อมต่อ...");
            while (running) {
                socket = serverSocket.accept();
                int clientId;
                String clientIp = socket.getInetAddress().getHostAddress();
                if (ip_all.containsKey(clientIp)) {
                    clientId = ip_all.get(clientIp);

                } else {
                    clientId = clientIdCounter.incrementAndGet();
                    ip_all.put(clientIp, clientId);
                }
                System.out.println("Client #" + clientId + " เชื่อมต่อ: " + socket.getInetAddress());
                ClientHandler handler = new ClientHandler(socket, clientId, data);
                clientHandlers.add(handler);
                new Thread(handler).start();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // ปิด serverSocket เพื่อหยุด accept()
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }

    // ส่งข้อมูลไปยังทุก Client รวมถึงผู้ส่ง
    public static void broadcast(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler aClient : clientHandlers) {
                aClient.sendMessage(message);
            }
        }
    }

    // ส่งข้อมูลถึงผู้เชื่อมต่อใหม่เกี่ยวกับตำแหน่งเมาส์ที่มีอยู่
    public static void sendExistingMousePositions(ClientHandler newClient) {
        synchronized (clientHandlers) {
            for (ClientHandler aClient : clientHandlers) {
                if (aClient != newClient && aClient.data.getLastMousePosition().isPresent()) {
                    Point pos = aClient.data.getLastMousePosition().get();
                    newClient.sendMessage("MOVE," + aClient.clientId + "," + pos.x + "," + pos.y);
                }
            }
        }
    }

    // ลบ Client ออกจากเซ็ตเมื่อ Disconnect
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client #" + clientHandler.clientId + " ถูกลบ: " + clientHandler.socket.getInetAddress());
        // แจ้งให้ Client อื่นรู้ว่า Client นี้ได้ออกไป
        broadcast("DISCONNECT," + clientHandler.clientId);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int clientId;
        private data_Mouse data;

        public ClientHandler(Socket socket, int clientId, data_Mouse data) {
            this.socket = socket;
            this.clientId = clientId;
            this.data = data;
            try {
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                out = new PrintWriter(this.socket.getOutputStream(), true);
                // ส่ง Client ID ให้กับ Client
                out.println("ID," + this.clientId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            // ส่งข้อมูลตำแหน่งเมาส์ที่มีอยู่ให้กับ Client ใหม่
            sendExistingMousePositions(this);
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // System.out.println("ได้รับจาก Client #" + clientId + ": " + message);
                    // คาดว่า message มีรูปแบบ "MOVE,x,y"
                    String[] parts = message.split(",");
                    if (parts.length == 3 && parts[0].equals("MOVE")) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        data.setLastMousePosition(Optional.of(new Point(x, y)));
                        // ส่งต่อข้อมูลให้ทุก Client พร้อมระบุว่าเป็น Client ไหน
                        broadcast("MOVE," + clientId + "," + x + "," + y);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Server_01.removeClient(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class data_Mouse {
    private Optional<Point> lastMousePosition = Optional.empty();

    public Optional<Point> getLastMousePosition() {
        return lastMousePosition;
    }

    public void setLastMousePosition(Optional<Point> lastMousePosition) {
        this.lastMousePosition = lastMousePosition;
    }
}
