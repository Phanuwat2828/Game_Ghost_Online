import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server_01 {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    private static AtomicInteger clientIdCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server เริ่มต้นแล้ว รอการเชื่อมต่อ...");
            while (true) {
                Socket socket = serverSocket.accept();
                int clientId = clientIdCounter.incrementAndGet();
                System.out.println("Client #" + clientId + " เชื่อมต่อ: " + socket.getInetAddress());
                ClientHandler handler = new ClientHandler(socket, clientId);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                if (aClient != newClient && aClient.lastMousePosition.isPresent()) {
                    Point pos = aClient.lastMousePosition.get();
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
        public Optional<Point> lastMousePosition = Optional.empty();

        public ClientHandler(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
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
                    System.out.println("ได้รับจาก Client #" + clientId + ": " + message);
                    // คาดว่า message มีรูปแบบ "MOVE,x,y"
                    String[] parts = message.split(",");
                    if (parts.length == 3 && parts[0].equals("MOVE")) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        lastMousePosition = Optional.of(new Point(x, y));
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
