package com.example.martin.econome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
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
import java.util.Collections;
import java.util.Date;


public class TransactionsActivity extends ActionBarActivity {
    private ArrayList<Transaction> transactionList;
    private ArrayList<Transaction> allTransactions;
    private ListView transactionListView;
    private ArrayAdapter transactionAdapter;
    private Context context;
    private Spinner monthSpinner;
    private SimpleDateFormat format;
    private SimpleDateFormat myFormat;
    private BackgroundClass bgc;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        context = this;
        bgc = (BackgroundClass) getApplicationContext();
        format = new SimpleDateFormat("yyyy-MM-dd");
        myFormat = new SimpleDateFormat("MMM yyyy");
        fillSpinner();

        allTransactions = bgc.getAllTransactions();
        transactionList = new ArrayList<>();
        monthSpinner = (Spinner) findViewById(R.id.monthspinner);

        transactionListView = (ListView) findViewById(R.id.transactionslistview);
        transactionAdapter = new TransactionArrayAdapter(context,R.layout.transactionlayout, transactionList);
        transactionListView.setAdapter(transactionAdapter);
        loadThisMonth();

        transactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("transactionactivity","editing:" + transactionList.get(position).getKey());
                edit(transactionList.get(position).getKey());

            }
        });
        registerForContextMenu(transactionListView);
        checkRepeatingTransactions();


    }

    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        if(v == findViewById(R.id.transactionslistview))
        {
            menu.setHeaderTitle("Edit");
            menu.add(0, 1, 0, "Delete");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemID = item.getItemId();

        if(itemID == 1){
            bgc.remove(transactionList.get(info.position));
            transactionList.remove(info.position);
            transactionAdapter.notifyDataSetChanged();
        }  else {return false;}
        return true;
    }

    private void edit(String key){
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("key", key);
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadThisMonth();
    }

    private void fillSpinner() {
        ArrayAdapter<String> spinMonthAdapter =
                new ArrayAdapter<>(this, R.layout.simple_spinner_item, bgc.getArrayListMonths());
        Spinner spinMonth = (Spinner) findViewById(R.id.monthspinner);
        spinMonth.setAdapter(spinMonthAdapter);
        Intent intent = getIntent();
        if(intent.hasExtra("Selection")) spinMonth.setSelection(intent.getIntExtra("Selection",0));
        spinMonth.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        loadThisMonth();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    public void increaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() > 0)
        monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() - 1);
    }

    public void decreaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() < monthSpinner.getAdapter().getCount()-1)
        monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() + 1);
    }

    public void add(View view){
        Intent intent = new Intent(this,EditorActivity.class);
        String type;
        if(view.getId() == R.id.addincome){type = "INCOME";}
        else {type = "EXPENSE";}
        intent.putExtra("type", type);
        intent.putExtra("new", true);
        startActivityForResult(intent, 0);
    }

    private void checkRepeatingTransactions(){
        ArrayList<Transaction> thisMonthTransactions = new ArrayList<>();
        ArrayList<Transaction> thisWeekTransactions = new ArrayList<>();
        ArrayList<Transaction> todayTransactions = new ArrayList<>();

        for(Transaction tc : allTransactions)
        {
            try {
                Date date = format.parse(tc.getDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                if (cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)) {
                    thisMonthTransactions.add(tc);
                    if (cal.get(Calendar.WEEK_OF_MONTH)==Calendar.getInstance().get(Calendar.WEEK_OF_MONTH))
                        thisWeekTransactions.add(tc);
                }
                if (cal.get(Calendar.DATE)==Calendar.getInstance().get(Calendar.DATE))
                    todayTransactions.add(tc);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ArrayList<Transaction> repeatingTransactions = new ArrayList<>();
        for(Transaction t : allTransactions){
            if(t.isRepeating()) repeatingTransactions.add(t);
        }
        for(Transaction t : repeatingTransactions){
            int found = 0;
            switch(t.getFrequency()){
                case "Daily":
                    for(Transaction tc : todayTransactions)
                    {
                        if(tc.getAmount() == t.getAmount() && tc.getCategory().equals(t.getCategory())) {
                            found = 1;
                            break;
                        }
                    }
                    break;
                case "Weekly":
                    for(Transaction tc : thisWeekTransactions)
                    {
                        if(tc.getAmount() == t.getAmount() && tc.getCategory().equals(t.getCategory())){
                            found = 1;
                            break;
                        }
                    }
                    break;
                case "Monthly":
                    for(Transaction tc : thisMonthTransactions)
                    {
                        if(tc.getAmount() == t.getAmount() && tc.getCategory().equals(t.getCategory())){
                            found = 1;
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            if(found==0)
            {
                bgc.add(t.getAmount(), t.getCategory(), format.format(new Date()), false, "Daily", t.getType());
                transactionAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadThisMonth(){
        transactionList.clear();
        int year = 0;
        int month = 0;
        try {
            Calendar spinnerCal = Calendar.getInstance();
            spinnerCal.setTime(myFormat.parse(monthSpinner.getSelectedItem().toString()));
            year = spinnerCal.get(Calendar.YEAR);
            month = spinnerCal.get(Calendar.MONTH);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        for(Transaction newTransaction : allTransactions){
            if(newTransaction.getMonth()-1==month && newTransaction.getYear()==year)
                transactionList.add(newTransaction);
        }
        Collections.sort(transactionList);
        transactionAdapter.notifyDataSetChanged();
    }
}
