package com.mycompany.artistworld.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.adapter.ViewPagerAdapter;
import com.mycompany.artistworld.fragments.HomeFragment;
import com.mycompany.artistworld.fragments.PopularFragment;
import com.mycompany.artistworld.fragments.RecentlyAddedFragment;
import com.mycompany.artistworld.other.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;
    private View navHeader;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;


    HomeFragment mHomeFragment;
    RecentlyAddedFragment mRecentlyAddedFragment;
    PopularFragment mPopularFragment;

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager.setOffscreenPageLimit(3); // the number of "off screen" pages to keep loaded each side of the
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();


    }


    private void setupViewPager(ViewPager viewPager) {
        mHomeFragment = HomeFragment.newInstance("Home title");
        mRecentlyAddedFragment = RecentlyAddedFragment.newInstance("Recently title");
        mPopularFragment = PopularFragment.newInstance("Popular title");

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mHomeFragment, "Home");//2nd param == tab title
        adapter.addFragment(mRecentlyAddedFragment, "Recently added");
        adapter.addFragment(mPopularFragment, "Popular");//2nd param == tab title
        viewPager.setAdapter(adapter);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        //https://developer.android.com/training/search/setup.html#create-sc
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        ComponentName componentName = new ComponentName(this, SearchActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        //text has changed , apply filtering
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //perform the final search
                        MenuItem search_menu_item = menu.findItem(R.id.search);
                        search_menu_item.collapseActionView();
                        return false;
                    }
                }
        );

        return super.onCreateOptionsMenu(menu);
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // loading header background image
        Picasso.with(this)
                .load(urlNavHeaderBg)
                .into(imgNavHeaderBg);

        Picasso.with(this)
                .load(R.mipmap.a_ic)
                .transform(new CircleTransform())
                .into(imgProfile);

    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_login:
                        navItemIndex = 0;
//                        CURRENT_TAG = TAG_HOME;
                        Intent LoginIntent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(LoginIntent);
                        break;
                    case R.id.nav_logout:
                        navItemIndex = 2;
//                      borrar el preference y enganchar el listener en esta actividad para algun posible cambio en el ui, como mostrar uno u otra login / logout
                        break;
                    case R.id.nav_create:
                        navItemIndex = 1;
//                        CURRENT_TAG = TAG_HOME;
                        Intent signUpIntent = new Intent(getBaseContext(), SignUpActivity.class);
                        startActivity(signUpIntent);
                        break;
                    case R.id.nav_favorite:
                        navItemIndex = 3;
//                      CURRENT_TAG = TAG_SETTINGS;
                        Intent intent = new Intent(getBaseContext(), FavoriteActivity.class);
                        startActivity(intent);
                        break;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

//                loadHomeFragment();

                //Closing drawer on item click
                drawer.closeDrawers();
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }
}
