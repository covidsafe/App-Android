package com.example.covidsafe.utils;

import android.content.Context;
import android.util.Log;

import com.example.covidsafe.models.BleRecord;
import com.example.covidsafe.models.GpsRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import unused.BlacklistRecord;

public class FileOperations {

    public static void writeLastSentLog(Context cxt, long lastTimestamp) {
        String dir = cxt.getExternalFilesDir(null).toString()+"/"+ Constants.gpsDirName+"/"+Constants.lastSentFileName;
        File path = new File(dir);

        try {
            if (!path.exists()) {
                path.createNewFile();
            }

            BufferedWriter buf = new BufferedWriter(new FileWriter(path,false));

            buf.append(lastTimestamp+"");
            buf.newLine();

            buf.flush();
            buf.close();
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    public static void markStatusSubmitted(Context cxt) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/");
        if (!dir.exists()) {
            return;
        }
        try {
            File file = new File(dir + File.separator + Constants.DiagnosisReportFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));

            buf.append(System.currentTimeMillis()+"");
            buf.newLine();

            buf.flush();
            buf.close();
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    public static boolean reportStatusSubmitted(Context cxt) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/");
        if (!dir.exists()) {
            return false;
        }
        File file = new File(dir + File.separator + Constants.DiagnosisReportFileName);
        return file.exists();
    }

    public static ArrayList<GpsRecord> readGpsRecords(Context cxt, String dirname, String filename) {
        ArrayList<GpsRecord> ll = new ArrayList<GpsRecord>();

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName+"/"+dirname+"/"+filename);
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

    public static ArrayList<BleRecord> readBleRecords(Context cxt, String filename) {
        ArrayList<BleRecord> ll = new ArrayList<BleRecord>();

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.bleDirName+"/"+filename);
        if (!dir.exists()) {
            return null;
        }
        try {
            Scanner inp = new Scanner(dir);
            while (inp.hasNextLine()) {
                String line = inp.nextLine();
                if (line.length()>0) {
                    ll.add(new BleRecord(line));
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

    public static Date[] readBleFileList(Context cxt, boolean ascending) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.bleDirName);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (ascending) {
            Arrays.sort(files);
        }
        else {
            Arrays.sort(files, Collections.reverseOrder());
        }

        LinkedList<Date> ss = new LinkedList();

        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".txt")) {
                name = name.substring(0,name.length()-4);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                ss.add(format.parse(name));
            }
            catch(Exception e) {
                Log.e("logme",e.getMessage());
            }
        }

        return ss.toArray(new Date[ss.size()]);
    }

    public static Date[] readGpsFileList(Context cxt, boolean ascending, String direc) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName+"/"+direc);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (ascending) {
            Arrays.sort(files);
        }
        else {
            Arrays.sort(files, Collections.reverseOrder());
        }

        LinkedList<Date> ss = new LinkedList();

        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".txt")) {
                name = name.substring(0,name.length()-4);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                ss.add(format.parse(name));
            }
            catch(Exception e) {
                Log.e("logme",e.getMessage());
            }
        }

        return ss.toArray(new Date[ss.size()]);
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
