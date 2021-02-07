package hand.agent;

public class Seller extends Agent {

    public Seller(double minimumPrice) {
        super(minimumPrice);
    }

    @Override
    public boolean willTransact(double price) {
        // TODO sub-problem 1
        if(!hadTransaction && price>=expectedPrice) return true;
        return false;
    }

    @Override
    public void reflect() {
        // TODO sub-problem 1
        if(hadTransaction){
            expectedPrice+= adjustment;
        }
        else{
            expectedPrice = expectedPrice-adjustment<priceLimit?
                    priceLimit:expectedPrice-adjustment;
        }
        hadTransaction = false;
    }
}
