package edu.uw.covidsafe.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileOperations {

    public static String[] readfilelisthuman(Context cxt) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        String[] ss = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            ss[i] = name;
        }

        Arrays.sort(ss, Collections.reverseOrder());

        String[] out = new String[Math.min(ss.length, Constants.NumFilesToDisplay)];
        for(int i = 0; i < out.length; i++) {
            out[i] = Utils.formatDate(ss[i]);
        }
        return out;
    }

    public static void append(String s, Context cxt, String dirname, String filename) {
        try {
            String dir = cxt.getExternalFilesDir(null).toString()+"/"+dirname+"/";
            File path = new File(dir);
            if (!path.exists()) {
                path.mkdir();
            }

            File file = new File(dir+File.separator+filename);
            if(!file.exists()) {
                file.createNewFile();
            }
            Log.e("gps","writing to "+file.toString());
            BufferedWriter buf = new BufferedWriter(new FileWriter(file,true));

            buf.append(s);
            buf.newLine();

            buf.flush();
            buf.close();
        } catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    public static String readrawasset(Context context, int id) {
        Scanner inp = new Scanner(context.getResources().openRawResource(id));

        String out = "";
        while (inp.hasNextLine()) {
            out += inp.nextLine();
        }
        inp.close();

        return out;
    }

    // mapping from ID => RSSI threshold
    public static HashMap<Integer,Integer> readDeviceThresholds(Context context, int id) {
        HashMap<Integer,Integer> bleThresh = new HashMap<>();

        Scanner inp = new Scanner(context.getResources().openRawResource(id));
        inp.nextLine();

        int counter = 1;
        while (inp.hasNextLine()) {
            String[] elts = inp.nextLine().split(",");
            bleThresh.put(counter, (int)Double.parseDouble(elts[5]));
            counter += 1;
        }
        inp.close();

        return bleThresh;
    }

    // list of phone names
    public static List<String> readDeviceList(Context context, int id) {
        List<String> devices = new LinkedList<>();

        Scanner inp = new Scanner(context.getResources().openRawResource(id));
        inp.nextLine();

        while (inp.hasNextLine()) {
            String[] elts = inp.nextLine().split(",");
            devices.add(elts[2].toLowerCase());
        }
        inp.close();

        return devices;
    }

    // list of manufacturer
    public static List<String> readManufacturerList(Context context, int id) {
        List<String> devices = new LinkedList<>();

        Scanner inp = new Scanner(context.getResources().openRawResource(id));
        inp.nextLine();

        while (inp.hasNextLine()) {
            String[] elts = inp.nextLine().split(",");
            devices.add(elts[0].toLowerCase());
        }
        inp.close();

        return devices;
    }
}
