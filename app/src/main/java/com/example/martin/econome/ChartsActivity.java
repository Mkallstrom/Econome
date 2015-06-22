package com.example.martin.econome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChartsActivity extends ActionBarActivity {
    private ArrayList<Transaction> allTransactions;
    private ArrayList<Transaction> shownTransactions;
    private Spinner monthSpinner;
    private PieChart pieChart;
    private PieDataSet pieDataSet;
    private BarChart barChart;

    private BackgroundClass bgc;

    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();

    private ToggleButton pieToggleButton;
    private ToggleButton barToggleButton;

    private SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        bgc = (BackgroundClass) getApplicationContext();
        allTransactions = bgc.getAllTransactions();
        shownTransactions = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd");
        monthSpinner = (Spinner) findViewById(R.id.monthspinner);
        fillSpinner();

        pieToggleButton = (ToggleButton) findViewById(R.id.pie);
        barToggleButton = (ToggleButton) findViewById(R.id.bar);

        loadThisMonth();

        setCategories();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        initiatePieChart();
        initiateBarChart();

        loadChart();
    }

    private void initiatePieChart(){
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setDescription("");
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setLabels(categories);
        l.setTextColor(Color.WHITE);
        pieChart.setHoleColor(Color.LTGRAY);
        pieChart.setDrawSliceText(false);
    }

    private void initiateBarChart(){
        barChart = (BarChart) findViewById(R.id.barchart);
        barChart.setDescription("");
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextColor(Color.WHITE);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(4f);
        l.setColors(colors);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charts, menu);
        return true;
    }

    private void setCategories(){
        categories.clear();
        categories.add("Food");
        categories.add("Phone");
        categories.add("Transportation");
        categories.add("Rent");
        categories.add("Eating Out");
        categories.add("Clothes");
        categories.add("Hobbies");
        categories.add("Misc");
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

    private ArrayList<String> addPercentages(ArrayList<String> strings, ArrayList<Float> amounts, float total)
    {
        ArrayList<String> returnStrings = new ArrayList<>();
        for(String s : strings)
        {
            returnStrings.add(s + " - " + (Math.round((amounts.get(strings.indexOf(s))/total)*100)) + "%");
        }
        return returnStrings;
    }

    public void increaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() > 0)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() - 1);
    }

    public void decreaseMonth(View view){
        if(monthSpinner.getSelectedItemPosition() < monthSpinner.getAdapter().getCount()-1)
            monthSpinner.setSelection(monthSpinner.getSelectedItemPosition() + 1);
    }

    private void loadChart(){
        Log.d("loadChart", "Loading chart");
        setCategories();
        amounts.clear();
        entries.clear();
        for(int i = 0;i<=7;i++) {
            amounts.add(0, 0f);
        }
        for(Transaction i : shownTransactions)
        {
            switch(i.getCategory())
            {
                case "Food":
                    amounts.set(0, new Float(amounts.get(0)+i.getAmount()));
                    break;
                case "Phone":
                    amounts.set(1, new Float(amounts.get(1)+i.getAmount()));
                    break;
                case "Transportation":
                    amounts.set(2, new Float(amounts.get(2)+i.getAmount()));
                    break;
                case "Rent":
                    amounts.set(3, new Float(amounts.get(3)+i.getAmount()));
                    break;
                case "Eating Out":
                    amounts.set(4, new Float(amounts.get(4)+i.getAmount()));
                    break;
                case "Clothes":
                    amounts.set(5, new Float(amounts.get(5)+i.getAmount()));
                    break;
                case "Hobbies":
                    amounts.set(6, new Float(amounts.get(6)+i.getAmount()));
                    break;
                case "Misc":
                    amounts.set(7, new Float(amounts.get(7)+i.getAmount()));
            }
        }
        float total = 0;
        for(int i = 0;i<=7;i++)
        {
            entries.add(new Entry(amounts.get(i),i));
            total+=amounts.get(i);

        }
        pieChart.setCenterText(Float.toString(total));

        pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(colors);
        categories = addPercentages(categories,amounts,total);

        PieData data = new PieData(categories, pieDataSet);
        pieChart.clear();
        pieChart.setData(data);

        barChart.clear();

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        for(int i = 0; i<8; i++){
            ArrayList<BarEntry> newArrayListBarEntry = new ArrayList<>();

            BarEntry newBarEntry = new BarEntry(amounts.get(i),i);

            newArrayListBarEntry.add(newBarEntry);

            BarDataSet newBarDataSet = new BarDataSet(newArrayListBarEntry,categories.get(i));
            newBarDataSet.setColor(colors.get(i));
            Log.d("barchart", Integer.toString(newBarDataSet.getEntryCount()));
            barDataSets.add(newBarDataSet);
        }

        BarData barData = new BarData(categories, barDataSets);
        barData.setGroupSpace(80f);
        barData.setValueTextSize(10f);
        barData.setValueTextColor(Color.WHITE);
        barChart.setData(barData);
        barChart.fitScreen();
        barChart.zoom(4f, 1f, 0, 0);
        Log.d("barchart", Integer.toString(barChart.getBarData().getDataSetCount()));
    }

    public void pie(View view){
        if(pieChart.getVisibility()==View.INVISIBLE) {
            pieChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.INVISIBLE);
            barToggleButton.setChecked(false);
        }
        else
            pieToggleButton.setChecked(true);
    }

    public void bar(View view){
        if(barChart.getVisibility()==View.INVISIBLE) {
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
            pieToggleButton.setChecked(false);
        }
        else
            barToggleButton.setChecked(true);
    }

    private void loadThisMonth(){
        shownTransactions.clear();
        for(Transaction newTransaction : allTransactions){
                try {
                    Date date = format.parse(newTransaction.getDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if(correctMonth(cal))
                        shownTransactions.add(newTransaction);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    private boolean correctMonth(Calendar cal){
        try {
            Calendar spinnerCal = Calendar.getInstance();
            spinnerCal.setTime(new SimpleDateFormat("MMM yyyy").parse(monthSpinner.getSelectedItem().toString()));
            if(spinnerCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && spinnerCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
                        loadChart();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }
    public void toTransactions(View view){
        Intent intent = new Intent(this,TransactionsActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }
    public void toSummary(View view){
        Intent intent = new Intent(this,SummaryActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }

    public void toHistory(View view){
        Intent intent = new Intent(this,HistoryActivity.class);
        intent.putExtra("Selection", monthSpinner.getSelectedItemPosition());
        startActivity(intent);
    }
    public void chartsClicked(View view){
        ToggleButton button = (ToggleButton) view;
        button.setChecked(true);
    }
}
