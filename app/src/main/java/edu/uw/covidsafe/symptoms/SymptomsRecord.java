package edu.uw.covidsafe.symptoms;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity(tableName = "symptoms_record_table")
public class SymptomsRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    @NonNull
    @ColumnInfo(name = "logTime")
    private long logTime;

    @NonNull
    @ColumnInfo(name = "fever")
    private boolean fever;

    @NonNull
    @ColumnInfo(name = "feverOnset")
    private long feverOnset;

    @NonNull
    @ColumnInfo(name = "feverTemp")
    private double feverTemp;

    @NonNull
    @ColumnInfo(name = "feverUnit")
    private String feverUnit = "";

    @NonNull
    @ColumnInfo(name = "feverDaysExperienced")
    private int feverDaysExperienced;

    @NonNull
    @ColumnInfo(name = "abdominalPain")
    private boolean abdominalPain;

    @NonNull
    @ColumnInfo(name = "chills")
    private boolean chills;

    @NonNull
    @ColumnInfo(name = "cough")
    private boolean cough;

    @NonNull
    @ColumnInfo(name = "coughOnset")
    private long coughOnset;

    @NonNull
    @ColumnInfo(name = "coughDaysExperienced")
    private int coughDaysExperienced;

    @NonNull
    @ColumnInfo(name = "coughSeverity")
    private String coughSeverity = "";

    @NonNull
    @ColumnInfo(name = "diarrhea")
    private boolean diarrhea;

    @NonNull
    @ColumnInfo(name = "troubleBreathing")
    private boolean troubleBreathing;

    @NonNull
    @ColumnInfo(name = "headache")
    private boolean headache;

    @NonNull
    @ColumnInfo(name = "chestPain")
    private boolean chestPain;

    @NonNull
    @ColumnInfo(name = "soreThroat")
    private boolean soreThroat;

    @NonNull
    @ColumnInfo(name = "vomiting")
    private boolean vomiting;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public boolean isFever() {
        return fever;
    }

    public void setFever(boolean fever) {
        this.fever = fever;
    }

    public long getFeverOnset() {
        return feverOnset;
    }

    public void setFeverOnset(long feverOnset) {
        this.feverOnset = feverOnset;
    }

    public double getFeverTemp() {
        return feverTemp;
    }

    public void setFeverTemp(double feverTemp) {
        this.feverTemp = feverTemp;
    }

    @NonNull
    public String getFeverUnit() {
        return feverUnit;
    }

    public void setFeverUnit(@NonNull String feverUnit) {
        this.feverUnit = feverUnit;
    }

    public int getFeverDaysExperienced() {
        return feverDaysExperienced;
    }

    public void setFeverDaysExperienced(int feverDaysExperienced) {
        this.feverDaysExperienced = feverDaysExperienced;
    }

    public boolean isAbdominalPain() {
        return abdominalPain;
    }

    public void setAbdominalPain(boolean abdominalPain) {
        this.abdominalPain = abdominalPain;
    }

    public boolean isChills() {
        return chills;
    }

    public void setChills(boolean chills) {
        this.chills = chills;
    }

    public boolean isCough() {
        return cough;
    }

    public void setCough(boolean cough) {
        this.cough = cough;
    }

    public long getCoughOnset() {
        return coughOnset;
    }

    public void setCoughOnset(long coughOnset) {
        this.coughOnset = coughOnset;
    }

    public int getCoughDaysExperienced() {
        return coughDaysExperienced;
    }

    public void setCoughDaysExperienced(int coughDaysExperienced) {
        this.coughDaysExperienced = coughDaysExperienced;
    }

    @NonNull
    public String getCoughSeverity() {
        return coughSeverity;
    }

    public void setCoughSeverity(@NonNull String coughSeverity) {
        this.coughSeverity = coughSeverity;
    }

    public boolean isDiarrhea() {
        return diarrhea;
    }

    public void setDiarrhea(boolean diarrhea) {
        this.diarrhea = diarrhea;
    }

    public boolean isTroubleBreathing() {
        return troubleBreathing;
    }

    public void setTroubleBreathing(boolean troubleBreathing) {
        this.troubleBreathing = troubleBreathing;
    }

    public boolean isHeadache() {
        return headache;
    }

    public void setHeadache(boolean headache) {
        this.headache = headache;
    }

    public boolean isChestPain() {
        return chestPain;
    }

    public void setChestPain(boolean chestPain) {
        this.chestPain = chestPain;
    }

    public boolean isSoreThroat() {
        return soreThroat;
    }

    public void setSoreThroat(boolean soreThroat) {
        this.soreThroat = soreThroat;
    }

    public boolean isVomiting() {
        return vomiting;
    }

    public void setVomiting(boolean vomiting) {
        this.vomiting = vomiting;
    }

    public SymptomsRecord() {

    }

    public SymptomsRecord(@NonNull long ts, long logTime, boolean fever, long feverOnset, double feverTemp, String feverUnit,
                          int feverDaysExperienced, boolean abdominalPain, boolean chills,
                          boolean cough, long coughOnset, int coughDaysExperienced,
                          String coughSeverity, boolean diarrhea, boolean troubleBreathing,
                          boolean headache, boolean chestPain, boolean soreThroat, boolean vomiting) {
        this.ts = ts;
        this.logTime = logTime;
        this.fever = fever;
        this.feverOnset = feverOnset;
        this.feverTemp = feverTemp;
        this.feverUnit = feverUnit;
        this.feverDaysExperienced = feverDaysExperienced;
        this.abdominalPain = abdominalPain;
        this.chills = chills;
        this.cough = cough;
        this.coughOnset = coughOnset;
        this.coughDaysExperienced = coughDaysExperienced;
        this.coughSeverity = coughSeverity;
        this.diarrhea = diarrhea;
        this.troubleBreathing = troubleBreathing;
        this.headache = headache;
        this.chestPain = chestPain;
        this.soreThroat = soreThroat;
        this.vomiting = vomiting;
    }

    public SymptomsRecord copy() {
        SymptomsRecord out = new SymptomsRecord();
        out.ts = ts;
        out.logTime = logTime;
        out.fever = fever;
        out.feverOnset = feverOnset;
        out.feverTemp = feverTemp;
        out.feverUnit = feverUnit;
        out.feverDaysExperienced = feverDaysExperienced;
        out.abdominalPain = abdominalPain;
        out.chills = chills;
        out.cough = cough;
        out.coughOnset = coughOnset;
        out.coughDaysExperienced = coughDaysExperienced;
        out.coughSeverity = coughSeverity;
        out.diarrhea = diarrhea;
        out.troubleBreathing = troubleBreathing;
        out.headache = headache;
        out.chestPain = chestPain;
        out.soreThroat = soreThroat;
        out.vomiting = vomiting;
        return out;
    }

    public int numSymptoms() {
        List<Boolean> values = Lists.newArrayList(
                fever, abdominalPain, chills, cough, diarrhea, troubleBreathing, headache, chestPain,
                soreThroat, vomiting
        );
        return Booleans.countTrue(Booleans.toArray(values));
    }

    public List<String> getSymptoms() {
        List<String> values = new LinkedList<>();
        if (this.fever) {
            values.add("fever");
        }
        if (this.abdominalPain) {
            values.add("abdominal");
        }
        if (this.chills) {
            values.add("chills");
        }
        if (this.cough) {
            values.add("cough");
        }
        if (this.diarrhea) {
            values.add("diarrhea");
        }
        if (this.troubleBreathing) {
            values.add("breathing");
        }
        if (this.headache) {
            values.add("headache");
        }
        if (this.chestPain) {
            values.add("chest");
        }
        if (this.soreThroat) {
            values.add("sore");
        }
        if (this.vomiting) {
            values.add("vomiting");
        }
        return values;
    }
}
