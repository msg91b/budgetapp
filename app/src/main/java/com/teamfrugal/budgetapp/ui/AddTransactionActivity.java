package com.teamfrugal.budgetapp.ui;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.database.DataAccess;
import com.teamfrugal.budgetapp.database.ListContent;
import com.teamfrugal.budgetapp.ui.quote.ListActivity;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class AddTransactionActivity extends Activity implements OnItemSelectedListener {

    private DataAccess mDataAccess;
    private String mItemSelected;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> expenseCategories = new ArrayList<String>();
        expenseCategories.add("Food/Groceries");
        expenseCategories.add("Gas/Travel");
        expenseCategories.add("Housing");
        expenseCategories.add("Utilities");
        expenseCategories.add("Healthcare");
        expenseCategories.add("Education");
        expenseCategories.add("Personal");
        expenseCategories.add("Entertainment");
        expenseCategories.add("Debt");

        List<String> incomeCategories = new ArrayList<String>();
        incomeCategories.add("Income");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseCategories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        EditText accountBox = (EditText) findViewById(R.id.amountText);


        accountBox.setText(ListContent.newest.amount+"");

        Button add = (Button) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mDataAccess = new DataAccess(getApplicationContext());
                mDataAccess.open();
                //Transaction newTransaction = mDataAccess.newTransact(ListContent.newest.store, ListContent.newest.amount, "acct", mItemSelected , "type", "date");
                final String SQL_ADD = "INSERT INTO transactionA Values (" + ListContent.newest.id + ", '" + ListContent.newest.store + "', '" + ListContent.newest.amount
                        + "', " + "'a', '" + mItemSelected + "', 'c', 'd' );";
                mDataAccess.getDatabase().execSQL(SQL_ADD);
                //System.out.println("item added to db");
                mDataAccess.close();
                //Context context = getApplicationContext();
                //finish();
                getApplicationContext().startActivity(new Intent(getApplicationContext(), ListActivity.class));
                finish();

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        mItemSelected = item;
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        //nothing happens???
    }

}
