package com.example.martin.econome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SummaryActivity extends ActionBarActivity {

    private ListView summaryListView;
    private ListView specificsListView;

    private ArrayAdapter summaryAdapter;
    private ArrayAdapter specificsAdapter;

    private ArrayList<TitledFloat> summaries;
    private ArrayList<TitledFloat> specifics;
    private ArrayList<Transaction> transactionList;

    private Spinner monthSpinner;
    private ArrayList<String> categories;
    private int month = 0;
    private Context context;

    private SimpleDateFormat myFormat;

    private BackgroundClass bgc;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        bgc = (BackgroundClass) getApplicationContext();
        myFormat = new SimpleDateFormat("MMM yyyy");
        monthSpinner = (Spinner) findViewById(R.id.monthspinner);
        fillSpinner();
        context = this;

        summaryListView = (ListView) findViewById(R.id.summaries);
        specificsListView = (ListView) findViewById(R.id.specifics);

        summaries = new ArrayList<>();
        specifics = new ArrayList<>();
        transactionList = bgc.getAllTransactions();
        categories = bgc.getCategories();

        progressDialog = new ProgressDialog(SummaryActivity.this);
        progressDialog.setTitle("Summary");
        progressDialog.setMessage("Summarizing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateLists();
                progressDialog.dismiss();
            }
        };
        new Thread(runnable).start();
        this.progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                summaryListView.setAdapter(summaryAdapter);
                specificsListView.setAdapter(specificsAdapter);
                monthSpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(
                                    AdapterView<?> parent, View view, int position, long id) {
                                Log.d("spinner", "triggered");
                                updateLists();
                                summaryListView.setAdapter(summaryAdapter);
                                specificsListView.setAdapter(specificsAdapter);
                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
            }
        });

    }

    private void updateLists(){
        Log.d("updateLists", "Start");
        float[]summariesAmounts = getSummaries(monthSpinner.getSelectedItem().toString());
        float[]summariesAverages = getAverageSummaries();
        summaries.clear();
        summaries.add(new TitledFloat(summariesAmounts[0], summariesAverages[0], "Expenses"));
        summaries.add(new TitledFloat(summariesAmounts[1], summariesAverages[1], "Incomes"));
        summaries.add(new TitledFloat(summariesAmounts[1]-summariesAmounts[0], summariesAverages[2], "Total"));

        summaryAdapter = new SummariesArrayAdapter(context, R.layout.summarylayout, summaries);
        summaryAdapter.notifyDataSetChanged();

        specifics.clear();
        Log.d("updateLists",Integer.toString(categories.size()));
        for(String category : categories)
        {
            specifics.add(new TitledFloat(getSpecifics(monthSpinner.getSelectedItem().toString(),category),getAverageSpecifics(category),category));
        }
        specificsAdapter = new SummariesArrayAdapter(context,R.layout.summarylayout, specifics);
        specificsAdapter.notifyDataSetChanged();
        Log.d("updateLists", "Done");
        }

    private float getSpecifics(String monthYear, String category){
        Log.d("getSpecifics", "Getting specifics");
        int month = 0;
        int year = 0;
        try {
            Calendar spinnerCal = Calendar.getInstance();
            spinnerCal.setTime(myFormat.parse(monthYear));
            month = spinnerCal.get(Calendar.MONTH);
            year = spinnerCal.get(Calendar.YEAR);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        float amount = 0;
        for (Transaction t : transactionList) {
            cal.setTime(t.getRealDate());
            if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year && t.getCategory().equals(category)) {
                amount += t.getAmount();
            }
        }
        Log.d("getSpecifics", "Done getting specifics");
        return amount;
    }

    private float getAverageSpecifics(String category){
        Log.d("getAverages", "Getting averages");
        int count = 0;
        float amount = 0;
        for(int i = 0; i<monthSpinner.getAdapter().getCount(); i++){
            float newAmount = getSpecifics(monthSpinner.getItemAtPosition(i).toString(), category);
            if(newAmount>0){
                amount+=newAmount;
                count +=1;
            }
        }
        if(count>0) amount = amount/count;
        Log.d("getAverages", "Done getting averages");
        return amount;
    }

    private float[] getSummaries(String monthYear) {
        int month = 0;
        int year = 0;

        try {
            Calendar spinnerCal = Calendar.getInstance();
            spinnerCal.setTime(myFormat.parse(monthYear));
            month = spinnerCal.get(Calendar.MONTH);
            year = spinnerCal.get(Calendar.YEAR);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        float expenses = 0;
        float incomes = 0;
        for (Transaction t : transactionList) {
            cal.setTime(t.getRealDate());
            if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                if (t.getType() == TransactionType.EXPENSE) {
                    expenses += t.getAmount();
                } else if (t.getType() == TransactionType.INCOME) {
                    incomes += t.getAmount();
                }
            }
        }
        float[] returned = new float[2];
        returned[0] = expenses;
        returned[1] = incomes;
        return returned;
    }

    private float[] getAverageSummaries(){
        Log.d("getAverageSummaries", "Start");
        float averageExpenses = 0;
        float averageIncomes = 0;
        float averageTotal = 0;
        int months = 0;
        float[] sums;
        for(int i = 0; i<monthSpinner.getAdapter().getCount(); i++){
            sums = getSummaries((monthSpinner.getAdapter().getItem(i).toString()));
            if(sums[0]>0 || sums[1]>0) {
                averageExpenses+=sums[0];
                averageIncomes+=sums[1];
                averageTotal+=sums[1]-sums[0];
                months++;
            }
        }
        float[] result = new float[3];
        result[0] = averageExpenses/months;
        result[1] = averageIncomes/months;
        result[2] = averageTotal/months;
        Log.d("getAverageSummaries", "Done");
        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillSpinner() {
        Log.d("fillSpinner", "Start");
        ArrayAdapter<String> spinMonthAdapter =
                new ArrayAdapter<>(this, R.layout.simple_spinner_item, bgc.getArrayListMonths());
        monthSpinner.setAdapter(spinMonthAdapter);
        Intent intent = getIntent();
        if(intent.hasExtra("Selection")) monthSpinner.setSelection(intent.getIntExtra("Selection",0));

        Log.d("fillSpinner", "Done");
    }
    public void increaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() > 0)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() - 1);
    }

    public void decreaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() < monthSpinner.getAdapter().getCount()-1)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() + 1);
    }
}
