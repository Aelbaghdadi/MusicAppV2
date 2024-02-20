package org.milaifontanals.musicappv2.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.api.AlbumApi;
import org.milaifontanals.musicappv2.api.ArtistApi;
import org.milaifontanals.musicappv2.dao.AlbumDao;
import org.milaifontanals.musicappv2.dao.SongDao;
import org.milaifontanals.musicappv2.db.MusicDatabase;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.Artist;
import org.milaifontanals.musicappv2.model.Song;
import kotlinx.coroutines.Dispatchers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlinx.coroutines.Dispatchers;

public class MainViewModel extends AndroidViewModel implements AlbumApi.AlbumApiCallback, ArtistApi.ArtistApiCallback {

    private AlbumApi albumApi;
    private ArtistApi artistApi;
    private AlbumDao albumDao;
    private SongDao songDao;
    private MusicDatabase db;
    private MutableLiveData<Album> albumSelected = new MutableLiveData<>();
    private MutableLiveData<Song> songSelected = new MutableLiveData<>();
    private MutableLiveData<List<Album>> llAlbums = new MutableLiveData<>();
    private MutableLiveData<List<Album>> llAlbumsSrch = new MutableLiveData<>();

    private MutableLiveData<List<Artist>> llArtists = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = Room.databaseBuilder(application,
                MusicDatabase.class, "music_db").build();
        albumDao = db.albumDao();
        songDao = db.songDao();
        loadAlbums();
    }

    public MutableLiveData<List<Album>> getLlAlbums() {
        return llAlbums;
    }

    public MutableLiveData<List<Album>> getLlAlbumsSrch() {
        return llAlbumsSrch;
    }

    public MutableLiveData<List<Artist>> getLlArtists() {
        return llArtists;
    }

    public LiveData<Album> getAlbumSelected() {
        return albumSelected;
    }

    public void setAlbumSelected(Album albumSelected) {
        this.albumSelected.setValue(albumSelected);
    }

    public MutableLiveData<Song> getSongSelected() {
        return songSelected;
    }

    public void setSongSelected(Song songSelected) {
        this.songSelected.setValue(songSelected);
    }

    public void setLlAlbumsSrch(MutableLiveData<List<Album>> llAlbumsSrch) {
        this.llAlbumsSrch = llAlbumsSrch;
    }

    public void addAlbum(Album a) {
        insertAlbumDB(a);
        List<Album> currentAlbums = llAlbums.getValue();
        List<Album> newAlbums = new ArrayList<>(currentAlbums != null ? currentAlbums : Collections.emptyList());
        newAlbums.add(a);
        llAlbums.setValue(newAlbums);
        Log.d("XXX2", llAlbums.getValue().get(llAlbums.getValue().size() - 1).toString());
    }



    public void deleteAlbum(Album album) {
        List<Album> albums = llAlbums.getValue();
        if (albums != null) {
            albums.remove(album);
            llAlbums.setValue(albums);
        }
        deleteAlbumDB(album);
    }

    private void deleteAlbumDB(Album album) {
        new Thread(() -> {
            albumDao.delete(album);
        }).start();
    }

    public void deleteSongFromAlbum(Album album, Song song) {
        List<Album> albums = llAlbums.getValue();
        if (albums != null) {
            int albumIndex = albums.indexOf(album);
            if (albumIndex != -1) {
                albums.get(albumIndex).getSongs().remove(song);
                llAlbums.setValue(albums);
            }
        }
    }

    public void addSongToAlbum(Album album, Song song) {
        List<Album> albums = llAlbums.getValue();
        if (albums != null) {
            int albumIndex = albums.indexOf(album);
            if (albumIndex != -1) {
                albums.get(albumIndex).getSongs().add(song);
                llAlbums.setValue(albums);
            }
        }
    }

    public void searchAlbum(String searchTerm) {
        albumApi = new AlbumApi(this);
        String albumName = albumApi.encodeName(searchTerm);
        String url = "https://ws.audioscrobbler.com/2.0/?method=album.search&album=" + albumName + "&api_key=d09f5b174e26c00f59a7e995b34af07b&format=json";

        albumApi.execute(url, "albums");
    }

    public void searchAlbumInfo(Album a) {
        albumApi = new AlbumApi(this);
        String albumName = albumApi.encodeName(a.getName());
        String artistName = albumApi.encodeName(a.getArtist());
        String url = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=d09f5b174e26c00f59a7e995b34af07b&artist=" + artistName + "&album=" + albumName + "&format=json";

        albumApi.execute(url, "album");
    }

    public void searchArtist(String searchTerm) {
        artistApi = new ArtistApi(this);
        String artistName = artistApi.encodeName(searchTerm);
        String url = "https://ws.audioscrobbler.com/2.0/?method=artist.search&artist=" + artistName + "&api_key=d09f5b174e26c00f59a7e995b34af07b&format=json";

        artistApi.execute(url);
    }

    private void insertAlbumDB(Album album) {
        new Thread(() -> {
            albumDao.insert(album);
            songDao.insertSongs(album.getSongs());
        }).start();
    }

    private void loadAlbums() {
        new LoadAlbumsTask().execute();
    }
    public void loadSongs(String albumMbid, OnSongsLoadedCallback callback) {
        AsyncTask.execute(() -> {
            List<Song> songs = songDao.getSongsByAlbumMbid(albumMbid);
            if (callback != null) {
                callback.onSongsLoaded(songs);
            }
        });
    }
    @Override
    public void onApiResponseAlbum(Album result) {
        addAlbum(result);
    }

    @Override
    public void onApiResponseAlbumList(List<Album> result) {
        llAlbumsSrch.setValue(result);
    }

    @Override
    public void onApiResponseArtist(List<Artist> result) {
        llArtists.setValue(result);
    }

    private class LoadAlbumsTask extends AsyncTask<Void, Void, List<Album>> {
        @Override
        protected List<Album> doInBackground(Void... voids) {

            return albumDao.getAllAlbums();
        }

        @Override
        protected void onPostExecute(List<Album> albums) {
            llAlbums.setValue(albums);
        }
    }

    public interface OnSongsLoadedCallback {
        void onSongsLoaded(List<Song> songs);
    }

}

