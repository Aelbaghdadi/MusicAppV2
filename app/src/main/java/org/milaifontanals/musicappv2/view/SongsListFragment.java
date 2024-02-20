package org.milaifontanals.musicappv2.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.adapters.SongsAdapter;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.ISongSeleccionada;
import org.milaifontanals.musicappv2.model.Song;
import org.milaifontanals.musicappv2.viewmodel.MainViewModel;

import java.util.List;


public class SongsListFragment extends Fragment implements ISongSeleccionada {

    private MainViewModel mViewModel;
    private RecyclerView rcySongs;
    private SongsAdapter adapter;
    private ImageView imgAlbum;
    private TextView ttlAlbm, nameArtist;
    private FloatingActionButton fabCreate;

    public SongsListFragment() {
        // Required empty public constructor
    }


    public static SongsListFragment newInstance(String param1, String param2) {
        SongsListFragment fragment = new SongsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songs_list, container, false);
        mViewModel.getAlbumSelected().observe(getViewLifecycleOwner(), new Observer<Album>() {
            @Override
            public void onChanged(Album data) {
                ttlAlbm = v.findViewById(R.id.albmTitle);
                ttlAlbm.setText(data.getName());
                imgAlbum = v.findViewById(R.id.imgAlbum);
                Glide.with(requireContext())
                        .load(data.getUrlPhoto())
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                        .into(imgAlbum);
                nameArtist = v.findViewById(R.id.artistName);
                nameArtist.setText(data.getArtist());
                fabCreate = v.findViewById(R.id.addSong);
                fabCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSongDialog();
                    }
                });
                rcySongs = v.findViewById(R.id.rcySongs);
                rcySongs.addItemDecoration(new DividerItemDecoration(rcySongs.getContext(), DividerItemDecoration.HORIZONTAL));
                rcySongs.setLayoutManager(new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false));
                rcySongs.setHasFixedSize(true);

                mViewModel.loadSongs(data.getMbid(), new MainViewModel.OnSongsLoadedCallback() {
                    @Override
                    public void onSongsLoaded(List<Song> songs) {
                        data.setSongs(songs);
                        adapter = new SongsAdapter(SongsListFragment.this, data, requireContext());
                        rcySongs.setAdapter(adapter);
                    }
                });
            }
        });

        return v;
    }

    private void showSongDialog() {
        SongDialog dialog = new SongDialog();
        dialog.show(getChildFragmentManager(), "song_dialog");
    }

    @Override
    public void onSongSelected(Song s) {

    }

    @Override
    public void onLongSongSelected(Song s, Album album) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_options, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageButton editOption = bottomSheetView.findViewById(R.id.editOption);
        ImageButton deleteOption = bottomSheetView.findViewById(R.id.deleteOption);
        mViewModel.setSongSelected(s);
        editOption.setOnClickListener(v -> {
            showSongDialog();
            adapter.notifyDataSetChanged();
            adapter.setSelectedPosition(-1);
            bottomSheetDialog.dismiss();
        });

        deleteOption.setOnClickListener(v -> {
            mViewModel.deleteSongFromAlbum(album, s);
            adapter.setSelectedPosition(-1);
            adapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }
}