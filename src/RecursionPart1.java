import java.util.Scanner;

public class RecursionPart1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        main(n);


    }
    // When a function call itself until a condition is met is called Recursion

    static void main(int n){

        while(n < 1000000000){
            n++;
            main(n);
        }

    }

}
