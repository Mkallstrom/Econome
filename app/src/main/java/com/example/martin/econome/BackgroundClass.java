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
    private ArrayList<String> categories;
    private boolean processing = false;

    public void initiate(){
        if(!firstRun) return;
        firstRun = false;
        transactionSP = getSharedPreferences("transactions",0);
        transactionSPEditor = transactionSP.edit();
        index = Integer.parseInt(transactionSP.getString("index", "0"));

        allTransactions = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add("Salary");
        categories.add("Other");
        categories.add("Food");
        categories.add("Phone");
        categories.add("Transportation");
        categories.add("Rent");
        categories.add("Eating Out");
        categories.add("Clothes");
        categories.add("Hobbies");
        categories.add("Misc");
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
            Transaction newTransaction;
            Log.d("BGC","Loading shared preferences.");
            allTransactions.clear();
            Map<String,?> keys = transactionSP.getAll();

            for(Map.Entry<String,?> entry : keys.entrySet()){
                if(!entry.getKey().equals("index"))
                {
                    newTransaction = parseTransaction(entry.getValue().toString(),entry.getKey());
                    allTransactions.add(newTransaction);
                }
            }

           /* try
            {

                File traceFile = new File(((Context)this).getExternalFilesDir(null), "Test.txt");
                if (!traceFile.exists())
                    traceFile.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true));
                for(Transaction transaction : allTransactions)
                {
                    writer.write(transaction.toString()+"|"+transaction.getKey()+"\r\n");
                }
                writer.close();

                MediaScannerConnection.scanFile((Context) (this),
                        new String[]{traceFile.toString()},
                        null,
                        null);

            }
            catch (IOException e)
            {
                Log.e("FileTest", "Unable to write to the TraceFile.txt file.");
            }*/
        }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
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
        allTransactions.remove(getTransaction(transaction.getKey()));
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
