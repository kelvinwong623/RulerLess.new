package com.mysampleapp.navigation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.user.IdentityProfile;
import com.mysampleapp.R;

import static com.mysampleapp.R.string.app_name;

public class NavigationDrawer {
    private AppCompatActivity containingActivity;

    /** The helper class used to toggle the left navigation drawer open and closed. */
    private ActionBarDrawerToggle drawerToggle;

    /* The navigation drawer layout view control. */
    private DrawerLayout drawerLayout;

    /** The view group that will contain the navigation drawer menu items. */
    private ListView drawerItems;

    /** The id of the fragment container. */
    private int fragmentContainerId;

    /**
     * Constructs the Navigation Drawer.
     * @param activity the activity that will contain this navigation drawer.
     * @param toolbar the toolbar the activity is using.
     * @param layout the DrawerLayout for this navigation drawer.
     * @param drawerItemsContainer the parent view group for the navigation drawer items.
     */
    public NavigationDrawer(final AppCompatActivity activity,
                            final Toolbar toolbar,
                            final DrawerLayout layout,
                            final ListView drawerItemsContainer,
                            final int fragmentContainerId) {
        // Keep a reference to the activity containing this navigation drawer.
        this.containingActivity = activity;
        this.drawerItems = drawerItemsContainer;


        drawerItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final FragmentManager fragmentManager = activity.getSupportFragmentManager();

                // Clear back stack when navigating from the Nav Drawer.
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                if (position == 0) {
                    // home
                    showHome();
                    return;
                }

                closeDrawer();
            }
        });
        this.drawerLayout = layout;
        this.fragmentContainerId = fragmentContainerId;

        // Create the navigation drawer toggle helper.
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar,
            app_name, app_name) {
        };

        // Set the listener to allow a swipe from the screen edge to bring up the navigation drawer.
        drawerLayout.setDrawerListener(drawerToggle);

        // Display the home button on the toolbar that will open the navigation drawer.
        final ActionBar supportActionBar = containingActivity.getSupportActionBar();
        //supportActionBar.setDisplayHomeAsUpEnabled(true);
        //supportActionBar.setHomeButtonEnabled(true);

        // Switch to display the hamburger icon for the home button.
        drawerToggle.syncState();
    }

    public void showHome() {

        // Set the title for the fragment.
        final ActionBar actionBar = containingActivity.getSupportActionBar();
        //actionBar.setTitle(R.string.app_name);
        closeDrawer();
    }

    /**
     * Closes the navigation drawer.
     */
    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    /**
     * @return true if the navigation drawer is open, otherwise false.
     */
    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }
}
