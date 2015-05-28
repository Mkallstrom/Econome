package com.example.martin.econome;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Martin on 2015-05-27.
 */
public class BackgroundClass extends Application {
    private SharedPreferences transactionSP;
    private SharedPreferences.Editor transactionSPEditor;
    private ArrayList<Transaction> allTransactions;
    private int index;
    private boolean firstRun = true;
    private ArrayList<String> arrayListMonths;

    public void initiate(){
        if(!firstRun) return;
        firstRun = false;
        transactionSP = getSharedPreferences("transactions",0);
        transactionSPEditor = transactionSP.edit();
        index = Integer.parseInt(transactionSP.getString("index", "0"));
        allTransactions = new ArrayList<>();
        loadSharedPreferences();

        arrayListMonths = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        for (int i = 0; i <= 10; i++) {
            arrayListMonths.add(format.format(calendar.getTime()) + " " + Integer.toString(calendar.get(Calendar.YEAR)));
            calendar.add(Calendar.MONTH, -1);
        }
    }
        private void loadSharedPreferences(){
            Log.d("BGC","Loading shared preferences.");
            allTransactions.clear();
            Map<String,?> keys = transactionSP.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                if(!entry.getKey().equals("index"))
                {
                    allTransactions.add(parseTransaction(entry.getValue().toString(), entry.getKey()));
                }
            }
        }

    private Transaction parseTransaction(String value, String key)
    {
        String[] splitString = value.split("\\|");
        return new Transaction(Float.parseFloat(splitString[0]),splitString[1],splitString[2],Boolean.valueOf(splitString[3]),splitString[4],TransactionType.valueOf(splitString[5]),key);
    }

    public void add(float amount, String category, String date, boolean repeating, String frequency, TransactionType type){
        Transaction transaction = new Transaction(amount, category, date, repeating, frequency, type, Integer.toString(index));
        allTransactions.add(transaction);
        transactionSPEditor.putString(Integer.toString(index), transaction.toString());
        transactionSPEditor.remove("index");
        index++;
        transactionSPEditor.putString("index", Integer.toString(index));
        transactionSPEditor.commit();
    }
    public void remove(Transaction transaction){
        allTransactions.remove(transaction);
        transactionSPEditor.remove(transaction.getKey());
        transactionSPEditor.commit();
    }
    public ArrayList<Transaction> getAllTransactions(){
        return allTransactions;
    }
    public Transaction getTransaction(String key){
        for(Transaction transaction : allTransactions){
            if(transaction.getKey().equals(key)) return transaction;
        }
        return null;
    }

    public ArrayList getArrayListMonths() {
        return arrayListMonths;
    }

}
