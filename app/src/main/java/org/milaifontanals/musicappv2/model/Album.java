package org.milaifontanals.musicappv2.model;

import androidx.annotation.NonNull;
import androidx.room.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "albums")
public class Album implements Serializable {
    @NonNull
    @PrimaryKey
    private String mbid;
    private String artist;
    private String name;
    private int year;
    @ColumnInfo(name = "url_photo")
    private String urlPhoto;
    @Ignore
    private List<Song> songs;

    public Album(){

    }

    public Album(String mbid, String artist, String name, int year, String urlPhoto, List<Song> songs) {
        this.mbid = mbid;
        this.artist = artist;
        this.name = name;
        this.year = year;
        this.urlPhoto = urlPhoto;
        this.songs = songs;
    }

    public Album(String mbid, String artist, String name, int year, String urlPhoto) {
        this.mbid = mbid;
        this.artist = artist;
        this.name = name;
        this.year = year;
        this.urlPhoto = urlPhoto;
        this.songs = new ArrayList<>();
    }

    public Album(String artist, String name, int year, String urlPhoto) {
        this.artist = artist;
        this.name = name;
        this.year = year;
        this.urlPhoto = urlPhoto;
        this.songs = new ArrayList<>();
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getMbid() {
        return mbid;
    }
    public void addSong(Song s) {
        this.songs.add(s);
    }

    @Override
    public String toString() {
        return "Album{" +
                "artist='" + artist + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", urlPhoto='" + urlPhoto + '\'' +
                ", songs=" + songs +
                '}';
    }
}
