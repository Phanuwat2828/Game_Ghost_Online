public class setting_ {
    private boolean creator = false;
    private String ip;
    private String name;
    private boolean ready = false;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    public boolean getCreator() {
        return creator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean getReady() {
        return this.ready;
    }
}
