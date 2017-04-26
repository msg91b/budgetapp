package com.teamfrugal.budgetapp.ui.quote;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.database.ListContent;
import com.teamfrugal.budgetapp.dummy.DummyContent;
import com.teamfrugal.budgetapp.ui.base.BaseActivity;
import com.teamfrugal.budgetapp.ui.base.BaseFragment;

/**
 * Shows the quote detail page.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class ArticleDetailFragment extends BaseFragment {

    /**
     * The argument represents the dummy item ID of this fragment.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content of this fragment.
     */
    //private DummyContent.DummyItem dummyItem;
    private ListContent.Item listItem;

    @Bind(R.id.amount)
    TextView amount;

    @Bind(R.id.backdrop)
    ImageView backdropImg;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // load dummy item by using the passed item ID.
            //dummyItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            listItem = ListContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_article_detail);

        if (!((BaseActivity) getActivity()).providesActivityToolbar()) {
            // No Toolbar present. Set include_toolbar:
            ((BaseActivity) getActivity()).setToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
        }

       // if (dummyItem != null) {
        if (listItem != null) {
            loadBackdrop();
            //collapsingToolbar.setTitle("Store: " + dummyItem.title);
            //amount.setText("Amount: " + dummyItem.author);
            collapsingToolbar.setTitle("Store: " + listItem.store);
            amount.setText("Amount: " + listItem.amount);
        }

        return rootView;
    }

    private void loadBackdrop() {
        //Glide.with(this).load(dummyItem.photoId).centerCrop().into(backdropImg);
        Glide.with(this).load(listItem.photoId).centerCrop().into(backdropImg);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // your logic
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ArticleDetailFragment newInstance(String itemID) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ArticleDetailFragment.ARG_ITEM_ID, itemID);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleDetailFragment() {}
}
