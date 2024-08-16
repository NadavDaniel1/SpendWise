package com.example.spendwise.Model;

import android.widget.Toast;

public class Transaction {

    private String name = "";
    private String amount = "";
    private String date = "";
    private String image = "";


    public Transaction() {
    }

    public Transaction(String name, String amount, String date, String image) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {return image; }
}
