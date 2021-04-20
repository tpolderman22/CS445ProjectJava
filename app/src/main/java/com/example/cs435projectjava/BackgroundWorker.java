package com.example.cs435projectjava;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * I used a youtube channel called ProgrammingKnowledge to help me put together database
 * elements. There are a couple tutorial videos I can link to if need be.
 */
public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;
    AsyncResponse ar = null;

    /**
     * A new background worker with a reference to the context of the view it was
     * instantiated in and an asyncResponse for sharing with the UI thread
     *
     * @param context
     */
    public BackgroundWorker(Context context, AsyncResponse ar) {
        this.context = context;
        this.ar = ar;
    }

    /**
     * A new background worker with a reference to the context of the view it was
     * instantiated in
     *
     * @param context
     */
    public BackgroundWorker(Context context) {
        this.context = context;
    }

    @Override
    /**
     * create a new alert dialog box
     */
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        //add a progress bar for the async task
    }

    @Override
    /**
     * set the message in the dialog box to the result of the database query
     */
    protected void onPostExecute(String result) {
        alertDialog.setMessage(result);
        alertDialog.show();
        Log.d("login", result);
        ar.processFinish(result);
    }

    @Override
    /**
     * takes string params for username (email), password, and the type of query being executed.
     * Uses different php scripts for logging in and registering a new user to the database.
     */
    protected String doInBackground(String... params) {

        String type = params[0]; //the type of db transaction

        //setting username and password to post
        String username = params[1];
        String password = params[2];
        String dbUrl = "";

        if (type.equals("login")) { //perform login attempt
            Log.d("login", "loggin in");
            dbUrl = "http://192.168.1.156/cs445project/cs445login.php";   //localhost
        } else if (type.equals("register")) { //push new user to db
            Log.d("login", "registration");
            dbUrl = "http://192.168.1.156/cs445project/cs445registerUser.php";   //localhost
        }
        return getUserFromDb(username, password, dbUrl);
    }


    public String getUserFromDb(String username, String password, String dbUrl) {
        try {
            Log.d("data", username + " " + password);

            //setting up the database connection
            URL url = new URL(dbUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);

            //output things
            OutputStream out = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            String postData = URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(username, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(password, "UTF-8");

            Log.d("post", postData);

            //write and flush with buffered writer + close output
            writer.write(postData);
            writer.flush();
            writer.close();
            out.close();

            //input stuff
            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }

            //close input stuff
            reader.close();
            in.close();
            con.disconnect();

            //return the result
            Log.d("result", result);
            return result;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed to get results from database";
    }
}
