package com.example.corona;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class FileOperations {
    public static ArrayList<GpsRecord> readGpsRecords(Context cxt, String filename) {
        ArrayList<GpsRecord> ll = new ArrayList<GpsRecord>();
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName+"/"+filename);
        if (!dir.exists()) {
            return null;
        }
        try {
            Scanner inp = new Scanner(dir);
            while (inp.hasNextLine()) {
                String line = inp.nextLine();
                if (line.length()>0) {
                    ll.add(new GpsRecord(line));
                }
            }
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return ll;
    }

    public static ArrayList<BlacklistRecord> readBlacklist(Context cxt) {
        ArrayList<BlacklistRecord> ll = new ArrayList<BlacklistRecord>();
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.blacklistDirName+"/"+Constants.blacklistFileName);
        if (!dir.exists()) {
            return null;
        }
        try {
            Scanner inp = new Scanner(dir);
            while (inp.hasNextLine()) {
                String line = inp.nextLine();
                if (line.length()>0) {
                    ll.add(new BlacklistRecord(line));
                }
            }
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return ll;
    }

    public static String[] readfilelist(Context cxt) {
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

    public static String readLastSentLog(Context cxt) {
        String dir = cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName+"/"+Constants.lastSentFileName;
        File path = new File(dir);
        if (!path.exists()) {
            return "";
        }

        try {
            Scanner inp = new Scanner(path);
            String lastLine = "";
            while (inp.hasNextLine()) {
                lastLine = inp.nextLine();
            }
            return lastLine;
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }

        return "";
    }

    public static String readSubmitLog(Context cxt) {
        String dir = cxt.getExternalFilesDir(null).toString()+"/"+Constants.formDirName+"/"+Constants.logFileName;
        File path = new File(dir);
        if (!path.exists()) {
            return "";
        }

        try {
            Scanner inp = new Scanner(path);
            String lastLine = "";
            while (inp.hasNextLine()) {
                lastLine = inp.nextLine();
            }
            return lastLine;
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }

        return "";
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
            BufferedWriter buf = new BufferedWriter(new FileWriter(file,true));

            buf.append(s);
            buf.newLine();

            buf.flush();
            buf.close();
        } catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }
}
