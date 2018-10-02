package toyuiapp.example.com.toyuiapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Set;

@Entity(tableName = "alarm_objects")
public class AlarmObject {
    @PrimaryKey
    int uniqueId;
    String humanReadableDate;
    long dateInMillis;

    public AlarmObject(int uniqueId, String humanReadableDate, long dateInMillis) {
        this.uniqueId = uniqueId;
        this.humanReadableDate = humanReadableDate;
        this.dateInMillis = dateInMillis;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getHumanReadableDate() {
        return humanReadableDate;
    }

    public void setHumanReadableDate(String humanReadableDate) {
        this.humanReadableDate = humanReadableDate;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }
}
