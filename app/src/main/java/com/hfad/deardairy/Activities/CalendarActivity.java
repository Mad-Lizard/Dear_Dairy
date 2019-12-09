package com.hfad.deardairy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.ViewModels.DataViewModel;
import com.hfad.deardairy.Db.DatabaseHelper;
import com.hfad.deardairy.Dropbox_access.DropboxActivity;
import com.hfad.deardairy.Dropbox_access.DropboxClientFactory;
import com.hfad.deardairy.Dropbox_access.GetDropboxAccount;
import com.hfad.deardairy.Dropbox_access.GetDropboxFile;
import com.hfad.deardairy.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class CalendarActivity extends DropboxActivity {

    MaterialCalendarView calendarView;
    Calendar calendar;
    private boolean switchEdit = true;
    private String monthTitle;
    HashSet<Date> datesOfMonth = new HashSet<>();
    String accessToken;
    DbxClientV2 client;
    SharedPreferences preferences;
    private static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get CalendarView
        calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        if (savedInstanceState != null) {
            accessToken = savedInstanceState.getString("access-token");
        }

        calendar = Calendar.getInstance();
        int month = calendar.get(MONTH)+1;
        int year = calendar.get(YEAR);
        monthTitle = String.valueOf("%"+month+"-"+year);
        datesOfMonth.add(calendar.getTime());

        datesOfMonth = getDatasDateForMonth();
        setDatasDateForMonth(datesOfMonth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Switch switchMain = findViewById(R.id.switch_main);
        switchMain.setText(R.string.switch_main);
        switchMain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    switchEdit = true;
                } else {
                    switchEdit = false;
                }
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int month = date.getMonth()+1;
                int year = date.getYear();
                monthTitle = String.valueOf("%"+month+"-"+year);
                datesOfMonth = getDatasDateForMonth();
                setDatasDateForMonth(datesOfMonth);
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int month = date.getMonth()+1;
                String dateTitle = String.valueOf(date.getDay()+"-"+month+"-"+date.getYear());
                if(switchEdit) {
                    Intent intent = new Intent(widget.getContext(), DairyActivity.class);
                    intent.putExtra("date", (String) dateTitle);
                    startActivity(intent);
                    calendarView.setDateSelected(date, false);
                } else {
                    Intent intent = new Intent(widget.getContext(), DataActivity.class);
                    intent.putExtra("date", (String) dateTitle);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean hasToken() {
        preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        accessToken = preferences.getString("access-token", null);
        return accessToken != null;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


   // @Override
    protected void loadBackupToDropbox() {
        final String dbPath = getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();
        final File db = new File(dbPath);

            Future<String>future = Executors.newFixedThreadPool(4).submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String result;
                    try (FileInputStream is = new FileInputStream(db);) {
                        FileMetadata metadata = client.files().uploadBuilder("/cloud-data.sqlite3")
                                .uploadAndFinish(is);
                        result = "Done";
                        Log.v("file", "done");
                    } catch (UploadErrorException e) {
                        Log.e(getClass().getName(), "Failed to upload file.", e);
                        result = "Failed";
                    } catch (DbxException e) {
                        Log.e(getClass().getName(), "Failed to upload file.", e);
                        result = "Failed";
                    } catch (IOException e) {
                        Log.e(getClass().getName(), "Failed to upload file.", e);
                        result = "Failed";
                    }
                    return result;
                }
            });
            try {
                future.get();
            } catch (Exception e) {
                Log.e(getClass().getName(), "Failed to upload file.", e);
            }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CalendarActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(CalendarActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(CalendarActivity.this,
                    "Доступ к хранилищу позволит синхронизировать бд.",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            ActivityCompat.requestPermissions(CalendarActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    protected void getDataFromDropbox() {
        final FileMetadata dropboxFile = GetDropboxFile.getFileMetadata();

        Future<File>future = Executors.newFixedThreadPool(4).submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                try{
                    File sourcePath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    File sourceFile = new File(sourcePath, dropboxFile.getName());
                   if(Build.VERSION.SDK_INT >= 23) {
                       if(checkPermission()) {
                           if(!sourcePath.exists()) {
                               if(!sourcePath.mkdir()) {
                                   Exception e = new RuntimeException("Unable to create directory: " +sourcePath);
                               }
                           } else if(!sourcePath.isDirectory()){
                               Exception e = new IllegalStateException("Download path is not a directory: " + sourcePath);
                               return null;
                           }
                           client = DropboxClientFactory.getClient();
                           try (OutputStream outputStream = new FileOutputStream(sourceFile)) {
                               client.files().download(dropboxFile.getPathLower(), dropboxFile.getRev())
                                       .download(outputStream);
                           }
                           return sourceFile;
                       } else {
                           requestPermission();
                       }
                   }

                } catch (Exception e) {

                }
                return null;
            }
        });
        File sourceDbFile = null;
        try {
            sourceDbFile = future.get();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Failed to download file.", e);
        }
        String destinationDbPath = getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();
        File destinationDbFile = new File(destinationDbPath);
        copyFiles(sourceDbFile, destinationDbFile);
    }

    private static Boolean copyFiles(File source, File destination) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
            return true;
        } catch (Exception e) {
            Log.e("CopyFiles", "Has an error.", e);
            return false;
        }

    }

    public HashSet<Date> getDatasDateForMonth() {
        DataViewModel dataViewModel = new DataViewModel(getApplication());
        List<DataModel> dataModels = dataViewModel.getDatasForMonth(monthTitle);
        for(int i = 0; i < dataModels.size(); i++) {
            DataModel dataModel = dataModels.get(i);
            String dateString = dataModel.getDate();
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                datesOfMonth.add(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return datesOfMonth;
    }

    public void setDatasDateForMonth(HashSet<Date> datesOfMonth) {
        for(Date thisDate : datesOfMonth) {
            calendarView.setDateSelected(thisDate, true);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        datesOfMonth = getDatasDateForMonth();
        setDatasDateForMonth(datesOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.titlesButton:
                    Intent intent = new Intent(this, TitleSelectionActivity.class);
                    this.startActivity(intent);
                    break;
                    default:
                    return super.onOptionsItemSelected(item);
                case R.id.dropbox_button:
                    if(!hasToken()) {
                        Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.app_key));
                    } else {
                       // getAccessToken();
                        getDataFromDropbox();
                        String name = GetDropboxAccount.getUserName();
                        if (name != null) {
                            Toast.makeText(this, R.string.dropbox_hello, Toast.LENGTH_LONG).show();
                        }
                    }
            }
        return  true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("access-token", accessToken);
    }

}
