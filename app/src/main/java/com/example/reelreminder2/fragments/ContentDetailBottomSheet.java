package com.example.reelreminder2.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.reelreminder2.R;
import com.example.reelreminder2.models.Content;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class ContentDetailBottomSheet extends DialogFragment {
    private Content content;
    private OnContentActionListener listener;

    public interface OnContentActionListener {
        void onWatchedStateChanged(Content content);
    }

    public static ContentDetailBottomSheet newInstance(Content content) {
        ContentDetailBottomSheet fragment = new ContentDetailBottomSheet();
        Bundle args = new Bundle();
        args.putLong("content_id", content.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void setOnContentActionListener(OnContentActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_content_detail, container, false);

        // Initialize views
        ImageView ivContentImage = view.findViewById(R.id.ivContentImage);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        Chip tvType = view.findViewById(R.id.tvType);
        Chip tvDuration = view.findViewById(R.id.tvDuration);
        Chip tvGenre = view.findViewById(R.id.tvGenre);
        Chip tvYear = view.findViewById(R.id.tvYear);
        TextView tvDetails = view.findViewById(R.id.tvDetails);
        MaterialButton btnWatched = view.findViewById(R.id.btnWatched);
        MaterialButton btnShare = view.findViewById(R.id.btnShare);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> dismiss());

        // Load content details
        if (content != null) {
            // Load image
            Glide.with(requireContext())
                .load(content.getImagePath())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivContentImage);

            // Set text views
            tvTitle.setText(content.getTitle());
            tvType.setText(content.getType());
            tvDuration.setText(String.format("%d min", content.getDuration()));
            tvGenre.setText(content.getGenre());
            tvYear.setText(String.valueOf(content.getYear()));

            // Show details if available
            if (content.getDetails() != null && !content.getDetails().isEmpty()) {
                tvDetails.setText(content.getDetails());
                tvDetails.setVisibility(View.VISIBLE);
            } else {
                tvDetails.setVisibility(View.GONE);
            }

            // Configure watched button
            updateWatchedButton(btnWatched);
            btnWatched.setOnClickListener(v -> {
                content.setWatched(!content.isWatched());
                updateWatchedButton(btnWatched);
                if (listener != null) {
                    listener.onWatchedStateChanged(content);
                }
            });

            // Configure share button
            btnShare.setOnClickListener(v -> {
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

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Compartir mediante"));
            });
        }

        return view;
    }

    private void updateWatchedButton(MaterialButton button) {
        button.setText(content.isWatched() ? "Visto" : "Marcar como visto");
    }
} 