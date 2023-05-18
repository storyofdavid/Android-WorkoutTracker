package com.workouttracker.gamestudi.workouttracker.MainWorkout;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.workouttracker.gamestudi.workouttracker.CalendarScreen.ShowCalendarActivity;
import com.workouttracker.gamestudi.workouttracker.ColorSchemeScreen.ColorSchemeActivity;
import com.workouttracker.gamestudi.workouttracker.Database.DBManager;
import com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseItem;
import com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseList;
import com.workouttracker.gamestudi.workouttracker.ProgressScreen.ShowProgressActivity;
import com.workouttracker.gamestudi.workouttracker.R;
import com.workouttracker.gamestudi.workouttracker.ViewAnimation;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.ArchivedWorkoutList;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.MainActivityWorkoutList;
import java.util.ArrayList;
import java.util.List;


public class StartWorkoutActivity extends AppCompatActivity implements WorkoutRecyclerViewAdaptor.OnItemLongSelectedListener, WorkoutRecyclerViewAdaptor.OnButtonClickListener {

    private DBManager dbManager;

    private RecyclerView recyclerView;

    // Item List
    private List<com.workouttracker.gamestudi.workouttracker.ExerciseListScreen.ExerciseItem> ExerciseItem = new ArrayList();

    // Custom Recycler View Adaptor
    private WorkoutRecyclerViewAdaptor adapter;

    private Double exerciseWeight;

    //Public variables which are used across classes/voids
    public String id;
    public String title;

    //Will set this to true when workout has been paused
    public boolean isPaused = false;

    //Will be used when the chronometer is paused.
    public long timeWhenStopped = 0;

    //Will be populated when we call the loadExerciseData() class
    public String log_id;

    private View parent_view;
    private View back_drop;
    private boolean rotate = false;
    private View lyt_pause_workout;
    private View lyt_finish_workout;

    private FloatingActionButton fab_add;
    private FloatingActionButton fab_pause_workout;
    private FloatingActionButton fab_finish_workout;
    private CardView cv_pause_workout;
    private CardView cv_finish_workout;
    private TextView txt_pause_workout;
    private Parcelable recyclerViewState;



    //Related to service
    WorkoutService mBoundService;
    boolean mServiceBound = false;
    Intent serviceIntent;

    private String strNumberOfExercises;

    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView txtTitle;
    private Chronometer simpleChronometer;

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
        stub.setLayoutResource(R.layout.activity_start_workout);
        stub.inflate();


        //Gets the values of the intent sent in the previous activity
        //Passes the values through to the public variables defined earlier
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        //Sets up the toolbar and navigation menu
        initToolbar();
        initNavigationMenu();



        parent_view = findViewById(android.R.id.content);
        back_drop = findViewById(R.id.back_drop);
        lyt_pause_workout = findViewById(R.id.lyt_pause_workout);
        lyt_finish_workout = findViewById(R.id.lyt_finish_workout);

        fab_add = findViewById(R.id.fab_add);
        fab_pause_workout = findViewById(R.id.fab_pause_workout);
        fab_finish_workout = findViewById(R.id.fab_finish_workout);
        cv_pause_workout = findViewById(R.id.cv_pause_workout);
        cv_finish_workout = findViewById(R.id.cv_finish_workout);
        txt_pause_workout = findViewById(R.id.txt_pause_workout);

        back_drop.setVisibility(View.GONE);
        ViewAnimation.initShowOut(lyt_pause_workout);
        ViewAnimation.initShowOut(lyt_finish_workout);


        //Chronometer is used for the counter timer
        simpleChronometer = findViewById(R.id.simpleChronometer);



        //Starts the WorkoutService which keeps track of the workout time
        serviceIntent = new Intent(this, WorkoutService.class);
        serviceIntent.putExtra("id", id);
        serviceIntent.putExtra("title", title);
        startService(serviceIntent);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


        //Opens the database connection
        dbManager = new DBManager(this);
        dbManager.open();


        //Loads the Exercise logs data using recyclerview and the custom adapter
        loadExerciseData();


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(v);
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(fab_add);
            }
        });

        fab_pause_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
            }
        });


        cv_pause_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
            }
        });

        fab_finish_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });

        cv_finish_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });


    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        txtTitle = findViewById(R.id.txtTitle);
        //Hide the textview as we will be using the chronometer instead to time the workout
        txtTitle.setVisibility(View.GONE);

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



    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WorkoutService.MyBinder myBinder = (WorkoutService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;

            //Once the Bound service is connected it starts the timer for the workout
            startChronometer();
        }
    };

    public void startChronometer(){
            long timer = mBoundService.getTime();
            simpleChronometer.setBase(timer);
            simpleChronometer.start();
        }




    private void loadExerciseData() {

        strNumberOfExercises = dbManager.countExercises(id);
        Log.d("countExercises Value", strNumberOfExercises);

        //We pass the database manager the id AND title variable in case the user has entered in two workouts which
        //have the same name. We obviously only want to return the one they clicked on rather than everything
        //with that duplicate workout name
        Cursor cursor = dbManager.fetchExerciseLogs(id, strNumberOfExercises);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //If the cursor has a value in it then hide the empty textview
        //In English. If there is a workout returned, then remove the text saying no workouts found
        if (cursor.getCount() > 0) {
            TextView empty = (TextView) findViewById(R.id.empty);
            empty.setVisibility(View.GONE);
        }

        int intSet1Improvement;
        int intSet2Improvement;
        int intSet3Improvement;
        int intSet4Improvement;
        int intSet5Improvement;

        //fetchExerciseLogs returns the data in reverse order. So we start at the end of the cursor and work our way
        //backwards. This way the data appear is the correct order.
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            ExerciseItem exerciseItem = new ExerciseItem();
            //uses the cursor to populate the item LOG_ID value
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
                    exerciseItem.setButton1Colour(R.drawable.button_shape_green);
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
                    exerciseItem.setButton2Colour(R.drawable.button_shape_green);
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
                    exerciseItem.setButton3Colour(R.drawable.button_shape_green);
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
                    exerciseItem.setButton4Colour(R.drawable.button_shape_green);
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
                    exerciseItem.setButton5Colour(R.drawable.button_shape_green);
                    break;
            }



            exerciseWeight = cursor.getDouble(cursor.getColumnIndex("weight"));
            exerciseItem.setWeight(exerciseWeight);
            ExerciseItem.add(exerciseItem);

            log_id = exerciseItem.getId();
        }


        adapter = new WorkoutRecyclerViewAdaptor(ExerciseItem, this, this, this);
        recyclerView.setAdapter(adapter);


        //Loads the intents
        //If there is an intent value for recyclerViewState then we'll load it
        //And point the user to the same poisition in the recyclerview that they were in previously
        Intent intent = getIntent();
        if (intent.hasExtra("recyclerViewState")) {
            final Parcelable recyclerViewState;
            recyclerViewState = intent.getParcelableExtra("recyclerViewState");
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }


    @Override
    protected void onDestroy() {

        dbManager.close();
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        //When the user clicks on the back button we want to take them back to the workout list page
        this.finish();
    }


    @Override
    public void onItemLongSelected(String itemId, String itemTitle, Double itemWeight) {
        modifyExercise(itemId, itemTitle, itemWeight);

    }

    private void modifyExercise(String itemId, String itemTitle, Double itemWeight){
        showCustomModifyDialog(itemId, itemTitle, itemWeight);
    }

    private void showCustomModifyDialog(final String itemId, String itemTitle, Double itemWeight) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_modify_light);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText exerciseEditText = dialog.findViewById(R.id.name_edittext);
        final EditText weightEditText = dialog.findViewById(R.id.weight_edittext);
        TextView txtTitle  = dialog.findViewById(R.id.txt_title);
        Button btnUpdate = dialog.findViewById(R.id.btn_update);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        Button btnArchive = dialog.findViewById(R.id.btn_archive);
        Button btnPlaceholder = dialog.findViewById(R.id.btn_placeholder);



        txtTitle.setText("Modify Exercise");
        exerciseEditText.setText(itemTitle);
        weightEditText.setText(itemWeight.toString());

        //Hides the archive and placeholder buttons
        btnArchive.setVisibility(View.GONE);
        btnPlaceholder.setVisibility(View.GONE);

        //Sets the cursor position to the end of text, rather than at the start
        exerciseEditText.setSelection( exerciseEditText.getText().length());
        weightEditText.setSelection( weightEditText.getText().length());




        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Does a validation check to make sure the user has entered in a value for the exercise name
                if (TextUtils.isEmpty(exerciseEditText.getText())) {
                    Toast.makeText(StartWorkoutActivity.this,
                            "You must give an exercise name", Toast.LENGTH_LONG).show();

                    //If the user has given an exercise name then we will update the exercise name in the database
                } else {
                    String newWorkoutName = exerciseEditText.getText().toString();
                    Long _id = Long.parseLong(itemId);

                    //Updates with the new value
                    String newExerciseName = exerciseEditText.getText().toString();
                    dbManager.updateExerciseName(_id, newExerciseName);


                    //If there is a weight given then update the database
                    if (weightEditText.getText().toString().trim().length() > 0)  {
                        Double newExerciseWeight = Double.parseDouble(weightEditText.getText().toString());
                        dbManager.updateExerciseWeight(_id, newExerciseWeight);
                    } else {
                        //If no weight value was given then update with a default value of 0
                        Double newExerciseWeight = 0.0;
                        dbManager.updateExerciseWeight(_id, newExerciseWeight);
                    }


                    //Remembers the poisiton of the reclcyer view when modify exercise or delete exercise is called
                    final Parcelable recyclerViewState;
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                    //Shows the update made by clearing the recyclerview and re-adding all the items
                    //Works better this way as we don't have to re-create the entire activity
                    ExerciseItem.clear();
                    loadExerciseData();
                    adapter.notifyDataSetChanged();

                    //places the user back at the same position in the recycler view rather than scrolling all the way back up to the top
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);


                    //Closes the dialog
                    dialog.dismiss();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long _id = Long.parseLong(itemId);

                //Deletes the selected exercise
                dbManager.deleteExercise(_id);

                //Remembers the poisiton of the reclcyer view when modify exercise or delete exercise is called
                final Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                //Shows the update made by clearing the recyclerview and re-adding all the items
                //Works better this way as we don't have to re-create the entire activity
                ExerciseItem.clear();
                loadExerciseData();
                adapter.notifyDataSetChanged();

                //places the user back at the same position in the recycler view rather than scrolling all the way back up to the top
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                //Closes the dialog
                dialog.dismiss();

            }
        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    @Override
    protected void onPause() {
        //When the user nagivates away from this screen we will minimise the floating action menu
        //floatingActionsMenu.collapse();

        // Save state - used when user clicks on an item far down the recycler view list
        // It remembers the state or position
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        //Refreshes the data when the activity is resumed.
        //Mainly used for when an exercise is updated.

       ExerciseItem.clear();
       loadExerciseData();
       adapter.notifyDataSetChanged();

        // Restore state
        // Once the data is re-loaded we load the same state or position
        // This stops the recycler view of scrolling all the way back to the top when a button is clicked

        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

    }

    @Override
    public void onButtonClick(String itemId, String itemTitle, String setSelected, Integer intReps, Integer intImprovement) {
        //itemId is currently being stored as a string, covert it to an integer value
        Integer intItemId = Integer.parseInt(itemId);

        //Validation to make sure reps can never be less than zero
        if (intReps < 0) {
            intReps = 0;
        }

        //We pass through the itemId, set selected, number of reps & integer value of if there was an improvement made
        dbManager.updateExerciseLogsWithImprovement(intItemId, setSelected, intReps, intImprovement);

        // Save state - used when user clicks on an item far down the recycler view list
        // It remembers the state or position
        final Parcelable recyclerViewState;
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();


        //Triggers the refresh of data in the recyclerview
        //Clears what's currently in the view, loads the new data, refreshes the recyclerview
       // ExerciseItem.clear();
       // loadExerciseData();
       // adapter.notifyDataSetChanged();



        // Restore state
        // Once the data is re-loaded we load the same state or position
        // This stops the recycler view of scrolling all the way back to the top when a button is clicked
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);


    }

    public void bottomNavigationHomeClick(View view){
            Intent intent = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
            startActivity(intent);
    }

    public void bottomNavigationCalendarClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ShowCalendarActivity.class);
            startActivity(intent);
    }

    private void toggleFabMode(View v) {
        rotate = ViewAnimation.rotateFab(v, !rotate);
        if (rotate) {
            ViewAnimation.showIn(lyt_pause_workout);
            ViewAnimation.showIn(lyt_finish_workout);
            back_drop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lyt_pause_workout);
            ViewAnimation.showOut(lyt_finish_workout);
            back_drop.setVisibility(View.GONE);
        }
    }

    public void pauseWorkout() {
        //If workout isn't already paused then do the following
        if (isPaused == false) {

            //minimises the floating action button
            toggleFabMode(fab_add);
            //Shows a snackbar message to the user letting them know the workout has been paused
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.viewSnack), "", Snackbar.LENGTH_SHORT);
            //inflate view
            View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
            snackBarView.setPadding(0, 0, 0, 0);

            ((TextView) custom_view.findViewById(R.id.message)).setText("Workout Paused!");
            ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
            (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.green_500));
            snackBarView.addView(custom_view, 0);
            snackbar.show();

            //Calculates the time when stopped
            timeWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
            //Stops the timer
            simpleChronometer.stop();
            txt_pause_workout.setText("Resume Workout");
            fab_pause_workout.setImageResource(R.drawable.fab_resume_workout);
            isPaused = true;
        }

        //If workout is already paused then do the following
        else if (isPaused == true) {

            //minimises the floating action button
            toggleFabMode(fab_add);
            //Shows a snackbar message to the user letting them know the workout has resumed
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.viewSnack), "", Snackbar.LENGTH_SHORT);
            //inflate view
            View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
            snackBarView.setPadding(0, 0, 0, 0);

            ((TextView) custom_view.findViewById(R.id.message)).setText("Workout Resumed!");
            ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
            (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.green_500));
            snackBarView.addView(custom_view, 0);
            snackbar.show();


            //Sets the correct timer time when you resume the chronometer
            simpleChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            simpleChronometer.start();
            txt_pause_workout.setText("Pause Workout");
            fab_pause_workout.setImageResource(R.drawable.fab_pause_workout);
            isPaused = false;
        }
    }


    public void finishWorkout() {

        //minimises the floating action button
        toggleFabMode(fab_add);

        //General clean up tasks
        simpleChronometer.stop();

        //unbinds the service
        unbindService(mServiceConnection);
        stopService(serviceIntent);



        //Fetches all of the exercise logs in this workout so we can record the workout duration
        Cursor cursor = dbManager.fetchExerciseLogs(id, strNumberOfExercises);
        //Works out how many seconds have elasped. It records it in milliseconds so we divide by 1000 to convert it to seconds
        Long workoutDuration = (SystemClock.elapsedRealtime() - simpleChronometer.getBase()) / 1000;

        //Records the duration in the database
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            dbManager.recordExerciseLogDuration(cursor.getString(cursor.getColumnIndex("log_id")), workoutDuration);
            i++;
        }


        //Shows the EndOfWorkoutDialog
        showWorkoutSummaryDialog();
    }

    private void showWorkoutSummaryDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_workout_summary);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;



        ((View) dialog.findViewById(R.id.bt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
                startActivity(i);
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivityWorkoutList.class);
                startActivity(i);
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
