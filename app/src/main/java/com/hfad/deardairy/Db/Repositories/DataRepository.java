package com.hfad.deardairy.Db.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hfad.deardairy.Db.DAO.DataDao;
import com.hfad.deardairy.Db.DatabaseHelper;
import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.Models.DataWithTitle;
import com.hfad.deardairy.Db.WorkManager.DropboxRemoteDb;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DataRepository {

    private static DataDao mDataDao;
    private LiveData<List<DataModel>> mAllTitleDatas;
    private LiveData<List<DataWithTitle>> mAllDateDatasWithTitles;
    private static List<DataModel> mAllDatasForMonth;
    private DataModel modelByTitleAndDate;

    public DataRepository(Application application) {
        DatabaseHelper db = DatabaseHelper.getDatabase(application);
        mDataDao = db.getDataDao();
    }

    public LiveData<List<DataWithTitle>> getByTitleWithTitle(int titleId){
        LiveData<List<DataWithTitle>> mDatasByTitle = mDataDao.getByTitleWithTitle(titleId);
        return mDatasByTitle;
    }

    public DataModel getModelByTitleAndDate(final int titleId, final String date) {
        Future<DataModel> future = DatabaseHelper.databaseExecutor.submit(new Callable<DataModel>() {
            @Override
            public DataModel call() throws Exception {
                modelByTitleAndDate = mDataDao.getByTitleAndDate(titleId, date);
                return modelByTitleAndDate;
            }
        });
        performFuture(future);
        return modelByTitleAndDate;
    }

    public LiveData<List<DataModel>> getAllTitleDatas(final int titleId) {
       new AllTitleDatasAsynkTask().doInBackground(titleId);
       return mAllTitleDatas;
    }

    private class AllTitleDatasAsynkTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            int titleId = integers[0];
            mAllTitleDatas = mDataDao.getByTitle(titleId);
            return null;
        }
    }

    public List<DataModel> getAllDatasForMonth(final String monthTitle) {
        Future<List<DataModel>> future = DatabaseHelper.databaseExecutor
                .submit(new Callable<List<DataModel>>() {
            @Override
            
            public List<DataModel> call() throws Exception {
                mAllDatasForMonth = mDataDao.getDatasForMonth(monthTitle);
                return mAllDatasForMonth;
            }
        });
        performFuture(future);
        return mAllDatasForMonth;
    }


    public LiveData<List<DataWithTitle>> getAllDateDatasWithTitles(final String date) {
        new AllDateDatasWithTitleAsyncTask().doInBackground(date);
        return mAllDateDatasWithTitles;
    }

    private class AllDateDatasWithTitleAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            String date = strings[0];
            mAllDateDatasWithTitles = mDataDao.getByDateWithTitle(date);
            return null;
        }
    }

    public void insert(final DataModel dataModel) {
        DatabaseHelper.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataDao.insert(dataModel);
            }
        });
        DropboxRemoteDb.saveDb();
    }

    public void update(final DataModel dataModel) {
        DatabaseHelper.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataDao.update(dataModel);
            }
        });
        DropboxRemoteDb.saveDb();
    }

    public void deleteData(final int titleId, final String date) {
        DatabaseHelper.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataDao.deleteData(titleId, date);
            }
        });
        DropboxRemoteDb.saveDb();
    }

    //method get() waits for results of query through Future if necessary.
    // It should be perform in try-catch
    private void performFuture(Future future) {
        try {
            //with get it will be wait for result. Also you can specify a time of waiting.
            future.get();
        } catch (ExecutionException ex){
            Log.e("ExecExep", ex.toString());
        } catch (InterruptedException ei) {
            Log.e("InterExec", ei.toString());
        }
    }
}
