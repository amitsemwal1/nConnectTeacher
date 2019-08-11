package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.DashBoardModel;

public class DashBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DashBoardModel> data = Collections.emptyList();

    public DashBoardAdapter(Context context, List<DashBoardModel> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dashboard_item, parent, false);
        MyHolderDashBoard dashBoard = new MyHolderDashBoard(view);
        return dashBoard;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderDashBoard holderDashBoard = (MyHolderDashBoard) holder;
        DashBoardModel model = data.get(position);
        holderDashBoard.tvNAme.setText(model.getDashBoardName());
        Picasso.get().load(model.getDrawableImage()).error(model.getDrawableImage()).into(holderDashBoard.ivImage);

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

    class MyHolderDashBoard extends RecyclerView.ViewHolder {

        CardView cardContainer;
        TextView tvNAme;
        ImageView ivImage;

        public MyHolderDashBoard(View itemView) {
            super(itemView);
            cardContainer = (CardView) itemView.findViewById(R.id.cardContainer);
            tvNAme = (TextView) itemView.findViewById(R.id.cardTExt);
            ivImage = (ImageView) itemView.findViewById(R.id.cardImage);
        }
    }


}
