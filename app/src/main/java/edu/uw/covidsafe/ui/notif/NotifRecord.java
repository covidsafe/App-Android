package edu.uw.covidsafe.ui.notif;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notif_record_table")
public class NotifRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts_start")
    public long ts_start;

    @ColumnInfo(name = "ts_end")
    public long ts_end;

    @NonNull
    @ColumnInfo(name = "msgType")
    public int msgType;

    @NonNull
    @ColumnInfo(name = "msg")
    public String msg;

    public NotifRecord(@NonNull long ts_start, long ts_end, String msg, int msgType) {
        this.ts_start = ts_start;
        this.ts_end = ts_end;
        this.msg = msg;
        this.msgType = msgType;
    }

    public long getTs_start() {
        return this.ts_start;
    }

    public long getTs_end() {
        return this.ts_end;
    }

    public String getMsg() { return this.msg; }

    public int getMsgType() { return this.msgType; }

    public String toString() {
        return this.ts_start+","+this.ts_end+","+this.msg+","+this.msgType;
    }
}
