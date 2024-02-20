package org.milaifontanals.musicappv2.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.Song;
import org.milaifontanals.musicappv2.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlbumApi extends AsyncTask<String, Void, Object> {

    private AlbumApiCallback callback;

    public AlbumApi(AlbumApiCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... strings) {
        try {
            String url = strings[0];
            String type = strings[1];
            String json = NetworkUtils.getJSon(url);

            if (json != null) {
                if (type.equals("album")) {
                    return getAlbum(json);
                } else {
                    return getAlbumListInfo(json);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("AlbumApi", "Error during API request", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (callback != null) {
            if (result instanceof Album) {
                callback.onApiResponseAlbum((Album) result);
            } else if (result instanceof List<?>) {
                callback.onApiResponseAlbumList((List<Album>) result);
            }
        }
    }

    private List<Album> getAlbumListInfo(String json) {
        List<Album> albumInfoList = new ArrayList<>();

        try {
            JSONObject resultsObject = new JSONObject(json).getJSONObject("results");
            JSONObject albumMatchesObject = resultsObject.getJSONObject("albummatches");
            JSONArray albumsArray = albumMatchesObject.getJSONArray("album");

            for (int i = 0; i < albumsArray.length(); i++) {
                JSONObject albumObject = albumsArray.getJSONObject(i);

                String albumName = albumObject.getString("name");
                String artist = albumObject.getString("artist");
                String imageUrl = getExtralargeImage(albumObject.getJSONArray("image"));

                Album albumInfo = new Album(artist, albumName, 2020, imageUrl);
                albumInfoList.add(albumInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return albumInfoList;
    }

    private Album getAlbum(String url) {
        try {
            JSONObject albumObject = new JSONObject(url).getJSONObject("album");
            String mbid = albumObject.getString("mbid");
            String albumName = albumObject.getString("name");
            String artist = albumObject.getString("artist");
            String imageUrl = getExtralargeImage(albumObject.getJSONArray("image"));
            JSONObject wikiObject = albumObject.optJSONObject("wiki");
            String dateString = "05 May 2023, 01:26";
            if (wikiObject != null && wikiObject.has("published")) {
                dateString = wikiObject.getString("published");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);

            Album albumInfo = new Album(mbid, artist, albumName, year, imageUrl);

            JSONArray tracksArray = albumObject.getJSONObject("tracks").getJSONArray("track");
            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackObject = tracksArray.getJSONObject(i);
                String songName = trackObject.getString("name");
                int duration = trackObject.optInt("duration", 0);

                int minutes = duration / 60;
                int seconds = duration % 60;

                int rank = trackObject.getJSONObject("@attr").getInt("rank");
                Song songInfo = new Song(rank, songName, minutes,seconds, mbid);
                albumInfo.addSong(songInfo);
            }

            return albumInfo;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String getMegaImage(JSONArray imageArray) throws JSONException {
        for (int i = 0; i < imageArray.length(); i++) {
            JSONObject imageObject = imageArray.getJSONObject(i);
            String size = imageObject.getString("size");
            if ("mega".equals(size)) {
                return imageObject.getString("#text");
            }
        }
        return "";
    }


    private static String getExtralargeImage(JSONArray imageArray) throws JSONException {
        for (int i = 0; i < imageArray.length(); i++) {
            JSONObject imageObject = imageArray.getJSONObject(i);
            String size = imageObject.getString("size");
            if ("extralarge".equals(size)) {
                return imageObject.getString("#text");
            }
        }
        return "";
    }

    public String encodeName(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al codificar el valor para la URL", e);
        }
    }

    public interface AlbumApiCallback {
        void onApiResponseAlbum(Album result);
        void onApiResponseAlbumList(List<Album> result);
    }
}
