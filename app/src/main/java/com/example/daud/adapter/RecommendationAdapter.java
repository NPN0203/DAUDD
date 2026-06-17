package com.example.daud.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.daud.R;
import com.example.daud.model.Article;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    private List<Article> articles;
    private OnItemClickListener listener;
    private boolean isNightMode = false;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public RecommendationAdapter(List<Article> articles, OnItemClickListener listener) {
        this.articles = articles;
        this.listener = listener;
    }

    public void setNightMode(boolean nightMode) {
        this.isNightMode = nightMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvTitle.setTextColor(isNightMode ? Color.WHITE : Color.WHITE); // Slider thường để chữ trắng trên nền ảnh
        
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(article.getImages().get(0)).into(holder.ivImage);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(article));
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivSliderImage);
            tvTitle = itemView.findViewById(R.id.tvSliderTitle);
        }
    }
}
