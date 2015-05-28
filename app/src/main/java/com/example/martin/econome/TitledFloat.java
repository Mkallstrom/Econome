package com.example.martin.econome;

/**
 * Created by Martin on 2015-05-25.
 */
public class TitledFloat {
    private float amount;
    private String title;
    private float average;

    public TitledFloat(float amount, float average, String title)
    {
        this.amount = amount;
        this.title = title;
        this.average = average;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }
}
