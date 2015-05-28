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
 * Created by Martin on 2015-05-26.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private ArrayList<String> categories;
    private LayoutInflater inflater;

    public CategorySpinnerAdapter(Context context, int resource, ArrayList<String> categories) {
        super(context, resource, categories);
        this.context=context;
        this.resource=resource;
        this.categories = categories;
        inflater=((Activity)context).getLayoutInflater();
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(resource,parent,false);

        TextView label=(TextView)row.findViewById(R.id.category);
        ImageView icon = (ImageView)row.findViewById(R.id.icon);

        label.setText(categories.get(position));
        switch(label.getText().toString()){
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
            case "Incomes":
                icon.setImageResource(R.drawable.incomesicon);
                break;
            case "Expenses":
                icon.setImageResource(R.drawable.expensesicon);
                break;
            case "Total":
                icon.setImageResource(R.drawable.totalicon);
                break;
            default:
                icon.setVisibility(View.INVISIBLE);
                break;
        }

        return row;
    }
}