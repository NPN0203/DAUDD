package com.example.daud.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.R;
import com.example.daud.model.Comment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private OnCommentInteractionListener listener;

    public interface OnCommentInteractionListener {
        void onLikeClick(Comment comment);
        void onDislikeClick(Comment comment);
        void onReplyClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList, OnCommentInteractionListener listener) {
        this.commentList = commentList;
        this.listener = listener;
    }

    public void setCommentList(List<Comment> newList) {
        this.commentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        
        holder.tvUsername.setText(comment.getUsername() != null ? comment.getUsername() : "Người dùng");
        holder.tvContent.setText(comment.getContent());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(comment.getTimestamp())));
        
        holder.tvLikes.setText(String.valueOf(comment.getLikes()));
        if (holder.tvDislikes != null) {
            holder.tvDislikes.setText(String.valueOf(comment.getDislikes()));
        }

        // Xử lý thụt lề cho câu trả lời
        if (comment.getParentId() != 0) {
            holder.mainContainer.setPadding(60, 16, 16, 16);
            holder.mainContainer.setBackgroundResource(R.drawable.bg_reply_comment);
        } else {
            holder.mainContainer.setPadding(16, 16, 16, 16);
            holder.mainContainer.setBackground(null);
        }

        holder.btnLike.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClick(comment);
        });
        
        if (holder.btnDislike != null) {
            holder.btnDislike.setOnClickListener(v -> {
                if (listener != null) listener.onDislikeClick(comment);
            });
        }
        
        holder.btnReply.setOnClickListener(v -> {
            if (listener != null) listener.onReplyClick(comment);
        });
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvContent, tvTime, tvLikes, tvDislikes, btnReply;
        View btnLike, btnDislike, mainContainer;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mainContainer = itemView.findViewById(R.id.commentMainContainer);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvTime = itemView.findViewById(R.id.tvCommentTime);
            tvLikes = itemView.findViewById(R.id.tvCommentLikes);
            tvDislikes = itemView.findViewById(R.id.tvCommentDislikes);
            btnReply = itemView.findViewById(R.id.btnReplyComment);
            btnLike = itemView.findViewById(R.id.btnLikeComment);
            btnDislike = itemView.findViewById(R.id.btnDislikeComment);
        }
    }
}
