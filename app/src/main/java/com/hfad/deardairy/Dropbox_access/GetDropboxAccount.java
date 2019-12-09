package com.hfad.deardairy.Dropbox_access;

import android.util.Log;

import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetDropboxAccount {

    public static String getUserName() {
        Future<String> future = Executors.newFixedThreadPool(2).submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                DbxUserUsersRequests user = DropboxClientFactory.getClient().users();
                FullAccount account = user.getCurrentAccount();
                return account.getName().getDisplayName();

            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("Dropbox Account", "Failed to get user data.", e);
            return null;
        }
    }
}
