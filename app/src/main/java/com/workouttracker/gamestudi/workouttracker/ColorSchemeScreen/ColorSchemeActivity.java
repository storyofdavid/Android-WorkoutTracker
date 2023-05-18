package com.workouttracker.gamestudi.workouttracker.ColorSchemeScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.workouttracker.gamestudi.workouttracker.CalendarScreen.ShowCalendarActivity;
import com.workouttracker.gamestudi.workouttracker.ProgressScreen.ShowProgressActivity;
import com.workouttracker.gamestudi.workouttracker.R;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.ArchivedWorkoutList;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.MainActivityWorkoutList;


public class ColorSchemeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView txtTitle;
    private Chronometer simpleChronometer;
    private Switch switchTheme;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to the Shared Preferences object
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);

        // Get the value of the "dark_mode" key, or "false" if it doesn't exist
        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);

        // If dark mode is enabled then do the following
        if (darkModeEnabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkAppTheme_NoActionBar);
            // Otherwise do this
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AppTheme_NoActionBar);
        }

        setContentView(R.layout.activity_menu_drawer_simple_light);

        //Use view stubs to programatically change the include view at runtime
        ViewStub stub = findViewById(R.id.main_view_stub);
        stub.setLayoutResource(R.layout.activity_color_scheme_screen);
        stub.inflate();

        //Sets up the toolbar, navigation menu and switch
        initToolbar();
        initNavigationMenu();
        initSwitch(darkModeEnabled);

    }

    private void initSwitch(Boolean darkModeEnabled){
        //Used for the light/dark theme switch
        switchTheme = (Switch) findViewById(R.id.switchTheme);

        if (darkModeEnabled){
            switchTheme.setOnCheckedChangeListener(null);
            switchTheme.setChecked(true);
            switchTheme.setText("Dark");
            switchTheme.setOnCheckedChangeListener(this);
        } else{
            switchTheme.setOnCheckedChangeListener(null);
            switchTheme.setChecked(false);
            switchTheme.setText("Light");
            switchTheme.setOnCheckedChangeListener(this);
        }

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Color Scheme");

        //Hides the chronometer as we don't need it for this activity
        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.setVisibility(View.GONE);

    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // open drawer at start
        //drawer.openDrawer(GravityCompat.START);


        //Handles side navigation menu clicks
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                String itemCLicked = item.getTitle().toString();
                Intent intent;

                switch (itemCLicked) {

                    case "Workouts":
                        Log.d("menu item clicked", "Workouts");
                        //Starts the MainActivityWorkout activity
                        intent = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
                        startActivity(intent);
                        break;
                    case "Archived":
                        Log.d("menu item clicked", "Archived");
                        intent = new Intent(getApplicationContext(), ArchivedWorkoutList.class);
                        startActivity(intent);
                        break;
                    case "Progress":
                        Log.d("menu item clicked", "Progress");
                        intent = new Intent(getApplicationContext(), ShowProgressActivity.class);
                        startActivity(intent);
                        break;
                    case "Calendar":
                        Log.d("menu item clicked", "Calendar");
                        //Starts the Calendar activity
                        intent = new Intent(getApplicationContext(), ShowCalendarActivity.class);
                        startActivity(intent);
                        break;
                    case "Color Scheme":
                        Log.d("menu item clicked", "Color Scheme");
                        intent = new Intent(getApplicationContext(), ColorSchemeActivity.class);
                        startActivity(intent);
                        break;
                    case "Settings":
                        Log.d("menu item clicked", "Settings");
                        //Do something
                        //TODO Create Settings Page
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
                        break;
                    case "About":
                        Log.d("menu item clicked", "About");
                        //Do something
                        //TODO Create About Page
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
                        break;
                }


                drawer.closeDrawers();
                return true;

            }


        });
    }

    public void bottomNavigationHomeClick(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
        startActivity(intent);
    }

    public void bottomNavigationCalendarClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ShowCalendarActivity.class);
        startActivity(intent);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Get a reference to the Shared Preferences object
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);

        // Edit the shared preferences object
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isChecked) {
            //do stuff when Switch is ON
            switchTheme.setText("Dark");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            // Put the boolean value "true" in the "dark_mode" key
            editor.putBoolean("dark_mode", true);

            // Commit the changes
            editor.apply();


        } else {
            //do stuff when Switch if OFF
            switchTheme.setText("Light");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            // Put the boolean value "true" in the "dark_mode" key
            editor.putBoolean("dark_mode", false);

            // Commit the changes
            editor.apply();
        }
    }
}
