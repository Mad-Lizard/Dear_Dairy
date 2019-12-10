package com.hfad.deardairy.Db.WorkManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.hfad.deardairy.Db.DatabaseHelper;
import com.hfad.deardairy.Dropbox_access.DropboxClientFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BackupWorker extends Worker {

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final String dbPath = getApplicationContext().getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();
        final File db = new File(dbPath);
        DbxClientV2 client = DropboxClientFactory.getClient();
        try (FileInputStream is = new FileInputStream(db);) {
            FileMetadata metadata = client.files().uploadBuilder("/cloud-data.sqlite3")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(is);
            return Result.success();
        } catch (UploadErrorException e) {
            Log.e(getClass().getName(), "Failed to upload file.", e);
            return Result.failure();
        } catch (DbxException e) {
            Log.e(getClass().getName(), "Failed to upload file.", e);
            return Result.failure();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to upload file.", e);
            return Result.failure();
        }
    }
}
