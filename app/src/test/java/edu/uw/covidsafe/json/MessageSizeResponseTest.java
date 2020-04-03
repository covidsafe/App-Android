package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageSizeResponseTest {

    @Test
    public void parse() {
        JsonObject obj = new JsonObject();
        obj.addProperty("hi",535);

        System.out.println(obj.get("hi").getAsInt());
    }
}