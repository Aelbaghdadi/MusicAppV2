package org.milaifontanals.musicappv2.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.Artist;
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

public class ArtistApi extends AsyncTask<String, Void, Object> {

    private ArtistApiCallback callback;

    public ArtistApi(ArtistApiCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... strings) {
        try {
            String url = strings[0];
            String json = NetworkUtils.getJSon(url);

            if (json != null) {
                return getArtist(json);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("ArtistApi", "Error during API request", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (callback != null) {
            callback.onApiResponseArtist((List<Artist>) result);
        }
    }

    private List<Artist> getArtist(String url) {
        List<Artist> artistList = new ArrayList<>();
        try {
            JSONObject resultsObject = new JSONObject(url).getJSONObject("results");
            JSONObject albumMatchesObject = resultsObject.getJSONObject("artistmatches");
            JSONArray artistArray = albumMatchesObject.getJSONArray("artist");

            for (int i = 0; i < artistArray.length(); i++) {
                JSONObject artistObject = artistArray.getJSONObject(i);
                String artistName = artistObject.getString("name");
                String imageUrl = getExtralargeImage(artistObject.getJSONArray("image"));
                Artist artistInfo = new Artist(artistName,imageUrl);
                artistList.add(artistInfo);
            }

            return artistList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    public interface ArtistApiCallback {
        void onApiResponseArtist(List<Artist> result);
    }
}
