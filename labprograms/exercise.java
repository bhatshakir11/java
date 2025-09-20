import java.util.*;
public class exercise {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("enter num:");
        int n=sc.nextInt();
        int i=2;
        int count=0;
        while(i<=n-1 && n!=2){
            if(n%i==0){
                count++;
            }
            if(count>=1){
                break;
            }
            i++;
        }
        if(count>=1){
            System.out.println("number is not prime:");
        }
        else{
            System.out.println("number is prime");
        }

        
        
       
    }
    
}
