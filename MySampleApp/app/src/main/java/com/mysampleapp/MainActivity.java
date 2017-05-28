//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.17
//
package com.mysampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.multidex.MultiDexApplication;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.mysampleapp.navigation.NavigationDrawer;

import com.amazonaws.models.nosql.LocationsDO;
import android.os.Build;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /** Username */
    private final String m_username = getDeviceName();

    /** User's GPS lon/lat/el. */
    // TODO: Replace default values with current GPS data
    private final double m_lat=0, m_lon=0, m_el=0;

    /** Database Mapper */
    final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    EditText editTextConnect;
    EditText editTextUsername;
    TextView textViewUserList;

    /** Class name for log messages. */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    /** The identity manager used to keep track of the current user account. */
    private IdentityManager identityManager;

    /** The toolbar view control. */
    private Toolbar toolbar;

    /** Our navigation drawer class for handling navigation drawer logic. */
    private NavigationDrawer navigationDrawer;

    /** The helper class used to toggle the left navigation drawer open and closed. */
    private ActionBarDrawerToggle drawerToggle;

    /** Data to be passed between fragments. */
    private Bundle fragmentBundle;

    int MAX_BATCH_SIZE_FOR_DELETE = 10;
    /**
     * Initializes the Toolbar for use with the activity.
     */
    private void setupToolbar(final Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            // Some IDEs such as Android Studio complain about possible NPE without this check.
            assert getSupportActionBar() != null;

            // Restore the Toolbar's title.
            getSupportActionBar().setTitle(
                savedInstanceState.getCharSequence(BUNDLE_KEY_TOOLBAR_TITLE));
        }
    }


    /**
     * Initializes the navigation drawer menu to allow toggling via the toolbar or swipe from the
     * side of the screen.
     */
    private void setupNavigationMenu(final Bundle savedInstanceState) {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView drawerItems = (ListView) findViewById(R.id.nav_drawer_items);

        // Create the navigation drawer.
        navigationDrawer = new NavigationDrawer(this, toolbar, drawerLayout, drawerItems,
            R.id.main_fragment_container);

        // Add navigation drawer menu items.
        // Home isn't a demo, but is fake as a demo.
//        DemoConfiguration.DemoFeature home = new DemoConfiguration.DemoFeature();
//        home.iconResId = R.mipmap.icon_home;
//        home.titleResId = R.string.main_nav_menu_item_home;
//        navigationDrawer.addDemoFeatureToMenu(home);

//        for (DemoConfiguration.DemoFeature demoFeature : DemoConfiguration.getDemoFeatureList()) {
//            navigationDrawer.addDemoFeatureToMenu(demoFeature);
//        }

        if (savedInstanceState == null) {
            // Add the home fragment to be displayed initially.
            navigationDrawer.showHome();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_main);

        setupToolbar(savedInstanceState);

        setupNavigationMenu(savedInstanceState);

        // TODO: Change replace the time arguement with current time
        insertData(m_username, "currentTime: TODO", m_lat, m_lon, m_el);

        // Update activity_main
        editTextConnect = (EditText)findViewById(R.id.editText_Connect);
        editTextUsername = (EditText)findViewById(R.id.editText_Username);
        textViewUserList = (TextView)findViewById(R.id.textView_UserList);
        editTextConnect.setText(m_username, TextView.BufferType.EDITABLE);
        editTextUsername.setText(m_username, TextView.BufferType.EDITABLE);


        // Refresh List of Users to connnect to
        displayAllUsers();

        // TODO:  Remove user's location from list when application closes, not sure where that goes.
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here excluding the home button.

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        // Save the title so it will be restored properly to match the view loaded when rotation
        // was changed or in case the activity was destroyed.
        if (toolbar != null) {
            bundle.putCharSequence(BUNDLE_KEY_TOOLBAR_TITLE, toolbar.getTitle());
        }
    }

    @Override
    public void onClick(final View view) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        
        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
            return;
        }

        if (fragmentManager.getBackStackEntryCount() == 0) {
//            if (fragmentManager.findFragmentByTag(HomeDemoFragment.class.getSimpleName()) == null) {
//                final Class fragmentClass = HomeDemoFragment.class;
//                // if we aren't on the home fragment, navigate home.
//                final Fragment fragment = Fragment.instantiate(this, fragmentClass.getName());
//
//                fragmentManager
//                    .beginTransaction()
//                    .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();
//
//                // Set the title for the fragment.
//                final ActionBar actionBar = this.getSupportActionBar();
//                if (actionBar != null) {
//                    actionBar.setTitle(getString(R.string.app_name));
//                }
//                return;
//            }
        }
        super.onBackPressed();
    }


    /**
     * Stores data to be passed between fragments.
     * @param fragmentBundle fragment data
     */
    public void setFragmentBundle(final Bundle fragmentBundle) {
        this.fragmentBundle = fragmentBundle;
    }

    /**
     * Gets data to be passed between fragments.
     * @return fragmentBundle fragment data
     */
    public Bundle getFragmentBundle() {
        return this.fragmentBundle;
    }

    /**
     * inserts user gps data to database with timestamp and desired username
     * replacing any existing data for that user.  The unique userId is
     * generated by the AWS API so ensure each entry in the database is unique
     * @return none
     */
    private void insertData(final String username, final String time, final double lat, final double lon, final double el) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Fetch the default configured DynamoDB ObjectMapper
                    final LocationsDO item = new LocationsDO(); // Initialize the location Object

                    // The userId has to be set to user's Cognito Identity Id for private / protected tables.
                    // User's Cognito Identity Id can be fetched by using:
                    // AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()
                    item.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
                    item.setUsername(username);
                    item.setElevation(el);
                    item.setLatitude(lat);
                    item.setLongitude(lon);
                    item.setTime(time); // GMT: Fri, 19 Aug 2016 21:53:47 GMT
                    AmazonClientException lastException = null;

                    try {
                        mapper.save(item);
                    } catch (final AmazonClientException ex) {
                        Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
                        lastException = ex;
                    }

                    if (lastException != null) {
                        // Re-throw the last exception encountered to alert the user.
                        throw lastException;
                    }
                } catch (final AmazonClientException ex) {
                    return;
                }
            }
        }).start();
    }

    /**
     * remove current user's location data from database
     */
    private void removeSample() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final LocationsDO itemToFind = new LocationsDO();
                    itemToFind.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

                    final DynamoDBQueryExpression<LocationsDO> queryExpression = new DynamoDBQueryExpression<LocationsDO>()
                            .withHashKeyValues(itemToFind)
                            .withConsistentRead(false)
                            .withLimit(MAX_BATCH_SIZE_FOR_DELETE);

                    final PaginatedQueryList<LocationsDO> results = mapper.query(LocationsDO.class, queryExpression);

                    Iterator<LocationsDO> resultsIterator = results.iterator();

                    AmazonClientException lastException = null;

                    if (resultsIterator.hasNext()) {
                        final LocationsDO item = resultsIterator.next();

                        // Demonstrate deleting a single item.
                        try {
                            mapper.delete(item);
                        } catch (final AmazonClientException ex) {
                            Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                            lastException = ex;
                        }
                    }

                    final List<LocationsDO> batchOfItems = new LinkedList<LocationsDO>();
                    while (resultsIterator.hasNext()) {
                        // Build a batch of books to delete.
                        for (int index = 0; index < MAX_BATCH_SIZE_FOR_DELETE && resultsIterator.hasNext(); index++) {
                            batchOfItems.add(resultsIterator.next());
                        }
                        try {
                            // Delete a batch of items.
                            mapper.batchDelete(batchOfItems);
                        } catch (final AmazonClientException ex) {
                            Log.e(LOG_TAG, "Failed deleting item batch : " + ex.getMessage(), ex);
                            lastException = ex;
                        }

                        // clear the list for re-use.
                        batchOfItems.clear();
                    }


                    if (lastException != null) {
                        // Re-throw the last exception encountered to alert the user.
                        // The logs contain all the exceptions that occurred during attempted delete.
                        throw lastException;
                    }
                } catch (final AmazonClientException ex) {
                    return;
                }
            }
        }).start();
    }

    /**
     * retrieve location object of given username
     */
    // TODO: Need to figure out how to run this on seperate thread to prevent app from crashing from exception
    public LocationsDO getGPS(String username)
    {
        final LocationsDO itemToFind = new LocationsDO();
        final LocationsDO item = new LocationsDO();
        itemToFind.setUsername(username);
        item.setUsername("");
//        try {
//
//            final DynamoDBQueryExpression<LocationsDO> queryExpression = new DynamoDBQueryExpression<LocationsDO>()
//                    .withHashKeyValues(itemToFind)
//                    .withConsistentRead(false)
//                    .withLimit(MAX_BATCH_SIZE_FOR_DELETE);
//
//            final PaginatedQueryList<LocationsDO> results = mapper.query(LocationsDO.class, queryExpression);
//
//            Iterator<LocationsDO> resultsIterator = results.iterator();
//
//            AmazonClientException lastException = null;
//
//            if (resultsIterator.hasNext()) {
//                final LocationsDO item = resultsIterator.next();
//                return item;
//            }
//            else {
//                final LocationsDO item = new LocationsDO();
//                item.setUserId("");
//                return item;
//            }
//        } catch (final AmazonClientException ex) {
//            final LocationsDO item = new LocationsDO();
//            item.setUserId("");
//            return item;
//        }
        return item;
    }

    /**
     * returns string containing device name from phone model/manufacturer
     * @return String device name
     */
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * Capatlizes all alpha characters
     * @return String converted to all CAPS
     */
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    // TODO: List all recent items in database and display to user
    public void displayAllUsers()
    {
        // attach method to button
        String results = "Username\t\t\t\t\t\t\t\t\tUserId\n";
        PaginatedScanList<LocationsDO> users = scan();
        Iterator<LocationsDO> usersIterator;
        if (users != null){
            usersIterator = users.iterator();
            while(usersIterator.next() != null) {
                LocationsDO temp = (LocationsDO)usersIterator;
                results += temp.getUsername() + "\t" + temp.getUserId() + "\n";
            }
        }
        textViewUserList.setText(results, TextView.BufferType.EDITABLE);
    }

    // TODO: obtain userId from username (query database)
    public void getUserId(String username)
    {

    }

    // TODO: onClick action for refresh, refreshes list of users and distance
    public void refresh()
    {
        displayAllUsers();
        // TODO: Calculate distance to the user "connected" to

    }


    /**
     * Returns all location objects in database if they exist, null otherwise
     * @return PaginatedScanList of LocationsDO objects
     */
    // TODO: Need to figure out how to run scan on seperate thread to prevent exception from being thrown
    public PaginatedScanList<LocationsDO> scan()
    {
        PaginatedScanList<LocationsDO> results = null;
//        try {
//            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//            results = mapper.scan(LocationsDO.class, scanExpression);
//        } catch (final AmazonClientException ex) {
//            return results;
//        }
        return results;
    }

}
