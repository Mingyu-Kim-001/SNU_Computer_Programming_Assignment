public class IncreasingString {
    public static void printLongestIncreasingSubstringLength(String inputString) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int maxIncLen = 0;
        int IncLen = 0;
        //int where = 0;
        char priorchar = 'a'-1;
        for(int i=0;i<inputString.length();i++){
            if(priorchar<inputString.toCharArray()[i]){
                //System.out.println(priorchar + " " + inputString.toCharArray()[i]);
                IncLen++;
            }
            else{
                IncLen = 1;
            }
            if(maxIncLen<IncLen){
                maxIncLen = IncLen;
                //where = i;
            }
            priorchar = inputString.toCharArray()[i];
        }
        System.out.println(maxIncLen);
        //System.out.println(where);
    }
}
