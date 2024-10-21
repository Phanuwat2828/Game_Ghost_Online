public class MultiServer {
    public static void main(String[] args) {
        // สร้างและเริ่มการทำงานของ Server_01 ใน thread ใหม่
        Thread server01Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Server_01.main(null);
            }
        });
        server01Thread.start();

        // สร้างและเริ่มการทำงานของ Server02 ใน thread ใหม่
        // Thread server02Thread = new Thread(new Runnable() {
        // @Override
        // public void run() {
        // Server02.main(null);
        // }
        // });
        // server02Thread.start();
    }
}
