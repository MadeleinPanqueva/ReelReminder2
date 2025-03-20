package com.example.reelreminder2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.R;
import com.example.reelreminder2.models.Content;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private List<Content> contentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Content content);
    }

    public ContentAdapter(List<Content> contentList, OnItemClickListener listener) {
        this.contentList = contentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Content content = contentList.get(position);
        holder.bind(content, listener);
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public void updateContent(List<Content> newContent) {
        this.contentList = new ArrayList<>(newContent);
        notifyDataSetChanged();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivContentImage;
        private final TextView tvTitle;
        private final TextView tvType;
        private final TextView tvDuration;
        private final TextView tvGenre;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvGenre = itemView.findViewById(R.id.tvGenre);
        }

        public void bind(final Content content, final OnItemClickListener listener) {
            tvTitle.setText(content.getTitle());
            tvType.setText(content.getType());
            tvDuration.setText(String.format("%d min", content.getDuration()));
            tvGenre.setText(content.getGenre());

            if (content.getImagePath() != null) {
                // TODO: Load image using Glide or Picasso
                // For now, we'll use a placeholder
                ivContentImage.setImageResource(R.drawable.ic_image_placeholder);
            } else {
                ivContentImage.setImageResource(R.drawable.ic_image_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(content);
                }
            });
        }
    }
} 