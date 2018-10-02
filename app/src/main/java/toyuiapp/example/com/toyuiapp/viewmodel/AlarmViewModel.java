package toyuiapp.example.com.toyuiapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import java.util.List;

import toyuiapp.example.com.toyuiapp.database.AppDataBase;
import toyuiapp.example.com.toyuiapp.model.AlarmObject;

public class AlarmViewModel extends AndroidViewModel {
    private LiveData<List<AlarmObject>>alarmObjects;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        AppDataBase db = AppDataBase.getsInstance(this.getApplication());
        alarmObjects = db.alarmDao().loadAllAlarmObjects();
    }

    public LiveData<List<AlarmObject>> getAlarmObjects() {
        return alarmObjects;
    }

}
