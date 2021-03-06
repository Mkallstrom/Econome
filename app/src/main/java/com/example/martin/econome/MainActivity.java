package com.example.martin.econome;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    BackgroundClass bgc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bgc = (BackgroundClass) getApplicationContext();
        bgc.initiate();
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

    public void toTransactions(View view){
        Intent intent = new Intent(this,TransactionsActivity.class);
        startActivity(intent);
    }
    public void toCharts(View view){
        Intent intent = new Intent(this,ChartsActivity.class);
        startActivity(intent);
    }
    public void toHistory(View view){
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }
    public void toSummary(View view){
        Intent intent = new Intent(this,SummaryActivity.class);
        startActivity(intent);
    }
    public void fixIndices(View view){
        bgc.fixIndices();
    }
}
