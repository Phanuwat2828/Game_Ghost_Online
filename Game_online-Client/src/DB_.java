import java.io.Serializable;

public class DB_ implements Serializable{
    private static final long serialVersionUID = 1L;
    private String sender ;
    private String content;

    DB_(String sender,String content){
        this.sender = sender;
        this.content = content;
    }

    public String getSender(){
        return sender;
    }
    public String getContent(){
        return content;
    }
    @Override
    public String toString() {
        return " "+this.sender + " : "+ this.content;
    }

}