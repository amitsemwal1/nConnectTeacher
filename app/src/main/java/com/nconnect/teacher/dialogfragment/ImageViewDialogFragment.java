package com.nconnect.teacher.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import com.nconnect.teacher.R;

public class ImageViewDialogFragment extends DialogFragment {
    public String image_url;
    PhotoView imageView;
    ImageView closeButton;

    public static ImageViewDialogFragment newInstance(String image_url) {
        ImageViewDialogFragment imageViewDialogFragments = new ImageViewDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("image_url", image_url);
        imageViewDialogFragments.setArguments(bundle);
        return imageViewDialogFragments;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Bundle mArgs = getArguments();
        //image_url = mArgs.getString("image_url");
        image_url = mArgs.getString("image_url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.image_view_dialog, container, false);
        closeButton = view.findViewById(R.id.closeButton);
        imageView = view.findViewById(R.id.imageView);


        //listener to handle video back button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            Glide.with(getActivity())
                    .load(image_url)
                    .into(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        /*Dialog d = getDialog();
        WindowManager.LayoutParams a = d.getWindow().getAttributes();
        a.dimAmount = 0;
        d.getWindow().setAttributes(a);


        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}