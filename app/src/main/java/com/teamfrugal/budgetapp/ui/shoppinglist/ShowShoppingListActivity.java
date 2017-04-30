package com.teamfrugal.budgetapp.ui.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class ShowShoppingListActivity extends BaseActivity {
	private ListView lv;
	private ArrayList array;

	private float estPrice = 0.0f;

	AppCompatActivity main = this;
	//Swiping
	private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
	private boolean mItemPressed = false; // Detects if user is currently holding down a view
	private static final int MOVE_DURATION = 150;
	HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopping_list);
		setupToolbar();
		this.setTitle("Est cost: ");
		Item bread = new Item("bread", 2.43f, 1);
		Item milk = new Item("milk", 3.80f, 1);
		Item OJ = new Item("orange juice", 5f, 1);
		Item corn = new Item("corn", 0.99f, 1);
		Item beef = new Item("beef", 4.32f,1);
		Item beer = new Item("beer", 5.43f, 1);
		Item bacon = new Item("bacon", 4.35f, 1);
		Item cheese = new Item("pepperjack", 6.84f, 1);
		Item let = new Item("lettuce", 6.84f, 1);
		Item list[] = {bread, milk, OJ, corn, beef, beer, bacon, cheese, let};
		array = new ArrayList<>();
		for(Item i : list)
			array.add(i);

		lv = (ListView) findViewById(R.id.listview);
		StringAdapter adapter = new StringAdapter(ShowShoppingListActivity.this, array, mTouchListener);
		lv.setAdapter(adapter);
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


	// swiping code
	// https://github.com/KevCron/AndroidTutorials/
	private View.OnTouchListener mTouchListener = new View.OnTouchListener()
	{
		float mDownX;
		private int mSwipeSlop = -1;
		boolean swiped;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0)
			{
				mSwipeSlop = ViewConfiguration.get(ShowShoppingListActivity.this).getScaledTouchSlop();
			}
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mItemPressed)
					{
						// Doesn't allow swiping two items at same time
						return false;
					}
					mItemPressed = true;
					mDownX = event.getX();
					swiped = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					v.setTranslationX(0);
					mItemPressed = false;
					break;
				case MotionEvent.ACTION_MOVE:
				{
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);

					if (!mSwiping)
					{
						if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
						{
							mSwiping = true;
							lv.requestDisallowInterceptTouchEvent(true);
						}
					}
					if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
					{
						v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

						if (deltaX >= v.getWidth() /3 ) // swipe to right
						{
							Item t = (Item) array.get(lv.getPositionForView(v));
							estPrice += t.price;
							main.setTitle(String.format("Est cost: $%.2f", estPrice));
							v.setEnabled(false); // need to disable the view for the animation to run

							// stacked the animations to have the pause before the views flings off screen
							v.animate().setDuration(30).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
								@Override
								public void run()
								{
									v.animate().setDuration(30).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable()
									{
										@Override
										public void run()
										{
											mSwiping = false;
											mItemPressed = false;
											animateRemoval(lv, v);
										}
									});
								}
							});
							mDownX = x;
							swiped = true;
							return true;




						}
						else if (deltaX < -1 * (v.getWidth() / 3)) // swipe to left
						{


							mDownX = x;
							swiped = true;
							mSwiping = false;
							mItemPressed = false;


							v.animate().setDuration(10).translationX(v.getWidth()/3); // could pause here if you want, same way as delete
							TextView tv = (TextView) v.findViewById(R.id.row_item);
							tv.setText("Swiped!");
							return true;
						}
					}

				}
				break;
				case MotionEvent.ACTION_UP:
				{
					if (mSwiping) // if the user was swiping, don't go to the and just animate the view back into position
					{
						v.animate().setDuration(30).translationX(0).withEndAction(new Runnable()
						{
							@Override
							public void run()
							{
								mSwiping = false;
								mItemPressed = false;
								lv.setEnabled(true);
							}
						});
					}
					else // user was not swiping; registers as a click
					{
						mItemPressed = false;
						lv.setEnabled(true);

						int i = lv.getPositionForView(v);

						Toast.makeText(ShowShoppingListActivity.this, array.get(i).toString(), Toast.LENGTH_LONG).show();

						return false;
					}
				}
				default:
					return false;
			}
			return true;
		}
	};

	// animates the removal of the view, also animates the rest of the view into position
	private void animateRemoval(final ListView listView, View viewToRemove)
	{
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		final ArrayAdapter adapter = (ArrayAdapter)lv.getAdapter();
		for (int i = 0; i < listView.getChildCount(); ++i)
		{
			View child = listView.getChildAt(i);
			if (child != viewToRemove)
			{
				int position = firstVisiblePosition + i;
				long itemId = listView.getAdapter().getItemId(position);
				mItemIdTopMap.put(itemId, child.getTop());
			}
		}

		adapter.remove(adapter.getItem(listView.getPositionForView(viewToRemove)));

		final ViewTreeObserver observer = listView.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw()
			{
				observer.removeOnPreDrawListener(this);
				boolean firstAnimation = true;
				int firstVisiblePosition = listView.getFirstVisiblePosition();
				for (int i = 0; i < listView.getChildCount(); ++i)
				{
					final View child = listView.getChildAt(i);
					int position = firstVisiblePosition + i;
					long itemId = adapter.getItemId(position);
					Integer startTop = mItemIdTopMap.get(itemId);
					int top = child.getTop();
					if (startTop != null)
					{
						if (startTop != top)
						{
							int delta = startTop - top;
							child.setTranslationY(delta);
							child.animate().setDuration(MOVE_DURATION).translationY(0);
							if (firstAnimation) {
								child.animate().withEndAction(new Runnable()
								{
									public void run()
									{
										mSwiping = false;
										lv.setEnabled(true);
									}
								});
								firstAnimation = false;
							}
						}
					}
					else {
						// Animate new views along with the others. The catch is that they did not
						// exist in the start state, so we must calculate their starting position
						// based on neighboring views.
						int childHeight = child.getHeight() + listView.getDividerHeight();
						startTop = top + (i > 0 ? childHeight : -childHeight);
						int delta = startTop - top;
						child.setTranslationY(delta);
						child.animate().setDuration(MOVE_DURATION).translationY(0);
						if (firstAnimation) {
							child.animate().withEndAction(new Runnable()
							{
								public void run()
								{
									mSwiping = false;
									lv.setEnabled(true);
								}
							});
							firstAnimation = false;
						}
					}
				}
				mItemIdTopMap.clear();
				return true;
			}
		});
	}

}

/*
public class ShowShoppingListActivity extends AppCompatActivity {

    private SwipeDetector swipeDetector;

    private float estPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        this.setTitle("ShoppingList Est: ");
        Item bread = new Item("bread", 2.43f, 1);
        Item milk = new Item("milk", 3.80f, 1);
        Item OJ = new Item("orange juice", 5f, 1);
        Item corn = new Item();
        corn.name = "corn";
        corn.price = 1.2f;
        corn.quantity = 1;
        Item beef = new Item();
        beef.name = "beef";
        beef.price = 1.6f;
        beef.quantity = 1;
        Item beer = new Item("beer", 5.43f, 1);
        Item bacon = new Item("bacon", 4.35f, 1);
        Item cheese = new Item("pepperjack", 6.84f, 1);
        Item let = new Item("lettuce", 6.84f, 1);

        Item list[] = {bread, milk, OJ, corn, beef, beer, bacon, cheese, let};

        String gg[] = new String[23];

        final ArrayAdapter appenderList = new ArrayAdapter<Item>(this, R.layout.listview_item, list);
        //StringAdapter s = new StringAdapter(this, gg, null);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if(swipeDetector.swipeDetected()){
                Log.d("bruh", "p " + position);
                //} else{

                //}
            }
        };

		//View.OnTouchListener mListener = new

        ListView shoppingList = (ListView) findViewById(R.id.listview);
		LayoutInflater in = LayoutInflater.from(appenderList.getContext());
        // swipeDetector = new SwipeDetector(shoppingList);

        //shoppingList.setOnTouchListener(swipeDetector);
        //shoppingList.setOnItemClickListener(listener);
        shoppingList.setAdapter(appenderList);


    }


}
*/
