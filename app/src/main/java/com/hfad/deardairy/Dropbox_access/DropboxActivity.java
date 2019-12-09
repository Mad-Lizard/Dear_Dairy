package com.hfad.deardairy.Dropbox_access;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences;


import com.dropbox.core.android.Auth;
import com.hfad.deardairy.Db.WorkManager.BackupWorker;

//Base class for all activities that require auth token
public abstract class DropboxActivity extends AppCompatActivity {

    //every time, when activity gets focus...
    @Override
    protected void onPostResume() {
        super.onPostResume();

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

        String uid = Auth.getUid();
        String storeUid = preferences.getString("user-uid", null);
        if(uid != null && !uid.equals(storeUid)) {
            preferences.edit().putString("user-uid", uid).apply();
        }
    }

//    private void initAndLoadData(String accessToken) {
//        DropboxClientFactory.init(accessToken);
//        loadData();
//    }

   // protected abstract void loadData();

    public boolean hasToken() {
        SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        String accessToken = preferences.getString("access-token", null);
        return accessToken != null;
    }
}
