package com.example.reelreminder2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.R;
import com.example.reelreminder2.models.Content;
import com.example.reelreminder2.DatabaseHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private List<Content> contentList;
    private final OnItemClickListener listener;
    private final DatabaseHelper dbHelper;

    public interface OnItemClickListener {
        void onItemClick(Content content);
    }

    public ContentAdapter(List<Content> contentList, OnItemClickListener listener, DatabaseHelper dbHelper) {
        this.contentList = contentList;
        this.listener = listener;
        this.dbHelper = dbHelper;
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
        holder.tvTitle.setText(content.getTitle());
        holder.tvType.setText(content.getType());
        holder.tvDuration.setText(String.valueOf(content.getDuration()));
        holder.tvGenre.setText(content.getGenre());
        holder.tvYear.setText(String.valueOf(content.getYear()));

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
            .load(content.getImagePath())
            .placeholder(R.drawable.placeholder_image) // Imagen por defecto mientras carga
            .error(R.drawable.error_image) // Imagen que se muestra si hay error
            .centerCrop()
            .into(holder.ivContentImage);

        // Configurar el botón "Visto"
        holder.btnWatched.setText(content.isWatched() ? "Visto" : "Marcar como visto");
        holder.btnWatched.setOnClickListener(v -> {
            content.setWatched(!content.isWatched());
            dbHelper.setContentWatched(content.getId(), content.isWatched());
            notifyItemChanged(holder.getAdapterPosition());
        });

        // Configurar el botón "Eliminar"
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Content contentToDelete = contentList.get(currentPosition);
                dbHelper.deleteContent(contentToDelete.getId());
                contentList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(content));
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
        TextView tvTitle;
        TextView tvType;
        TextView tvDuration;
        TextView tvGenre;
        TextView tvYear;
        ImageView ivContentImage;
        Button btnWatched;
        Button btnDelete;

        ContentViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvYear = itemView.findViewById(R.id.tvYear);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            btnWatched = itemView.findViewById(R.id.btnWatched);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 