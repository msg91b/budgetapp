package com.teamfrugal.budgetapp.ui.shoppinglist;

import java.util.Locale;

/**
 * Created by Anthony on 4/24/2017.
 */

public class Item {
    public String name;
    public float price;
    public int quantity;
    Item(){}
    Item(String name, float price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public float getPrice(){ return quantity * price; }
    public String toString(){
        return String.format(Locale.ENGLISH, "%s $%.2f \tquantity: %d", name, price, quantity);
    }
}
