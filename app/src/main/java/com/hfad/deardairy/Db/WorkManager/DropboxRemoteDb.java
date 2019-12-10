package com.hfad.deardairy.Db.WorkManager;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SaveRemoteDb {
    //save changes to remote db (rewrite all file)
    public static void saveDb() {
        Constraints constraints = new  Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackupWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }
}
