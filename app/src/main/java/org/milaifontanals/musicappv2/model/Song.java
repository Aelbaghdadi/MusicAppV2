package org.milaifontanals.musicappv2.model;

import androidx.annotation.NonNull;
import androidx.room.*;
import org.milaifontanals.musicappv2.R;
import java.io.Serializable;

@Entity(tableName = "songs",
        primaryKeys = {"album_mbid", "num"},
        foreignKeys = @ForeignKey(entity = Album.class,
                parentColumns = "mbid",
                childColumns = "album_mbid",
                onDelete = ForeignKey.CASCADE))
public class Song implements Serializable {
    @NonNull
    @ColumnInfo(name = "num")
    private int num;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "min")
    private int min;

    @ColumnInfo(name = "sec")
    private int sec;

    @ColumnInfo(name = "is_fav")
    private int isFav;
    @NonNull
    @ColumnInfo(name = "album_mbid")
    private String albumMbid;


    @Ignore
    public Song(int num, String name, int min, int sec, int isFav) {
        this.num = num;
        this.name = name;
        this.min = min;
        this.sec = sec;
        this.isFav = isFav;
    }
    public Song(int num, String name, int min, int sec, String albumMbid) {
        this.num = num;
        this.name = name;
        this.min = min;
        this.sec = sec;
        this.isFav = R.drawable.favborder;
        this.albumMbid = albumMbid;
    }
    public int getIsFav() {
        return isFav;
    }
    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public int getSec() {
        return sec;
    }
    public void setSec(int sec) {
        this.sec = sec;
    }

    public String getAlbumMbid() {
        return albumMbid;
    }

    public void setAlbumMbid(String albumMbid) {
        this.albumMbid = albumMbid;
    }
}
