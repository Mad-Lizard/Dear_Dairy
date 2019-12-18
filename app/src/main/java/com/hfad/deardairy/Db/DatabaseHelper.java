package com.hfad.deardairy.Db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hfad.deardairy.Db.DAO.DataDao;
import com.hfad.deardairy.Db.DAO.TitleDao;
import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.Models.TitleModel;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DataModel.class, TitleModel.class}, version = 3, exportSchema = true)
public abstract class DatabaseHelper extends RoomDatabase {

    public final static String DB_NAME = "dairy-data";
    public final static String DB_REMOTE_NAME = "cloud-data";
    public abstract DataDao getDataDao();
    public abstract TitleDao getTitleDao();
    private static volatile DatabaseHelper INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE UNIQUE INDEX index_titles_name ON titles(name);");
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE titles (title_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL)");
            database.execSQL("CREATE TABLE datas (text_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "date TEXT NOT NULL, title INTEGER NOT NULL DEFAULT 1, text TEXT NOT NULL, " +
                    "FOREIGN KEY(title) REFERENCES titles(title_id) ON DELETE SET DEFAULT);");
            database.execSQL("DROP TABLE DataModel;");

        }
    };

    public static DatabaseHelper getDatabase(final Context context) {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                //Add reserved row for default title that appears when there are no titles in db
                db.execSQL("INSERT INTO titles (title_id, name) VALUES (1, 'Обо всём и ни о чём');");
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                String dbRemotePath = context.getDatabasePath(DB_NAME).getAbsolutePath();
                db.execSQL(attach(DB_REMOTE_NAME, dbRemotePath));
                File remoteDb = new File(dbRemotePath, DB_REMOTE_NAME);
                try {
                    remoteDb.delete();
                } catch (Exception e) {
                }
                super.onOpen(db);
            }
        };

        if(INSTANCE == null) {
            synchronized (DatabaseHelper.class){
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseHelper.class, DB_NAME)
                           // .allowMainThreadQueries()
                            .addCallback(rdc)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //SQL query for attaching local and remote databases
    private static String attach(final String dbName, final String dbPath) {
        return "ATTACH DATABASE '" + dbPath + dbName + "' AS \"" + dbName + "\";";
    }
}
