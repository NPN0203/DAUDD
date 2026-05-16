package com.example.daud.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.daud.NewsDetailActivity;
import com.example.daud.R;
import com.example.daud.model.Article;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> articleList;
    private boolean isNightMode = false;

    public NewsAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return articleList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Article.TYPE_THREE_IMAGES) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_three_images, parent, false);
            return new ThreeImagesViewHolder(view);
        } else if (viewType == Article.TYPE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_big_image, parent, false);
            return new BigImageViewHolder(view);
        } else if (viewType == Article.TYPE_VIDEO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_video, parent, false);
            return new VideoViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_one_image, parent, false);
            return new OneImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = articleList.get(position);
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        int bgColor = isNightMode ? Color.parseColor("#121212") : Color.WHITE;

        holder.itemView.setBackgroundColor(bgColor);

        if (holder instanceof OneImageViewHolder) {
            OneImageViewHolder h = (OneImageViewHolder) holder;
            h.tvTitle.setText(article.getTitle());
            h.tvTitle.setTextColor(textColor);
            h.tvInfo.setText(article.getSource() + " | " + article.getTimeOrComment());
            if (article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(h.itemView.getContext()).load(article.getImages().get(0)).into(h.ivThumbnail);
            }
        } else if (holder instanceof ThreeImagesViewHolder) {
            ThreeImagesViewHolder h = (ThreeImagesViewHolder) holder;
            h.tvTitle.setText(article.getTitle());
            h.tvTitle.setTextColor(textColor);
            h.tvInfo.setText(article.getSource() + " | " + article.getTimeOrComment());
            if (article.getImages() != null && article.getImages().size() >= 3) {
                Glide.with(h.itemView.getContext()).load(article.getImages().get(0)).into(h.ivImage1);
                Glide.with(h.itemView.getContext()).load(article.getImages().get(1)).into(h.ivImage2);
                Glide.with(h.itemView.getContext()).load(article.getImages().get(2)).into(h.ivImage3);
            }
        } else if (holder instanceof BigImageViewHolder) {
            BigImageViewHolder h = (BigImageViewHolder) holder;
            h.tvTitle.setText(article.getTitle());
            h.tvTitle.setTextColor(textColor);
            h.tvInfo.setText(article.getSource() + " | " + article.getTimeOrComment());
            if (article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(h.itemView.getContext()).load(article.getImages().get(0)).into(h.ivBigImage);
            }
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder h = (VideoViewHolder) holder;
            h.tvTitle.setText(article.getTitle());
            h.tvTitle.setTextColor(textColor);
            h.tvInfo.setText(article.getSource() + " | " + article.getTimeOrComment());
            if (article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(h.itemView.getContext()).load(article.getImages().get(0)).into(h.ivVideoThumbnail);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("nightMode", isNightMode);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    static class OneImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo;
        ImageView ivThumbnail;
        public OneImageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }

    static class ThreeImagesViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo;
        ImageView ivImage1, ivImage2, ivImage3;
        public ThreeImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivImage1 = itemView.findViewById(R.id.ivImage1);
            ivImage2 = itemView.findViewById(R.id.ivImage2);
            ivImage3 = itemView.findViewById(R.id.ivImage3);
        }
    }

    static class BigImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo;
        ImageView ivBigImage;
        public BigImageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivBigImage = itemView.findViewById(R.id.ivBigImage);
        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo;
        ImageView ivVideoThumbnail;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivVideoThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
        }
    }
}
