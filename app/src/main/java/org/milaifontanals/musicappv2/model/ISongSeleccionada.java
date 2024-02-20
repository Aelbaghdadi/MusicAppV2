package org.milaifontanals.musicappv2.model;

public interface ISongSeleccionada {
    public void onSongSelected(Song s);
    public void onLongSongSelected(Song s, Album a);
}
