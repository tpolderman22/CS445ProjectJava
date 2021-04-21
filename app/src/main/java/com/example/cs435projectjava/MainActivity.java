package com.example.cs435projectjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The first view the user sees is a login screen that will compare their credentials against
 * the database and allow them on if they match. The user is also able to sign up on this page
 * if they are not yet in the database.
 */
public class MainActivity extends AppCompatActivity implements AsyncResponse{

    boolean loginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The buttons and the view itself
        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.signup);
        final View thisView =  findViewById(R.id.mainView);

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
    public void logIn(View view) {
        //the text fields
        final EditText username =  findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        //execute the database call and see if the user is in the db
        makeUserDatabaseCall("login", username.getText().toString(),password.getText().toString());

    }

    public void makeUserDatabaseCall(String type, String username, String password){
        BackgroundWorker worker = new BackgroundWorker(this, this);
        worker.execute(type, username, password);
    }

    /**
     * Adds a new user to the database. If the user is already registered it will fail.
     */
    public void registerUser(){
        //references to input texts
        EditText username =  findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        //execute the database call and see if the user is in the db, if not, add them
        BackgroundWorker worker = new BackgroundWorker(this, this);
        worker.execute("register", username.getText().toString(), password.getText().toString());

        TextView errTxt = findViewById(R.id.errorText);
        errTxt.setText("Registering User");
        errTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void processFinish(String result, String additionalData) {
        final TextView errorText = findViewById(R.id.errorText);
        if (result.contains("Login Success")){
            errorText.setVisibility(View.INVISIBLE);
            //change the view
            Intent intent = new Intent(this, LoggedInActivity.class);
            intent.putExtra("userid", additionalData);
            startActivity(intent);
        }else if(result.equals("Login Failed")){
            errorText.setText("Invalid Credentials");
            errorText.setVisibility(View.VISIBLE);
        }else if (result.equals("Registration Success")){
            errorText.setText("Log In Under New Credentials");
        }else if (result.equals("Registration Failed")){
            errorText.setText("Failed To Register New User");
        }
    }
}