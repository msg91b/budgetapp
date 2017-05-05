package com.teamfrugal.budgetapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.dummy.DummyContent;
import com.teamfrugal.budgetapp.ui.base.BaseActivity;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.random;

/**
 * Created by Matthew on 4/25/2017.
 */

public class ListContent extends BaseActivity {
    public static ArrayList<Item> results = new ArrayList<>();
    public static final Map<String, ListContent.Item> ITEM_MAP = new HashMap<>();
    static DataAccess da = null;
    private Cursor cursor;
    public static Item newest;


    NumberFormat nf = NumberFormat.getCurrencyInstance();
    static Context con;


    TextView tTextView;
    public static double total;

    static Random r = new Random();

   // @Override
   // protected void onCreate(Bundle savedInstanceState) {
    public void init(Context con, TextView tTextView) {
       //e this.tTextView = tTextView;
        System.out.println("outsideeeeeeeeeeeeeeee");
        this.con = con;
        total = 0.00;
        int i = 1;
        try {
            System.out.println("onCreate ListContent");
            //con = this.getApplicationContext();
            da = new DataAccess(con);
            da.open();
            //da.drop();
            //da.insertTest();
            System.out.println("opening");
            cursor = da.query();
            System.out.println("querying");
            results.clear();

            if (cursor != null) {
                System.out.println("CURSOR NOT NULL"    );
                if  (cursor.moveToFirst()) {
                    do {
                        Item item;
                        System.out.println("item");
                        String isExpense = "";
                        int id = cursor.getInt(0);
                        String store = cursor.getString(1);
                        Double amount = cursor.getDouble(2);
                        String date = cursor.getString(6);

                        System.out.println("ST-----> " + store + " ::::: " + amount);

                        System.out.println(cursor.getString(5));
                        if (cursor.getString(5).compareTo("expense") == 0) {
                            total -= amount;
                            isExpense = "Expense";
                            System.out.println("TOTAL IS (after income): " + total);
                        } else {
                            total += amount;
                            isExpense = "Income";
                            System.out.println("TOTAL IS: " + total);
                        }
                        System.out.println("dt: " + date.substring(0, 10));
                        date = date.substring(0, 10).replace("-", "/");
                        if (isExpense.compareTo("Expense") == 0) {
                            item = new Item(id, R.drawable.ic_remove, store, amount, isExpense, date);
                        } else{
                            item = new Item(id, R.drawable.ic_add, store, amount, isExpense, date);
                        }
                        results.add(item);
                        ITEM_MAP.put(""+id, item);


                    } while (cursor.moveToNext());
                }
            }

        } catch (SQLException e) {
            System.out.println("DB FAIL");
        } finally {
            da.close();
        }

        //tTextView.setText(total+"");
    }

    public static int randPhotoId() {
        int rnd = r.nextInt() % 5 + 1;
        if (rnd == 1) {
            return R.drawable.p1;
        } else if (rnd == 2) {
            return R.drawable.p2;
        } else if (rnd == 3) {
            return R.drawable.p3;
        } else if (rnd == 4) {
            return R.drawable.p4;
        } else {
            return R.drawable.p5;
        }
    }

    public static void addItem(Item item) {
        da.open();
        int id = da.nextId();
        item.id = id;
        newest = item;
        //results.add(item);
        //da = new DataAccess(con);
        //da.open();
        //Transaction newTransaction = da.newTransact(item.store, item.amount, "acct", "cate", "type", "date");
        //final String SQL_ADD = "INSERT INTO  Transaction Values (" + 0 + ", '" + item.store + "', '" + item.amount
        //        + "', " + "'a', 'b', 'c', 'd' );";
        //da.getDatabase().execSQL(SQL_ADD);
        //System.out.println("item added to db");
        //da.close();
    }



    @Override
    public boolean providesActivityToolbar() {
        return false;
    }

    public static class Item {
        public int id;
        public final int photoId;
        public final String store;
        public final double amount;
        public final String isExpense;
        public final String date;

        public Item(int id, int photoId, String store, double amount, String date) {
            this.id = id;
            this.photoId = photoId;
            this.store = store;
            this.amount = amount;
            this.date = date;
            this.isExpense = "Expense";
        }

        public Item(int id, int photoId, String store, double amount, String isExpense, String date) {
            this.id = id;
            this.photoId = photoId;
            this.store = store;
            this.amount = amount;
            this.isExpense = isExpense;
            this.date = date;
        }
    }
}