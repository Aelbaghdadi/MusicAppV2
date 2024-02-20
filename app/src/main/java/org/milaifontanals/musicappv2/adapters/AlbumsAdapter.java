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

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {


    private List<Album> llAlbums;
    private IAlbumSeleccionat mListener;
    private Context mContext;

    private int selectedPosition = -1;

    public AlbumsAdapter(IAlbumSeleccionat a, Context c, List<Album> albums){
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

        int layout = R.layout.album_box;
        View row = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album albumActual = llAlbums.get(position);
        Glide.with(mContext)
                .load(albumActual.getUrlPhoto()) // Pasa la URL de la imagen
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)) // Placeholder mientras se carga
                .into(holder.artistLogo);
        holder.txvArtist.setText(albumActual.getArtist());
        holder.txvAlbum.setText(albumActual.getName());
        holder.txvDate.setText(albumActual.getYear()+"");
        holder.itemView.setBackgroundColor(position == selectedPosition
                ? Color.parseColor("#EE96B4") // Selected color
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
        if(llAlbums==null){
            return 0;
        }
        return llAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artistLogo;
        TextView txvArtist;
        TextView txvAlbum;
        TextView txvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistLogo = itemView.findViewById(R.id.imgArtist);
            txvArtist = itemView.findViewById(R.id.artist);
            txvAlbum = itemView.findViewById(R.id.album);
            txvDate = itemView.findViewById(R.id.release);
        }
    }
}
