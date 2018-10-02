package toyuiapp.example.com.toyuiapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import timber.log.Timber;
import toyuiapp.example.com.toyuiapp.model.AlarmObject;

@Database(entities = {AlarmObject.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static final String LOG_TAG = AppDataBase.class.getSimpleName();
    public static final Object LOCK = new Object();
    public static final String DATABASE_NAME = "ALERT_DATABASE";
    private static AppDataBase sInstance;

    public static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class, AppDataBase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }
    public abstract AlarmDao alarmDao();
}
