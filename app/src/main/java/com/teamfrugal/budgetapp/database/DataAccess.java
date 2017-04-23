package com.teamfrugal.budgetapp.database;

/* This is the DAO
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataAccess {
    private SQLiteDatabase database;
    private SQLiteHelper helper;
    private String[] allColumns = { SQLiteHelper.COLUMN_transID,
            SQLiteHelper.COLUMN_name, SQLiteHelper.COLUMN_amount, SQLiteHelper.COLUMN_account,
            SQLiteHelper.COLUMN_category, SQLiteHelper.COLUMN_type, SQLiteHelper.COLUMN_datetime
    };

    public DataAccess (Context context){
        helper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public Transaction newTransact(String name, String amount, String account, String category,
                                   String type, String date){
        Transaction t = new Transaction();

        return t;
    }
}
