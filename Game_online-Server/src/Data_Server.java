import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Data_Server
 */
public class Data_Server {
    private int Mouse_p[] = new int[10];
    private boolean Status_conecttion[] = new boolean[10];
    private static Map<String, int[]> clientPositions = new HashMap<>();

    Data_Server() {

    }

    public void setclientPositions(String key, int[] value) {
        clientPositions.put(key, value);
    }

    public int[] getMouse_p(String key) {
        if (clientPositions.containsKey(key)) {
            int[] position = clientPositions.get(key); // ดึงค่าตำแหน่ง X และ Y
            return position;
        } else {
            return new int[2];
        }

    }

    public static Map<String, int[]> getClientPositions() {
        return clientPositions;
    }

}
// clientPositions.put(clientKey, new int[] {x, y});