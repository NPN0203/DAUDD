package com.example.daud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.adapter.RecommendationAdapter;
import com.example.daud.model.Article;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private View homeContainer, profileContainer, channelsContainer, exploreContainer;
    private ImageView ivNavHome, ivNavExplore, ivNavProfile;
    private TextView tvNavHome, tvNavExplore, tvNavProfile;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;
    private EditText etSearch, etExploreSearch;

    private ViewPager2 vpRecommendation;
    private RecommendationAdapter recommendationAdapter;
    private final List<Article> recommendationList = new ArrayList<>();
    private Timer timer;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());

    private NewsViewModel viewModel;
    private boolean isNightMode = false;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);
        isNightMode = pref.getBoolean("nightMode", false);
        viewModel.setUserId(currentUserId);

        initViews();
        setupRecyclerView();
        setupSlider();
        setupNavigation();
        setupTabs();
        setupSearch();
        observeData();
        
        if (isNightMode) applyNightMode();
    }

    private void setupSlider() {
        recommendationAdapter = new RecommendationAdapter(recommendationList, article -> {
            Intent intent = new Intent(this, NewsDetailActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("nightMode", isNightMode);
            startActivity(intent);
        });
        vpRecommendation.setAdapter(recommendationAdapter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sliderHandler.post(() -> {
                    if (vpRecommendation != null && !recommendationList.isEmpty()) {
                        int nextItem = (vpRecommendation.getCurrentItem() + 1) % recommendationList.size();
                        vpRecommendation.setCurrentItem(nextItem, true);
                    }
                });
            }
        }, 2000, 2000);
    }

    private void setupSearch() {
        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        if (etSearch != null) etSearch.addTextChangedListener(searchWatcher);
        if (etExploreSearch != null) etExploreSearch.addTextChangedListener(searchWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileUI();
        if (isNightMode) applyNightMode();
    }

    private void initViews() {
        homeContainer = findViewById(R.id.homeContainer);
        profileContainer = findViewById(R.id.profileContainer);
        channelsContainer = findViewById(R.id.channelsContainer);
        exploreContainer = findViewById(R.id.exploreContainer);
        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavExplore = findViewById(R.id.ivNavExplore);
        ivNavProfile = findViewById(R.id.ivNavProfile);
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavExplore = findViewById(R.id.tvNavExplore);
        tvNavProfile = findViewById(R.id.tvNavProfile);
        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabBongDa = findViewById(R.id.tabBongDa);
        tabVideo = findViewById(R.id.tabVideo);
        tabXaHoi = findViewById(R.id.tabXaHoi);
        tabGiaiTri = findViewById(R.id.tabGiaiTri);
        tabTheGioi = findViewById(R.id.tabTheGioi);
        etSearch = findViewById(R.id.etSearch);
        etExploreSearch = findViewById(R.id.etExploreSearch);
        vpRecommendation = findViewById(R.id.vpRecommendation);
    }

    private void setupRecyclerView() {
        rvNews = findViewById(R.id.rvNews);
        if (rvNews != null) {
            rvNews.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NewsAdapter(articleList);
            adapter.setNightMode(isNightMode);
            rvNews.setAdapter(adapter);
        }
    }

    private void observeData() {
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                if (adapter != null) adapter.notifyDataSetChanged();
                
                if (recommendationList.isEmpty() && articles.size() > 0) {
                    for (int i = 0; i < Math.min(5, articles.size()); i++) {
                        recommendationList.add(articles.get(i));
                    }
                    recommendationAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> showPage(homeContainer));
        findViewById(R.id.btnNavExplore).setOnClickListener(v -> showPage(exploreContainer));
        findViewById(R.id.btnNavProfile).setOnClickListener(v -> showPage(profileContainer));
        
        View btnOpen = findViewById(R.id.btnOpenChannels);
        if (btnOpen != null) btnOpen.setOnClickListener(v -> channelsContainer.setVisibility(View.VISIBLE));
        
        View btnCloseChan = findViewById(R.id.btnCloseChannels);
        if (btnCloseChan != null) btnCloseChan.setOnClickListener(v -> channelsContainer.setVisibility(View.GONE));

        findViewById(R.id.btnThongBaoMe).setOnClickListener(v -> {
            if (currentUserId != -1) {
                startActivity(new Intent(this, NotificationActivity.class));
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để xem thông báo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileUI() {
        View rootProfile = findViewById(R.id.profileRootLayout);
        if (rootProfile == null) return;

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
        currentUserId = pref.getInt("userId", -1);
        viewModel.setUserId(currentUserId);
        
        String fullName = pref.getString("fullName", "Đăng nhập / Đăng ký");
        String username = pref.getString("username", "");

        TextView tvUserStatus = rootProfile.findViewById(R.id.tvUserStatus);
        TextView tvUserSubStatus = rootProfile.findViewById(R.id.tvUserSubStatus);
        ImageView btnLogout = rootProfile.findViewById(R.id.btnLogout);

        tvUserStatus.setText(fullName);
        tvUserSubStatus.setText(isLoggedIn ? "@" + username : "Bấm để đăng nhập và đồng bộ");
        btnLogout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        rootProfile.findViewById(R.id.loginHeader).setOnClickListener(v -> {
            if (!isLoggedIn) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, EditProfileActivity.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            currentUserId = -1;
            viewModel.setUserId(-1);
            updateProfileUI();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
        
        rootProfile.findViewById(R.id.btnMenuLuu).setOnClickListener(v -> 
            startActivity(new Intent(this, SavedArticlesActivity.class).putExtra("nightMode", isNightMode)));
        rootProfile.findViewById(R.id.btnMenuLichSu).setOnClickListener(v -> 
            startActivity(new Intent(this, HistoryActivity.class).putExtra("nightMode", isNightMode)));
        
        rootProfile.findViewById(R.id.btnMenuLiked).setOnClickListener(v -> 
            startActivity(new Intent(this, LikedArticlesActivity.class).putExtra("nightMode", isNightMode)));
        rootProfile.findViewById(R.id.btnMenuCommented).setOnClickListener(v -> 
            startActivity(new Intent(this, CommentedArticlesActivity.class).putExtra("nightMode", isNightMode)));
            
        rootProfile.findViewById(R.id.btnNightMode).setOnClickListener(v -> toggleNightMode());
        
        if (isNightMode) {
            ((TextView) rootProfile.findViewById(R.id.tvNightMode)).setText("Chế độ sáng");
        } else {
            ((TextView) rootProfile.findViewById(R.id.tvNightMode)).setText("Ban đêm");
        }
    }

    private void showPage(View page) {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        channelsContainer.setVisibility(View.GONE);
        page.setVisibility(View.VISIBLE);
        updateNavUI(page.getId());
    }

    private void updateNavUI(int id) {
        int active = Color.RED;
        int inactive = isNightMode ? Color.LTGRAY : Color.GRAY;
        ivNavHome.setColorFilter(id == R.id.homeContainer ? active : inactive);
        tvNavHome.setTextColor(id == R.id.homeContainer ? active : inactive);
        ivNavExplore.setColorFilter(id == R.id.exploreContainer ? active : inactive);
        tvNavExplore.setTextColor(id == R.id.exploreContainer ? active : inactive);
        ivNavProfile.setColorFilter(id == R.id.profileContainer ? active : inactive);
        tvNavProfile.setTextColor(id == R.id.profileContainer ? active : inactive);
    }

    private void setupTabs() {
        View.OnClickListener listener = v -> switchCategory(v.getId(), ((TextView) v).getText().toString());
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView t : tabs) if (t != null) t.setOnClickListener(listener);
    }

    private void switchCategory(int id, String name) {
        int active = Color.RED;
        int inactive = isNightMode ? Color.LTGRAY : Color.DKGRAY;
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView t : tabs) {
            if (t != null) {
                t.setTextColor(t.getId() == id ? active : inactive);
                t.setTypeface(null, t.getId() == id ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
        viewModel.setCategory(name);
    }
    
    private void toggleNightMode() {
        isNightMode = !isNightMode;
        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
            .putBoolean("nightMode", isNightMode).apply();
        applyNightMode();
    }

    private void applyNightMode() {
        int bgColor = isNightMode ? Color.parseColor("#121212") : Color.parseColor("#EEEEEE");
        int cardColor = isNightMode ? Color.parseColor("#1E1E1E") : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        int subTextColor = isNightMode ? Color.LTGRAY : Color.GRAY;
        int headerColor = isNightMode ? Color.BLACK : Color.parseColor("#D32F2F");

        findViewById(R.id.mainLayout).setBackgroundColor(bgColor);
        findViewById(R.id.headerBar).setBackgroundColor(headerColor);
        findViewById(R.id.categoryBar).setBackgroundColor(cardColor);
        findViewById(R.id.bottomNav).setBackgroundColor(cardColor);
        
        if (adapter != null) {
            adapter.setNightMode(isNightMode);
        }
        
        if (recommendationAdapter != null) {
            recommendationAdapter.setNightMode(isNightMode);
        }
        
        View rootExplore = findViewById(R.id.exploreRoot);
        if (rootExplore != null) {
            rootExplore.setBackgroundColor(bgColor);
            int[] exploreTexts = {R.id.tvTrendingHeader, R.id.tvUtilitiesHeader, R.id.tvSuggestionHeader, R.id.tvExploreSuggestionTitle};
            for (int id : exploreTexts) {
                TextView tv = rootExplore.findViewById(id);
                if (tv != null) tv.setTextColor(textColor);
            }
        }
        
        View rootProfile = findViewById(R.id.profileRootLayout);
        if (rootProfile != null) {
            rootProfile.setBackgroundColor(bgColor);
            ((TextView) rootProfile.findViewById(R.id.tvUserStatus)).setTextColor(textColor);
            ((TextView) rootProfile.findViewById(R.id.tvNightMode)).setText(isNightMode ? "Chế độ sáng" : "Ban đêm");
            ((ImageView) rootProfile.findViewById(R.id.ivNightMode)).setColorFilter(isNightMode ? Color.YELLOW : Color.parseColor("#4FC3F7"));
            
            int[] menuTextIds = {
                R.id.menuLuu, R.id.menuLichSu, R.id.menuPhanHoi, R.id.menuCaiDat, 
                R.id.tvTheoDoi, R.id.tvThongBao, R.id.menuLiked, R.id.menuCommented,
                R.id.tvHoatDongHeader
            };
            for (int id : menuTextIds) {
                View v = rootProfile.findViewById(id);
                if (v instanceof TextView) {
                    if (id == R.id.tvHoatDongHeader) {
                        ((TextView) v).setTextColor(isNightMode ? Color.GRAY : Color.parseColor("#999999"));
                    } else {
                        ((TextView) v).setTextColor(textColor);
                    }
                }
            }
        }
        
        if (etSearch != null) {
            etSearch.setTextColor(textColor);
            etSearch.setHintTextColor(isNightMode ? Color.LTGRAY : Color.GRAY);
        }

        updateNavUI(profileContainer.getVisibility() == View.VISIBLE ? R.id.profileContainer : R.id.homeContainer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
