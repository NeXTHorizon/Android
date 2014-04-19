package org.nhzcrypto.accounts;

import java.util.LinkedList;

import org.nhzcrypto.alias.Alias;
import org.nhzcrypto.transactions.Transaction;

public class Account {
    public String mId;
    public String mTag;
    public String mImg;
    public float mBalance;
    public float mUnconfirmedBalance;
    public LinkedList<Transaction> mTransactionList;
    public LinkedList<Alias> mAliasList;

    public Account(){
        mBalance = -1;
        mTag = "null";
    }
    
    @Override
    public Account clone(){
        Account acct = new Account();
        acct.mId = mId;
        acct.mTag = mTag;
        acct.mBalance = mBalance;
        acct.mUnconfirmedBalance = mUnconfirmedBalance;
        return acct;
    }

    public String getBalanceText(){
        if ( mBalance < 0 )
            return "";

        if ( mBalance == mUnconfirmedBalance )
            return String.valueOf(mBalance);
        else
            return String.valueOf(mBalance) + "/" + String.valueOf(mUnconfirmedBalance);
    }
}
