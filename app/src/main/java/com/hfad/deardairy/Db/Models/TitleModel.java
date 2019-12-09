package com.hfad.deardairy.Db.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "titles",
        indices = {
            @Index(value = "name", unique = true)
        })
public class TitleModel {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "title_id")
    private int titleId;
    @NonNull
    private String name;

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getTitleId() {
        return this.titleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
