package com.example.daud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.daud.adapter.CommentAdapter;
import com.example.daud.adapter.RecommendationAdapter;
import com.example.daud.model.Article;
import com.example.daud.model.Comment;
import com.example.daud.model.UserArticleInteraction;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewsDetailActivity extends AppCompatActivity {

    private NewsViewModel viewModel;
    private Article article;
    private int currentUserId;
    private String currentUsername;
    private UserArticleInteraction interaction;

    private ImageView ivSaveDetail, ivLike, ivDislike;
    private TextView tvLikeCount, tvDislikeCount, tvNoComments;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private EditText etComment;
    private int replyingToId = 0;

    private ViewPager2 viewPagerSuggestions;
    private RecommendationAdapter suggestionAdapter;
    private List<Article> suggestionList = new ArrayList<>();
    private Timer timer;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);
        currentUsername = pref.getString("fullName", "Khách");
        viewModel.setUserId(currentUserId);

        Article articleFromIntent = (Article) getIntent().getSerializableExtra("article");
        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);

        initViews();
        setupNightMode(isNightMode);

        if (articleFromIntent != null) {
            this.article = articleFromIntent;
            displayArticle();
            
            viewModel.getArticleById(article.getId()).observe(this, updatedArticle -> {
                if (updatedArticle != null) {
                    this.article = updatedArticle;
                    tvLikeCount.setText(String.valueOf(article.getLikes()));
                    tvDislikeCount.setText(String.valueOf(article.getDislikes()));
                }
            });

            setupInteractions();
            setupComments();
            setupSuggestions(isNightMode);
        }
    }

    private void initViews() {
        ivSaveDetail = findViewById(R.id.ivSaveDetail);
        ivLike = findViewById(R.id.ivLike);
        ivDislike = findViewById(R.id.ivDislike);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvDislikeCount = findViewById(R.id.tvDislikeCount);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        tvNoComments = new TextView(this); // Sẽ xử lý trong layout sau nếu cần
        viewPagerSuggestions = findViewById(R.id.viewPagerSuggestions);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSendComment).setOnClickListener(v -> sendComment());
    }

    private void displayArticle() {
        ((TextView) findViewById(R.id.tvDetailTitle)).setText(article.getTitle());
        ((TextView) findViewById(R.id.tvDetailSourceTime)).setText(article.getSource() + " - " + article.getTimeOrComment());
        ((TextView) findViewById(R.id.tvDetailContent)).setText(article.getContent());
        tvLikeCount.setText(String.valueOf(article.getLikes()));
        tvDislikeCount.setText(String.valueOf(article.getDislikes()));

        ImageView ivImage = findViewById(R.id.ivDetailImage);
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            Glide.with(this).load(article.getImages().get(0)).into(ivImage);
        }
    }

    private void setupInteractions() {
        viewModel.getInteraction(article.getId()).observe(this, i -> {
            this.interaction = i;
            if (interaction == null) {
                interaction = new UserArticleInteraction(currentUserId, article.getId());
            }
            updateInteractionUI();
        });

        findViewById(R.id.btnLike).setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để Like", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean wasLiked = interaction.isLiked();
            boolean wasDisliked = interaction.isDisliked();
            interaction.setLiked(!wasLiked);
            if (interaction.isLiked()) {
                interaction.setDisliked(false);
                article.setLikes(article.getLikes() + 1);
                if (wasDisliked) article.setDislikes(Math.max(0, article.getDislikes() - 1));
            } else {
                article.setLikes(Math.max(0, article.getLikes() - 1));
            }
            viewModel.updateInteraction(interaction);
            viewModel.updateArticle(article);
        });

        findViewById(R.id.btnDislike).setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để Dislike", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean wasLiked = interaction.isLiked();
            boolean wasDisliked = interaction.isDisliked();
            interaction.setDisliked(!wasDisliked);
            if (interaction.isDisliked()) {
                interaction.setLiked(false);
                article.setDislikes(article.getDislikes() + 1);
                if (wasLiked) article.setLikes(Math.max(0, article.getLikes() - 1));
            } else {
                article.setDislikes(Math.max(0, article.getDislikes() - 1));
            }
            viewModel.updateInteraction(interaction);
            viewModel.updateArticle(article);
        });

        findViewById(R.id.btnSaveDetail).setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để Lưu", Toast.LENGTH_SHORT).show();
                return;
            }
            interaction.setSaved(!interaction.isSaved());
            viewModel.updateInteraction(interaction);
            Toast.makeText(this, interaction.isSaved() ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateInteractionUI() {
        if (interaction == null) return;
        ivLike.setColorFilter(interaction.isLiked() ? Color.RED : Color.GRAY);
        ivDislike.setColorFilter(interaction.isDisliked() ? Color.BLACK : Color.GRAY);
        ivSaveDetail.setColorFilter(interaction.isSaved() ? Color.RED : Color.GRAY);
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter(commentList, new CommentAdapter.OnCommentInteractionListener() {
            @Override
            public void onLikeClick(Comment comment) {
                comment.setLikes(comment.getLikes() + 1);
                viewModel.updateComment(comment);
            }
            @Override
            public void onDislikeClick(Comment comment) {
                comment.setDislikes(comment.getDislikes() + 1);
                viewModel.updateComment(comment);
            }
            @Override
            public void onReplyClick(Comment comment) {
                replyingToId = comment.getId();
                etComment.setHint("Trả lời " + comment.getUsername() + "...");
                etComment.requestFocus();
            }
        });
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setNestedScrollingEnabled(false);
        rvComments.setAdapter(commentAdapter);

        viewModel.getComments(article.getId()).observe(this, comments -> {
            if (comments != null) {
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupSuggestions(boolean isNightMode) {
        suggestionAdapter = new RecommendationAdapter(suggestionList, target -> {
            Intent intent = new Intent(this, NewsDetailActivity.class);
            intent.putExtra("article", target);
            intent.putExtra("nightMode", isNightMode);
            startActivity(intent);
            finish(); 
        });
        viewPagerSuggestions.setAdapter(suggestionAdapter);

        viewModel.getAllArticles().observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                suggestionList.clear();
                int count = 0;
                for (Article a : articles) {
                    if (a.getId() != article.getId()) {
                        suggestionList.add(a);
                        if (++count >= 5) break;
                    }
                }
                suggestionAdapter.notifyDataSetChanged();
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sliderHandler.post(() -> {
                    if (viewPagerSuggestions != null && !suggestionList.isEmpty()) {
                        int nextItem = (viewPagerSuggestions.getCurrentItem() + 1) % suggestionList.size();
                        viewPagerSuggestions.setCurrentItem(nextItem, true);
                    }
                });
            }
        }, 2000, 2000);
    }

    private void sendComment() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = etComment.getText().toString().trim();
        if (text.isEmpty()) return;

        Comment newComment = new Comment(article.getId(), currentUserId, currentUsername, text, System.currentTimeMillis(), replyingToId);
        viewModel.addComment(newComment);
        etComment.setText("");
        etComment.setHint("Viết bình luận...");
        replyingToId = 0;
        Toast.makeText(this, "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
    }

    private void setupNightMode(boolean isNightMode) {
        View root = findViewById(R.id.newsDetailRoot);
        if (isNightMode) {
            root.setBackgroundColor(Color.BLACK);
            ((TextView) findViewById(R.id.tvDetailTitle)).setTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.tvDetailContent)).setTextColor(Color.WHITE);
            findViewById(R.id.topBar).setBackgroundColor(Color.BLACK);
            findViewById(R.id.commentInputBar).setBackgroundColor(Color.parseColor("#1A1A1A"));
            etComment.setTextColor(Color.WHITE);
            etComment.setHintTextColor(Color.GRAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
