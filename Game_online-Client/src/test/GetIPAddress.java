package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIPAddress {
    public static void main(String[] args) {
        try {
            // ดึง IP Address ของเครื่องตัวเอง
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("IP Address ของเครื่อง: " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
