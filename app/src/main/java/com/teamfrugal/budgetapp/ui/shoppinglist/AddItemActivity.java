package com.teamfrugal.budgetapp.ui.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.ui.base.BaseActivity;

/**
 * Created by Anthony on 4/30/2017.
 */

public class AddItemActivity extends BaseActivity{



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
	//	setupToolbar();
	}


	private void setupToolbar() {
		final ActionBar ab = getActionBarToolbar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sample_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				openDrawer();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return R.id.nav_shoppingList;
	}

	@Override
	public boolean providesActivityToolbar() {
		return true;
	}

}
