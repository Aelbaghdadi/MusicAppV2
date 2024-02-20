package org.milaifontanals.musicappv2.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.adapters.AlbumsAdapter;
import org.milaifontanals.musicappv2.adapters.AlbumsAdapterSearch;
import org.milaifontanals.musicappv2.adapters.ArtistAdapter;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.Artist;
import org.milaifontanals.musicappv2.model.IAlbumSeleccionat;
import org.milaifontanals.musicappv2.viewmodel.MainViewModel;

import java.util.List;

public class DownloadFragment extends Fragment implements IAlbumSeleccionat {
    private View v;
    private RecyclerView rcyView;
    private ImageButton goButton;
    private EditText editText;
    private MainViewModel mainViewModel;
    private AlbumsAdapterSearch albumAdapter;
    private ArtistAdapter artistAdapter;
    private RadioButton radioArtist, radioAlbum;

    public DownloadFragment() {

    }

    public static DownloadFragment newInstance(String param1, String param2) {
        DownloadFragment fragment = new DownloadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_download, container, false);
        editText = v.findViewById(R.id.srchText);
        rcyView = v.findViewById(R.id.rcySrch);
        radioAlbum = v.findViewById(R.id.radio_album);
        radioArtist = v.findViewById(R.id.radio_artist);
        GridLayoutManager glm = new GridLayoutManager(requireContext(),1);
        rcyView.setLayoutManager(glm);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        albumAdapter = new AlbumsAdapterSearch(this, requireContext(),mainViewModel.getLlAlbumsSrch().getValue());
        artistAdapter = new ArtistAdapter(mainViewModel.getLlArtists().getValue(),requireContext());
        mainViewModel.getLlAlbumsSrch().observe(getViewLifecycleOwner(), new Observer<List<Album>>() {
            @Override
            public void onChanged(List<Album> albums) {
                albumAdapter.setLlAlbums(albums);
            }
        });
        mainViewModel.getLlArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(List<Artist> artists) {
                artistAdapter.setLlArtists(artists);
            }
        });
        rcyView.setAdapter(albumAdapter); // Set initial adapter
        goButton = v.findViewById(R.id.btnSrch);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });

        initImageLoader(requireContext());
        return v;
    }

    private void performSearch() {
        String searchTerm = editText.getText().toString().trim();

        if (!searchTerm.isEmpty()) {
            if (radioArtist.isChecked()) {
                rcyView.setAdapter(artistAdapter);
                mainViewModel.searchArtist(searchTerm);
                artistAdapter.notifyDataSetChanged();
            }else if (radioAlbum.isChecked()) {
                rcyView.setAdapter(albumAdapter);
                mainViewModel.searchAlbum(searchTerm);
                albumAdapter.notifyDataSetChanged();
            }
        }
    }
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();

        ImageLoader.getInstance().init(config.build());

    }
    @Override
    public void onAlbumSelected(Album a) {

    }

    @Override
    public void onLongAlbumSelected(Album a) {
        mainViewModel.setAlbumSelected(a);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_down, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageButton downOption = bottomSheetView.findViewById(R.id.downloadOption);

        downOption.setOnClickListener(v -> {
            mainViewModel.searchAlbumInfo(a);
            albumAdapter.setSelectedPosition(-1);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }


}