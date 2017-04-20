package com.teamfrugal.budgetapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "FrugalApp.db";
    private static int DATABASE_VERSION = 1;

    public static final String TABLE_TRANSACTION = "transaction";
    public static final String COLUMN_transID = "transID";
    public static final String COLUMN_name = "name";
    public static final String COLUMN_amount = "amount";
    public static final String COLUMN_account = "account";
    public static final String COLUMN_category = "category";
    public static final String COLUMN_type = "type";
    public static final String COLUMN_datetime = "date";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("Database created.");
    }


    private static final String CREATE_DB =
            "create table if not exists" + TABLE_TRANSACTION
                    + "( " + COLUMN_transID + " integer primary key autoincrement, "
                    + COLUMN_name + "text not null, "
                    + COLUMN_amount + " real not null, "
                    + COLUMN_account + " text, "
                    + COLUMN_category + " text, "
                    + COLUMN_type + "text, "
                    + COLUMN_datetime + "text "
                    + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DB);
    }

    //necessary overrides
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion +
                        " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }
}
