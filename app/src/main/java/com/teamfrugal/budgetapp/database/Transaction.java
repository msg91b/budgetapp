package com.teamfrugal.budgetapp.database;

//class for transaction, helps us fill in everything on the layout
public class Transaction {
    private int id;
    private String name;
    private double amount;
    private String account;
    private String category;
    private String type;
    private String date;

    public int getId(){
        return id;
    }

    public void setId(int n){
        this.id = n;
    }

    public String getName(){
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double n){
        this.amount = n;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return name;
    }
}
