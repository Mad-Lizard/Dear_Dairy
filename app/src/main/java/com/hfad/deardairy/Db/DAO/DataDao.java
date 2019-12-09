package com.hfad.deardairy.Db.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.Models.DataWithTitle;

import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insert(DataModel dataModel);

    @Update
    void update(DataModel dataModel);

    @Query("DELETE FROM datas WHERE title = :titleId AND date = :date")
    void deleteData(int titleId, String date);

    @Query("SELECT * FROM datas WHERE title LIKE :titleId AND date LIKE :date")
    DataModel getByTitleAndDate(int titleId, String date);

    @Query("SELECT * FROM datas WHERE title = :title ORDER BY text_id DESC")
    LiveData<List<DataModel>>getByTitle(int title);

    @Transaction @Query("SELECT * FROM datas WHERE date = :date ORDER BY text_id DESC")
    LiveData<List<DataModel>>getByDate(String date);

    //load with title name by room-relation
    @Transaction @Query("SELECT * FROM datas WHERE date = :date ORDER BY text_id DESC")
    LiveData<List<DataWithTitle>> getByDateWithTitle(String date);

    @Transaction @Query("SELECT * FROM datas WHERE title = :titleId ORDER BY text_id DESC")
    LiveData<List<DataWithTitle>> getByTitleWithTitle(int titleId);

    @Query("SELECT * FROM datas WHERE date LIKE :date")
    List<DataModel> getDatasForMonth(String date);

}
