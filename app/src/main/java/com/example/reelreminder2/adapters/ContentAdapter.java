package com.example.reelreminder2.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reelreminder2.R;
import com.example.reelreminder2.models.Content;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private List<Content> contentList;
    private final OnItemClickListener listener;

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
        holder.tvTitle.setText(content.getTitle());
        holder.tvType.setText(content.getType());
        holder.tvDuration.setText(String.valueOf(content.getDuration()) + " min");
        holder.tvGenre.setText(content.getGenre());
        holder.tvYear.setText(String.valueOf(content.getYear()));

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
            .load(content.getImagePath())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(holder.ivContentImage);

        // Configurar el botón "Visto"
        holder.btnWatched.setText(content.isWatched() ? "Visto" : "Marcar como visto");
        holder.btnWatched.setOnClickListener(v -> {
            content.setWatched(!content.isWatched());
            notifyItemChanged(holder.getAdapterPosition());
        });

        // Configurar el botón "Compartir"
        holder.btnShare.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            
            String shareMessage = String.format(
                "¡Te recomiendo ver %s!\n\n" +
                "Tipo: %s\n" +
                "Género: %s\n" +
                "Duración: %d minutos\n" +
                "Año: %d\n\n" +
                "Compartido desde ReelReminder",
                content.getTitle(),
                content.getType(),
                content.getGenre(),
                content.getDuration(),
                content.getYear()
            );
            
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Compartir mediante"));
        });

        // Configurar el botón "Eliminar"
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                showDeleteConfirmationDialog(
                    holder.itemView.getContext(),
                    content,
                    currentPosition
                );
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(content));
    }

    private void showDeleteConfirmationDialog(Context context, Content content, int position) {
        new AlertDialog.Builder(context)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro que deseas eliminar \"" + content.getTitle() + "\"?")
            .setPositiveButton("Eliminar", (dialog, which) -> {
                contentList.remove(position);
                notifyItemRemoved(position);
            })
            .setNegativeButton("Cancelar", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
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
        MaterialButton btnWatched;
        MaterialButton btnShare;
        ImageButton btnDelete;

        ContentViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvYear = itemView.findViewById(R.id.tvYear);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            btnWatched = itemView.findViewById(R.id.btnWatched);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 