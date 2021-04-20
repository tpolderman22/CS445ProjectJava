package com.example.cs435projectjava;

/**
 * implemented for the purposes of recieving data after the PostExecute method is called in BackgroundWorker.
 * This is necessary to authenticate database call results
 */
public interface AsyncResponse {
    void processFinish(String output);
}
