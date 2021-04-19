package com.example.cs435projectjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The first view the user sees is a login screen that will compare their credentials against
 * the database and allow them on if they match. The user is also able to sign up on this page
 * if they are not yet in the database.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //The different UI components
        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.signup);
        final EditText username =  findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final TextView errorText = findViewById(R.id.errorText);

        /**
         * when the login button is pressed the text fields must be filled, with the email box containing
         * a valid email address. The database will then check if the email is there, and if the
         * password entered is a match. If so, the view will be set to the OrganizationsDisplay
         * where the user can see all of the organization they belong to. If not, an error
         * message will be displayed.
         */
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        /**
         * when the signup button is clicked the database will be checked for the user
         * and if they are already there it will display an error. If not, they will be added
         * to the databse with the username and password in the text fields.
         */
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the new username and password to the database
                registerUser();
            }
        });
    }

    /**
     * called when the user taps the login button if the login info matches the database entry
     */
    public void logIn() {

        //references to input texts
        EditText username =  findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        //execute the database call and see if the user is in the db
        BackgroundWorker worker = new BackgroundWorker(this);
        worker.execute("login", username.getText().toString(), password.getText().toString());

        //change the view the user sees
        Intent intent = new Intent(this, LoggedInActivity.class);
        TextView errTxt = findViewById(R.id.errorText);
        //String message = errTxt.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }

    /**
     * Adds a new user to the database. If the user is already registered it will fail.
     */
    public void registerUser(){
        //references to input texts
        EditText username =  findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        //execute the database call and see if the user is in the db
        BackgroundWorker worker = new BackgroundWorker(this);
        worker.execute("register", username.getText().toString(), password.getText().toString());

        //change the view the user sees
        Intent intent = new Intent(this, LoggedInActivity.class);
        TextView errTxt = findViewById(R.id.errorText);
        errTxt.setText("Registering User");
        errTxt.setVisibility(View.VISIBLE);
    }

}