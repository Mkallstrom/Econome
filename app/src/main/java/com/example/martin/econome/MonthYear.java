package com.example.martin.econome;

/**
 * Created by Martin on 2015-05-30.
 */
public class MonthYear {
    private int month;
    private int year;

    public MonthYear(int month, int year) {
        this.month = month;
        this.year = year;
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
}
