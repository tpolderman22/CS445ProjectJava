package com.example.cs435projectjava;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

/**
 * show the time slots available at the location
 */
public class LocationView extends AppCompatActivity implements AsyncResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_on_location);

        //the calendar view and the submit button for picking dates
        CalendarView appointmentCal = findViewById(R.id.appointmentCalendar);
        Button submit = findViewById(R.id.submitDate);

        //store this activity as an AsyncResponse and Context
        Context context = this;
        AsyncResponse ar = this;
        String dateChosen = String.valueOf(appointmentCal.getDate()); //keep track of the date the user selects

        //store the location name and description that are passed in from the previous activity. Also get the logged in user id
        Bundle extras = getIntent().getExtras();
        String userid = extras.getString("userid");
        String locationName = extras.getString("locationName");  //the name of this location
        String locationDescription = extras.getString("locationDescription");  //the name of this location

        //set the text to the name and description so the user cna see what they have chosen
        TextView title = findViewById(R.id.locationName);
        title.setText(locationName);
        TextView description = findViewById(R.id.locationDescription);
        description.setText(locationDescription);

        /**
         * when the date is changed, save that value so that the user can submit it and add it to their selected times
         */
        appointmentCal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateChosen = year + "/" + month + "/" + dayOfMonth;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.setText(dateChosen);
                //execute the database call and add the selected date to the appointments table.
                BackgroundWorker worker = new BackgroundWorker(context, ar);
                worker.execute("selectTime", locationName, userid, dateChosen);
                finish();
                startActivity(getIntent());
            }
        });
    }


    @Override
    public void processFinish(String result, String additionalData) {

        if(result.contains("Sign-up Complete")){
            //TODO allow the user to see the sign up they successfully made
        }
    }
}
