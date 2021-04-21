package com.example.cs435projectjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This class is represents the view the user sees when they successfully log into their account.
 * They should see a list of the organizations they are registered to, and should be able to register
 * to a new organization in the database. If they select an organization they will be shown a list
 * of locations where events are available to sign up. When they select a location, a list of events
 * and times will be generated. The user can sign up for these events.
 */
public class LoggedInActivity extends AppCompatActivity implements AsyncResponse{

    String userid;
    boolean hidePress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        BackgroundWorker db = new BackgroundWorker(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        userid = extras.getString("userid");

        //execute the database call and find all of the oranizations this user belongs to
        BackgroundWorker worker = new BackgroundWorker(this, this);
        worker.execute("returnOrgs", userid);

        //show the user id of the logged in user for testing purposes
        TextView userDisplay = findViewById(R.id.user);
        userDisplay.setText("showing results for user " + userid);

        Button joinOrg = findViewById(R.id.joinOrgBtn);

        /**
         * when this button is pressed, check if the organization entered in the text field exists,
         * make the user a member of it
         */
        joinOrg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                joinOrg();
            }
        });

    }

    /**
     * adds a new button for each membership to the second nested linear layout
     */
    public void addOrganizationButton(String name) {

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
        newbtn.setText(name); //display the name of the organization this button represents
        newbtn.setPadding(10,25,10,25);
        int myColor = Color.argb(255, 3,161,252);
        newbtn.setBackgroundColor(myColor);

        /**
         * add a click listener that will generate a new list of events that can be signed up for
         */
        newbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!hidePress){
                    for (int i=0; i<5;i++){   //fix this so it wont perpetually add button, but instead shows buttons for each organizations locations
                        addLocationList(orgLayout);
                    }
                }else {
                    //hide the buttons
                }
            }
        });

        //add the new components as children
        orgLayout.addView(newbtn);
        linLay2.addView(scroll);
        scroll.addView(orgLayout);
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
        //ui components for joining new organization
        Button joinOrg = findViewById(R.id.joinOrgBtn);
        EditText orgToJoin = findViewById(R.id.toJoin);

        BackgroundWorker bw = new BackgroundWorker(this,this);
        bw.execute("newMembership", orgToJoin.getText().toString(), userid);
    }

    /**
     * create an Intent and start the new activity
     */
    public void startNewActivity(){
        startActivity(new Intent(this, LocationView.class));
    }


    @Override
    public void processFinish(String result, String additionalData) {
        if (result.contains("Membership Check Success")) {
            String[] organizations = additionalData.split(";");
            for (int i = 0; i < organizations.length; i++) {
                addOrganizationButton(organizations[i]);
            }
        }else if (result.contains("Welcome New Member")){
            finish();
            startActivity(getIntent());
        }
        else {
            //do nothing for now
        }
    }
}