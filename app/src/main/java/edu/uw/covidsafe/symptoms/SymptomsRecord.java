package edu.uw.covidsafe.symptoms;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "symptoms_record_table")
public class SymptomsRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "fever")
    private boolean fever;

    @NonNull
    @ColumnInfo(name = "cough")
    private boolean cough;

    @NonNull
    @ColumnInfo(name = "shortnessOfBreath")
    private boolean shortnessOfBreath;

    @NonNull
    @ColumnInfo(name = "troubleBreathing")
    private boolean troubleBreathing;

    @NonNull
    @ColumnInfo(name = "chestPain")
    private boolean chestPain;

    @NonNull
    @ColumnInfo(name = "confusion")
    private boolean confusion;

    @NonNull
    @ColumnInfo(name = "blue")
    private boolean blue;

    public SymptomsRecord(@NonNull long ts, boolean fever, boolean cough, boolean shortnessOfBreath, boolean troubleBreathing,
                     boolean chestPain, boolean confusion, boolean blue) {
        this.ts = ts;
        this.fever = fever;
        this.cough = cough;
        this.shortnessOfBreath = shortnessOfBreath;
        this.troubleBreathing = troubleBreathing;
        this.chestPain = chestPain;
        this.confusion = confusion;
        this.blue = blue;
    }

    public long getTs() { return this.ts; }
    public boolean getFever() { return this.fever; }
    public boolean getCough() { return this.cough; }
    public boolean getShortnessOfBreath() { return this.shortnessOfBreath; }
    public boolean getTroubleBreathing() { return this.troubleBreathing; }
    public boolean getChestPain() { return this.chestPain; }
    public boolean getConfusion() { return this.confusion; }
    public boolean getBlue() { return this.blue; }

    public String toString() {
        return this.ts+","+this.fever+","+this.cough+","+this.shortnessOfBreath+","+
                this.troubleBreathing+","+this.chestPain+","+this.confusion+","+this.blue;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("ts",ts);
        obj.put("fever",fever);
        obj.put("cough",cough);
        obj.put("shortnessOfBreath",shortnessOfBreath);
        obj.put("troubleBreathing",troubleBreathing);
        obj.put("chestPain",chestPain);
        obj.put("confusion",confusion);
        obj.put("blue",blue);
        return obj;
    }
}
