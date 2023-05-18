package com.workouttracker.gamestudi.workouttracker.MainWorkout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import com.workouttracker.gamestudi.workouttracker.R;

import androidx.core.app.NotificationCompat;


public class WorkoutService extends Service {

    private static String LOG_TAG = "ForegroundSerivce";
    private static String LOG_TAG_BOUND = "BoundService";
    //TODO Use this instead of hardcoded values
    private static String NOTIFICATION_CHANNEL_ID = "1";

    //Chronometer is used for the counter timer
    private Chronometer chronometer;

    // interface for clients that bind
    private IBinder mBinder = new MyBinder();

    //Used with pausing the chornometer
    private boolean mIsPaused;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "in onCreate");

        mIsPaused = false;

        chronometer = new Chronometer(this);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        Log.d(LOG_TAG, "Chronometer Started");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "in onStartCommand");

        //Gets the variables from the intent that called the service
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");

        //Creates the notification channel
        createNotificationChannel();

        //Creates the notification intent
        Intent notificationIntent = new Intent(this, StartWorkoutActivity.class);
        notificationIntent.putExtra("id", id);
        notificationIntent.putExtra("title", title);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        //Creates and shows the notification
        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Workout In Progress")
                .setContentText("Click here update your workout log")
                .setSmallIcon(R.drawable.shield_heart_icon)
                .setContentIntent(pendingIntent)
                .setTicker("Workout in Progress")
                .build();

        //Starts the service as a foreground service
        startForeground(1, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "in onDestroy");
        chronometer.stop();
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG_BOUND, "in onBind");
        // A client is binding to the service with bindService()
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG_BOUND, "in onUnbdind");
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder{
        WorkoutService getService(){
            return WorkoutService.this;
        }
    }

    //Is used to return the value of the chronometer
    public long getTime() {
        return chronometer.getBase();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel deascription";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
