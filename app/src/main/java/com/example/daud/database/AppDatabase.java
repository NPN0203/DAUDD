package com.example.daud.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.daud.model.Article;
import com.example.daud.model.Category;

@Database(entities = {Article.class, Category.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ArticleDao articleDao();
    public abstract CategoryDao categoryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "news_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
