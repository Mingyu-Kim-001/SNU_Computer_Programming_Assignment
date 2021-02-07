public class CardGameSimulator {
    private static final Player[] players = new Player[2];

    public static void simulateCardGame(String inputA, String inputB) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        String[] InitCardA = inputA.split(" ");
        String[] InitCardB = inputB.split(" ");
        Card[] deckA = new Card[10];
        Card[] deckB = new Card[10];
        for(int i=0;i<10;i++){
            deckA[i] = new Card();
            deckB[i] = new Card();
            deckA[i].PutCardInfo(InitCardA[i].charAt(0) - '0', InitCardA[i].charAt(1));
            deckB[i].PutCardInfo(InitCardB[i].charAt(0) - '0', InitCardB[i].charAt(1));
        }
        Player playerA = new Player();
        Player playerB = new Player();
        playerA.PutPlayerInfo(deckA,"A");
        playerB.PutPlayerInfo(deckB,"B");
        Card prevCard = playerA.getCard(playerA.maxIndex());
        prevCard.UseCard();
        playerA.playCard(prevCard);
        Player currentPlayer;
        for(int i=0;i<19;i++){
            currentPlayer = i%2==1?playerA:playerB;
            int index;
            if((index=currentPlayer.IndexWithNumber(prevCard.getNumber()))!=-1){
                prevCard = currentPlayer.getCard(index);
                prevCard.UseCard();
                currentPlayer.playCard(prevCard);
            }
            else if((index=currentPlayer.maxIndexWithShape(prevCard.getShape()))!=-1){
                prevCard = currentPlayer.getCard(index);
                prevCard.UseCard();
                currentPlayer.playCard(prevCard);
            }
            else{
                printLoseMessage(currentPlayer);
                return;
            }
        }
        printLoseMessage(playerA);
    }

    private static void printLoseMessage(Player player) {
        System.out.printf("Player %s loses the game!\n", player);
    }

}


class Player {
    private String name;
    private Card[] deck;
    public void PutPlayerInfo(Card[] deck, String name){
        this.deck = deck;
        this.name = name;
    }
    public int maxIndex(){//for the very first card
        int maxindex = -1;
        int maxvalue = -1;
        for(int i=0;i<10;i++){
            int temp = deck[i].getNumber()*2 + (deck[i].getShape()=='O'?1:0);
            if(maxvalue<temp){
                maxvalue = temp;
                maxindex = i;
            }
        }
        return maxindex;
    }
    public int maxIndexWithShape(char shape){
        int maxvalue = -1;
        int maxindex = -1;
        for(int i=0;i<10;i++){
            if(deck[i].getShape() == shape && !deck[i].isUsed() && maxvalue<deck[i].getNumber()){
                maxvalue = deck[i].getNumber();
                maxindex = i;
            }
        }
        return maxindex;
    }
    public int IndexWithNumber(int number){
        for(int i=0;i<10;i++){
            if(deck[i].getNumber() == number && !deck[i].isUsed()){
                return i;
            }
        }
        return -1;
    }
    public Card getCard(int n){
        return this.deck[n];
    }
    public void playCard(Card card) {
        System.out.printf("Player %s: %s\n", name, card);
    }

    @Override
    public String toString() {
        return name;
    }
}


class Card {
    private int number;
    private char shape;
    private boolean isUsed;
    @Override
    public String toString() {
        return "" + number + shape;
    }
    public void PutCardInfo(int number, char shape){
        this.number = number;
        this.shape = shape;
        this.isUsed = false;
    }
    public int getNumber(){
        return this.number;
    }
    public char getShape(){
        return this.shape;
    }
    public void UseCard(){
        this.isUsed = true;
    }
    public boolean isUsed(){
        return this.isUsed;
    }
}
