package Bitwise;

public class FindUnique {
    public static void main(String[] args) {
        int[] arr = {1,3,1,3,4,4,6,5,7,5,7};
//        int[] arr = {-1, 1, 4, -4, 3, 2, -2};
        System.out.println(ans(arr));

    }
    static int ans(int[] arr) {
        int unique = 0;
        for(int n : arr){
            unique ^= n;
        }
        return unique;

    }


}
