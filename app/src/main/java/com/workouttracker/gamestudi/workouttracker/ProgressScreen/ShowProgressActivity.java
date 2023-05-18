package com.workouttracker.gamestudi.workouttracker.ProgressScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.workouttracker.gamestudi.workouttracker.CalendarScreen.ShowCalendarActivity;
import com.workouttracker.gamestudi.workouttracker.ColorSchemeScreen.ColorSchemeActivity;
import com.workouttracker.gamestudi.workouttracker.Database.DBManager;
import com.workouttracker.gamestudi.workouttracker.R;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.ArchivedWorkoutList;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.Item;
import com.workouttracker.gamestudi.workouttracker.WorkoutListScreen.MainActivityWorkoutList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowProgressActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView txtTitle;
    private Chronometer simpleChronometer;

    private BarChart chart;
    private DBManager dbManager;

    // Item List
    private List<Item> listItem = new ArrayList();
    private List<String> exerciseNames = new ArrayList();

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
        stub.setLayoutResource(R.layout.activity_progress);
        stub.inflate();

        //Sets up the toolbar and navigation menu
        initToolbar();
        initNavigationMenu();

        //Loads the chart
        initChart();


    }

    private void initChart(){
        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);

        Spinner spinner = findViewById(R.id.progress_spinner);
        spinner.setOnItemSelectedListener(this);




        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.getAllExercises();
        dbManager.close();

        for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() ) {
            //Grabs column index 1 which equals exercise name
            exerciseNames.add(cursor.getString(1));
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, exerciseNames);



        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        xAxis.setValueFormatter(xAxisFormatter);


        //Left Axis settings
        chart.getAxisLeft().setGranularity(1f);
        //chart.getAxisLeft().setEnabled(false);

        //Right Axis settings
        chart.getAxisRight().setGranularity(1f);
        //chart.getAxisRight().setEnabled(false);



        chart.getAxisLeft().setDrawGridLines(false);




        // add a nice and smooth animation
        chart.animateY(500);

        chart.getLegend().setEnabled(false);

    }



    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        //Hides the chronometer as we don't need it for this activity
        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.setVisibility(View.GONE);

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Progress");
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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        chart.clear();
        dbManager.open();
        //Grabs the selected exercise name from the spinner and gets the exerciseID
        String exercisesId = dbManager.getExerciseId(parent.getItemAtPosition(pos).toString());

        Cursor cursor = dbManager.getExerciseLogProgress(exercisesId);
        ArrayList<BarEntry> values = new ArrayList<>();

        for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() ) {
            ProgressItem progressItem = new ProgressItem();
            //uses the cursor to populate the progressItem weight value
            progressItem.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
            //uses the cursor to populate the progressItem date value
            progressItem.setDate(cursor.getString(cursor.getColumnIndex("date")));

            //Converts the date to a day of the year number
            String dayOfTheYear = convertDate(progressItem.getDate());

            //adds the values to the bar chart
            values.add(new BarEntry(Integer.parseInt(dayOfTheYear), Integer.parseInt(progressItem.getWeight())));
        }
        dbManager.close();
        cursor.close();



        BarDataSet set1 = new BarDataSet(values, "Data Set");
        set1.setValues(values);
        //TODO Change colour scheme
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setData(data);


    }

    private String convertDate(String dateToConvert){

        //Splits the date to convert out into Year, Month and Date
        Log.d("Date to convert", dateToConvert);
        String strYear = dateToConvert.substring(0, 4);
        Log.d("year", strYear);
        String strMonth = dateToConvert.substring(5, 7);
        Log.d("month", strMonth);
        String strDay = dateToConvert.substring(8, 10);
        Log.d("day", strDay);

        int year = Integer.parseInt(strYear);
        int monthNumber = Integer.parseInt(strMonth);
        int dayNumber = Integer.parseInt(strDay);

        //Converts the date to a numbered day of the year
        LocalDate date = LocalDate.of(year, monthNumber, dayNumber);
        int dayOfYear = date.getDayOfYear();

        //Converts it back to a string
        dateToConvert = Integer.toString(dayOfYear);

        Log.d("Day of the year", dateToConvert);
        return dateToConvert;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
