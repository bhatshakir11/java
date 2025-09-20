import java.util.*;
public class Roman {
        public static void main(String[] args) {
        int[] arr={7,5,3,6,8};
        for(int i=0;i<arr.length-1;i++){
            int smallest=i;
            for(int j=i+1;j<arr.length;j++){
                if(arr[smallest]>arr[j]){
                    smallest=j;
                }
            }
            int temp=arr[smallest];
            arr[smallest]=arr[i];
            arr[i]=temp;
        }
        System.out.print(Arrays.toString(arr));
    }
}
