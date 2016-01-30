package com.example.ankitwal.grabmovieinfo2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;  // Page Adapter
    private ItemFragment upcomingPage;//Fragment for upComing Movies
    private ItemFragment favPage;// Fragment for Favourite Movies
    public List<ListItem> favs;// List For Favourites

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) {

            favs=new ArrayList<ListItem>();
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String favString = gson.toJson(favs);
            System.out.println("the String is   "+favString);
            if(!mPrefs.contains("Favs")) {
                // First Initialization of Favs into persistant memory
                prefsEditor.putString("Favs", favString);
                prefsEditor.commit();
            }
            String json = mPrefs.getString("Favs", "[]");
            favs = (List<ListItem>) gson.fromJson(json, new TypeToken<List<ListItem>>() {}.getType());

            // Initialise the view
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(String id)
    {
        System.out.println(id);
        if(id.equals("Load More"))
         {
           upcomingPage.loadMore(); // Load more items into the Upcoming page
          }
        else {
            Intent i = new Intent(this, DetailView.class);
            i.putExtra("id", id);
            startActivityForResult(i,1); // Display activity for detail view of movie
        }


    }
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Collect data from the intent and use it
        System.out.println("we should update"); // When return to Main Activity update Favourite movies
        updateFavs();
    }


    public void updateFavs() // Fuction to update favourite movies, as OnResume is not called on either back or up navigation.
    {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("Favs", "[]");
        favs = (List<ListItem>) gson.fromJson(json, new TypeToken<List<ListItem>>() {}.getType());
       if(favPage!=null){ favPage.updateContents(favs);}


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                upcomingPage=ItemFragment.newInstance(0);
                return upcomingPage;
            }
            else {

                favPage=ItemFragment.newInstance(1);
                return favPage;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Upcoming";
                case 1:
                    return "Favourites";

            }
            return null;
        }
    }
}
