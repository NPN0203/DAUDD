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
import com.example.daud.model.Comment;
import com.example.daud.model.UserArticleInteraction;
import com.example.daud.model.Notification;

@Database(entities = {Article.class, Category.class, User.class, Comment.class, UserArticleInteraction.class, Notification.class}, version = 19, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ArticleDao articleDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract CommentDao commentDao();
    public abstract UserArticleInteractionDao userArticleInteractionDao();
    public abstract NotificationDao notificationDao();

    static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `articleId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `username` TEXT, `content` TEXT, `timestamp` INTEGER NOT NULL, `parentId` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `dislikes` INTEGER NOT NULL)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `user_article_interactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `articleId` INTEGER NOT NULL, `isSaved` INTEGER NOT NULL, `lastReadTime` INTEGER NOT NULL, `isLiked` INTEGER NOT NULL, `isDisliked` INTEGER NOT NULL)");
        }
    };

    static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `title` TEXT, `message` TEXT, `timestamp` INTEGER NOT NULL, `isRead` INTEGER NOT NULL)");
        }
    };

    static final Migration MIGRATION_18_19 = new Migration(18, 19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE articles ADD COLUMN likes INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE articles ADD COLUMN dislikes INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "daud_news_stable.db")
                            .addMigrations(MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
