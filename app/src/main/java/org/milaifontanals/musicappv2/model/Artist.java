package org.milaifontanals.musicappv2.model;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String name;
    private String imgUrl;
    private List<Album> albums = new ArrayList<Album>();

    public Artist(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public Artist(String name, String imgUrl, List<Album> albums) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.albums = albums;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
