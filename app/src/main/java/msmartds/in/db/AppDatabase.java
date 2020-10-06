package msmartds.in.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Credentials.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDBDao();
}