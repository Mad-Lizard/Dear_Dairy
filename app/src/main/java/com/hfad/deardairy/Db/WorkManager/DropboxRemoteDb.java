package com.hfad.deardairy.Db.WorkManager;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class DropboxRemoteDb {
    static Constraints constraints = new  Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build();
    //save changes to remote db (rewrite all file)
    public static void saveDb() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackupWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }

    public static void downloadDb() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }
}
