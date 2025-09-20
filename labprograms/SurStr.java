import java.util.Scanner;

public class SurStr {
    static String sur(String s, String w) {
        StringBuilder result = new StringBuilder();
        String input = s.toLowerCase();
        String word = w.toLowerCase();
        int wordLen = w.length();
        int strLen= s.length();
        for (int i = 0; i <= strLen - wordLen; i++) {
            if (input.substring(i, i + wordLen).equals(word)) {
                if (i > 0) {
                    result.append(s.charAt(i - 1));
                }
                if (i + wordLen < strLen) {
                    result.append(s.charAt(i + wordLen));
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        String w = scanner.nextLine();
        System.out.println(sur(s, w));
        scanner.close();
    }
}
