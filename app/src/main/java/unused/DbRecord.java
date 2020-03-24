package unused;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "record_table")
public class DbRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "lat")
    private long lat;

    @NonNull
    @ColumnInfo(name = "longi")
    private long longi;

    public DbRecord(@NonNull long ts, long lat, long longi) {
        this.ts = ts;
        this.lat = lat;
        this.longi = longi;
    }

    public long getTs() {
        return this.ts;
    }

    public long getLat() { return this.lat; }

    public long getLongi() { return this.longi; }

    public String toString(Context context) {
        return this.ts+","+this.lat+","+this.longi;
    }
}
