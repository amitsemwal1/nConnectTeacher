package com.nconnect.teacher.helper;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.PostStoryActivity;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = BottomSheetFragment.class.getSimpleName();
    private ImageView ivCamera, ivVideo;
    private TextView tvTAG;

    public BottomSheetFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.intent_camera_video, container, false);
        intializeViews(view);
        return view;
    }

    public void intializeViews(View view) {
        tvTAG = (TextView) view.findViewById(R.id.intent_tag);
        int type = getArguments().getInt("type");
        if (type == 1) {
            tvTAG.setText("Select Camera or Video");
        } else {
            tvTAG.setText("Pick image or video");
        }

        ivCamera = (ImageView) view.findViewById(R.id.camera);
        ivVideo = (ImageView) view.findViewById(R.id.video);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().detach(BottomSheetFragment.this).commit();
                    ((PostStoryActivity) getActivity()).captureImage();

                } else {
                    dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().detach(BottomSheetFragment.this).commit();
                    ((PostStoryActivity) getActivity()).selectImage();

                }

            }
        });
        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().detach(BottomSheetFragment.this).commit();
                    ((PostStoryActivity) getActivity()).captureVideo();
                } else {
                    dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().detach(BottomSheetFragment.this).commit();
                    ((PostStoryActivity) getActivity()).selectVideo();
                }

            }
        });
    }

}
