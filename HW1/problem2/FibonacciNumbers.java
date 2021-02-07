public class FibonacciNumbers {
    public static void printFibonacciNumbers(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int[] fibo = new int[n+1];
        fibo[0] = 0;
        if(n>0) fibo[1] = 1;
        for(int i=1;i<n-1;i++){
            fibo[i+1] = fibo[i] + fibo[i-1];
        }
        for(int i=0;i<n;i++){
            System.out.print(fibo[i] + " ");
        }
    }
}
