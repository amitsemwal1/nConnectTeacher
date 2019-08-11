package com.nconnect.teacher.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nconnect.teacher.R;
import com.nconnect.teacher.util.AppWebViewClients;

public class DocumentViewDialog extends DialogFragment {

    View view;
    String url;
    String file_name;
    WebView webView;
    ImageView backButton;
    TextView titleView;
    public static View loadingDialog;

    public static DocumentViewDialog newInstance(String url, String file_name){

        DocumentViewDialog documentViewDialog = new DocumentViewDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("file_name", file_name);
        documentViewDialog.setArguments(bundle);

        return documentViewDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        url = bundle.getString("url");
        file_name = bundle.getString("file_name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.document_view, container, false);
        webView = view.findViewById(R.id.webView);
        backButton = view.findViewById(R.id.backButton);
        titleView = view.findViewById(R.id.titleView);


        titleView.setText(file_name);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        loadingDialog = view.findViewById(R.id.progress_layout);
        TextView progressText = view.findViewById(R.id.progressText);
        progressText.setText("Please wait . . .");
        loadingDialog.setVisibility(View.VISIBLE);
        loadingDialog.setClickable(false);

        //method to load document
        loadData();

        return view;
    }


    //method to load document
    void loadData(){

        try{

            if (!url.equalsIgnoreCase("")){

                //remove extension from url
                webView.setWebViewClient(new AppWebViewClients());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setInitialScale(1);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+url);

                loadingDialog.setVisibility(View.GONE);
            }

        }catch (Exception e){
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