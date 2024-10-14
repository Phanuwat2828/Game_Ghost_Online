import java.io.Serializable;

public class DB_ implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String content;
    private int xy[] = new int[2];

    DB_() {

    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setX(int x, int y) {
        xy[0] = x;
        xy[1] = y;
    }

    public int[] getXy() {
        return xy;
    }

    @Override
    public String toString() {
        return " " + this.sender + " : " + this.content;
    }

}
