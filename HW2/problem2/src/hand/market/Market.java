package hand.market;

import hand.agent.Buyer;
import hand.agent.Seller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Pair<K,V> {
    public K key;
    public V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class Market {
    public ArrayList<Buyer> buyers;
    public ArrayList<Seller> sellers;

    public Market(int nb, ArrayList<Double> fb, int ns, ArrayList<Double> fs) {
        buyers = createBuyers(nb, fb);
        sellers = createSellers(ns, fs);
    }
    
    private ArrayList<Buyer> createBuyers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Buyer> buyerArrayList = new ArrayList<Buyer>();
        if(f==null){
            for(int i=1;i<=n;i++){
                buyerArrayList.add(new Buyer((double)i/n));
            }
            return buyerArrayList;
        }
        int m = f.size();
        double priceLimit=0;

        for(int i=1;i<=n;i++){
            priceLimit = 0;
            for(int j=0;j<m;j++) {
                priceLimit+= f.get(j) * Math.pow((double)i/n,m-1-j);
                //if(i==1) System.out.println(priceLimit);
            }
            buyerArrayList.add(new Buyer(priceLimit));
            //if(i==1) System.out.println(priceLimit);
        }
        return buyerArrayList;
    }

    private ArrayList<Seller> createSellers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Seller> sellerArrayList = new ArrayList<Seller>();
        if(f==null){
            for(int i=1;i<=n;i++){
                sellerArrayList.add(new Seller((double)i/n));
            }
            return sellerArrayList;
        }
        int m = f.size();
        double priceLimit=0;
        for(int i=1;i<=n;i++){
            priceLimit = 0;
            for(int j=0;j<m;j++) {
                priceLimit+= f.get(j) * Math.pow((double)i/n,m-1-j);
                //if(i==1) System.out.println(priceLimit);
            }
            sellerArrayList.add(new Seller(priceLimit));
        }
        return sellerArrayList;
    }

    private ArrayList<Pair<Seller, Buyer>> matchedPairs(int day, int round) {
        ArrayList<Seller> shuffledSellers = new ArrayList<>(sellers);
        ArrayList<Buyer> shuffledBuyers = new ArrayList<>(buyers);
        Collections.shuffle(shuffledSellers, new Random(71 * day + 43 * round + 7));
        Collections.shuffle(shuffledBuyers, new Random(67 * day + 29 * round + 11));
        ArrayList<Pair<Seller, Buyer>> pairs = new ArrayList<>();
        for (int i = 0; i < shuffledBuyers.size(); i++) {
            if (i < shuffledSellers.size()) {
                pairs.add(new Pair<>(shuffledSellers.get(i), shuffledBuyers.get(i)));
            }
        }
        return pairs;
    }

    public double simulate() {
        // TODO sub-problem 2 and 3
        int total = 0;
        int transactNum = 0;
        for (int day = 1; day <= 1000; day++) { // do not change this line
            for (int round = 1; round <= 10; round++) { // do not change this line
                ArrayList<Pair<Seller, Buyer>> pairs = matchedPairs(day, round); // do not change this line
                for(Pair<Seller,Buyer> pair: pairs){
                    Seller seller = pair.key;
                    Buyer buyer = pair.value;
                    if(seller.willTransact(buyer.getExpectedPrice())
                            && buyer.willTransact(seller.getExpectedPrice())){
                        seller.makeTransaction();
                        buyer.makeTransaction();
                        if(day==1000) {
                            total += seller.getExpectedPrice();
                            transactNum++;
                        }
                    }
                }
            }
            for(Seller seller: sellers){
                seller.reflect();
            }
            for(Buyer buyer: buyers){
                buyer.reflect();
            }
        }
        return ((double)total / transactNum);
    }
}
