package com.workouttracker.gamestudi.workouttracker.CalendarScreen;


public class CalendarItem {
    private String title;
    private String workout_id;
    private String date;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWorkoutId(String workout_id) {
        this.workout_id = workout_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getWorkoutId() {
        return workout_id;
    }

    public String getDate() {
        return date;
    }

}
