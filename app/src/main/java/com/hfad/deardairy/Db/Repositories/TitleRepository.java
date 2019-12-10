package com.hfad.deardairy.Db.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hfad.deardairy.Db.DAO.TitleDao;
import com.hfad.deardairy.Db.DatabaseHelper;
import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.Db.WorkManager.DropboxRemoteDb;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TitleRepository {

    private static TitleDao mTitleDao;
    private static LiveData<List<TitleModel>> mAllTitles;
    private int titleId;
    private int titlesCount;
    private static List<String> titleNames;
    private static List<String> reservedTitle;
    private static TitleModel titleModel;

    //Constructor for class, where we get access to db throw DAO
    //Queries that return LiveData can be execute through AsyncTask, because LiveData waits for complete results
    //Other queries perform through Executor and Future, where I can use method get() to wait for complete results
    public TitleRepository(Application application) {
        DatabaseHelper db = DatabaseHelper.getDatabase(application);
        mTitleDao = db.getTitleDao();
    }

    public LiveData<List<TitleModel>> getAllTitles() {
        new AllTitlesAsyncTask().doInBackground();
        return mAllTitles; }

    private static class AllTitlesAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mAllTitles = mTitleDao.getTitles();
            return null;
        }
    }

    public List<String> getReservedTitle() {
        Future<List<String>> future = DatabaseHelper.databaseExecutor.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                reservedTitle = mTitleDao.getReservedTitle();
                return null;
            }
        });
        performFuture(future);
        return reservedTitle;
    }

    public TitleModel getTitle(final String titleName) {
        Future<TitleModel> future = DatabaseHelper.databaseExecutor.submit(new Callable<TitleModel>() {
            @Override
            public TitleModel call() throws Exception {
                titleModel = mTitleDao.getTitle(titleName);
                return titleModel;
            }
        });
        performFuture(future);
        return titleModel;
    }

    public List<String> getTitleNames() {
        Future<List<String>> future = DatabaseHelper.databaseExecutor.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                titleNames = mTitleDao.getTitleNames();
                return titleNames;
            }
        });
        performFuture(future);
        return titleNames;
    }


    public int getTitleId(final String titleName) {
        Future<Integer> future = DatabaseHelper.databaseExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                titleId = mTitleDao.getTitleId(titleName);
                return titleId;
            }
        });
        performFuture(future);
        return titleId;
    }

    public int getCountTitles() {
        Future<Integer> future = DatabaseHelper.databaseExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                titlesCount = mTitleDao.countTitles();
                return titlesCount;
            }
        });
        performFuture(future);
        return titlesCount;
    }

    public void insert(final TitleModel titleModel){
        DatabaseHelper.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTitleDao.insert(titleModel);
            }
        });
        DropboxRemoteDb.saveDb();
    }

    public void deleteTitle(final String titleName) {
        DatabaseHelper.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTitleDao.deleteTitle(titleName);
            }
        });
        DropboxRemoteDb.saveDb();
    }


    //method get() waits for results of query through Future if necessary.
    // It should be perform in try-catch
    private void performFuture(Future future) {
        try {
            future.get();
        } catch (ExecutionException ex){
            Log.e("ExecExep", ex.toString());
        } catch (InterruptedException ei) {
            Log.e("InterExec", ei.toString());
        }
    }
}
