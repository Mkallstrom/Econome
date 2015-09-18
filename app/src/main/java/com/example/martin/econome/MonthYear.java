package com.example.martin.econome;

/**
 * Created by Martin on 2015-05-30.
 */
public class MonthYear implements Comparable<MonthYear> {
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

    public int compareTo(MonthYear comparedTo){
        if(year-comparedTo.getYear() == 0)
        {
            return comparedTo.getMonth()-month;
        }
        else
            return comparedTo.getYear()-year;
    }

    public String toString(){
        String string;
        switch(month) {
            case 0:
                string = "Jan";
                break;
            case 1:
                string = "Feb";
                break;
            case 2:
                string = "Mar";
                break;
            case 3:
                string = "Apr";
                break;
            case 4:
                string = "May";
                break;
            case 5:
                string = "Jun";
                break;
            case 6:
                string = "Jul";
                break;
            case 7:
                string = "Aug";
                break;
            case 8:
                string = "Sep";
                break;
            case 9:
                string = "Oct";
                break;
            case 10:
                string = "Nov";
                break;
            case 11:
                string = "Dec";
                break;
            default:
                string = "Error";
        }
        return string + " " + Integer.toString(year);
    }
}
