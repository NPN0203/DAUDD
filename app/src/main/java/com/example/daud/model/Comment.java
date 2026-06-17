package com.example.daud.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments")
public class Comment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int articleId;
    private int userId;
    private String username;
    private String content;
    private long timestamp;
    private int parentId; // 0 if it's a top-level comment
    private int likes;
    private int dislikes;

    public Comment(int articleId, int userId, String username, String content, long timestamp, int parentId) {
        this.articleId = articleId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.likes = 0;
        this.dislikes = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
}
