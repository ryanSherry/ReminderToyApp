package toyuiapp.example.com.toyuiapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import toyuiapp.example.com.toyuiapp.model.AlarmObject;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM ALARM_OBJECTS")
    LiveData<List<AlarmObject>> loadAllAlarmObjects();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAlarmObject(AlarmObject alarmObject);

    @Delete
    void deleteAlarmObject(AlarmObject alarmObject);

}
