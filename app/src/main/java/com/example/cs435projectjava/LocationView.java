package com.example.cs435projectjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LocationView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("rrrr", "opened new activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events_on_location);

    }
}