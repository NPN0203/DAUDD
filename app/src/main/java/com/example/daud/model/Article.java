package com.example.daud.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "articles")
public class Article implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public static final int TYPE_ONE_IMAGE = 1;
    public static final int TYPE_THREE_IMAGES = 2;
    public static final int TYPE_BIG_IMAGE = 3;
    public static final int TYPE_VIDEO = 4;

    private String title;
    private String source;
    private String timeOrComment;
    private List<String> images;
    private int viewType;
    private String content;
    private boolean isSaved;
    private int likes;
    private int dislikes;

    @ColumnInfo(name = "lastReadTime") // Đảm bảo Room tìm đúng cột này
    private long lastReadTime;

    public Article(String title, String source, String timeOrComment, List<String> images, int viewType, String content) {
        this.title = title;
        this.source = source;
        this.timeOrComment = timeOrComment;
        this.images = images;
        this.viewType = viewType;
        this.content = content;
        this.isSaved = false;
        this.lastReadTime = 0;
        this.likes = 0;
        this.dislikes = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getTimeOrComment() { return timeOrComment; }
    public List<String> getImages() { return images; }
    public int getViewType() { return viewType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public long getLastReadTime() { return lastReadTime; }
    public void setLastReadTime(long lastReadTime) { this.lastReadTime = lastReadTime; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
}
