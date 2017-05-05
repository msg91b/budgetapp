package com.teamfrugal.budgetapp.ui.quote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.database.DataAccess;
import com.teamfrugal.budgetapp.database.ListContent;
import com.teamfrugal.budgetapp.database.SQLiteHelper;
import com.teamfrugal.budgetapp.dummy.DummyContent;
import com.teamfrugal.budgetapp.ui.CameraActivity;
import com.teamfrugal.budgetapp.ui.base.BaseActivity;
import com.teamfrugal.budgetapp.util.LogUtil;

import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Lists all available quotes. This Activity supports a single pane (= smartphones) and a two pane mode (= large screens with >= 600dp width).
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class ListActivity extends BaseActivity implements ArticleListFragment.Callback {
    /**
     * Whether or not the activity is running on a device with a large screen
     */
    private boolean twoPaneMode;
    static final String TAG = "mainActivity";
    private static final int PERMISSION_FOR_CAMERA = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        setupToolbar();


        ListContent content = new ListContent();

        //TextView total = (TextView) findViewById(R.id.textView);
        TextView tTextView = (TextView) findViewById(R.id.textView);
        content.init(this, tTextView);


//        DataAccess da = new DataAccess(this);
//        da.open();
//        da.drop();

        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
            // handle this here
        } else {
            Log.d(TAG, "OpenCV loaded");
        }


        System.out.println("created ");

        if (isTwoPaneLayoutUsed()) {
            twoPaneMode = true;
            LogUtil.logD("TEST","TWO POANE TASDFES");
            enableActiveItemState();
        }

        if (savedInstanceState == null && twoPaneMode) {
            setupDetailFragment();
        }
    }

    @OnClick(R.id.fabCamera)
    public void onFabClicked(View view) {

        //this checks if we have camera permissions and requests them if we don't
        int permissionCheck[] = {ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA),
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)};
        int suc = PackageManager.PERMISSION_GRANTED;

        if (permissionCheck[0] != suc){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_FOR_CAMERA);
        }
        if(permissionCheck[1] != suc){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_FOR_CAMERA);
        }
        if(permissionCheck[2] != suc) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_FOR_CAMERA);
        }
        if( permissionCheck[0] == suc && permissionCheck[1] == suc && permissionCheck[2] == suc){
            Intent intent = new Intent(ListActivity.this, CameraActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Called when an item has been selected
     *
     * @param id the selected quote ID
     */
    @Override
    public void onItemSelected(String id) {
        if (twoPaneMode) {
            // Show the quote detail information by replacing the DetailFragment via transaction.
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(id);
            getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
        } else {
            // Start the detail activity in single pane mode.
            Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
            detailIntent.putExtra(ArticleDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupDetailFragment() {
        //ArticleDetailFragment fragment =  ArticleDetailFragment.newInstance(DummyContent.ITEMS.get(0).id);
        ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(""+ListContent.results.get(0).id);
        getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
    }

    /**
     * Enables the functionality that selected items are automatically highlighted.
     */
    private void enableActiveItemState() {
        ArticleListFragment fragmentById = (ArticleListFragment) getFragmentManager().findFragmentById(R.id.article_list);
        fragmentById.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * Is the container present? If so, we are using the two-pane layout.
     *
     * @return true if the two pane layout is used.
     */
    private boolean isTwoPaneLayoutUsed() {
        return findViewById(R.id.article_detail_container) != null;
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
        return R.id.nav_quotes;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    Intent intent = new Intent(ListActivity.this,
                            CameraActivity.class);
                    startActivity(intent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    //maybe put a toast here saying that the app can't do anything without camera?
                    String cameraExplain = "Cannot open camera without permission.";
                    Toast toast = Toast.makeText(this, cameraExplain, Toast.LENGTH_SHORT);
                    toast.show();
                }
                return;
            }
        }
    }
}
