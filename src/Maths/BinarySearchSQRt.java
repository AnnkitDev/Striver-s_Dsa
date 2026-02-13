package Maths;


public class BinarySearchSQRt {
    public static void main(String[] args) {
        int n = 40;
        int p = 3;


        System.out.printf("%.3f", sqrt(n,p));
    }

    static double sqrt(int n, int p) {
        int start = 0;
        int end = n ;
        double ans = 0.0 ;

        while(start <= end){
            int mid  = start + ( end - start ) / 2;

            if(mid * mid == n){
                ans = mid;
                return ans;
            }

            if(mid * mid < n){
                start = mid + 1;
            }else {
                end = mid - 1;

            }
        }

        double incr = 0.1;
        for (int i = 0; i < p; i++) {
            while (ans * ans < n){
            ans += incr;
            }
            ans -= incr;;
            incr /= 10;

        }

        return ans;
    }
}
