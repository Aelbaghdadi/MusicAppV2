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

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.ISongSeleccionada;
import org.milaifontanals.musicappv2.model.Song;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private List<Song> mSongs;
    private Album album;
    private Context mContext;
    private ISongSeleccionada mListener;
    private int selectedPosition = -1;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public SongsAdapter(ISongSeleccionada s, Album al, Context mContext) {
        this.mSongs = al.getSongs();
        this.mContext = mContext;
        this.mListener = s;
        this.album = al;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.song_row;
        View row = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song actualSong = mSongs.get(position);
        holder.txvNum.setText(actualSong.getNum() + "");
        holder.isFav.setImageResource(actualSong.getIsFav());
        holder.txvSong.setText(actualSong.getName());
        holder.txvDuration.setText(actualSong.getMin() + ":"+actualSong.getSec());

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
            mListener.onLongSongSelected(mSongs.get(position),album);
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            mListener.onSongSelected(mSongs.get(position));
        });
    }

    @Override
    public int getItemCount() {
        if (mSongs == null) {
            return 0;
        }
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvNum;
        ImageView isFav;
        TextView txvSong;
        TextView txvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvNum = itemView.findViewById(R.id.songNum);
            isFav = itemView.findViewById(R.id.isfav);
            txvSong = itemView.findViewById(R.id.songTitle);
            txvDuration = itemView.findViewById(R.id.songDuration);
        }
    }
}
