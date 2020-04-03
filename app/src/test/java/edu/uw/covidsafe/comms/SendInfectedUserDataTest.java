package edu.uw.covidsafe.comms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.util.UUID;

import edu.uw.covidsafe.json.MatchMessage;

public class SendInfectedUserDataTest {

    @Test
    public void sendRequest() {
        long ts = System.currentTimeMillis();
        String seed = UUID.randomUUID().toString();
        ///////////////////////////////////////////////////////////

//        JsonObject matchMessage = MatchMessage.toJson(seed,ts);
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(matchMessage.toString());
//        String prettyJsonString = gson.toJson(je);
//        System.out.println(prettyJsonString);

        int[] aa=new int[]{1,2,3};
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();
        array.add(1);
        array.add(2);
        array.add(3);
        obj.add("adsf",array);
    }
}