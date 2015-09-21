package com.example.martin.econome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EditorActivity extends ActionBarActivity {

    private EditText amountText;
    private Spinner categorySpinner;
    private CheckBox repeatingBox;
    private Spinner frequencySpinner;
    private ToggleButton incomeToggle;
    private ToggleButton expenseToggle;
    private TransactionType newType;
    private String key;
    private BackgroundClass bgc;

    private boolean newTransaction = true;
    private SimpleDateFormat format;
    ArrayList<String> expenseCategories;
    ArrayList<String> incomeCategories;
    ArrayAdapter<String> expenseAdapter;
    ArrayAdapter<String> incomeAdapter;

    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        bgc = (BackgroundClass) getApplicationContext();
        format = new SimpleDateFormat("yyyy-MM-dd");

        expenseCategories = new ArrayList<>();
        expenseCategories.add("Food");
        expenseCategories.add("Phone");
        expenseCategories.add("Transportation");
        expenseCategories.add("Rent");
        expenseCategories.add("Eating Out");
        expenseCategories.add("Clothes");
        expenseCategories.add("Hobbies");
        expenseCategories.add("Misc");
        expenseAdapter = new CategorySpinnerAdapter(this, R.layout.categoryspinnerlayout, expenseCategories);

        incomeCategories = new ArrayList<>();
        incomeCategories.add("Salary");
        incomeCategories.add("Other");
        incomeAdapter = new CategorySpinnerAdapter(this, R.layout.categoryspinnerlayout, incomeCategories);

        amountText = (EditText) findViewById(R.id.amount);
        categorySpinner = (Spinner) findViewById(R.id.categoryspinner);
        repeatingBox = (CheckBox) findViewById(R.id.repeatingbox);
        frequencySpinner = (Spinner) findViewById(R.id.frequencyspinner);
        incomeToggle = (ToggleButton) findViewById(R.id.income);
        expenseToggle = (ToggleButton) findViewById(R.id.expense);

        fillSpinner();

        Intent myIntent = getIntent();
        newTransaction = myIntent.getBooleanExtra("new", false);
        Log.d("Editor","New: " + Boolean.toString(newTransaction));
        if(newTransaction) {
            TransactionType type = TransactionType.valueOf(myIntent.getStringExtra("type"));

            if (type == TransactionType.EXPENSE) {
                incomeToggle.setChecked(false);
                expenseToggle.setChecked(true);
                newType = TransactionType.EXPENSE;
                categorySpinner.setAdapter(expenseAdapter);
            } else {
                incomeToggle.setChecked(true);
                expenseToggle.setChecked(false);
                newType = TransactionType.INCOME;
                categorySpinner.setAdapter(incomeAdapter);
            }
        }
        else{
            key = myIntent.getStringExtra("key");
            transaction = bgc.getTransaction(key);
            if(transaction.getType()==TransactionType.EXPENSE){
                incomeToggle.setChecked(false);
                expenseToggle.setChecked(true);
                newType = TransactionType.EXPENSE;
                categorySpinner.setAdapter(expenseAdapter);
            }
            else{
                incomeToggle.setChecked(true);
                expenseToggle.setChecked(false);
                newType = TransactionType.INCOME;
                categorySpinner.setAdapter(incomeAdapter);
            }
            amountText.setText(Float.toString(transaction.getAmount()));
            categorySpinner.setSelection(getSpinnerSelection(transaction.getCategory(),categorySpinner));
            repeatingBox.setChecked(transaction.isRepeating());
            if(transaction.isRepeating()) frequencySpinner.setVisibility(View.VISIBLE);
            frequencySpinner.setSelection(getSpinnerSelection(transaction.getFrequency(),frequencySpinner));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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

    public void Save(View view){
        float newAmount = Float.parseFloat(amountText.getText().toString());
        String newCategory = categorySpinner.getSelectedItem().toString();
        String newDate;
        if(newTransaction) {
            newDate = format.format(new Date());
            Log.d("Save", newDate);
        }
        else {
            newDate = transaction.getDate();
            key = transaction.getKey();
            bgc.remove(transaction);
        }
        boolean newRepeating = repeatingBox.isChecked();
        String newFrequency = frequencySpinner.getSelectedItem().toString();
        Transaction newTransactionItem = new Transaction(newAmount, newCategory, newDate, newRepeating, newFrequency, newType, key);
        if(key != null) bgc.remove(newTransactionItem);
        bgc.add(newTransactionItem.getAmount(),newTransactionItem.getCategory(),newTransactionItem.getDate(),newTransactionItem.isRepeating(),newTransactionItem.getFrequency(),newTransactionItem.getType());
        finish();
    }

    private void fillSpinner(){
        ArrayList<String> frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Monthly");
        ArrayAdapter spinnerArrayAdapter2 = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                frequencies);
        frequencySpinner.setAdapter(spinnerArrayAdapter2);
    }

    private int getSpinnerSelection(String string, Spinner spinner){
        for(int i = 0; i<spinner.getAdapter().getCount();i++)
        {
            if (spinner.getItemAtPosition(i).toString().equals(string)) return i;
        }
        return 0;
    }

    public void toggleIncome(View view){
        if(incomeToggle.isChecked()) {
            categorySpinner.setAdapter(incomeAdapter);
            expenseToggle.toggle();
            newType = TransactionType.INCOME;
        }
        else incomeToggle.toggle();
    }
    public void toggleExpense(View view){
        if(expenseToggle.isChecked()) {
            categorySpinner.setAdapter(expenseAdapter);
            incomeToggle.toggle();
            newType = TransactionType.EXPENSE;
        }
        else expenseToggle.toggle();
    }
    public void repeating(View view){
        if(frequencySpinner.getVisibility() == View.INVISIBLE) frequencySpinner.setVisibility(View.VISIBLE);
        else frequencySpinner.setVisibility(View.INVISIBLE);
    }
}
