package com.hfad.deardairy.Dropbox_access;

import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

//singleton
public class DropboxClientFactory {

    private static DbxClientV2 mDbxClient2;

    public static void init(String accessToken) {
        if(mDbxClient2 == null) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/dear-dairy")
            //.withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();

            mDbxClient2 = new DbxClientV2(config, accessToken);
        }
    }

    public static DbxClientV2 getClient() {
        if(mDbxClient2 == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return mDbxClient2;
    }

}
