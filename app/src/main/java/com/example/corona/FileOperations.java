package com.example.corona;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class FileOperations {

    public static String[] readfilelist(Context cxt, String dirname) {
        File dir = new File(cxt.getExternalFilesDir(null).toString()+"/"+Constants.gpsDirName);
        File[] files = dir.listFiles();
        String[] ss = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            String formatted = Utils.formatDate(name);
            if (!formatted.isEmpty()) {
                ss[i] = formatted;
            }
        }
        return ss;
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
