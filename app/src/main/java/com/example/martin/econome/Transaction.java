package com.example.martin.econome;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Martin on 2015-05-23.
 */
public class Transaction implements Comparable<Transaction> {
    private float amount;
    private String category;
    private String date;
    private boolean repeating;
    private String frequency;
    private TransactionType type;
    private String key;

    private int month;
    private int year;

    public Transaction(float amount, String category, String date, boolean repeating, String frequency, TransactionType type, String key){
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.repeating = repeating;
        this.frequency = frequency;
        this.type = type;
        this.key = key;
        this.year = Integer.parseInt(date.substring(0,4));
        this.month = Integer.parseInt(date.substring(5,7));
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getRealDate(){
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return myDateFormat.parse(date);
        }
        catch(ParseException e) { // String could not get parsed to a date
            e.printStackTrace();
            return null;
        }
    }

    public int daysAgo() {
        String todaysDate = getTodaysDate();
        int[] todaysDateArray = dateStringToArray(todaysDate);
        int todaysYear = todaysDateArray[0];
        int todaysMonth = todaysDateArray[1];
        int todaysDay = todaysDateArray[2];

        // Based on: http://stackoverflow.com/a/20165708
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = myDateFormat.parse(todaysDate);
            Date date2 = myDateFormat.parse(date);
            long diff = date2.getTime() - date1.getTime();
            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch(ParseException e) { // String could not get parsed to a date
            e.printStackTrace();
            return -9001; // Setting a trash value
        }
    }

    private String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private int[] dateStringToArray(String date) {
        try {
            String[] splitDate = date.split("-");
            int parsedYear = Integer.parseInt(splitDate[0]);
            int parsedMonth = Integer.parseInt(splitDate[1]);
            int parsedDay = Integer.parseInt(splitDate[2]);

            return new int[] {parsedYear, parsedMonth, parsedDay};
        } catch (Exception e) {  // If it fails to parse the date, return [0,0,0]
            e.printStackTrace();
            return new int[]{0, 0, 0};
        }
    }

    public int compareTo (Transaction compared)
    {
        return compared.daysAgo() - daysAgo();
    }

    public String toString(){
        return Float.toString(amount) + "|" + category + "|" + date + "|" + Boolean.toString(repeating) + "|" + frequency + "|" + type.toString();
    }
}
