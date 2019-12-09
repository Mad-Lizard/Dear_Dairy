package com.hfad.deardairy.Db.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.SET_DEFAULT;

@Entity(tableName = "datas",
        foreignKeys = @ForeignKey(entity = TitleModel.class,
                                    parentColumns = "title_id",
                                    childColumns = "title",
                                    onDelete = SET_DEFAULT))
public class DataModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "text_id")
    private int textId;
    @NonNull
    private String date;
    @NonNull
    @ColumnInfo(defaultValue = "1")
    private int title;
    @NonNull
    private String text;


    public int getTextId() {
        return textId;
    }

    public void setTextId(int textId) { this.textId = textId; }

    @NonNull
    public int getTitle() {
        return title;
    }

    public void setTitle(@NonNull int title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
