package com.example.cs435projectjava;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * I used a youtube channel called ProgrammingKnowledge to help me put together database
 * elements. There are a couple tutorial videos I can link to if need be.
 */
public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;
    ProgressBar pb;
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
        //add progress bar
    }

    @Override
    /**
     * set the message in the dialog box to the result of the database query
     */
    protected void onPostExecute(String result) {
        if(result!=null) {
            if (result.contains("Login Success")) { //for a successful login the userid will be passed back
                String[] splitOutput = result.split(" ");
                result = splitOutput[1] + " " + splitOutput[2]; //this will be the users email
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, splitOutput[0]);
            } else if (result.contains("Membership Check Success")) {
                String[] splitOutput = result.split(",");
                result = splitOutput[0];
                ar.processFinish(result, splitOutput[1]);
            } else if (result.contains("Login Failed")) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, null);
            } else if (result.contains("Registration Success")) {
                alertDialog.setTitle("New Membership Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, null);
            } else if(result.contains("User Already Exists")){
                alertDialog.setTitle("New Membership Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, null);
            } else if (result.contains("Registration Failed")){
                alertDialog.setTitle("New Membership Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, null);
            } else if (result.contains("Locations Check Success")){
                String[] splitOutput = result.split("%");
                ar.processFinish(splitOutput[0],splitOutput[1]);
            }else{
                alertDialog.setTitle("Status");
                alertDialog.setMessage(result);
                alertDialog.show();
                ar.processFinish(result, null);
            }
        }
    }

    @Override
    /**
     * takes any number of String params and depending on the first parameter(type), different database
     * calls will be made. The type determines which url will be used and what data will be returned
     */
    protected String doInBackground(String... params) {

        String type = params[0]; //the type of db transaction
        String dbUrl = "";
        //String ip = "192.168.1.156";
        String ip = "192.168.0.156";


        if (type.equals("login")) { //perform login attempt
            String username = params[1];
            String password = params[2];
            dbUrl = "http://" + ip + "/cs445project/cs445login.php";   //local database
            Log.d("ip", dbUrl);
            try {
                return getUserFromDb(username, password, dbUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("register")) { //push new user to db
            String username = params[1];
            String password = params[2];
            dbUrl = "http://" + ip + "/cs445project/cs445registerUser.php";   //local database
            try {
                return getUserFromDb(username, password, dbUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("returnOrgs")) { //find the orgs a user belongs to
            String userid = params[1];
            dbUrl = "http://" + ip + "/cs445project/cs445returnMemberships.php";   //local database
            try {
                return pullFromMembershipDb(userid, dbUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (type.equals("newMembership")){ //add a new membership table entry
            String userid = params[2];
            String orgName = params[1];
            dbUrl = "http://" + ip + "/cs445project/cs445addMembership.php"; //local database
            try {
                return addMembership(orgName,userid,dbUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (type.equals("getLocations")){
            String orgid = params[1];
            dbUrl = "http://" + ip + "/cs445project/cs445queryLocations.php"; //local database
            try{
                return queryLocations(orgid, dbUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else if (type.equals("selectTime")){
            String locName = params[1];
            String user = params[2];
            String date = params[3];
            try {
                return makeAppointment(locName, user,date,dbUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * check all of the memberships of a given userid
     * @param userid
     * @param dbUrl
     * @return
     * @throws IOException
     */
    public String pullFromMembershipDb(String userid, String dbUrl) throws IOException {
        HttpURLConnection con = establishDbConnection(dbUrl);
        //output username + password for checking
        OutputStream out = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        String postData = URLEncoder.encode("userid", "UTF-8") + "="
                + URLEncoder.encode(userid, "UTF-8");

        //write and flush with buffered writer + close output
        writer.write(postData);
        writer.flush();
        writer.close();
        out.close();

        //receive and return input back from db
        return readFromDb(con);
    }

    /**
     * send the given username and password as a post request to the given url
     * and receive the db response
     * @param username
     * @param password
     * @param dbUrl
     * @return
     * @throws IOException
     */
    public String getUserFromDb(String username, String password, String dbUrl) throws IOException {
            HttpURLConnection con = establishDbConnection(dbUrl);

            //output username + password for checking
            OutputStream out = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            String postData = URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(username, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(password, "UTF-8");

            //write and flush with buffered writer + close output
            writer.write(postData);
            writer.flush();
            writer.close();
            out.close();

            Log.d("postdata", username + " " + password);
            //receive and return input back from db
            return readFromDb(con);
    }

    /**
     * add a user to the memberships database with the given organization
     * @param orgName
     * @param userid
     * @param dbUrl
     * @return
     * @throws IOException
     */
    public String addMembership(String orgName, String userid, String dbUrl) throws IOException {
        HttpURLConnection con = establishDbConnection(dbUrl);

        //output username + password for checking
        OutputStream out = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        String postData = URLEncoder.encode("orgName", "UTF-8") + "="
                + URLEncoder.encode(orgName, "UTF-8") + "&" + URLEncoder.encode("userid", "UTF-8") + "="
                + URLEncoder.encode(userid, "UTF-8");

        //write and flush with buffered writer + close output
        writer.write(postData);
        writer.flush();
        writer.close();
        out.close();

        //receive and return input back from db
        return readFromDb(con);
    }

    /**
     * return all of the locations/event spots at an organization
     * @param orgid
     * @param dbUrl
     * @return
     * @throws IOException
     */
    public String queryLocations(String orgid, String dbUrl) throws IOException{
        HttpURLConnection con = establishDbConnection(dbUrl);

        //output username + password for checking
        OutputStream out = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        String postData = URLEncoder.encode("orgid", "UTF-8") + "="
                + URLEncoder.encode(orgid, "UTF-8");

        //write and flush with buffered writer + close output
        writer.write(postData);
        writer.flush();
        writer.close();
        out.close();

        //receive and return input back from db
        return readFromDb(con);
    }

    /**
     * submit the given time, user, and location to the appointment table in the database
     * @param locationName
     * @param dbUrl
     * @return
     * @throws IOException
     */
    public String makeAppointment(String locationName, String user, String date, String dbUrl) throws IOException {
        HttpURLConnection con = establishDbConnection(dbUrl);

        //output username + password for checking
        OutputStream out = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        String postData = URLEncoder.encode("locationName", "UTF-8") + "="
                + URLEncoder.encode(locationName, "UTF-8") + "&" + URLEncoder.encode("user", "UTF-8") + "="
                + URLEncoder.encode(user, "UTF-8") + URLEncoder.encode("date", "UTF-8") + "="
                + URLEncoder.encode(date, "UTF-8");

        //write and flush with buffered writer + close output
        writer.write(postData);
        writer.flush();
        writer.close();
        out.close();

        //receive and return input back from db
        return readFromDb(con);
    }

    public HttpURLConnection establishDbConnection(String dbUrl) throws IOException {
        //setting up the database connection
        URL url = new URL(dbUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        return con;
    }

    /**
     * receive input from the url connection passed in and return the result as a string
     * @param con
     * @return
     * @throws IOException
     */
    public String readFromDb(HttpURLConnection con) throws IOException {
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
    }

}
