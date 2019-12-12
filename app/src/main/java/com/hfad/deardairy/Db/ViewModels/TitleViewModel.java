package com.hfad.deardairy.Db.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hfad.deardairy.Db.DatabaseHelper;
import com.hfad.deardairy.Db.Models.TitleModel;
import com.hfad.deardairy.Db.Repositories.TitleRepository;

import java.util.List;

public class TitleViewModel extends AndroidViewModel {
    private DatabaseHelper db;
    private TitleRepository mTitleRepository;
    private LiveData<List<TitleModel>> mAllTitles;

    public TitleViewModel(Application application) {
        super(application);
        mTitleRepository = new TitleRepository(application);
        db = DatabaseHelper.getDatabase(application);
        mAllTitles = mTitleRepository.getAllTitles();
    }

    public LiveData<List<TitleModel>> getAllTitles() {
        return mAllTitles;
    }

    public int getTitleId(String titleName) {
        return mTitleRepository.getTitleId(titleName);
    }

    public int getTitlesCount() {
        return mTitleRepository.getCountTitles();
    }

    public TitleModel getTitle(String titleName) {
        return mTitleRepository.getTitle(titleName);
    }

    public List<String> getTitleNames() {
        return mTitleRepository.getTitleNames();
    }

    public List<String> getReservedTitle() {
        return mTitleRepository.getReservedTitle();
    }

    public Boolean insert(TitleModel titleModel) {
        if(mTitleRepository.insert(titleModel)) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteTitle(String titleName) {mTitleRepository.deleteTitle(titleName);}
}
