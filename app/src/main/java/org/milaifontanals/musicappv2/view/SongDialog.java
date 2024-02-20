package org.milaifontanals.musicappv2.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.model.Song;
import org.milaifontanals.musicappv2.viewmodel.MainViewModel;

public class SongDialog extends DialogFragment {
    private NumberPicker rank,min,sec;
    private EditText songName;
    private boolean isEditMode;
    private MainViewModel mainViewModel;
    private Song songToEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_song, null);
        rank = view.findViewById(R.id.songRankPicker);
        rank.setMinValue(0);
        rank.setMaxValue(100);
        min = view.findViewById(R.id.minPicker);
        min.setMinValue(0);
        min.setMaxValue(60);
        sec = view.findViewById(R.id.secPicker);
        sec.setMinValue(0);
        sec.setMaxValue(60);
        songName = view.findViewById(R.id.songName);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        songToEdit = mainViewModel.getSongSelected().getValue();
        isEditMode = (songToEdit != null);
        if (isEditMode && songToEdit != null) {
            loadSongData(songToEdit);
        }
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if ((songName.getText()+"").isEmpty() || (min.getValue()==0 && sec.getValue()==0)) {
                            showSnackbar(view, "Please fill in all fields");
                        }else {
                            if(isEditMode){
                                songToEdit.setName(songName.getText()+"");
                                songToEdit.setMin(min.getValue());
                                songToEdit.setNum(rank.getValue());
                                songToEdit.setSec(sec.getValue());
                                mainViewModel.setSongSelected(null);
                            }else{
                                int isFav = R.drawable.favborder;
                                Song s = new Song(rank.getValue(),songName.getText()+"",min.getValue(),sec.getValue(),isFav);
                                mainViewModel.addSongToAlbum(mainViewModel.getAlbumSelected().getValue(),s);
                                mainViewModel.setSongSelected(null);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SongDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void showSnackbar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    private void loadSongData(Song songToEdit) {
        rank.setValue(songToEdit.getNum());
        min.setValue(songToEdit.getMin());
        sec.setValue(songToEdit.getSec());
        songName.setText(songToEdit.getName());
    }
}
