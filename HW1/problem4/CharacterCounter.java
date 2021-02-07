public class CharacterCounter {
    public static void countCharacter(String str) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int[] charcounter = new int[26];
        for(char i : str.toCharArray()){
            charcounter[(int)(i - 'a')]++;
        }
        for(int i=0;i<26;i++){
            if(charcounter[i]!=0) printCount((char)(i+'a'),charcounter[i]);
        }

    }

    private static void printCount(char character, int count) {
        System.out.printf("%c: %d times\n", character, count);
    }
}