package com.example.martin.econome;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Martin on 2015-05-27.
 */
public class BackgroundClass extends Application {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private ArrayList<Transaction> allTransactions;
    private int index;
    private boolean firstRun = true;
    private ArrayList<MonthYear> arrayListMonths;
    private ArrayList<String> categories;
    private boolean processing = false;

    public void initiate(){
        if(!firstRun) return;
        firstRun = false;

        sharedPreferences = getSharedPreferences("transactions",0);
        sharedPreferencesEditor = sharedPreferences.edit();
        index = Integer.parseInt(sharedPreferences.getString("index", "0"));

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
        arrayListMonths = new ArrayList<>();
        loadSharedPreferences();


        /*Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMM");
        for (int i = 0; i <= 10; i++) {
            arrayListMonths.add(format.format(calendar.getTime()) + " " + Integer.toString(calendar.get(Calendar.YEAR)));
            calendar.add(Calendar.MONTH, -1);
        }*/
    }
            private boolean arrayHasMY (ArrayList<MonthYear> array, MonthYear monthYear){
            if(array.isEmpty()) return false;
            for(MonthYear my : array){
                if(my.toString().equals(monthYear.toString()))
                    return true;
            }
            return false;
        }

        private void READSPTRANSACTIONS(){
            try{
                Transaction writeTransaction;
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getExternalFilesDir(null),"transactions.txt")));

                Map<String,?> keys = sharedPreferences.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    if(!entry.getKey().equals("index"))
                    {
                        writeTransaction = parseTransaction(entry.getValue().toString(),entry.getKey());
                        writer.newLine();
                        writer.write(writeTransaction.toString()+"|"+writeTransaction.getKey());
                    }
                }

                writer.close();
            }
            catch (Exception e){ e.printStackTrace();}
        }

        private void loadSharedPreferences(){

           //READSPTRANSACTIONS();

            try {
                Transaction newTransaction;
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(getExternalFilesDir(null),"transactions.txt")));
                    String receiveString = "";

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        Log.d("LSP", "Read: " + receiveString);
                        if(receiveString.length() > 0) {
                            newTransaction = parseTransaction(receiveString);
                            allTransactions.add(newTransaction);
                            MonthYear monthYear = new MonthYear(newTransaction.getMonth() - 1, newTransaction.getYear());
                            if (!arrayHasMY(arrayListMonths, monthYear)) {
                                arrayListMonths.add(monthYear);
                            }
                            Collections.sort(arrayListMonths);
                        }
                    }
                    bufferedReader.close();

            } catch (Exception e) {
                e.printStackTrace();
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

    private Transaction parseTransaction(String value)
    {
        String[] splitString = value.split("\\|");
        return new Transaction(Float.parseFloat(splitString[0]),splitString[1],splitString[2],Boolean.valueOf(splitString[3]),splitString[4],TransactionType.valueOf(splitString[5]),splitString[6]);
    }
    public void add(float amount, String category, String date, boolean repeating, String frequency, TransactionType type){
        Transaction transaction = new Transaction(amount, category, date, repeating, frequency, type, Integer.toString(index));
        allTransactions.add(transaction);
        index++;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(getExternalFilesDir(null),"transactions.txt"),true));
            bufferedWriter.newLine();
            bufferedWriter.write(transaction.toString() + "|" + transaction.getKey());
            bufferedWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sharedPreferencesEditor.remove("index");
        sharedPreferencesEditor.putString("index", Integer.toString(index));
        sharedPreferencesEditor.commit();
    }
    public void remove(Transaction transaction){
        String key = transaction.getKey();
        allTransactions.remove(getTransaction(transaction.getKey()));

        File inputFile = new File(getExternalFilesDir(null),"transactions.txt");
        File tempFile = new File(getExternalFilesDir(null),"TempFile.txt");


        Log.d("REMOVE", "Key is: " + key);
        String currentLine = "";
        String[] splitString;

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));

            while((currentLine = bufferedReader.readLine()) != null) {
                if(currentLine.length()>0) {
                    Log.d("REMOVE", "READ: " + currentLine);
                    splitString = currentLine.split("\\|");
                    if(splitString[6].equals(key)) continue;
                    bufferedWriter.write(currentLine + System.getProperty("line.separator"));
                }
            }

            bufferedWriter.close();
            bufferedReader.close();
            boolean successful = tempFile.renameTo(inputFile);

        }
        catch (Exception e) {
            e.printStackTrace();
        }


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

    public void fixIndices(){
        Log.d("Fix", "Running fixIndices()");
        File inputFile = new File(getExternalFilesDir(null),"transactions.txt");
        File tempFile = new File(getExternalFilesDir(null),"TempFile.txt");

        String currentLine = "";
        String[] splitString;
        int newIndex = 0;

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));

            while((currentLine = bufferedReader.readLine()) != null) {
                if(currentLine.length()>0) {
                    Log.d("FIX", "READ: " + currentLine);
                    splitString = currentLine.split("\\|");
                    Transaction newTransaction = new Transaction(Float.parseFloat(splitString[0]),splitString[1],splitString[2],Boolean.valueOf(splitString[3]),splitString[4],TransactionType.valueOf(splitString[5]), Integer.toString(newIndex));
                    newIndex++;
                    bufferedWriter.write(newTransaction.toString() + "|" + newTransaction.getKey() + System.getProperty("line.separator"));
                }
            }

            bufferedWriter.close();
            bufferedReader.close();
            inputFile.renameTo(new File(getExternalFilesDir(null), "backupTransactions.txt"));
            boolean successful = tempFile.renameTo(new File(getExternalFilesDir(null),"transactions.txt"));


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sharedPreferencesEditor.remove("index");
        sharedPreferencesEditor.putString("index", Integer.toString(newIndex));
        sharedPreferencesEditor.commit();
        index = newIndex;
        allTransactions.clear();
        loadSharedPreferences();
    }

}
