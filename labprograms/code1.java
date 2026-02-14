import java.util.*;
public class code1{
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter a strin:");
        String str=sc.nextLine();
        System.out.println("Enter the 2nd string:");
        String str2=sc.nextLine();
        for(int i=0;i<str.length()&&i<str2.length();i++){
            System.out.print(str.charAt(i));
            
        }
        
    }
}