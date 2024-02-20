package org.milaifontanals.musicappv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.model.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<Artist> llArtists;
    private Context mContext;

    public ArtistAdapter(List<Artist> llArtists, Context c) {
        this.llArtists = llArtists;
        this.mContext = c;
    }
    public void setLlArtists(List<Artist> llArtists) {
        this.llArtists = llArtists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_row, parent, false);
        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = llArtists.get(position);
        holder.nombreArtistaTextView.setText(artist.getName());
        Glide.with(mContext)
                .load(artist.getImgUrl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.artistImage);    }

    @Override
    public int getItemCount() {
        if(llArtists==null){
            return 0;
        }
        return llArtists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView nombreArtistaTextView;
        ImageView artistImage;
        public ArtistViewHolder(View itemView) {
            super(itemView);
            nombreArtistaTextView = itemView.findViewById(R.id.authorName);
            artistImage = itemView.findViewById(R.id.artistImg);
        }
    }
}
