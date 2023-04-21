package com.example.block;

import java.util.ArrayList;
import java.util.Date;


public class Block {
    //The hash contained by the block
    private String hash;
    //The hash of the previous block if there is one.
    private String previousHash;
    //timeStamp which is created when the block is added to the chain.
    private long timeStamp;
    //The limit of transactions per block which is set at 5 by default.
    private int limitTransactions=5;
    //The Array of Transactions that gets updated each time when a new transactions is approved by an authority till the limit is reached.
    private ArrayList<Transaction> Transactions;

    private String signature;
    private int score;

    //The Chain that contains all the approved blocks
    public Chain chain;
    Block(Chain chain){
        //init the TransactionList
        this.Transactions = new ArrayList<Transaction>(limitTransactions);
        //init the hash of this block created by the SHA.
        this.hash = "unique hash for the block";
        this.chain = chain;
        //init the previous hash.
        if(chain.getLastBlock() != null) this.previousHash = chain.getLastBlock().getHash();

    }

    public void updateLimit(int newLimit){
        //We update the limit of transactions
        this.limitTransactions=newLimit;
    }

    public int getLimit(){
        return this.limitTransactions;
    }

    //Method to add a new transaction until the array is full.
    public void updateTransactions(Transaction transaction){
        if(Transactions.size()<limitTransactions){
            Transactions.add(transaction);
        }

        if(Transactions.size()==limitTransactions){
            sendBlock();
        }
    }

    //We publish our block to the chain
    private void sendBlock(){
        //We set the timeStamp
        this.timeStamp = new Date().getTime();
        calculateScore();
        //We update the chain with our new block
        chain.updateChain(this);

    }

    private void calculateScore(){
        ArrayList<String> referees = new ArrayList<String>();
        for(int i = 0 ; i<Transactions.size();i++){
            if(!referees.contains(Transactions.get(i).getReferee())){
                referees.add(Transactions.get(i).getReferee().getPseudo());
            }
        }
        score = referees.size();
    }

    public String getHash(){
        return hash;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public int getScore() { return score; }

    public ArrayList<Transaction> getTransactions(){return Transactions;}
}