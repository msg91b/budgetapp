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
        total = 0.0;
        int i = 1;
        try {
            System.out.println("onCreate ListContent");
            //con = this.getApplicationContext();
            da = new DataAccess(con);
            da.open();
            //da.drop();
            System.out.println("opening");
            cursor = da.query();
            System.out.println("querying");
            results.clear();

            if (cursor != null) {
                System.out.println("CURSOR NOT NULL"    );
                if  (cursor.moveToFirst()) {
                    do {
                        System.out.println("item");
                        int id = cursor.getInt(0);
                        String store = cursor.getString(1);
                        Double amount = cursor.getDouble(2);

                        System.out.println("ST-----> " +store + " ::::: " + amount);
                        total += amount;
                        Item item = new Item(id, randPhotoId(), store, amount);
                        results.add(item);
                        ITEM_MAP.put(""+id, item);


                    }while (cursor.moveToNext());
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
        //int id = da.nextId();
        int id = SQLiteHelper.currentID;
        SQLiteHelper.currentID += 1;
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

        public Item(int id, int photoId, String store, double amount) {
            this.id = id;
            this.photoId = photoId;
            this.store = store;
            this.amount = amount;
        }
    }
}
