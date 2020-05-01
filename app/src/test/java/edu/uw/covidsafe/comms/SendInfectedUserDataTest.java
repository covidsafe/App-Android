package edu.uw.covidsafe.comms;

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

//        Gson gson = new Gson();
//        String json = "{'latitude' : '100','longitude':'200'}";
//        Location loc = (Location)gson.fromJson(json, Location.class);
//        System.out.println(loc.lattitude+","+loc.longitude);

//        int[] aa=new int[]{1,2,3};
//        JsonObject obj = new JsonObject();
//        JsonArray array = new JsonArray();
//        array.add(1);
//        array.add(2);
//        array.add(3);
//        obj.add("adsf",array);
    }
}