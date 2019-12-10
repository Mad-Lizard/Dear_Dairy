package com.hfad.deardairy.Db.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Operation;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.hfad.deardairy.Activities.CalendarActivity;
import com.hfad.deardairy.Dropbox_access.DropboxClientFactory;
import com.hfad.deardairy.Dropbox_access.GetDropboxFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadWorker extends Worker {

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final FileMetadata dropboxFile = GetDropboxFile.getFileMetadata();
        DbxClientV2 client = DropboxClientFactory.getClient();
        try{
            File sourcePath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File sourceFile = new File(sourcePath, dropboxFile.getName());
                    if(!sourcePath.exists()) {
                        if(!sourcePath.mkdir()) {
                            Exception e = new RuntimeException("Unable to create directory: " +sourcePath);
                        }
                    } else if(!sourcePath.isDirectory()){
                        Exception e = new IllegalStateException("Download path is not a directory: " + sourcePath);
                        return null;
                    }
                    try (OutputStream outputStream = new FileOutputStream(sourceFile)) {
                        client.files().download(dropboxFile.getPathLower(), dropboxFile.getRev())
                                .download(outputStream);
                    }
                    return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }


}
