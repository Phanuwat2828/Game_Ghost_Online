import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Map;
import java.util.LinkedHashMap;
import java.net.InetAddress;

public class A_Room_Server {
    // รายการของ clients ทั้งหมด
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        InetAddress ip = InetAddress.getLocalHost();
        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("Room Server Port 3000 : ip " + ip.getHostAddress());
        Data_ip data = new Data_ip();
        while (true) {
            // รับการเชื่อมต่อใหม่จาก client
            Socket clientSocket = serverSocket.accept();
            // เพิ่ม PrintWriter ของ client ที่เชื่อมต่อเพื่อส่งข้อมูลกลับ
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(out);
            // send_ send = new send_(clientSocket, data);
            // new Thread(send).start();
            handleClient_ send = new handleClient_(clientSocket, data);
            new Thread(send).start();
        }
    }

    // ฟังก์ชันจัดการข้อมูลที่ได้รับจาก client

    // ฟังก์ชันส่งข้อมูลให้ทุก client

}

class Data_ip {
    private Map<String, String> ip = new LinkedHashMap<>();

    public void setSend(String[] msg) {

        setMap(msg);
        System.out.println(msg[0] + ":" + msg[1] + ":" + msg[2]);
    }

    public void setMap(String[] value) {
        this.ip.put(value[1], value[2]);

    }

    public void remove(String value) {
        this.ip.remove(value);
        System.out.println("Remove" + ":" + "Room : " + value);
    }

    public Map<String, String> getIp() {
        return ip;
    }
}

class handleClient_ implements Runnable {
    private Socket socket;
    private Data_ip data;

    handleClient_(Socket clientSocket, Data_ip data) {
        this.socket = clientSocket;
        this.data = data;
    }

    @Override
    public void run() {
        try {

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(data.getIp());
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputData;

            // รับข้อมูลจาก client
            while ((inputData = in.readLine()) != null) {
                String[] name_ip = new String[3];
                name_ip = inputData.split(",");
                if (name_ip[0].equals("Remove")) {
                    data.remove(name_ip[1]);
                } else {
                    data.setSend(name_ip);
                }

            }

            in.close();
            out.flush(); // ควรเรียก flush เพื่อให้มั่นใจว่าข้อมูลถูกส่งออกไป
            out.close();
            socket.close();

        } catch (IOException e) {

        }
    }

}