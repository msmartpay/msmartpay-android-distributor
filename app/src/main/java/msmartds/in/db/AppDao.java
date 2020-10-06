package msmartds.in.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {
 
    @Query("SELECT * FROM Credentials")
    List<Credentials> getAll();
 
    @Insert
    void insert(Credentials task);
 
    @Delete
    void delete(Credentials task);

    @Query("DELETE from Credentials")
    void delete();


    @Update
    void update(Credentials task);
    
}