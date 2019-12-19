package com.hfad.deardairy.Dropbox_access;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.dropbox.core.android.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.hfad.deardairy.Activities.TitleSelectionActivity;
import com.hfad.deardairy.Db.WorkManager.DropboxRemoteDb;
import com.hfad.deardairy.R;

//Base class for all activities that require auth token
public abstract class DropboxActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 007;
    private static final int PERMISSION_REQUEST_CODE = 100;
    public static GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        checkDropboxSignIn();
        checkGoogleSignIn();
    }

    public boolean hasDropboxToken() {
        SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        String accessToken = preferences.getString("access-token", null);
        return accessToken != null;
    }

    private void checkDropboxSignIn() {
        //get token for dropbox
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
    }

    private void checkGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.v("signInResult:failed=", String.valueOf(e.getStatusCode()));
            updateUI(null);
        }
    }

    public void  updateUI(GoogleSignInAccount account){
        if(account != null){
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }
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
                startTitleSelectionActivity();
                break;
            default:
            case R.id.dropbox_button:
                syncWithDropboxButton();
                break;
            case R.id.google_menu:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account == null) {
                    Intent signInIntent = DropboxActivity.mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, DropboxActivity.RC_SIGN_IN);
                } else {
                    mGoogleSignInClient.signOut();
                    updateUI(null);
                }
                break;
        }
        return  true;
    }

    public void startTitleSelectionActivity() {
        Intent intent = new Intent(this, TitleSelectionActivity.class);
        this.startActivity(intent);
    }

    public void syncWithDropboxButton() {
        if(!hasDropboxToken()) {
            Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.app_key));
        }
        if(Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
        DropboxRemoteDb.downloadDb();
        String name = GetDropboxAccount.getUserName();
        if (name != null) {
            Toast.makeText(this, R.string.dropbox_hello, Toast.LENGTH_LONG).show();
        }
        Boolean dropboxSync = true;
        SharedPreferences preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
        preferences.edit().putBoolean("dropboxSync", dropboxSync).apply();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DropboxActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(DropboxActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(DropboxActivity.this,
                    "Доступ к хранилищу позволит синхронизировать бд.",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            ActivityCompat.requestPermissions(DropboxActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
