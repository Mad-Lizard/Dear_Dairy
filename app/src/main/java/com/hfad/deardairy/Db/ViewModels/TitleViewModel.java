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
        int titleId = mTitleRepository.getTitleId(titleName);
        return titleId;
    }

    public int getTitlesCount() {
        int titlesCount = mTitleRepository.getCountTitles();
        return titlesCount;
    }

    public TitleModel getTitle(String titleName) {
        TitleModel titleModel = mTitleRepository.getTitle(titleName);
        return titleModel;
    }

    public List<String> getTitleNames() {
        List<String> titleNames = mTitleRepository.getTitleNames();
        return titleNames;
    }

    public List<String> getReservedTitle() {
        List<String> reservedTitle = mTitleRepository.getReservedTitle();
        return reservedTitle;
    }
    public void insert(TitleModel titleModel) {mTitleRepository.insert(titleModel);}

    public void deleteTitle(String titleName) {mTitleRepository.deleteTitle(titleName);}
}
