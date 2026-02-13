package Maths;

public class Seive {
    public static void main(String[] args) {
        int n = 100;
        boolean[] primes = new boolean[n + 1];
        sieve(n, primes);

    }

    public static void sieve(int n, boolean[] prime){
        for(int i = 2; i*i <= n; i++){
            if(!prime[i]){
                for(int j = i*2; j <= n; j+= i){
                    prime[j] = true;
                }
            }
        }

        for(int i = 2; i <= n; i++){
            if(!prime[i]){
                System.out.println(i + " " );
            }

        }

    }



}
