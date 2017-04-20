package com.teamfrugal.budgetapp.database;

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
            SQLiteHelper.COLUMN_name, };

}
