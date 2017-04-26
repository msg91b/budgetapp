package com.teamfrugal.budgetapp.database;

/* This is the DAO
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.teamfrugal.budgetapp.database.SQLiteHelper.COLUMN_transID;

public class DataAccess {
    private static SQLiteDatabase database;
    private SQLiteHelper helper;
    private static Cursor cc;
    private String[] allColumns = { COLUMN_transID,
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

    public Transaction newTransact(String name, Double amount, String account, String category,
                                   String type, String date){
        Transaction t = new Transaction(name, amount, account, category, type, date);

        return t;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    // grabbing all items?
    public Cursor query() {
        System.out.println(database.rawQuery("SELECT * FROM transactionA",null));
        return database.rawQuery("SELECT * FROM transactionA",null);
    }
    public void drop() {
        database.execSQL("DROP TABLE if exists transactionA");
        database.execSQL(SQLiteHelper.CREATE_DB);
    }

    public static int nextId() {
        String stmt = "SELECT '" + COLUMN_transID + "' FROM transactionA";
        cc = database.rawQuery(stmt, null);

        int id = 0;
        if (cc.moveToFirst())
        {
            do
            {
                int tmp = cc.getInt(0);
                if (tmp > id) {
                    id = tmp;
                }
                //id = cc.getInt(0);
                System.out.println("iddd::::" + id);
            } while(cc.moveToNext());
        }
        System.out.println("idb: " + id);
        System.out.println("ida: " + (id+1));
        return id+1;
    }
}
