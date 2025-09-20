import java.util.Scanner;

interface NumberTransformer {
    void transform(int n);
}

class SquareTransformer implements NumberTransformer {
    public void transform(int n) {
        System.out.println("Square: " + (n * n));
    }
}

class CubeTransformer implements NumberTransformer {
    public void transform(int n) {
        System.out.println("Cube: " + (n * n * n));
    }
}

public class NumberTransform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int n = sc.nextInt();

        new SquareTransformer().transform(n);
        new CubeTransformer().transform(n);

        sc.close();
    }
}

