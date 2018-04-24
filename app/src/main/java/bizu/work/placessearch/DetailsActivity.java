package bizu.work.placessearch;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import db.Database;


public class DetailsActivity extends AppCompatActivity {

    private String place_id;
    private String response;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add a back button and a listener
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResultsActivity.class));
                finish();
            }
        });

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabIcons();

        Intent intent = getIntent();
        response = intent.getStringExtra("response");
        place_id = intent.getStringExtra("placeID");
        saveToDB(response);
        setPageTitle();

        Log.i("in oncCreate", "------onCreate DetailAcitivity--------------placeID-------------: " + place_id);

    }

    public String getDetailsPlaceID() {

        return place_id;
    }
    public String getDetailsData() {

        return "xyz";
    }

    private void saveToDB(String response) {

        Database db = new Database(this);
        db.saveDetailsToDB(response);

    }

//    public String getDetailsData() {
//
//        return response;
//    }

    private void setPageTitle() {

        // Set the global toolbar title for all the fragments

        Database db = new Database(this);
        String title = db.getPlaceName(place_id);

        TextView textView = this.findViewById(R.id.action_bar_title);
        textView.setText(title);

        getSupportActionBar().setTitle("");

    }


    private void setupTabIcons() {

        TextView infoTab = (TextView) LayoutInflater.from(this).inflate(R.layout.info_tab, null);
        tabLayout.getTabAt(0).setCustomView(infoTab);

        TextView photosTab = (TextView) LayoutInflater.from(this).inflate(R.layout.photos_tab, null);
        tabLayout.getTabAt(1).setCustomView(photosTab);

        TextView mapTab = (TextView) LayoutInflater.from(this).inflate(R.layout.map_tab, null);
        tabLayout.getTabAt(2).setCustomView(mapTab);

        TextView reviewsTab = (TextView) LayoutInflater.from(this).inflate(R.layout.reviews_tab, null);
        tabLayout.getTabAt(3).setCustomView(reviewsTab);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.separator));
            drawable.setSize(1, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        };

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new InfoFragment(), null);
        adapter.addFrag(new PhotosFragment(), null);
        adapter.addFrag(new MapFragment(), null);
        adapter.addFrag(new ReviewsFragment(), null);;
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}