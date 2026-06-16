package com.example.daud.adapter;

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

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.ViewHolder> {

    private List<Article> articleList;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Article article);
    }

    public AdminNewsAdapter(List<Article> articleList, OnDeleteClickListener deleteListener) {
        this.articleList = articleList;
        this.deleteListener = deleteListener;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvSource.setText(article.getSource());
        
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(article.getImages().get(0)).into(holder.ivThumb);
        } else {
            holder.ivThumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb, btnDelete;
        TextView tvTitle, tvSource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivAdminThumb);
            btnDelete = itemView.findViewById(R.id.btnDeleteNews);
            tvTitle = itemView.findViewById(R.id.tvAdminTitle);
            tvSource = itemView.findViewById(R.id.tvAdminSource);
        }
    }
}
