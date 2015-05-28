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
 * Created by Martin on 2015-05-23.
 */
public class TransactionArrayAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private int resource;
    private ArrayList<Transaction> transactions;
    private LayoutInflater inflater;

    public TransactionArrayAdapter(Context context, int resource, ArrayList transactions)
    {
        super(context, resource, transactions);
        this.context=context;
        this.resource=resource;
        this.transactions = transactions;
        inflater=((Activity)context).getLayoutInflater();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(resource,parent,false);

        TextView category = (TextView)row.findViewById(R.id.category);
        TextView date = (TextView)row.findViewById(R.id.date);
        TextView amount = (TextView)row.findViewById(R.id.amount);
        ImageView icon = (ImageView)row.findViewById(R.id.icon);

        category.setText(transactions.get(position).getCategory());
        date.setText(transactions.get(position).getDate());
        amount.setText(Float.toString(transactions.get(position).getAmount()) + ":-");

        if(transactions.get(position).getType()== TransactionType.EXPENSE)
            amount.setText("-" + amount.getText().toString());
        else
            amount.setText("+" + amount.getText().toString());
        switch(category.getText().toString()){
            case "Food":
                icon.setImageResource(R.drawable.foodicon);
                break;
            case "Rent":
                icon.setImageResource(R.drawable.renticon);
                break;
            case "Transportation":
                icon.setImageResource(R.drawable.transportationicon);
                break;
            case "Phone":
                icon.setImageResource(R.drawable.phoneicon);
                break;
            case "Clothes":
                icon.setImageResource(R.drawable.clothesicon);
                break;
            case "Eating Out":
                icon.setImageResource(R.drawable.eatingouticon);
                break;
            case "Hobbies":
                icon.setImageResource(R.drawable.hobbiesicon);
                break;
            case "Salary":
                icon.setImageResource(R.drawable.salaryicon);
                break;
            case "Misc":
                icon.setImageResource(R.drawable.miscicon);
                break;
            case "Other":
                icon.setImageResource(R.drawable.othericon);
                break;
            default:
                break;
        }

        return row;
    }


}
