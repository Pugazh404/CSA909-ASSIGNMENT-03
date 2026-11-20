class ScannerStation extends Thread {
    private final int scripts;
    ScannerStation(String name, int scripts) { super(name); this.scripts = scripts; }
    public void run() {
        try {
            for (int i = 0; i < scripts; i++) Thread.sleep(100);
            System.out.println(getName() + " scanned " + scripts + " scripts.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(getName() + " interrupted");
        }
    }
}
class RunnableStation implements Runnable {
    private final String name; private final int scripts;
    RunnableStation(String name, int scripts) { this.name=name; this.scripts=scripts; }
    public void run() {
        try {
            for (int i=0; i<scripts; i++) Thread.sleep(80);
            System.out.println(name + " scanned " + scripts + " scripts.");
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
public class ScannerStations {
    public static void main(String[] args) throws InterruptedException {
        Thread[] stations = {
            new ScannerStation("Station 1",5),
            new Thread(new RunnableStation("Station 2",6)),
            new ScannerStation("Station 3",4),
            new Thread(new RunnableStation("Station 4",7))
        };
        for (Thread t : stations) t.start();
        for (Thread t : stations)
            System.out.println(t.getName() + " alive: " + t.isAlive());
        for (Thread t : stations) t.join();
        System.out.println("All stations completed. Total scripts scanned: 22");
    }
}