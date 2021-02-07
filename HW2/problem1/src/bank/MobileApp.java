package bank;

import security.Encryptor;
import security.Encrypted;
import security.Message;
import security.key.BankPublicKey;
import security.key.BankSymmetricKey;

public class MobileApp {

    private String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private final String AppId = randomUniqueStringGen();
    public String getAppId() {
        return AppId;
    }
    private BankSymmetricKey bankSymKey;
    String id, password;
    public MobileApp(String id, String password){
        this.id = id;
        this.password = password;
    }

    public Encrypted<BankSymmetricKey> sendSymKey(BankPublicKey publickey){
        //TODO: Problem 1.3
        String randomString = randomUniqueStringGen();
        this.bankSymKey = new BankSymmetricKey(randomString);
        Encrypted<BankSymmetricKey> encrypted = new Encrypted<BankSymmetricKey>(bankSymKey,publickey);
        return encrypted;
    }

    public Encrypted<Message> deposit(int amount){
        //TODO: Problem 1.3
        Message depositMessage = new Message("deposit",id,password,amount);
        Encrypted<Message> encrypted = new Encrypted<Message>(depositMessage,bankSymKey);
        return encrypted;
    }

    public Encrypted<Message> withdraw(int amount){
        //TODO: Problem 1.3
        Message withdrawMessage = new Message("withdraw",id,password,amount);
        Encrypted<Message> encrypted = new Encrypted<Message>(withdrawMessage,bankSymKey);
        return encrypted;
    }

    public boolean processResponse(Encrypted<Boolean> obj){
        //TODO: Problem 1.3
        if(obj==null) return false;
        Boolean objDecrpyted = obj.decrypt(this.bankSymKey);
        if(objDecrpyted == null) return false;
        return objDecrpyted;
    }

}

