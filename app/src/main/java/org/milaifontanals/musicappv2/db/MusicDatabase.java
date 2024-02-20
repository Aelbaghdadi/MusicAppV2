package org.milaifontanals.musicappv2.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.milaifontanals.musicappv2.dao.AlbumDao;
import org.milaifontanals.musicappv2.dao.SongDao;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.Song;

@Database(entities = {Album.class, Song.class}, version = 1, exportSchema = false)
public abstract class MusicDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract SongDao songDao();
}
