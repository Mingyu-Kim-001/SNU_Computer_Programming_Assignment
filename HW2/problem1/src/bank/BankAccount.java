package bank;

import bank.event.*;

class BankAccount {
    private Event[] events = new Event[maxEvents];
    private int EventNum = 0;
    final static int maxEvents = 100;
    private String id;
    private String password;
    private int balance;
    BankAccount(String id, String password, int balance) {
        //TODO: Problem 1.1
        this.id = id;
        this.password = password;
        this.balance = balance;
    }

    boolean authenticate(String password) {
        //TODO: Problem 1.1
        return this.password.equals(password);
    }

    void deposit(int amount) {
        //TODO: Problem 1.1
        this.balance+= amount;
        //System.out.println(Integer.toString(amount) + " deposit");
        events[EventNum++] = new DepositEvent();
    }

    boolean withdraw(int amount) {
        //TODO: Problem 1.1
        if(this.balance<amount) return false;
        this.balance-= amount;
        events[EventNum++] = new WithdrawEvent();
        return true;
    }

    void receive(int amount) {
        //TODO: Problem 1.1
        this.balance+= amount;
        events[EventNum++] = new ReceiveEvent();
    }

    boolean send(int amount) {
        //TODO: Problem 1.1
        if(this.balance<amount) return false;
        this.balance-= amount;
        events[EventNum++] = new SendEvent();
        return true;
    }
    Event[] getEvents(){
        return events;
    }
    int getEventNum(){
        return EventNum;
    }
    int getBalance(){
        return this.balance;
    }

}
