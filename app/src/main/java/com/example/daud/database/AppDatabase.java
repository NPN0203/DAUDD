package com.example.daud.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.model.User;

@Database(entities = {Article.class, Category.class, User.class}, version = 15, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ArticleDao articleDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();

    // Di cư từ bản gốc (10) lên bản mới nhất (16) để thêm bảng users và các cột cần thiết
    static final Migration MIGRATION_10_16 = new Migration(10, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Thêm bảng users nếu chưa có
            database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT, `password` TEXT, `fullName` TEXT, `isAdmin` INTEGER NOT NULL DEFAULT 0)");
            
            // Kiểm tra và thêm cột vào bảng articles nếu cần (để tránh lỗi khi thêm tính năng Lưu/Lịch sử)
            try {
                database.execSQL("ALTER TABLE articles ADD COLUMN isSaved INTEGER NOT NULL DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE articles ADD COLUMN lastReadTime INTEGER NOT NULL DEFAULT 0");
            } catch (Exception ignored) {}
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "daud_news_stable.db")
                            .createFromAsset("databases/daud_news_stable.db")
                            .addMigrations(MIGRATION_10_16)
                            .build();
                }
            }
        }
        return instance;
    }
}
