package com.example.cs435projectjava;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * This class is represents the view the user sees when they successfully log into their account.
 * They should see a list of the organizations they are registered to, and should be able to register
 * to a new organization in the database. If they select an organization they will be shown a list
 * of locations where events are available to sign up. When they select a location, a list of events
 * and times will be generated. The user can sign up for these events.
 */
public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        BackgroundWorker db = new BackgroundWorker(getApplicationContext());

        //ui components
        Button joinOrg = findViewById(R.id.joinOrgBtn);
        EditText orgToJoin = findViewById(R.id.toJoin);

        // TODO: make this add a button for every org in database
        for(int i=0; i<getNumOrgs(); i++){
            addOrganizationButton();
        }

    }

    /**
     * adds a new button to the second nested linear layout
     */
    public void addOrganizationButton() {

        Button newbtn = new Button(this);
        LinearLayout orgLayout = new LinearLayout(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout linLay2 = findViewById(R.id.linLay2);

        //set up the new LinearView (orgLayout)
        orgLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        orgLayout.setOrientation(LinearLayout.VERTICAL);

        //set details for the buttons displaying each organization
        newbtn.setHeight(200);
        newbtn.setPadding(10,25,10,25);
        int myColor = Color.argb(255, 3,161,252);
        newbtn.setBackgroundColor(myColor);
        newbtn.setText("Default Organization");  //change this so that it shows the name of org

        /**
         * add a click listener that will generate a new list of events that can be signed up for
         */
        newbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //update this so that the first click displays the buttons, but the next click collapses them
                for (int i=0; i<getNumLocsAtOrg();i++){
                    addLocationList(orgLayout);
                }
            }
        });

        //add the new components as children
        orgLayout.addView(newbtn);
        linLay2.addView(scroll);
        scroll.addView(orgLayout);
    }

    /**
     * returns the number of organizations the user is registered to after checking the database
     * @return
     */
    public int getNumOrgs(){
        return 5;
    }

    /**
     * checks the database for the event list of a particular organization and
     * returns the number of them
     * @return
     */
    private int getNumLocsAtOrg(){
        return 5;
    }

    /**
     * adds a linear layout that will show the places holding events within an organization.
     * The name of the location is displayed along and a button is placed by it for the user
     * to select it.
     */
    public void addLocationList(LinearLayout orgLayout){

        LinearLayout locList = new LinearLayout(this);
        Button newButton = new Button(this);
        newButton.setText("New Location");

        locList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        locList.setOrientation(LinearLayout.VERTICAL);

        orgLayout.addView(newButton);
        orgLayout.addView(locList);

        /**
         * add a click listener that will open up the events at the location the user has selected
         */
        newButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startNewActivity();
            }
        });
    }

    /**
     * adds another nested linear layout that will have a text field describing the event,
     * and a button for signing up for it. It will show the time and description and any limit
     * to participation.
     */
    public void addEventList(){
        LinearLayout eventList = new LinearLayout(this);
        Button newButton = new Button(this);
        LinearLayout linlay2 =findViewById(R.id.linLay2);
        eventList.addView(newButton);
        linlay2.addView(eventList);
    }

    /**
     * join another organization and add it to the list of organizations the user can
     * interact with
     */
    public void joinOrg(){

        //TODO: call this method when the button is pressed, add the org to db
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("fakeOrgDatabase.txt", Context.MODE_APPEND));
            outputStreamWriter.write("dfcghjbk");
            outputStreamWriter.close();
        }
        catch (FileNotFoundException e) {
            Log.d("Exception", "File write failed: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create an Intent and start the new activity
     */
    public void startNewActivity(){
        startActivity(new Intent(this, LocationView.class));
    }


}