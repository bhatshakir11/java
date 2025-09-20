import java.util.Scanner;

class NumberPrinter implements Runnable {
    private int start, max;

    public NumberPrinter(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public void run() {
        for (int i = start; i <= max; i += 2) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try { Thread.sleep(100); } 
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}

public class EvenOddPrinter {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter max number: ");
        int max = sc.nextInt();

        Thread even = new Thread(new NumberPrinter(0, max), "Even");
        Thread odd = new Thread(new NumberPrinter(1, max), "Odd");

        even.start();
        odd.start();
        sc.close();
    }
}
