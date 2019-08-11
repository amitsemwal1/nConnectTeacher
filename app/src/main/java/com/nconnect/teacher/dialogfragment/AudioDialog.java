package com.nconnect.teacher.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import com.nconnect.teacher.R;

public class AudioDialog extends DialogFragment {

    View view;
    String url;
    String file_name;
    //public static View loadingDialog;

    SeekBar seekBar;
    ImageView playIcon;
    ImageView pauseIcon;
    int total_duration;
    TextView time_view;
    TextView closeView;
    TextView titleView;

    MediaPlayer mediaplayer;

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    private Handler handler;

    public static AudioDialog newInstance(String url, String file_name){

        AudioDialog audioDialog = new AudioDialog();
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

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        try{
            mediaplayer.reset();
        }catch (Exception e){}

    }

    /** The system calls this only when creating the layout in a dialog. */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audio_view, container, false);
        //backButton = view.findViewById(R.id.backButton);
        //titleView = view.findViewById(R.id.titleView);
        seekBar = view.findViewById(R.id.seekBar);
        playIcon = view.findViewById(R.id.playIcon);
        pauseIcon = view.findViewById(R.id.pauseIcon);
        time_view = view.findViewById(R.id.time_view);
        closeView = view.findViewById(R.id.closeView);
        titleView = view.findViewById(R.id.titleView);
        seekBar.getProgressDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);

        try{
            titleView.setText(file_name);
        }catch (Exception e){
        }

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mediaplayer.start();
                    playIcon.setVisibility(View.GONE);
                    pauseIcon.setVisibility(View.VISIBLE);
                }catch (Exception e){
                }
            }
        });
        pauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    mediaplayer.pause();
                    pauseIcon.setVisibility(View.GONE);
                    playIcon.setVisibility(View.VISIBLE);
                }catch (Exception e){
                }
            }
        });
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    mediaplayer.reset();
                    pauseIcon.setVisibility(View.GONE);
                    playIcon.setVisibility(View.VISIBLE);

                    dismiss();
                }catch (Exception e){
                }
            }
        });

        handler = new Handler();
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {

            mediaplayer.setDataSource(url);
            mediaplayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                total_duration = mediaplayer.getDuration();
                seekBar.setMax(total_duration);

//                time_view.setText(String.valueOf(mediaplayer.getDuration() / 1000));

                time_view.setText(ConvertMsToHour(total_duration));
                changeSeekbar();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaplayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                total_duration = mediaplayer.getCurrentPosition();

            }
        });
        return view;
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
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }
    private void changeSeekbar() {
        seekBar.setProgress(mediaplayer.getCurrentPosition());
        if(mediaplayer.isPlaying()){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if(total_duration > 0) {
                        total_duration = total_duration - 1000;
                        String time = ConvertMsToHour(total_duration);

                        time_view.setText(time);

                        changeSeekbar();
                    }
//                    else {
//                        total_duration = mediaplayer.getDuration();
//                        time_view.setText("0");
//                    }

                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private String ConvertMsToHour(long ms){
//        long ms = 10304004543l;
        StringBuffer text = new StringBuffer("");
//        if (ms > DAY) {
//            text.append(ms / DAY).append(":");
//            ms %= DAY;
//        }
        if (ms > HOUR) {
            long min = ms / HOUR;
            if(min < 10){
                text.append("0");
            }
            text.append(ms / HOUR).append(":");
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            long min = ms / MINUTE;
            if(min < 10){
                text.append("0");
            }
            text.append(ms / MINUTE).append(":");
            ms %= MINUTE;
        }
        if (ms < SECOND){
            text.append("00");
        }
        if (ms > SECOND) {
            long min = ms / SECOND;
            if(min < 10){
                text.append("0");
            }
            text.append(ms / SECOND);
            ms %= SECOND;
        }
//        text.append(ms);
        return  text.toString();
    }

}