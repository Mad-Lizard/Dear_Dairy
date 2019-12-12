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

    public DataViewModel(Application application) {
        super(application);
        mDataRepository = new DataRepository(application);
    }

    public LiveData<List<DataWithTitle>> getDatasWithTitle(int titleId) {
        return mDataRepository.getByTitleWithTitle(titleId);
    }

    public LiveData<List<DataWithTitle>> getDateDatasWithTitle(String date) {
        return mDataRepository.getAllDateDatasWithTitles(date);
    }

    public DataModel getByTitleAndDate(int titleId, String date) {
        return mDataRepository.getModelByTitleAndDate(titleId, date);
    }

    public List<DataModel> getDatasForMonth(String monthDate) {
        return mDataRepository.getAllDatasForMonth(monthDate);
   }

    public void insert(DataModel dataModel) {
        mDataRepository.insert(dataModel);
    }

    public void update(DataModel dataModel) {
        mDataRepository.update(dataModel);
    }

    public void deleteData(int titleId, String date) { mDataRepository.deleteData(titleId, date);}
}
