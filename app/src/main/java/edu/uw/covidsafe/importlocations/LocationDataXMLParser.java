package com.example.importlocations;

import android.content.Context;
import android.os.Environment;
import android.os.FileUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LocationDataXMLParser {

    public List<List<String>> getLinks (File path) {

        final String TRANSPORTATION_SECTION = "Data";
        final String TRANSPORTATION_SECTION_ATTR_NAME = "name";
        final String TRANSPORTATION_VALUE_TAG = "value";
        final String COORDINATES_SECTION = "coordinates";
        final String TIME_BEGIN_SECTION = "begin";
        final String TIME_END_SECTION = "end";

        List<List<String>> full_data = new ArrayList<List<String>>();
        List<String> first_lat = new ArrayList<String>();
        List<String> first_long = new ArrayList<String>();
        List<String> last_lat = new ArrayList<String>();
        List<String> last_long = new ArrayList<String>();
        List<String> timestamp_start = new ArrayList<String>();
        List<String> timestamp_end = new ArrayList<String>();
        List<String> mode_of_transportation = new ArrayList<String>();

        try {
            // Get the parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // XML data
            File file = new File(path.getAbsolutePath(), "covidSafe_loc_history");
            final String inputXML = getDataString(file);
            // Set the input
            xpp.setInput(new StringReader(inputXML));
            int eventType = xpp.getEventType();

            // Parser loop until end of the document
            boolean correctSection = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Read the tag name
                String tagname = xpp.getName();

                // Check the event type
                if (eventType == XmlPullParser.START_TAG) {
                    // Check 'section' tags
                    if (tagname.equalsIgnoreCase(TRANSPORTATION_SECTION)) {
                        // Opening tag, check the attribute
                        String attrvalue = xpp.getAttributeValue(null, TRANSPORTATION_SECTION_ATTR_NAME);
                        if (attrvalue.equals("Category")) {
                            correctSection = true;
                        }
                    } else if (tagname.equalsIgnoreCase(COORDINATES_SECTION)) {
                        String all_coords = "";
                        all_coords = xpp.nextText();

                        //Make sure the coordinates tag is not empty
                        if (all_coords.length() > 0) {
                            List<String> lats = new ArrayList<String>();
                            List<String> longs = new ArrayList<String>();

                            for (String entry : all_coords.split(",0")) {
                                if(entry.replace(" ", "").length() > 0) {
                                    System.out.println("Coord: " + entry);
                                    lats.add(entry.substring(entry.indexOf(",") + 1, entry.length()));
                                    longs.add(entry.substring(0, entry.indexOf(",")));
                                }
                            }
                            first_lat.add(lats.get(0));
                            first_long.add(longs.get(0));
                            if (lats.size() > 1 && longs.size() > 1) {
                                last_lat.add(lats.get(lats.size() - 1));
                                last_long.add(longs.get(longs.size() - 1));
                            } else {
                                last_lat.add("");
                                last_long.add("");
                            }

                        } else {
                            first_lat.add("");
                            first_long.add("");
                            last_lat.add("");
                            last_long.add("");
                        }

                    } else if (tagname.equalsIgnoreCase(TIME_BEGIN_SECTION)) {
                        String time = xpp.nextText().replace("Z", "");
                        System.out.println("TimeStart:" + time);
                        timestamp_start.add(Long.toString(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(time).getTime() / 1000));
                    } else if (tagname.equalsIgnoreCase(TIME_END_SECTION)) {
                        String time = xpp.nextText().replace("Z", "");
                        System.out.println("TimeEnd:" + time);
                        timestamp_end.add(Long.toString(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(time).getTime() / 1000));
                    } else if (correctSection && tagname.equalsIgnoreCase(TRANSPORTATION_VALUE_TAG)) {
                        correctSection = false;
                        String text = xpp.nextText();
                        System.out.println("Transportation:" + text);
                        if (text == null) {
                            text = "";
                        }
                        mode_of_transportation.add(text);
                    }
                }
                // Move to next event
                eventType = xpp.next();
            }
            // Delete the file when we're done with it
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        full_data.add(first_lat);
        full_data.add(first_long);
        full_data.add(last_lat);
        full_data.add(last_long);
        full_data.add(timestamp_start);
        full_data.add(timestamp_end);
        full_data.add(mode_of_transportation);
        System.out.println("All Done!");


        return full_data;
    }

    private static String getDataString(File file) {
        //Read text file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(text.toString());
        return text.toString();
    }

}
