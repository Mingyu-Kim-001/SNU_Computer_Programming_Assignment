public class NumberCounter {
    public static void countNumbers(String str0, String str1, String str2) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int int0 = Integer.parseInt(str0);
        int int1 = Integer.parseInt(str1);
        int int2 = Integer.parseInt(str2);
        String mult = Integer.toString(int0*int1*int2);
        int[] numcount = new int[10];
        for(char i:mult.toCharArray()){
            numcount[(int)(i - '0')]++;
        }
        for(int i=0;i<10;i++){
            if(numcount[i]!=0) printNumberCount(i,numcount[i]);
        }

    }

    private static void printNumberCount(int number, int count) {
        System.out.printf("%d: %d times\n", number, count);
    }
}
