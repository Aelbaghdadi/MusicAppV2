package org.milaifontanals.musicappv2.adapters;

import android.content.Context;
import android.graphics.Color;
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
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.IAlbumSeleccionat;

import java.util.List;

public class AlbumsAdapterSearch extends RecyclerView.Adapter<AlbumsAdapterSearch.ViewHolder> {

    private List<Album> llAlbums;
    private IAlbumSeleccionat mListener;
    private Context mContext;

    private int selectedPosition = -1;

    public AlbumsAdapterSearch(IAlbumSeleccionat a, Context c, List<Album> albums){
        mContext = c;
        mListener = a;
        llAlbums = albums;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
    public void setLlAlbums(List<Album> llAlbums) {
        this.llAlbums = llAlbums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout = R.layout.album_box_srch;
        View row = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album albumActual = llAlbums.get(position);
        Glide.with(mContext)
                .load(albumActual.getUrlPhoto())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.imgAlbum);
        holder.txvArtist.setText(albumActual.getArtist());
        holder.txvAlbum.setText(albumActual.getName());
        holder.itemView.setBackgroundColor(position == selectedPosition
                ? Color.parseColor("#EE96B4")
                : Color.TRANSPARENT);
        holder.itemView.setOnLongClickListener(view -> {
            if (selectedPosition == position) {
                selectedPosition = -1;
            } else {
                selectedPosition = position;
            }
            notifyDataSetChanged();
            mListener.onLongAlbumSelected(llAlbums.get(position));
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            mListener.onAlbumSelected(llAlbums.get(position));
        });

    }

    @Override
    public int getItemCount() {
        if (llAlbums==null){
            return 0;
        }
        return llAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbum;
        TextView txvArtist;
        TextView txvAlbum;
        TextView txvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            txvArtist = itemView.findViewById(R.id.artistNameSrch);
            txvAlbum = itemView.findViewById(R.id.albmTitleSrch);
        }
    }
}
