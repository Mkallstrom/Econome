package com.example.martin.econome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HistoryActivity extends ActionBarActivity {

    private ArrayList<Transaction> allTransactions;
    private Spinner monthSpinner;
    private LineChart lineChart;
    private SimpleDateFormat myFormat;

    private ArrayList<Integer> colors = new ArrayList<>();

    private BackgroundClass bgc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        monthSpinner = (Spinner) findViewById(R.id.monthspinner);
        bgc = (BackgroundClass) getApplicationContext();
        myFormat = new SimpleDateFormat("MMM yyyy");
        fillSpinner();

        allTransactions = bgc.getAllTransactions();

        colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);

        lineChart = (LineChart) findViewById(R.id.graphChart);

        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");

        lineChart.setBackgroundColor(Color.WHITE);
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setStartAtZero(false);
        rightAxis.setStartAtZero(false);

        // enable value highlighting
        lineChart.setHighlightEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        Legend l = lineChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        ArrayList<Entry> values1 = new ArrayList<Entry>();
        ArrayList<Entry> values2 = new ArrayList<Entry>();
        ArrayList<Entry> values3 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = monthSpinner.getCount()-1; i >= 0; i--) {
            xVals.add((monthSpinner.getItemAtPosition(i)) + "");
            values1.add(new Entry(getSummaries(monthSpinner.getItemAtPosition(i).toString())[0],monthSpinner.getCount()-1-i));
            values2.add(new Entry(getSummaries(monthSpinner.getItemAtPosition(i).toString())[1],monthSpinner.getCount()-1-i));
            values3.add(new Entry(getSummaries(monthSpinner.getItemAtPosition(i).toString())[2],monthSpinner.getCount()-1-i));
        }
        LineDataSet set1 = new LineDataSet(values1, "Expenses");
        set1.setLineWidth(2.5f);
        set1.setCircleSize(4f);
        int color = colors.get(0);
        set1.setColor(color);
        set1.setCircleColor(color);
        dataSets.add(set1);
        LineDataSet set2 = new LineDataSet(values2, "Income");
        set2.setLineWidth(2.5f);
        set2.setCircleSize(4f);
        color = colors.get(1);
        set2.setColor(color);
        set2.setCircleColor(color);
        dataSets.add(set2);
        LineDataSet set3 = new LineDataSet(values3, "Total");
        set3.setLineWidth(2.5f);
        set3.setCircleSize(4f);
        color = colors.get(2);
        set3.setColor(color);
        set3.setCircleColor(color);
        dataSets.add(set3);

        // make the first DataSet dashed
        //dataSets.get(0).enableDashedLine(10, 10, 0);
        //dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(xVals, dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
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
        for (Transaction t : allTransactions) {
            cal.setTime(t.getRealDate());
            if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                if (t.getType() == TransactionType.EXPENSE) {
                    expenses += t.getAmount();
                } else if (t.getType() == TransactionType.INCOME) {
                    incomes += t.getAmount();
                }
            }
        }
        float[] returned = new float[3];
        returned[0] = expenses;
        returned[1] = incomes;
        returned[2] = incomes-expenses;
        return returned;
    }

    public void increaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() > 0)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() - 1);
    }

    public void decreaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() < monthSpinner.getAdapter().getCount()-1)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() + 1);
    }

    private void fillSpinner() {
        ArrayAdapter<String> spinMonthAdapter =
                new ArrayAdapter<>(this, R.layout.simple_spinner_item, bgc.getArrayListMonths());
        Spinner spinMonth = (Spinner) findViewById(R.id.monthspinner);
        spinMonth.setAdapter(spinMonthAdapter);
        Intent intent = getIntent();
        if(intent.hasExtra("Selection")) spinMonth.setSelection(intent.getIntExtra("Selection",0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
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

    public void historyClicked(View view){
        ToggleButton tb = (ToggleButton) view;
        tb.setChecked(true);
    }
    public void toTransactions(View view){
        Intent intent = new Intent(this,TransactionsActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }
    public void toCharts(View view){
        Intent intent = new Intent(this,ChartsActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }
    public void toSummary(View view){
        Intent intent = new Intent(this,SummaryActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }
}