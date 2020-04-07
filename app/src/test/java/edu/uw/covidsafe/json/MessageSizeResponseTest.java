package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MessageSizeResponseTest {

    @Test
    public void parse() {
        String seed = UUID.randomUUID().toString();
        long ts_start = System.currentTimeMillis();
        long ts_end = System.currentTimeMillis() - 1000;
        double lat = 100;
        double longi = 50;
        int precision = 4;
        try {
            JSONObject obj = SelfReportRequest.toJson(new String[]{seed},
                    new long[]{ts_start}, new long[]{ts_end}, lat, longi, precision);
            System.out.println(obj.toString(2));
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}