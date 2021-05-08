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

/**
 * This class is represents the view the user sees when they successfully log into their account.
 * They should see a list of the organizations they are registered to, and should be able to register
 * to a new organization in the database. If they select an organization they will be shown a list
 * of locations where events are available to sign up. When they select a location, a list of events
 * and times will be generated. The user can sign up for these events.
 */
public class LoggedInActivity extends AppCompatActivity implements AsyncResponse{

    String userid;
    Context context; //store this activity for calls in onclick methods
    AsyncResponse ar; //store this for the same reason
    boolean hidePress = false;

    String[] locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        context = this;
        ar = this;
        Bundle extras = getIntent().getExtras();
        userid = extras.getString("userid");  //the user that logged in needs to be saved for showing the orgs
        //they belong to

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
     * adds a new button to the screen with the given name displayed
     * when pushed, this button will display the locations at the organization it represents
     */
    public void addOrganizationButton(String name, String id) {

        Button newbtn = new Button(this);
        LinearLayout orgLayout = new LinearLayout(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout linLay2 = findViewById(R.id.linLay2);

        int orgId = Integer.valueOf(id);

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

        //add the new components as children
        orgLayout.addView(newbtn);
        linLay2.addView(scroll);
        scroll.addView(orgLayout);

        /**
         * add a click listener that will generate a new list of events that can be signed up for
         */
        newbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("hide", String.valueOf(hidePress));
                if (!hidePress){
                    hidePress=true;
                    //execute the database call and find all of the locations for this particular organization
                    BackgroundWorker worker = new BackgroundWorker(context, ar);
                    worker.execute("getLocations", Integer.toString(orgId));

                    //add a button for each signup location
                    if(locations!=null && locations.length>0){
                        for (int i=0;i<locations.length;i++){
                            String[] locationData = locations[i].split("@");
                            addLocationButton(orgLayout, locationData[0], locationData[1]);
                        }
                        locations=null;
                    }
                } else {
                    for (int i=1;i<orgLayout.getChildCount();i++){
                        View child = orgLayout.getChildAt(i);
                        orgLayout.removeView(child);
                    }
                    hidePress=false;
                }
                Log.d("hide", String.valueOf(hidePress));
            }
        });
    }

    /**
     * adds a linear layout that will show the places holding events within an organization.
     * The name of the location is displayed along and a button is placed by it for the user
     * to select it.
     */
    public void addLocationButton(LinearLayout orgLayout,String name, String description){

        LinearLayout locList = new LinearLayout(this);
        Button newButton = new Button(this);

        locList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        locList.setOrientation(LinearLayout.VERTICAL);

        //set details for the buttons displaying each locationk
        newButton.setHeight(200);
        newButton.setText(name); //display the name of the organization this button represents
        newButton.setPadding(10,25,10,25);
//        int myColor = Color.argb(255, 255,0,0);
//        newButton.setBackgroundColor(myColor);
        newButton.setBackgroundColor(Color.WHITE);

        orgLayout.addView(newButton);
        orgLayout.addView(locList);

        /**
         * add a click listener that will open up the events at the location the user has selected
         */
        newButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startNewActivity(name, description);
            }
        });
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
    public void startNewActivity(String locationName, String locationDescription){
        Intent intent = new Intent(this, LocationView.class);
        intent.putExtra("userid", userid);
        intent.putExtra("locationName", locationName);
        intent.putExtra("locationDescription", locationDescription);
        startActivity(intent);
    }


    @Override
    public void processFinish(String result, String additionalData) {
        if (result.contains("Membership Check Success")) {
            String[] organizations = additionalData.split(";");
            for (int i = 0; i < organizations.length; i++) {
                String[] info = organizations[i].split("@");
                addOrganizationButton(info[1], info[0]);
            }
        }else if (result.contains("Welcome New Member")){  //restart the activity so the new org will show up
            finish();
            startActivity(getIntent());
        }else if(result.contains("Locations Check Success")){      //display the list of locations at each organization
            //String[] locations = additionalData.split(";;;");
            locations = additionalData.split(";;;");
        }
    }
}