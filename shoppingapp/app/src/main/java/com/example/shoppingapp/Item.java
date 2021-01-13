package com.example.shoppingapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Item {

    /**
     * Basic holder class for the items from database
     * Probably have to wait for Tony to send backend API so we can know how to name fields,etc
     */

     //TODO wait for tony backend stuff to see if we need pics or not

    private String name;
    private String price;
    private String description;


    public Item(String name, String price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
