package com.hfad.deardairy.Dropbox_access;

import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetDropboxFile {
    static DbxClientV2 client = DropboxClientFactory.getClient();

    public static FileMetadata getFileMetadata() {

        Future<FileMetadata> future = Executors.newFixedThreadPool(3).submit(new Callable<FileMetadata>() {
            @Override
            public FileMetadata call() throws Exception {
                try {
                    final DbxUserFilesRequests downloader = client.files();
                    final DbxDownloader<FileMetadata> getBackupDownloader = downloader
                            .download("/cloud-data.sqlite3");
                    return getBackupDownloader.getResult();
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Failed to download the file.", e);
                    return null;
                }
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("Dropbox Account", "Failed to upload file.", e);
            return null;
        }
    }


}