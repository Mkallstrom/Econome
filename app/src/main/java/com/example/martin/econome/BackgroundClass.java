package com.example.martin.econome;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

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

        private void loadSharedPreferences(){
            /*
            Transaction writeTransaction;
            Log.d("BGC", "Loading shared preferences.");

            try {
                Log.d("LSP", "Attempting to open file output");
                OutputStream outputStream = openFileOutput("transactions.txt", MODE_PRIVATE);
                Log.d("LSP", "Opened file output");
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);



                Map<String,?> keys = sharedPreferences.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    if(!entry.getKey().equals("index"))
                    {
                        writeTransaction = parseTransaction(entry.getValue().toString(),entry.getKey());
                        bufferedWriter.newLine();
                        Log.d("LSP", "Writing to output: " + writeTransaction.toString());
                        bufferedWriter.write(writeTransaction.toString()+"|"+writeTransaction.getKey());
                    }
                }
                bufferedWriter.close();
                writer.close();
                outputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            */
            try {
                InputStream inputStream = openFileInput("transactions.txt");
                Log.d("LSP", "Opened input.");
                Transaction newTransaction;
                if ( inputStream != null ) {
                    Log.d("LSP", "inputStream != null");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    if(inputStreamReader == null) Log.d("LSP", "NULL INPUTSTREAMREADER");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    if(bufferedReader == null) Log.d("LSP", "NULL BUFFEREDREADER");
                    String receiveString = "";
                    Log.d("LSP", "Starting reading");

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
                    inputStreamReader.close();
                    inputStream.close();
                }
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
            OutputStream outputStream = openFileOutput("transactions.txt", MODE_APPEND);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.newLine();
            bufferedWriter.write(transaction.toString()+"|"+transaction.getKey());
            bufferedWriter.close();
            writer.close();
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*
        sharedPreferencesEditor.putString(Integer.toString(index), transaction.toString());
        sharedPreferencesEditor.remove("index");
        index++;
        sharedPreferencesEditor.putString("index", Integer.toString(index));
        sharedPreferencesEditor.commit();
        */
    }
    public void remove(Transaction transaction){
        allTransactions.remove(getTransaction(transaction.getKey()));
        sharedPreferencesEditor.remove(transaction.getKey());
        sharedPreferencesEditor.commit();
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
