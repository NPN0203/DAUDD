package com.example.daud.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "articles")
public class Article implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public static final int TYPE_ONE_IMAGE = 1;     // Tin 1 ảnh bên phải
    public static final int TYPE_THREE_IMAGES = 2;   // Tin 3 ảnh ngang
    public static final int TYPE_BIG_IMAGE = 3;      // Tin ảnh lớn/Quảng cáo
    public static final int TYPE_VIDEO = 4;          // Tin Video

    private String title;
    private String source;
    private String timeOrComment;
    private List<String> images;
    private int viewType;
    private String content;

    public Article(String title, String source, String timeOrComment, List<String> images, int viewType, String content) {
        this.title = title;
        this.source = source;
        this.timeOrComment = timeOrComment;
        this.images = images;
        this.viewType = viewType;
        this.content = content;
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
}
