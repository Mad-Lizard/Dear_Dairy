package com.hfad.deardairy.Db.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DataWithTitle {
    @Embedded
    public DataModel data;
    @Relation(parentColumn = "title", entity = TitleModel.class, entityColumn = "title_id")
    public List<TitleModel> titles;
}

