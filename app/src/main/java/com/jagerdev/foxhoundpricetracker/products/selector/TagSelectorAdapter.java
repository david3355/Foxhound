package com.jagerdev.foxhoundpricetracker.products.selector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jagerdev.foxhoundpricetracker.R;

import java.util.ArrayList;
import java.util.List;


public class TagSelectorAdapter extends RecyclerView.Adapter<TagSelectorAdapter.TagViewHolder> {

    private List<Tag> items;
    private Context context;
    private TagSelectedListener tagSelectedListener;

    public TagSelectorAdapter(Context context, List<Tag> items, TagSelectedListener tagSelectedListeners) {
        this.items = new ArrayList<>(items);
        this.context = context;
        this.tagSelectedListener = tagSelectedListeners;
    }

    public void updateAdapterItems(List<Tag> newItems)
    {
        items.clear();
        items.addAll(newItems);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.tagName.setText(items.get(position).getTagName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tagName;
        private ImageView imgRemoveTag;
        private LinearLayout rootView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);

            tagName = itemView.findViewById(R.id.tag_name);
            imgRemoveTag = itemView.findViewById(R.id.img_remove_tag);
            rootView = itemView.findViewById(R.id.tag_root_view);
            tagName.setOnClickListener(this);
            imgRemoveTag.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.tag_name:
                    tagSelectedListener.tagSelected(items.get(getAdapterPosition()));
                    break;
                case R.id.img_remove_tag:
                    tagSelectedListener.tagRemoved(items.get(getAdapterPosition()));
                    break;
            }

        }
    }
}
