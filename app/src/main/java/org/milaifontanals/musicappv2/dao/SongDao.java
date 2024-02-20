package org.milaifontanals.musicappv2.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.milaifontanals.musicappv2.model.Song;

import java.util.List;

@Dao
public interface SongDao {
    @Insert
    void insert(Song song);
    @Insert
    void insertSongs(List<Song> songs);
    @Query("SELECT * FROM songs WHERE album_mbid = :mbid")
    List<Song> getSongsByAlbumMbid(String mbid);
}
