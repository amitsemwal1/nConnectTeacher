package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nconnect.teacher.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.dialogfragment.DocumentViewDialog;
import com.nconnect.teacher.helper.DownloadManager;

public class DocumentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = DocumentsAdapter.class.getSimpleName();
    private Context context;
    private Activity mActivity;
    private LayoutInflater inflater;
    List<String> data = Collections.emptyList();
    private String sessionIdValue = "";

    public DocumentsAdapter(Context context, List<String> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
        mActivity = (Activity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.documents_item, parent, false);
        MyHolderDocuements docuements = new MyHolderDocuements(view);
        return docuements;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderDocuements docuementsHolder = (MyHolderDocuements) holder;
        if (data.size() != 0) {
            String name = data.get(position);
            String extension = name.substring(name.lastIndexOf("."));
//            Log.e(TAG, "Extension : " + extension);
            if (extension.equalsIgnoreCase(".pdf")) {
                Picasso.get().load(R.drawable.pdf_icon).error(R.drawable.pdf_icon).into(docuementsHolder.ivDocType);
            } else if (extension.equalsIgnoreCase(".ppt") || extension.equalsIgnoreCase(".pptx")) {
                Picasso.get().load(R.drawable.icon_ppt).error(R.drawable.icon_ppt).into(docuementsHolder.ivDocType);
            } else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx")) {
                Picasso.get().load(R.drawable.icon_xl).error(R.drawable.icon_xl).into(docuementsHolder.ivDocType);
            } else {
                Picasso.get().load(R.drawable.ncp_word).error(R.drawable.ncp_word).into(docuementsHolder.ivDocType);
            }
            docuementsHolder.tvDocumentName.setText(getName(data.get(position)));
            String type = getType(getType(data.get(position)));
            docuementsHolder.tvDocuementType.setText(type + " Document");
            docuementsHolder.tvDocumentSize.setText("");
            docuementsHolder.itemLayout.setTag(data.get(position));
        }

        docuementsHolder.ivDownlaodDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentData = data.get(position);
                Toast.makeText(mActivity, "Downloading...", Toast.LENGTH_LONG).show();
              /*  Intent intent = new Intent(mActivity, DownloadNotificationService.class);
                intent.putExtra("file_url", currentData);
                mActivity.startService(intent);*/
                new DownloadManager(currentData, mActivity, Constants.DOCUMENT);

            }
        });
    }

    private String getName(String url) {
        String name = "";
        name = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        return name;
    }

    private String getType(String url) {
        String type = "";
        type = url.substring(url.lastIndexOf(".") + 1);
        return type;
    }

    public class GetSize extends AsyncTask<String, String, String> {

        private int size = 0;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection conn = url.openConnection();
                conn.connect();
                size = conn.getContentLength();
//                Log.e(TAG, "url : " + strings[0] + "\n" + "size : " + size);
                conn.getInputStream().close();
                return String.valueOf(size);
            } catch (MalformedURLException e) {
//                Log.e(TAG, "exception : " + e);
            } catch (IOException e) {
//                Log.e(TAG, "Exception " + e);
            } catch (Exception e) {
//                Log.e(TAG, "Excpetion : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class MyHolderDocuements extends RecyclerView.ViewHolder {
        TextView tvDocumentName, tvDocuementType, tvDocumentSize;
        ImageView ivDownlaodDocument;
        LinearLayout itemLayout;
        ImageView ivDocType;

        public MyHolderDocuements(View itemView) {
            super(itemView);
            tvDocumentName = (TextView) itemView.findViewById(R.id.document_name);
            tvDocuementType = (TextView) itemView.findViewById(R.id.document_type);
            tvDocumentSize = (TextView) itemView.findViewById(R.id.document_size);
            ivDownlaodDocument = (ImageView) itemView.findViewById(R.id.download_document);
            ivDocType = (ImageView) itemView.findViewById(R.id.attachment_document_image);
            itemLayout = itemView.findViewById(R.id.itemLayout);

            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String url = view.getTag().toString();
                        String file_name = getName(url);
                        if (!url.equalsIgnoreCase("")) {
                            //check dialog is already opened or not
                            Fragment prev = mActivity.getFragmentManager().findFragmentByTag("doc");
                            if (prev == null) {
                                FragmentManager fragmentManager = mActivity.getFragmentManager();
                                DocumentViewDialog dialogFragment = DocumentViewDialog.newInstance(url, file_name);
                                dialogFragment.show(fragmentManager, "doc");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

