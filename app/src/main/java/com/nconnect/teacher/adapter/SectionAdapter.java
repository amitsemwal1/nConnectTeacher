package com.nconnect.teacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.model.gradeandsection.Section;

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private LayoutInflater inflater;
    List<Section> data = Collections.emptyList();
    private String sessionIdValue = "";

    public SectionAdapter(Context context, List<Section> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_section_item, parent, false);
        MyHolderSection holderSection = new MyHolderSection(view);
        return holderSection;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderSection holderSection = (MyHolderSection) holder;
        holderSection.section = data.get(position);
        holderSection.tvSectionName.setText(holderSection.section.getSectionName());
        holderSection.tvSectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holderSection.tvSectionName.isChecked()) {
                    holderSection.tvSectionName.setChecked(false);
                    holderSection.section.setStatus(false);
                    holderSection.section.setSectionId(data.get(position).getSectionId());
                    holderSection.section.setSectionName(data.get(position).getSectionName());
                } else {
                    holderSection.section.setStatus(true);
                    holderSection.tvSectionName.setChecked(true);
                    holderSection.section.setStatus(true);
                    holderSection.section.setSectionId(data.get(position).getSectionId());
                    holderSection.section.setSectionName(data.get(position).getSectionName());
                }
            }
        });

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

    class MyHolderSection extends RecyclerView.ViewHolder {

        private CheckedTextView tvSectionName;
        Section section;

        public MyHolderSection(@NonNull View itemView) {
            super(itemView);
            tvSectionName = (CheckedTextView) itemView.findViewById(R.id.sectionName);
        }
    }

}
