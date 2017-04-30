package com.teamfrugal.budgetapp.ui.shoppinglist;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamfrugal.budgetapp.R;

import java.util.ArrayList;

/**
 * Created by kevin on 4/16/15.
 */
public class StringAdapter extends ArrayAdapter<Item>
{

	View.OnTouchListener mTouchListener;

	public StringAdapter(Context context, ArrayList<Item> values, View.OnTouchListener listener)
	{
		super(context, R.layout.listview_item, values);
		mTouchListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());

		View v = inflater.inflate(R.layout.listview_item, parent, false);

		TextView b = (TextView) v.findViewById(R.id.row_item);
		b.setText(getItem(position).toString());

		v.setOnTouchListener(mTouchListener);

		return v;
	}

}