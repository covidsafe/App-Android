package edu.uw.covidsafe.gps;

import android.content.Context;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.FileOperations;
import com.example.covidsafe.R;

public class LocationDataXMLParser {

    public void getLinks (Context cxt, File path) {

        final String TRANSPORTATION_SECTION = "Data";
        final String TRANSPORTATION_SECTION_ATTR_NAME = "name";
        final String TRANSPORTATION_VALUE_TAG = "value";
        final String COORDINATES_SECTION = "coordinates";
        final String TIME_BEGIN_SECTION = "begin";
        final String TIME_END_SECTION = "end";

        List<String> lats = new LinkedList<>();
        List<String> lons = new LinkedList<>();
        List<Long> tsstart = new LinkedList<>();
        List<Long> tsend = new LinkedList<>();
        List<String> mode_of_transportation = new ArrayList<String>();

        try {
            // Get the parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // XML data
            File file = new File(path.getAbsolutePath(), "covidSafe_loc_history.kml");
            Log.e("log","file exists? "+file.exists());
            String inputXML = getDataString(file);
            if (Constants.DEBUG) {
                inputXML = FileOperations.readrawasset(cxt, R.raw.loc);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Log.e("location",inputXML);
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
                        String all_coords = xpp.nextText();

                        //Make sure the coordinates tag is not empty
                        if (!all_coords.isEmpty()) {
                            String[] entries = all_coords.split(" ");

                            String[] elts = entries[0].split(",");
                            lats.add(elts[1]);
                            lons.add(elts[0]);

                            if (entries.length > 1) {
                                elts = entries[entries.length-1].split(",");
                                lats.add(elts[1]);
                                lons.add(elts[0]);
                            }
                            else {
                                lats.add("");
                                lons.add("");
                            }
                        }
                    } else if (tagname.equalsIgnoreCase(TIME_BEGIN_SECTION)) {
                        String time = xpp.nextText().replace("Z", "");
                        tsstart.add(format.parse(time).getTime());
                    } else if (tagname.equalsIgnoreCase(TIME_END_SECTION)) {
                        String time = xpp.nextText().replace("Z", "");
                        tsend.add(format.parse(time).getTime());
                    } else if (correctSection && tagname.equalsIgnoreCase(TRANSPORTATION_VALUE_TAG)) {
                        correctSection = false;
                        String text = xpp.nextText();
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
//            if (Constants.DEBUG) {
                file.delete();
//            }
        } catch (Exception e) {
            Log.e("log",e.getMessage());
        }


        String[] encryptedLats = CryptoUtils.encryptBatch(cxt,lats);
        String[] encryptedLons = CryptoUtils.encryptBatch(cxt,lons);

        List<GpsRecord> records = new LinkedList<>();
        int tscounter = 0;
        for (int i = 0; i < lats.size(); i++) {
            if (!lats.get(i).isEmpty()) {
                if (i+1 <= encryptedLats.length-1 && !lats.get(i+1).isEmpty()) {
                    GpsRecord record = new GpsRecord();
                    record.setTs_start(tsstart.get(tscounter));
                    record.setLatEncrypted(encryptedLats[i]);
                    record.setLongiEncrypted(encryptedLons[i]);
                    record.setProvider("");
                    records.add(record);

                    GpsRecord record2 = new GpsRecord();
                    record2.setTs_start(tsend.get(tscounter));
                    record2.setLatEncrypted(encryptedLats[i+1]);
                    record2.setLongiEncrypted(encryptedLons[i+1]);
                    record2.setProvider("");
                    records.add(record2);

                    tscounter += 1;
                    i+=1;
                }
                else {
                    GpsRecord record = new GpsRecord();
                    record.setTs_start(tsstart.get(tscounter));
                    record.setTs_end(tsend.get(tscounter));
                    tscounter += 1;
                    record.setLatEncrypted(encryptedLats[i]);
                    record.setLongiEncrypted(encryptedLons[i]);
                    record.setProvider("");
                    records.add(record);
                }
            }
        }

        Log.e("import","importing records "+records.size());
        for (GpsRecord record : records) {
            Log.e("record",record.toString());
            new GpsOpsAsyncTask(record, cxt).execute();
        }
        Log.e("import","done");
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
