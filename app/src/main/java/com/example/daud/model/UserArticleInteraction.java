package com.example.daud.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_article_interactions")
public class UserArticleInteraction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int articleId;
    private boolean isSaved;
    private long lastReadTime;
    private boolean isLiked;
    private boolean isDisliked;

    public UserArticleInteraction(int userId, int articleId) {
        this.userId = userId;
        this.articleId = articleId;
        this.isSaved = false;
        this.lastReadTime = 0;
        this.isLiked = false;
        this.isDisliked = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public long getLastReadTime() { return lastReadTime; }
    public void setLastReadTime(long lastReadTime) { this.lastReadTime = lastReadTime; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public boolean isDisliked() { return isDisliked; }
    public void setDisliked(boolean disliked) { isDisliked = disliked; }
}
