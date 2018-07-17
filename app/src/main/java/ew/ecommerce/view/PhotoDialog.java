package ew.ecommerce.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ew.ecommerce.R;
import ew.ecommerce.utilities.Downloader;

public class PhotoDialog extends DialogFragment {

    public static final String URL = "url";

    private String url;

    private static PhotoDialog dialog;

    public static PhotoDialog newInstance(){
        dialog = new PhotoDialog();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(URL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.dialog_photo, null);
        ImageView myPhoto = v.findViewById(R.id.full_photo);
        new Downloader(myPhoto, false).execute(url);
        return v;
    }
}
