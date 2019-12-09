package com.hfad.deardairy.Db.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.Repositories.DataRepository;
import com.hfad.deardairy.Db.Models.DataWithTitle;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private static DataRepository mDataRepository;
    private LiveData<List<DataModel>> mAllTitleDatas;
    private LiveData<List<DataWithTitle>> mAllDateDatasWithTitles;
    private DataModel modelByTitleAndDate;

    public DataViewModel(Application application) {
        super(application);
        mDataRepository = new DataRepository(application);
    }

    public LiveData<List<DataWithTitle>> getDatasWithTitle(int titleId) {
        LiveData<List<DataWithTitle>> mDatasWithTitle = mDataRepository.getByTitleWithTitle(titleId);
        return mDatasWithTitle;
    }

    LiveData<List<DataModel>> getTitleDatas(int titleId) {
        mAllTitleDatas = mDataRepository.getAllTitleDatas(titleId);
        return mAllTitleDatas;
    }

    public LiveData<List<DataWithTitle>> getDateDatasWithTitle(String date) {
        mAllDateDatasWithTitles = mDataRepository.getAllDateDatasWithTitles(date);
        return mAllDateDatasWithTitles;
    }

    public DataModel getByTitleAndDate(int titleId, String date) {
        modelByTitleAndDate = mDataRepository.getModelByTitleAndDate(titleId, date);
        return modelByTitleAndDate;
    }

    public List<DataModel> getDatasForMonth(String monthDate) {
        List<DataModel> mDatasForMonth = mDataRepository.getAllDatasForMonth(monthDate);
        return mDatasForMonth;
   }

    public void insert(DataModel dataModel) {
        mDataRepository.insert(dataModel);
    }

    public void update(DataModel dataModel) {
        mDataRepository.update(dataModel);
    }

    public void deleteData(int titleId, String date) { mDataRepository.deleteData(titleId, date);}
}
