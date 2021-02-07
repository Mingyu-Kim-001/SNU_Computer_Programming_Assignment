public class PrimeNumbers {
    public static void printPrimeNumbers(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int[] primelist = new int[100];
        int currentPrimeCount = 1;
        boolean isPrime;
        primelist[0] = 2;
        while(currentPrimeCount < n){
            for(int i = primelist[currentPrimeCount - 1]+1;;i++){
                isPrime = true;
                for(int j=0;j<currentPrimeCount;j++){
                    //System.out.println(i + " " + j + " " +primelist[j]);
                    if(i%primelist[j]==0){
                        isPrime = false;
                        break;
                    }
                }
                if(isPrime){
                    primelist[currentPrimeCount++] = i;
                    break;
                }
            }
        }
        for(int i=0;i<n;i++){
            System.out.print(primelist[i] + " ");
        }
    }
}
