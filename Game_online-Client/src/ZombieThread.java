class ZombieThread extends Thread {
    private int index;
    private boolean running = true;
    private Client_Jpanel panel;

    public ZombieThread(int index, Client_Jpanel panel) {
        this.index = index;
        this.panel = panel;
    }

    @Override
    public void run() {
        while (running) {
            // panel.Zombie_Movement();

            if (!panel.isZombieAlive(index)) {
                stopZombie();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Zombie thread " + index + " died due to an error.");
                e.printStackTrace();
            }
        }
    }

    public void stopZombie() {
        running = false;
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
