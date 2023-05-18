package com.workouttracker.gamestudi.workouttracker.CalendarScreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.workouttracker.gamestudi.workouttracker.BuildConfig;
import com.workouttracker.gamestudi.workouttracker.ColorSchemeScreen.ColorSchemeActivity;
import com.workouttracker.gamestudi.workouttracker.Database.DBManager;
import com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseItem;
import com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseRecyclerViewAdaptor;
import com.workouttracker.gamestudi.workouttracker.ProgressScreen.ShowProgressActivity;
import com.workouttracker.gamestudi.workouttracker.R;
import com.workouttracker.gamestudi.workouttracker.ViewAnimation;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.ArchivedWorkoutList;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.MainActivityWorkoutList;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarShowSelectedWorkout extends AppCompatActivity {


    private DBManager dbManager;

    private RecyclerView recyclerView;

    // Item List
    private List<com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseItem> ExerciseItem = new ArrayList();

    // Custom Recycler View Adaptor
    private ExerciseRecyclerViewAdaptor adapter;

    private Double exerciseWeight;
    private NumberFormat nf = new DecimalFormat("##.##");

    private String id;
    private String title;
    private String date;

    private View parent_view;
    private View back_drop;
    private boolean rotate = false;
    private View lyt_add_exercise;
    private View lyt_start_workout;

    private FloatingActionButton fab_add;
    private FloatingActionButton fab_add_exercise;
    private FloatingActionButton fab_start_workout;
    private CardView cv_add_exercise;
    private CardView cv_start_workout;
    private Parcelable recyclerViewState;

    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView txtTitle;
    private Chronometer simpleChronometer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        stub.setLayoutResource(R.layout.activity_exercise_list);
        stub.inflate();


        //Gets the values of the intent sent in the previous activity
        //Passes the values through to the public variables defined earlier
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        date = intent.getStringExtra("date");


        //Sets up the toolbar and navigation menu
        initToolbar();
        initNavigationMenu();


        parent_view = findViewById(android.R.id.content);
        back_drop = findViewById(R.id.back_drop);
        lyt_add_exercise = findViewById(R.id.lyt_add_exercise);
        lyt_start_workout = findViewById(R.id.lyt_start_workout);

        //Loads the Exercise logs data using recyclerview and the custom adapter
        loadExerciseData();

        fab_add = findViewById(R.id.fab_add);
        fab_add.hide();

        fab_add_exercise = findViewById(R.id.fab_add_exercise);
        fab_start_workout = findViewById(R.id.fab_start_workout);
        cv_add_exercise = findViewById(R.id.cv_add_exercise);
        cv_start_workout = findViewById(R.id.cv_start_workout);

        back_drop.setVisibility(View.GONE);
        ViewAnimation.initShowOut(lyt_add_exercise);
        ViewAnimation.initShowOut(lyt_start_workout);
    }


    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(title + "    " + date);

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


    public void loadExerciseData(){
        //We pass the database manager the id AND title variable in case the user has entered in two workouts which
        //have the same name. We obviously only want to return the one they clicked on rather than everything
        //with that duplicate workout name
        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetchExerciseLogsForSelectedDate(id, date);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //If the cursor has a value in it then hide the empty textview
        //In English. If there is a workout returned, then remove the text saying no workouts found
        if (cursor.getCount() > 0) {
            TextView empty = (TextView) findViewById(R.id.empty);
            empty.setVisibility(View.GONE);
        }

        int i = 0;
        int intSet1Improvement;
        int intSet2Improvement;
        int intSet3Improvement;
        int intSet4Improvement;
        int intSet5Improvement;

        for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() ) {
            ExerciseItem exerciseItem = new ExerciseItem();
            //uses the cursor to populate the item WORKOUT_ID value
            exerciseItem.setId(cursor.getString(cursor.getColumnIndex("log_id")));
            //uses the cursor to populate the item Exercise Names
            exerciseItem.setTitle(cursor.getString(cursor.getColumnIndex("exercise")));

            exerciseItem.setButton1(cursor.getString(cursor.getColumnIndex("set1")));
            exerciseItem.setButton2(cursor.getString(cursor.getColumnIndex("set2")));
            exerciseItem.setButton3(cursor.getString(cursor.getColumnIndex("set3")));
            exerciseItem.setButton4(cursor.getString(cursor.getColumnIndex("set4")));
            exerciseItem.setButton5(cursor.getString(cursor.getColumnIndex("set5")));


            intSet1Improvement = cursor.getInt(cursor.getColumnIndex("set1_improvement"));
            intSet2Improvement = cursor.getInt(cursor.getColumnIndex("set2_improvement"));
            intSet3Improvement = cursor.getInt(cursor.getColumnIndex("set3_improvement"));
            intSet4Improvement = cursor.getInt(cursor.getColumnIndex("set4_improvement"));
            intSet5Improvement = cursor.getInt(cursor.getColumnIndex("set5_improvement"));


            //All of the switch statements to determine which colour button to display for the sets
            switch(intSet1Improvement){
                case 0:
                    //If the value is null the int returns 0 so this is always the default case
                    //If no improvement was recorded then make the button show the default colour
                    exerciseItem.setButton1Colour(R.drawable.button_shape_default);
                        break;
                    case 1:
                        //If negative improvement was made then make the button show the negative colour
                        exerciseItem.setButton1Colour(R.drawable.button_shape_red);
                        break;
                    case 2:
                        //If positive improvement was made then make the button show the positive colour
                        exerciseItem.setButton1Colour(R.drawable.button_shape_blue);
                        break;
            }


            switch(intSet2Improvement){
                case 0:
                    exerciseItem.setButton2Colour(R.drawable.button_shape_default);
                    break;
                case 1:
                    exerciseItem.setButton2Colour(R.drawable.button_shape_red);
                    break;
                case 2:
                    exerciseItem.setButton2Colour(R.drawable.button_shape_blue);
                    break;
            }


            switch(intSet3Improvement){
                case 0:
                    exerciseItem.setButton3Colour(R.drawable.button_shape_default);
                    break;
                case 1:
                    exerciseItem.setButton3Colour(R.drawable.button_shape_red);
                    break;
                case 2:
                    exerciseItem.setButton3Colour(R.drawable.button_shape_blue);
                    break;
            }


            switch(intSet4Improvement){
                case 0:
                    exerciseItem.setButton4Colour(R.drawable.button_shape_default);
                    break;
                case 1:
                    exerciseItem.setButton4Colour(R.drawable.button_shape_red);
                    break;
                case 2:
                    exerciseItem.setButton4Colour(R.drawable.button_shape_blue);
                    break;
            }


            switch(intSet5Improvement){
                case 0:
                    exerciseItem.setButton5Colour(R.drawable.button_shape_default);
                    break;
                case 1:
                    exerciseItem.setButton5Colour(R.drawable.button_shape_red);
                    break;
                case 2:
                    exerciseItem.setButton5Colour(R.drawable.button_shape_blue);
                    break;
            }


            exerciseWeight = cursor.getDouble(cursor.getColumnIndex("weight"));
            exerciseItem.setWeight(exerciseWeight);
            ExerciseItem.add(exerciseItem);
            i++;
        }
        adapter = new ExerciseRecyclerViewAdaptor(ExerciseItem, this, null, null);
        recyclerView.setAdapter(adapter);
    }


    public void bottomNavigationHomeClick(View view){
            Intent intent = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
            startActivity(intent);
    }

    public void bottomNavigationCalendarClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ShowCalendarActivity.class);
            startActivity(intent);
        }

    }
