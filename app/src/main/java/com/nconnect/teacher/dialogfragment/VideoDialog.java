package com.nconnect.teacher.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import com.nconnect.teacher.R;
import com.nconnect.teacher.util.MyApplication;

public class VideoDialog extends DialogFragment {

    View view;
    String url;
    String file_name;
    //public static View loadingDialog;
    JzvdStd videoView;
    ImageView backButton;
    TextView titleView;


    public static VideoDialog newInstance(String url, String file_name) {

        VideoDialog audioDialog = new VideoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("file_name", file_name);
        audioDialog.setArguments(bundle);

        return audioDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        url = bundle.getString("url");
        file_name = bundle.getString("file_name");
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            videoView.release();
        } catch (Exception e) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_dialog, container, false);
        backButton = view.findViewById(R.id.backButton);
        titleView = view.findViewById(R.id.titleView);
        videoView = view.findViewById(R.id.videoView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        try {
            titleView.setText(file_name);
        } catch (Exception e) {
        }

        /*loadingDialog = Utils.initProgress(getActivity(), "Please wait . . .");
        loadingDialog.setVisibility(View.VISIBLE);
        loadingDialog.setClickable(false);*/

        //method to load document
        loadData();

        return view;
    }


    //method to load document
    void loadData() {

        try {

            if (!url.equalsIgnoreCase("")) {
                HttpProxyCacheServer proxy = MyApplication.getProxy(getActivity());
                String uri = proxy.getProxyUrl(url);
                videoView.setUp(uri, "", Jzvd.SCREEN_WINDOW_LIST);
                videoView.startButton.performClick();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        WindowManager.LayoutParams a = d.getWindow().getAttributes();
        a.dimAmount = 0;
        d.getWindow().setAttributes(a);

        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }
}