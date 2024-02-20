package org.milaifontanals.musicappv2.view;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.milaifontanals.musicappv2.R;
import org.milaifontanals.musicappv2.model.Album;
import org.milaifontanals.musicappv2.utils.MbidGenerator;
import org.milaifontanals.musicappv2.viewmodel.MainViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CrearAlbumFragment extends Fragment {

    private Button btnDate;
    private FloatingActionButton saveBtn;
    private EditText tvDate;
    private EditText edtArtist;
    private EditText edtAlbm;
    private Calendar cl;
    private ImageView imgArtist;
    private MainViewModel mViewModel;
    private boolean isEditMode = false;
    private Album albumToEdit;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri imageUri;

    public CrearAlbumFragment() {

    }

    public static CrearAlbumFragment newInstance(String param1, String param2) {
        CrearAlbumFragment fragment = new CrearAlbumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        albumToEdit = mViewModel.getAlbumSelected().getValue();
        isEditMode = (albumToEdit != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_crear_album, container, false);

        imgArtist = vi.findViewById(R.id.imgArtist);
        saveBtn = vi.findViewById(R.id.saveAlbum);
        edtArtist = vi.findViewById(R.id.nomArtist);
        edtAlbm = vi.findViewById(R.id.nomAlbm);
        btnDate = vi.findViewById(R.id.btnDate);
        tvDate = vi.findViewById(R.id.edtData);
        cl = Calendar.getInstance();
        imgArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickeDialog();
            }
        });

        saveBtn.setOnClickListener(v -> {
            String nomAlbum = edtAlbm.getText().toString();
            String nomArtist = edtArtist.getText().toString();
            String data = tvDate.getText().toString();

            if (nomAlbum.isEmpty() || nomArtist.isEmpty() || data.isEmpty()) {
                showSnackbar(v, "Please fill in all fields");
            } else {
                int year = Integer.parseInt(data.substring(6));

                if (year > Calendar.getInstance().get(Calendar.YEAR)) {
                    showSnackbar(v, "Invalid date. Please select a past or current date.");
                } else {
                    String imgUrl = imageUri.toString();

                    if (isEditMode) {
                        albumToEdit.setArtist(nomArtist);
                        albumToEdit.setName(nomAlbum);
                        albumToEdit.setYear(year);
                        albumToEdit.setUrlPhoto(imgUrl);
                        mViewModel.setAlbumSelected(null);
                    } else {
                        Album a = new Album(MbidGenerator.generateMbid(),nomArtist, nomAlbum, year, imgUrl);
                        mViewModel.addAlbum(a);
                    }

                    NavController nav = NavHostFragment.findNavController(this);
                    nav.navigateUp();
                }
            }
        });

        if (isEditMode && albumToEdit != null) {
            loadAlbumData(albumToEdit);
        }
        return vi;
    }

    private void showSnackbar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    private void showDatePickeDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cl.set(Calendar.YEAR, year);
                cl.set(Calendar.MONTH, month);
                cl.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDate();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.datepicker_dark,
                dateSetListener,
                cl.get(Calendar.YEAR),
                cl.get(Calendar.MONTH),
                cl.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.show();
    }

    private void updateDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String selectedDate = simpleDateFormat.format(cl.getTime());
        tvDate.setText(selectedDate);
    }
    private void loadAlbumData(Album album) {
        edtArtist.setText(album.getArtist());
        edtAlbm.setText(album.getName());
        Glide.with(requireContext())
                .load(album.getUrlPhoto())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(imgArtist);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(album.getYear());
        tvDate.setText(formattedDate);
    }
    private void showImagePickerOptions() {
        final CharSequence[] options = {"Take a photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose options:");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from gallery")) {
                    openGallery();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            loadImageFromUri(imageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadImageFromUri(imageUri);
        }
    }

    private void loadImageFromUri(Uri uri) {
        Glide.with(requireContext())
                .load(uri)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(imgArtist);
    }

}