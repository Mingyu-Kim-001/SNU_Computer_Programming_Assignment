package bank;

import bank.event.*;
import security.*;
import security.key.*;



public class Bank {
    private int numAccounts = 0;
    final static int maxAccounts = 100;
    private BankAccount[] accounts = new BankAccount[maxAccounts];
    private String[] ids = new String[maxAccounts];

    public void createAccount(String id, String password) {
        createAccount(id, password, 0);
    }

    public void createAccount(String id, String password, int initBalance) {
        int accountId = numAccounts;
        accounts[accountId] = new BankAccount(id, password, initBalance);
        ids[accountId] = id;
        numAccounts+=1;
    }

    public boolean deposit(String id, String password, int amount) {
        //TODO: Problem 1.1
        int i;
        boolean isMatched = false;
        for(i=0;i<numAccounts;i++){
            if(ids[i].equals(id) &&
                    accounts[i].authenticate(password)) {
                isMatched = true;
                break;
            }
        }
        if(isMatched){
            accounts[i].deposit(amount);
        }
        return false;
    }

    public boolean withdraw(String id, String password, int amount) {
        //TODO: Problem 1.1
        int i;
        boolean isMatched = false;
        for(i=0;i<numAccounts;i++){
            if(ids[i].equals(id) &&
                    accounts[i].authenticate(password)) {
                isMatched = true;
                break;
            }
        }
        if(isMatched){
            return accounts[i].withdraw(amount);
        }
        return false;
    }

    public boolean transfer(String sourceId, String password, String targetId, int amount) {
        //TODO: Problem 1.1

        //System.out.println("try: " + sourceId + " send " + Integer.toString(amount)+" to "+targetId);

        BankAccount sourceAccount=null, targetAccount=null;
        for(int i=0;i<numAccounts;i++){
            if(ids[i].equals(sourceId) &&
                    accounts[i].authenticate(password)) {
                sourceAccount = accounts[i];
                break;
            }
        }
        if(sourceAccount!=null){
            for(int i=0;i<numAccounts;i++){
                if(ids[i].equals(targetId)){
                    targetAccount = accounts[i];
                    break;
                }
            }
            if(targetAccount==null) {
                //System.out.println("targetId failed");
                return false;
            }
//            System.out.println("before:"+sourceId + "-"+
//                    Integer.toString(sourceAccount.getBalance()) +
//                    " " + targetId + "-" +
//                    Integer.toString(targetAccount.getBalance()));
            if(!sourceAccount.send(amount)) {
//                System.out.println("not enough balance");
                return false;
            }
            targetAccount.receive(amount);
            return true;
        }
//        System.out.println("sourceId failed");
        return false;
    }

    public Event[] getEvents(String id, String password) {
        //TODO: Problem 1.1
        BankAccount account = null;
        for(int i=0;i<numAccounts;i++){
            if(ids[i].equals(id) &&
                    accounts[i].authenticate(password)) {
                account = accounts[i];
                break;
            }
        }
        if(account!=null){
            Event[] events = account.getEvents();
            int realLength = account.getEventNum();
            if(realLength==0) return null;
            Event[] eventsWithoutNull = new Event[realLength];
            for(int i=0;i<realLength;i++){
                eventsWithoutNull[i] = events[i];
            }
            return eventsWithoutNull;
        }
        return null;
    }

    public int getBalance(String id, String password) {
        //TODO: Problem 1.1
        BankAccount account = null;
        for(int i=0;i<numAccounts;i++){
            if(ids[i].equals(id) &&
                    accounts[i].authenticate(password)) {
                account = accounts[i];
                break;
            }
        }
        if(account!=null){
            return account.getBalance();
        }
        return -1;
    }

    private static String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private BankAccount find(String id) {
        for (int i = 0; i < numAccounts; i++) {
            if(ids[i].equals(id)){return accounts[i];};
        }
        return null;
    }
    final static int maxSessionKey = 100;
    int numSessionKey = 0;
    String[] sessionKeyArr = new String[maxSessionKey];
    BankAccount[] bankAccountmap = new BankAccount[maxSessionKey];
    String generateSessionKey(String id, String password){
        BankAccount account = find(id);
        if(account == null || !account.authenticate(password)){
            return null;
        }
        String sessionkey = randomUniqueStringGen();
        sessionKeyArr[numSessionKey] = sessionkey;
        bankAccountmap[numSessionKey] = account;
        numSessionKey += 1;
        return sessionkey;
    }
    BankAccount getAccount(String sessionkey){
        for(int i = 0 ;i < numSessionKey; i++){
            if(sessionKeyArr[i] != null && sessionKeyArr[i].equals(sessionkey)){
                return bankAccountmap[i];
            }
        }
        return null;
    }

    boolean deposit(String sessionkey, int amount) {
        //TODO: Problem 1.2
        BankAccount account = getAccount(sessionkey);
        account.deposit(amount);
        return true;
    }

    boolean withdraw(String sessionkey, int amount) {
        //TODO: Problem 1.2
        BankAccount account = getAccount(sessionkey);
        return account.withdraw(amount);
    }

    boolean transfer(String sessionkey, String targetId, int amount) {
        //TODO: Problem 1.2
        BankAccount sourceAccount = getAccount(sessionkey);
        BankAccount targetAccount = find(targetId);
        if(targetAccount!=null && sourceAccount.send(amount)){
            targetAccount.receive(amount);
            return true;
        }
        return false;
    }

    private BankSecretKey secretKey;
    private int maxHandShakeNum = 10000;
    private BankSymmetricKey[] bankSymKeyArray = new BankSymmetricKey[maxHandShakeNum];
    private String[] AppIdArray = new String[maxHandShakeNum];
    private int handShakeNum;
    public BankPublicKey getPublicKey(){
        BankKeyPair keypair = Encryptor.publicKeyGen();
        secretKey = keypair.deckey;
        return keypair.enckey;
    }

    public void fetchSymKey(Encrypted<BankSymmetricKey> encryptedKey, String AppId){
        //TODO: Problem 1.3
        boolean isNewApp = true;
        for(int i=0;i<handShakeNum;i++){
            if(AppIdArray[i].equals(AppId)){
                isNewApp = false;
                bankSymKeyArray[i] = encryptedKey.decrypt(secretKey);
                break;
            }
        }
        if(isNewApp){
            bankSymKeyArray[handShakeNum] = encryptedKey.decrypt(secretKey);
            AppIdArray[handShakeNum++] = AppId;
        }
    }

    public Encrypted<Boolean> processRequest(Encrypted<Message> messageEnc, String AppId){
        //TODO: Problem 1.3
        Message messageDec = null;
        BankSymmetricKey bankSymKey = null;
        for(int i=0;i<handShakeNum;i++){
            if(AppIdArray[i].equals(AppId)){
                bankSymKey = bankSymKeyArray[i];
                messageDec = messageEnc.decrypt(bankSymKeyArray[i]);
                break;
            }
        }
        if(messageDec==null) return null;
        else{
            if(messageDec.getRequestType().equals("deposit")){
                boolean result = this.deposit(messageDec.getId(),messageDec.getPassword(),messageDec.getAmount());
                return new Encrypted<Boolean>(result,bankSymKey);
            }
            else if(messageDec.getRequestType().equals("withdraw")){
                boolean result = this.withdraw(messageDec.getId(),messageDec.getPassword(),messageDec.getAmount());
                return new Encrypted<Boolean>(result,bankSymKey);
            }
            return null;
        }
    }


}