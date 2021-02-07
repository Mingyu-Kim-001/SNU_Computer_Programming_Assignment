public class FractionalNumberCalculator {
    public static void printCalculationResult(String equation) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        String[] split = equation.split(" ");
        int numerator0, denominator0,numerator1,denominator1;
        if(split[0].indexOf('/')!=-1){
            numerator0 = Integer.parseInt(split[0].split("/")[0]);
            denominator0 = Integer.parseInt(split[0].split("/")[1]);
        }
        else{
            numerator0 = Integer.parseInt(split[0]);
            denominator0 = 1;
        }

        if(split[2].indexOf('/')!=-1){
            numerator1 = Integer.parseInt(split[2].split("/")[0]);
            denominator1 = Integer.parseInt(split[2].split("/")[1]);
        }
        else{
            numerator1 = Integer.parseInt(split[2]);
            denominator1 = 1;
        }
        FractionalNumber num0 = new FractionalNumber(numerator0,denominator0);
        FractionalNumber num1 = new FractionalNumber(numerator1,denominator1);

        FractionalNumber result = calculate(num0,num1,split[1].charAt(0));
        System.out.print(result.getNumerator());
        if(result.getDenominator()!=1) System.out.println("/"+result.getDenominator());
    }
    private static FractionalNumber calculate(FractionalNumber a, FractionalNumber b,char operator){
        int resultNumerator = -1,resultDenominator = -1;
        switch(operator){
            case '+':
                resultNumerator = a.getNumerator() * b.getDenominator() + a.getDenominator() * b.getNumerator();
                resultDenominator = a.getDenominator() * b.getDenominator();
                break;
            case '-':
                resultNumerator = a.getNumerator() * b.getDenominator() - a.getDenominator() * b.getNumerator();
                resultDenominator = a.getDenominator() * b.getDenominator();
                break;
            case '*':
                resultNumerator = a.getNumerator() * b.getNumerator();
                resultDenominator = a.getDenominator() * b.getDenominator();
                break;
            case '/':
                resultNumerator = a.getNumerator() * b.getDenominator();
                resultDenominator = a.getDenominator() * b.getNumerator();
                break;

        }
        int gcd_ = gcd(resultDenominator,resultNumerator);
        FractionalNumber result = new FractionalNumber(resultNumerator / gcd_, resultDenominator / gcd_);
        return result;

    }
    private static int gcd(int a, int b){
        a = a>0?a:-a;
        b = b>0?b:-b;
        int c = a>b?a:b;
        int d = a>b?b:a;
        while(d!=0){
            int n = c%d;
            c = d;
            d = n;
        }
        //System.out.println("gcd " + c);
        return c;
    }

}

class FractionalNumber {
    public FractionalNumber(int numerator,int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    private int numerator;
    private int denominator;
    public int getNumerator(){
        return this.numerator;
    }
    public int getDenominator(){
        return this.denominator;
    }
}