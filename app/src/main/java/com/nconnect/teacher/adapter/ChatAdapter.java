package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.FullScreenImageActivity;
import com.nconnect.teacher.dialogfragment.AudioDialog;
import com.nconnect.teacher.dialogfragment.DocumentViewDialog;
import com.nconnect.teacher.dialogfragment.VideoDialog;
import com.nconnect.teacher.helper.CircularImageView;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final String TAG = ChatAdapter.class.getSimpleName();
    public static ArrayList<ChatMessage> mChatList = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    Activity mContext;
    SeekBar tempSeekBar;
    ImageView tempPlayIcon;
    private int deviceWidth;
    private int deviceHeight;
    MediaMetadataRetriever retriever;
    public HashMap<Integer, ImageItem> bitmap_map = new HashMap<>();

    public ChatAdapter(Activity context, ArrayList<ChatMessage> msgList) {
        mContext = context;
        mChatList = msgList;
        bitmap_map.clear();

        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        //float density  = context.getResources().getDisplayMetrics().density;
        deviceHeight = Math.round(outMetrics.heightPixels);
        deviceWidth = Math.round(outMetrics.widthPixels);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        retriever = new MediaMetadataRetriever();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbubble, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = mChatList.get(position);
        holder.mBubbleTextView.setText(message.getBody());
        String file_url = message.getFile_url();
        holder.imageView.setVisibility(View.GONE);
        holder.videoIcon.setVisibility(View.GONE);
        holder.documentView.setVisibility(View.GONE);
        holder.seekbarLayout.setVisibility(View.GONE);
        holder.mBubbleTextView.setVisibility(View.VISIBLE);
        holder.imageLoading.setVisibility(View.GONE);
        holder.playIcon.setVisibility(View.GONE);
        holder.videoLoadingIcon.setVisibility(View.GONE);

       /* LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(700,
                LinearLayout.LayoutParams.WRAP_CONTENT);*/ // or set height to any fixed value you want
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams bubble_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams doc_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) holder.documentView.getLayoutParams();
        ViewGroup.MarginLayoutParams imageMarginParams = (ViewGroup.MarginLayoutParams) holder.imageLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams videoMarginParams = (ViewGroup.MarginLayoutParams) holder.videoLayout.getLayoutParams();

        String time = "";
        try {
            String date = Utils.convertUtcToLocalTime(message.getDate());
            time = Utils.getFormatDate(date, Constants.DATE_HOURS_MINI_FORMAT, Constants.HH_MM);

        } catch (Exception e) {
        }
        if (message.isMine()) {

            holder.profileView.setVisibility(View.GONE);
            holder.timeLayout.setGravity(Gravity.RIGHT);
            holder.mBubbleTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.mBubbleParentLinearLayout.setGravity(Gravity.RIGHT);
            holder.mBubbleLinearLayout.setBackgroundResource(R.drawable.purple_background);
            //holder.seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);

            doc_params.setMargins(0, 0, 0, 0);
            params.setMargins(0, 0, 0, 0);
            bubble_params.setMargins(0, 0, 0, 0);
            cardViewMarginParams.setMargins(0, 0, 0, 0);
            imageMarginParams.setMargins(0, 0, 0, 0);
            videoMarginParams.setMargins(0, 0, 0, 0);

        } else {
            /*lp = new LinearLayout.LayoutParams(750,
                    LinearLayout.LayoutParams.WRAP_CONTENT);*/
            holder.profileView.setVisibility(View.VISIBLE);
            holder.mBubbleTextView.setTextColor(mContext.getResources().getColor(R.color.chat_time));
            holder.mBubbleLinearLayout.setBackgroundResource(R.drawable.chatbubble);
            //holder.mBubbleLinearLayout.setBackgroundResource(R.drawable.orange_background);
            holder.mBubbleParentLinearLayout.setGravity(Gravity.LEFT);
            holder.timeLayout.setGravity(Gravity.LEFT);
            //holder.seekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            params.setMargins(35, 0, 0, 0);
            bubble_params.setMargins(70, 2, 0, 0);
            doc_params.setMargins(75, 2, 0, 0);
            cardViewMarginParams.setMargins(55, 0, 0, 0);
            imageMarginParams.setMargins(55, 0, 0, 0);
            videoMarginParams.setMargins(55, 0, 0, 0);
        }
        holder.mBubbleTextView.setLayoutParams(params);
        holder.bubbleRelativeLayout.setLayoutParams(bubble_params);
        holder.tvTime.setLayoutParams(params);

        holder.documentView.requestLayout();
        holder.imageLayout.requestLayout();
        holder.videoLayout.requestLayout();

        //holder.documentView.setLayoutParams(doc_params);
        //holder.imageLayout.setLayoutParams(doc_params);
        //holder.videoLayout.setLayoutParams(doc_params);

        //holder.mBubbleLinearLayout.setLayoutParams(lp);

        if (!file_url.equalsIgnoreCase("")) {

            holder.mBubbleTextView.setVisibility(View.GONE);
            String type = file_url.substring(file_url.lastIndexOf(".") + 1);

            String file_name = message.getFile_name();
            holder.mBubbleTextView.setText(file_name);
            if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("png")) {

                holder.mBubbleLinearLayout.setBackgroundResource(0);
                //holder.mBubbleLinearLayout.setPadding(0, 0, 0, 0);

                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageLayout.setTag(message);
                holder.imageLoading.setVisibility(View.VISIBLE);
                /*holder.imageLoading.getIndeterminateDrawable()
                        .setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);*/
                Glide.with(mContext)
                        .load(file_url)
                        .thumbnail(0.1f)
                        //.apply(new RequestOptions().override(300, 400))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //loadingDialog.setVisibility(View.GONE);
                                //holder.imageView.setVisibility(View.VISIBLE);
                                holder.imageLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //loadingDialog.setVisibility(View.GONE);
                                //holder.imageView.setVisibility(View.VISIBLE);
                                holder.imageLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.imageView);

            } else if (type.equalsIgnoreCase("mp4")) {

                Boolean hasThumbnail = false;

                if (bitmap_map == null) {
                    bitmap_map = new HashMap<>();
                }
                if (bitmap_map.size() > 0) {
                    ImageItem imageItem = bitmap_map.get(position);
                    if (imageItem != null && imageItem.img != null) {
                        holder.videoIcon.setImageBitmap(imageItem.img);
                        hasThumbnail = true;

                        holder.playIcon.setVisibility(View.VISIBLE);
                        holder.videoLoadingIcon.setVisibility(View.GONE);
                    }
                }

                if (!hasThumbnail && holder.mBubbleLinearLayout.getTag() != null) {
                    Object object = holder.mBubbleLinearLayout.getTag();
                    if (object instanceof Bitmap) {
                        holder.videoIcon.setImageBitmap((Bitmap) object);
                        holder.videoIcon.setTag((Bitmap) object);
                        hasThumbnail = true;

                        holder.playIcon.setVisibility(View.VISIBLE);
                        holder.videoLoadingIcon.setVisibility(View.GONE);
                    }
                }

                /*if (holder.mBubbleLinearLayout.getTag() != null) {
                    Object object = holder.mBubbleLinearLayout.getTag();
                    if (object instanceof Bitmap) {
                        holder.videoIcon.setImageBitmap((Bitmap) object);
                        hasThumbnail = true;

                        holder.playIcon.setVisibility(View.VISIBLE);
                        holder.videoLoadingIcon.setVisibility(View.GONE);
                    }
                }*/

                if (!hasThumbnail) {
                    new VideoViewTask(holder.videoIcon, holder.videoLoadingIcon, holder.playIcon, holder.mBubbleLinearLayout, file_url, position).execute();
                /*try{
                    retriever.setDataSource(file_url, new HashMap<String, String>());
                    Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    holder.videoIcon.setImageBitmap(image);
                    holder.mBubbleLinearLayout.setBackgroundResource(0);
                }catch (Exception e){}*/
                    holder.playIcon.setVisibility(View.GONE);
                    holder.videoLoadingIcon.setVisibility(View.VISIBLE);
                }

                holder.mBubbleLinearLayout.setBackgroundResource(R.drawable.roundgray);
                holder.videoIcon.setVisibility(View.VISIBLE);
                holder.videoIcon.setTag(position);

                //holder.videoView.setUp(file_url, "", Jzvd.SCREEN_WINDOW_LIST);

            } else if (type.equalsIgnoreCase("mp3")) {

                holder.seekbarLayout.setVisibility(View.VISIBLE);

                /*HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("file_url", file_url);
                hashMap.put("seekBarView", holder.seekBar);*/
                holder.audioIcon.setTag(position);

            } else {
                //holder.mBubbleLinearLayout.setBackgroundResource(R.drawable.roundgray);
                String extension = file_url.substring(file_url.lastIndexOf("."));

                if (extension.equalsIgnoreCase(".pdf")) {
                    holder.docIconView.setBackgroundResource(R.drawable.pdf_icon);
                } else if (extension.equalsIgnoreCase(".ppt") || extension.equalsIgnoreCase(".pptx")) {
                    holder.docIconView.setBackgroundResource(R.drawable.icon_ppt);
                } else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx")) {
                    holder.docIconView.setBackgroundResource(R.drawable.icon_xl);
                } else {
                    holder.docIconView.setBackgroundResource(R.drawable.ncp_word);
                }
                holder.fileNameView.setText(message.getFile_name());
                holder.documentView.setVisibility(View.VISIBLE);
                holder.documentView.setTag(position);
                holder.fileSizeView.setText(message.getFile_size());
            }

        }

        holder.tvTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public void add(ChatMessage chatMessage) {
        mChatList.add(chatMessage);
    }

    public void clear() {
        try {
            mChatList.clear();
        } catch (Exception e) {
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        LinearLayout timeLayout;
        LinearLayout mBubbleParentLinearLayout;
        LinearLayout mBubbleLinearLayout;
        LinearLayout seekbarLayout;
        TextView mBubbleTextView;
        TextView tvTime;
        ImageView imageView;
        View documentView;
        ImageView videoIcon;
        ImageView audioIcon;
        ProgressBar imageLoading;
        ProgressBar videoLoadingIcon;
        View imageLayout;
        CircularImageView profileView;
        ImageView playIcon;
        ImageView docIconView;
        TextView fileNameView;
        TextView fileSizeView;
        RelativeLayout bubbleRelativeLayout;
        View videoLayout;


        public ChatViewHolder(View itemView) {

            super(itemView);
            mBubbleParentLinearLayout = (LinearLayout) itemView.findViewById(R.id.bubble_layout_parent);
            mBubbleLinearLayout = (LinearLayout) itemView.findViewById(R.id.bubble_layout);
            mBubbleTextView = (TextView) itemView.findViewById(R.id.message_text);
            tvTime = (TextView) itemView.findViewById(R.id.time);
            timeLayout = (LinearLayout) itemView.findViewById(R.id.timeLayout);
            profileView = itemView.findViewById(R.id.profileView);
            playIcon = itemView.findViewById(R.id.playIcon);
            fileNameView = itemView.findViewById(R.id.fileNameView);
            docIconView = itemView.findViewById(R.id.docIconView);
            fileSizeView = itemView.findViewById(R.id.fileSizeView);
            videoLoadingIcon = itemView.findViewById(R.id.videoLoadingIcon);


            imageView = itemView.findViewById(R.id.imageView);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            documentView = itemView.findViewById(R.id.documentView);
            //seekBar = itemView.findViewById(R.id.seekBar);
            seekbarLayout = itemView.findViewById(R.id.seekbarLayout);
            //playIcon = itemView.findViewById(R.id.playIcon);
            audioIcon = itemView.findViewById(R.id.audioIcon);
            imageLoading = itemView.findViewById(R.id.imageLoading);
            imageLayout = itemView.findViewById(R.id.imageLayout);
            bubbleRelativeLayout = itemView.findViewById(R.id.bubbleRelativeLayout);
            videoLayout = itemView.findViewById(R.id.videoLayout);

            documentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        ChatMessage message = mChatList.get((int) view.getTag());

                        String url = message.getFile_url();
                        String file_name = message.getFile_name();
                        if (!url.equalsIgnoreCase("")) {

                            //check dialog is already opened or not
                            Fragment prev = mContext.getFragmentManager().findFragmentByTag("doc");
                            if (prev == null) {
                                FragmentManager fragmentManager = mContext.getFragmentManager();
                                DocumentViewDialog dialogFragment = DocumentViewDialog.newInstance(url, file_name);
                                dialogFragment.show(fragmentManager, "doc");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            videoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        ChatMessage message = mChatList.get((int) view.getTag());

                        String url = message.getFile_url();
                        String file_name = message.getFile_name();

                        if (!url.equalsIgnoreCase("")) {

                            //check dialog is already opened or not
                            Fragment prev = mContext.getFragmentManager().findFragmentByTag("video");
                            if (prev == null) {
                                FragmentManager fragmentManager = mContext.getFragmentManager();
                                VideoDialog dialogFragment = VideoDialog.newInstance(url, file_name);
                                dialogFragment.show(fragmentManager, "video");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            audioIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        try {

                            ChatMessage message = mChatList.get((int) view.getTag());

                            String url = message.getFile_url();
                            String file_name = message.getFile_name();

                            if (!url.equalsIgnoreCase("")) {

                                //check dialog is already opened or not
                                Fragment prev = mContext.getFragmentManager().findFragmentByTag("audio");
                                if (prev == null) {
                                    FragmentManager fragmentManager = mContext.getFragmentManager();
                                    AudioDialog dialogFragment = AudioDialog.newInstance(url, file_name);
                                    dialogFragment.show(fragmentManager, "audio");
                                }

                                //FileOpen.openFile(mContext, new File(url.trim()));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            imageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        try {

                            ChatMessage message = (ChatMessage) view.getTag();

                            String url = message.getFile_url();

                            if (!url.equalsIgnoreCase("")) {

                                Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                                intent.putExtra("image_url", url);
                                mContext.startActivity(intent);

                                //check dialog is already opened or not
                                /*Fragment prev = mContext.getFragmentManager().findFragmentByTag("audio");
                                if (prev == null) {
                                    FragmentManager fragmentManager = mContext.getFragmentManager();
                                    ImageViewDialogFragment dialogFragment = ImageViewDialogFragment.newInstance(url);
                                    dialogFragment.show(fragmentManager, "audio");
                                }*/

                                //FileOpen.openFile(mContext, new File(url.trim()));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    class VideoViewTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        ImageView playIcon;
        ImageView videoIcon;
        LinearLayout linearLayout;
        String file_url;
        ProgressBar videoLoadingIcon;
        int position;

        public VideoViewTask(ImageView videoIcon, ProgressBar videoLoadingIcon, ImageView playIcon,
                             LinearLayout linearLayout, String file_url, int position) {
            this.videoIcon = videoIcon;
            this.playIcon = playIcon;
            this.linearLayout = linearLayout;
            this.file_url = file_url;
            this.videoLoadingIcon = videoLoadingIcon;
            this.position = position;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... arg0) {

            try {
                retriever.setDataSource(file_url, new HashMap<String, String>());
                Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                ImageItem imageItem = new ImageItem();
                imageItem.id = position;
                imageItem.img = image;
                bitmap_map.put(position, imageItem);

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        videoIcon.setImageBitmap(image);
                        linearLayout.setTag(image);
                        linearLayout.setBackgroundResource(0);
                        videoIcon.setBackgroundResource(R.drawable.rounded_corner);
                        videoLoadingIcon.setVisibility(View.GONE);
                        playIcon.setVisibility(View.VISIBLE);
                    }
                });
               /* retriever.setDataSource(file_url, new HashMap<String, String>());
                Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        videoIcon.setImageBitmap(image);
                        linearLayout.setTag(image);
                        linearLayout.setBackgroundResource(0);
                        videoIcon.setBackgroundResource(R.drawable.rounded_corner);
                        videoLoadingIcon.setVisibility(View.GONE);
                        playIcon.setVisibility(View.VISIBLE);
                    }
                });*/
                /*Bitmap image = Utils.createThumbnailFromPath(file_url, MINI_KIND);
                Glide.with(mContext).load(image).into(videoIcon);*/
            } catch (Exception e) {
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class ImageItem {
        boolean selection;
        int id;
        Bitmap img;
    }
}