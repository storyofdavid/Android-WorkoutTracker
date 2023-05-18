package com.workouttracker.gamestudi.workouttracker.ExerciseListScreen;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.workouttracker.gamestudi.workouttracker.R;
import java.util.List;

public class ExerciseRecyclerViewAdaptor extends RecyclerView.Adapter<ExerciseRecyclerViewAdaptor.ViewHolder> {

    private List<ExerciseItem> list;
    private Context context;

    private OnItemLongSelectedListener itemLongSelectedListener;
    private OnButtonClickListener buttonClickListener;

    private static String LOG_TAG = "ExerciseRecyclerViewAdaptor";

    public ExerciseRecyclerViewAdaptor(List<ExerciseItem> list,
                                       Context context,
                                       OnItemLongSelectedListener listener, OnButtonClickListener buttonlistener) {
        this.list = list;
        this.context = context;
        this.itemLongSelectedListener = listener;
        this.buttonClickListener = buttonlistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item_style, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExerciseRecyclerViewAdaptor.ViewHolder holder, final int position) {
        ExerciseItem myList = list.get(position);

        holder.textViewExercise.setText(myList.getTitle());
        holder.button1.setText(myList.getButton1());
        holder.button2.setText(myList.getButton2());
        holder.button3.setText(myList.getButton3());
        holder.button4.setText(myList.getButton4());
        holder.button5.setText(myList.getButton5());
        holder.textViewWeight.setText(myList.getWeight().toString() + "kg");

        //Sets the background colour of the buttons
        holder.button1.setBackgroundResource(myList.getButton1colour());
        holder.button2.setBackgroundResource(myList.getButton2colour());
        holder.button3.setBackgroundResource(myList.getButton3colour());
        holder.button4.setBackgroundResource(myList.getButton4colour());
        holder.button5.setBackgroundResource(myList.getButton5colour());


        final String currentId = myList.getId();
        final String currentTitle = myList.getTitle();
        final Double currentWeight = myList.getWeight();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


             }
        });



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView text = (TextView) v.findViewById(R.id.textViewHead);
                Context context = v.getContext();
                Intent intent = new Intent();
                if (itemLongSelectedListener != null) {
                    itemLongSelectedListener.onItemLongSelected(currentId, currentTitle, currentWeight);
                }
                return true;
            }
        });

        //Handle normal button clicks
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button1 clicked");
                    String setSelected = "set1";
                    Integer intReps = Integer.parseInt(holder.button1.getText().toString());
                    intReps += 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button1.setBackgroundResource(R.drawable.button_shape_blue);
                    holder.button1.setText(intReps.toString());
                }
            }
        });

        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button2 clicked");
                    String setSelected = "set2";
                    Integer intReps = Integer.parseInt(holder.button2.getText().toString());
                    intReps += 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button2.setBackgroundResource(R.drawable.button_shape_blue);
                    holder.button2.setText(intReps.toString());
                }
            }
        });

        holder.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button3 clicked");
                    String setSelected = "set3";
                    Integer intReps = Integer.parseInt(holder.button3.getText().toString());
                    intReps += 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button3.setBackgroundResource(R.drawable.button_shape_blue);
                    holder.button3.setText(intReps.toString());
                }
            }
        });

        holder.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button4 clicked");
                    String setSelected = "set4";
                    Integer intReps = Integer.parseInt(holder.button4.getText().toString());
                    intReps += 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button4.setBackgroundResource(R.drawable.button_shape_blue);
                    holder.button4.setText(intReps.toString());
                }
            }
        });

        holder.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button5 clicked");
                    String setSelected = "set5";
                    Integer intReps = Integer.parseInt(holder.button5.getText().toString());
                    intReps += 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button5.setBackgroundResource(R.drawable.button_shape_blue);
                    holder.button5.setText(intReps.toString());
                }
            }
        });


        //Handle long button clicks
        //Have a look to see if there is a way to handle user action when the button is held down for some time
        holder.button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button1 long clicked");
                    String setSelected = "set1";
                    Integer intReps = Integer.parseInt(holder.button1.getText().toString());
                    intReps -= 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button1.setText(intReps.toString());
                }
                return true;
            }
        });

        holder.button2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button2 long clicked");
                    String setSelected = "set2";
                    Integer intReps = Integer.parseInt(holder.button2.getText().toString());
                    intReps -= 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button2.setText(intReps.toString());
                }
                return true;
            }
        });

        holder.button3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button3 long clicked");
                    String setSelected = "set3";
                    Integer intReps = Integer.parseInt(holder.button3.getText().toString());
                    intReps -= 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button3.setText(intReps.toString());
                }
                return true;
            }
        });

        holder.button4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button4 long clicked");
                    String setSelected = "set4";
                    Integer intReps = Integer.parseInt(holder.button4.getText().toString());
                    intReps -= 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button4.setText(intReps.toString());
                }
                return true;
            }
        });

        holder.button5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (buttonClickListener != null) {
                    Log.d(LOG_TAG, " Button5 long clicked");
                    String setSelected = "set5";
                    Integer intReps = Integer.parseInt(holder.button5.getText().toString());
                    intReps -= 1;
                    buttonClickListener.onButtonClick(currentId, currentTitle, setSelected, intReps);
                    holder.button5.setText(intReps.toString());
                }
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {

        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewExercise;
        //not sure what this id is used for, can probably be removed with some testing
        public TextView textViewId;

        public Button button1;
        public Button button2;
        public Button button3;
        public Button button4;
        public Button button5;

        public Boolean button1FirstClick = true;
        public Boolean button2FirstClick = true;
        public Boolean button3FirstClick = true;
        public Boolean button4FirstClick = true;
        public Boolean button5FirstClick = true;


        public TextView textViewWeight;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewExercise = (TextView) itemView.findViewById(R.id.exercise);
            button1 = (Button) itemView.findViewById(R.id.button1);
            button2 = (Button) itemView.findViewById(R.id.button2);
            button3 = (Button) itemView.findViewById(R.id.button3);
            button4 = (Button) itemView.findViewById(R.id.button4);
            button5 = (Button) itemView.findViewById(R.id.button5);
            textViewWeight = (TextView) itemView.findViewById(R.id.weight);
        }
    }

    public interface OnItemLongSelectedListener {
        void onItemLongSelected(String itemId, String itemTitle, Double itemWeight);
    }

    public interface OnButtonClickListener {
        void onButtonClick(String itemId, String itemTitle, String setSelected, Integer intReps);
    }
}
