package toyuiapp.example.com.toyuiapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import timber.log.Timber;
import toyuiapp.example.com.toyuiapp.database.AppDataBase;
import toyuiapp.example.com.toyuiapp.database.AppExecutors;
import toyuiapp.example.com.toyuiapp.model.AlarmObject;
import toyuiapp.example.com.toyuiapp.viewmodel.AlarmViewModel;

public class MainActivity extends AppCompatActivity {

    Calendar calendar;
    TextView date;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    Long dateInMillis;
    Set<Long> datesList;
    Set<String> listOfHumanReadableDates;
    String humanReadableDate;
    Set<Integer> setOfUniqueIds;
    List<AlarmObject> mAlarmObjects;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    int uniqueRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectADateButton = findViewById(R.id.btnDate);
        Button clearDatesButton = findViewById(R.id.clearDates);
        Button clearAlarmsButton = findViewById(R.id.clearAlarms);
        date = findViewById(R.id.tvSelectedDate);
        final TimePicker timePicker = findViewById(R.id.timePicker1);

        datesList = new HashSet<>();
        listOfHumanReadableDates = new TreeSet<>();
        setOfUniqueIds = new HashSet<>();

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(this, AlarmReceiver.class);
        final AppDataBase db = AppDataBase.getsInstance(this);

//        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, alarmIntent);

        AlarmViewModel alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        alarmViewModel.getAlarmObjects().observe(this, new Observer<List<AlarmObject>>() {
            @Override
            public void onChanged(@Nullable List<AlarmObject> alarmObjects) {
                mAlarmObjects = alarmObjects;
                if (alarmObjects != null) {
                    for(AlarmObject object : alarmObjects) {
                        datesList.add(object.getDateInMillis());
                        listOfHumanReadableDates.add(object.getHumanReadableDate());
                        setOfUniqueIds.add(object.getUniqueId());
                    }
                }
            }
        });



        selectADateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();




                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                uniqueRequestCode = (int) System.currentTimeMillis();
                                alarmIntent = PendingIntent.getBroadcast(MainActivity.this, uniqueRequestCode, intent, 0);
                                humanReadableDate = (month + 1) + "/" + day + "/" + year;
                                listOfHumanReadableDates.add(humanReadableDate);
                                date.setText(TextUtils.join(",", listOfHumanReadableDates));
                                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                calendar.set(Calendar.HOUR_OF_DAY,hour);
                                Timber.i("alarmHour %s", hour);
                                calendar.set(Calendar.MINUTE,minute);
                                Timber.i("alarmMinute %s", minute);
                                dateInMillis = calendar.getTimeInMillis();
                                datesList.add(dateInMillis);
//                                alarmMgr.set(AlarmManager.RTC_WAKEUP, 60000, alarmIntent);
                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000,alarmIntent);
                                setOfUniqueIds.add(uniqueRequestCode);

                                AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlarmObject alarmObject = new AlarmObject(uniqueRequestCode, humanReadableDate,dateInMillis);
                                        db.alarmDao().insertAlarmObject(alarmObject);
                                    }
                                });

                            }
                        },year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        clearDatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfHumanReadableDates.clear();
                date.setText("");
            }
        });

        clearAlarmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(final Object uniqueId : setOfUniqueIds) {
                    Timber.i("removing uid: %s", (int) uniqueId);
                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, (int) uniqueId,intent,0);
                    alarmMgr.cancel(alarmIntent);
                    AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            for(AlarmObject alarmObject : mAlarmObjects) {
                                db.alarmDao().deleteAlarmObject(alarmObject);
                            }
                        }
                    });
                }
                Toast.makeText(MainActivity.this,"Reminder Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
