package com.hfad.deardairy.Db.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hfad.deardairy.Db.Models.TitleModel;

import java.util.List;

@Dao
public interface TitleDao {
    @Insert
    void insert(TitleModel titleModel);

    @Query("DELETE FROM titles WHERE name = :title")
    void deleteTitle(String title);

    @Query("SELECT * FROM titles WHERE name = :title")
    TitleModel getTitle(String title);

    @Query("SELECT title_id FROM titles WHERE name = :title")
    int getTitleId(String title);

    @Query("SELECT name FROM titles WHERE title_id NOT IN (1)")
    List<String>getTitleNames();

    @Query("SELECT Count(*) FROM titles")
    int countTitles();

    //first row is reserved for default value
    @Query("SELECT * FROM titles WHERE title_id NOT IN (1) ORDER BY title_id")
    LiveData<List<TitleModel>> getTitles();

    //get first row if else rows are empty
    @Query("SELECT name FROM titles WHERE title_id = 1")
    List<String> getReservedTitle();
}
