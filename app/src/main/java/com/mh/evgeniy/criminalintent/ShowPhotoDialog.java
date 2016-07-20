package com.mh.evgeniy.criminalintent;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mh.evgeniy.criminalintent.database.PictureUtils;

/**
 * Created by evgeniy on 7/20/16.
 */
public class ShowPhotoDialog  extends DialogFragment {

    private static final String ARG_URI="uri";

    ImageView mImageView;

    public static ShowPhotoDialog newInstance(String imageUri){
        Bundle args=new Bundle();
        args.putString(ARG_URI,imageUri);
        ShowPhotoDialog fragment=new ShowPhotoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_show_photo,null);

        String path= getArguments().getString(ARG_URI);
        mImageView=(ImageView)v.findViewById(R.id.show_photo_dialog_photo_view);

        //Bitmap bitmap= PictureUtils.getScaledBitmap(path,getActivity());
        /*Point size=new Point();

        getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        Bitmap bitmap= PictureUtils.getScaledBitmap(path,size.x-100,size.y-100);*/
        mImageView.setImageURI(Uri.parse(path));

        builder.setView(v);
        return builder.create();
    }

}
