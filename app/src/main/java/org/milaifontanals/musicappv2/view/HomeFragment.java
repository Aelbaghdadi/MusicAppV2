package org.milaifontanals.musicappv2.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.adapters.AlbumsAdapter;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.model.IAlbumSeleccionat;
import org.milaifontanals.musicappv2.viewmodel.MainViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements IAlbumSeleccionat {

    private AlbumsAdapter adapter;
    private RecyclerView rcyAlbums;
    private MainViewModel mViewModel;
    private ActionMode actionMode;
    private int selectedAlbumPosition = -1;



    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rcyAlbums = v.findViewById(R.id.rcyAlbums);
        GridLayoutManager glm = new GridLayoutManager(requireContext(),2);
        rcyAlbums.setLayoutManager(glm);

        FloatingActionButton addAlbm = v.findViewById(R.id.addAlbum);

        adapter = new AlbumsAdapter(this, requireContext(),mViewModel.getLlAlbums().getValue());
        rcyAlbums.setAdapter(adapter);
        addAlbm.setOnClickListener(v1 -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_homeFragment_to_crearAlbumFragment);
        });
        mViewModel.getLlAlbums().observe(getViewLifecycleOwner(), new Observer<List<Album>>() {
            @Override
            public void onChanged(List<Album> albums) {
                Log.d("XXX",albums.get(albums.size()-1).toString());
                updateAlbumsList(albums);
            }
        });
        return v;
    }
    private void updateAlbumsList(List<Album> albums) {
        adapter.setLlAlbums(albums);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAlbumSelected(Album a) {
        mViewModel.setAlbumSelected(a);
        NavController nav = NavHostFragment.findNavController(this);
        nav.navigate(R.id.action_homeFragment_to_songsListFragment);
    }

    @Override
    public void onLongAlbumSelected(Album a) {
        mViewModel.setAlbumSelected(a);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_options, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageButton editOption = bottomSheetView.findViewById(R.id.editOption);
        ImageButton deleteOption = bottomSheetView.findViewById(R.id.deleteOption);

        editOption.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_homeFragment_to_crearAlbumFragment);
            bottomSheetDialog.dismiss();
        });

        deleteOption.setOnClickListener(v -> {
            mViewModel.deleteAlbum(a);
            adapter.setSelectedPosition(-1);
            adapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}