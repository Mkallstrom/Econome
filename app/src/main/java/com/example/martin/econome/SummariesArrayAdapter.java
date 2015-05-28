package com.example.martin.econome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Martin on 2015-05-25.
 */
public class SummariesArrayAdapter extends ArrayAdapter<TitledFloat> {

private Context context;
private int resource;
private ArrayList<TitledFloat> summaries;
private LayoutInflater inflater;

public SummariesArrayAdapter(Context context, int resource, ArrayList summaries)
        {
        super(context, resource, summaries);
        this.context=context;
        this.resource=resource;
        this.summaries = summaries;
        inflater=((Activity)context).getLayoutInflater();

        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(resource,parent,false);

        TextView category = (TextView)row.findViewById(R.id.category);
        TextView amount = (TextView)row.findViewById(R.id.amount);
        TextView average = (TextView)row.findViewById(R.id.average);

        ImageView icon = (ImageView)row.findViewById(R.id.icon);

        amount.setText(Float.toString(summaries.get(position).getAmount())+":-");
        category.setText(summaries.get(position).getTitle());
        average.setText(Float.toString(summaries.get(position).getAverage())+":-");

        switch(category.getText().toString()){
                case "Food":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.foodicon);
                        break;
                case "Rent":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                            icon.setImageResource(R.drawable.renticon);
                        break;
                case "Transportation":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.transportationicon);
                        break;
                case "Phone":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.phoneicon);
                        break;
                case "Clothes":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.clothesicon);
                        break;
                case "Eating Out":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.eatingouticon);
                        break;
                case "Hobbies":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.hobbiesicon);
                        break;
                case "Salary":
                    amount.setText("+" + amount.getText().toString());
                    average.setText("+" + average.getText().toString());
                        icon.setImageResource(R.drawable.salaryicon);
                        break;
                case "Misc":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                        icon.setImageResource(R.drawable.miscicon);
                        break;
                case "Other":
                    amount.setText("+" + amount.getText().toString());
                    average.setText("+" + average.getText().toString());
                        icon.setImageResource(R.drawable.othericon);
                        break;
                case "Incomes":
                    amount.setText("+" + amount.getText().toString());
                    average.setText("+" + average.getText().toString());
                        icon.setImageResource(R.drawable.incomesicon);
                        break;
                case "Expenses":
                    amount.setText("-" + amount.getText().toString());
                    average.setText("-" + average.getText().toString());
                    icon.setImageResource(R.drawable.expensesicon);
                        break;
                case "Total":
                        icon.setImageResource(R.drawable.totalicon);
                        break;
                default:
                    icon.setVisibility(View.INVISIBLE);
                    category.setVisibility(View.INVISIBLE);
                    break;
        }

        return row;
        }
}

