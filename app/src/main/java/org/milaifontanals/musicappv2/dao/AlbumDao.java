package org.milaifontanals.musicappv2.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.milaifontanals.musicappv2.model.Album;

import java.util.List;

@Dao
public interface AlbumDao {
    @Insert
    void insert(Album album);
    @Query("SELECT * FROM albums")
    List<Album> getAllAlbums();
    @Delete
    void delete(Album album);
}
