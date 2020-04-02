package edu.uw.covidsafe.comms;

import android.location.Location;

import org.junit.Test;

import static org.junit.Assert.*;

public class PullFromServerTaskTest {

    @Test
    public void getMessages() {
        float[] result = new float[1];
        Location.distanceBetween(47.6916997,-122.2592993,47.6532857,-122.3059575,result);
        System.out.println(result[1]);
    }
}