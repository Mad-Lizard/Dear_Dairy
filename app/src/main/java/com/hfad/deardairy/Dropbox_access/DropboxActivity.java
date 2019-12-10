package com.hfad.deardairy.Dropbox_access;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences;
import android.util.Log;


import com.dropbox.core.android.Auth;
import com.hfad.deardairy.Db.WorkManager.BackupWorker;
import com.hfad.deardairy.Db.WorkManager.DropboxRemoteDb;

//Base class for all activities that require auth token
public abstract class DropboxActivity extends AppCompatActivity {

    //every time, when activity gets focus...
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        String accessToken = preferences.getString("access-token", null);
        if(accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if(accessToken != null) {
                preferences.edit().putString("access-token", accessToken).apply();
                DropboxClientFactory.init(accessToken);
            }
        } else {
            DropboxClientFactory.init(accessToken);
        }
        Boolean dropboxSync = preferences.getBoolean("dropboxSync", false);
        if (dropboxSync) {
            DropboxRemoteDb.downloadDb();
        }
    }

    public boolean hasToken() {
        SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        String accessToken = preferences.getString("access-token", null);
        return accessToken != null;
    }
}
