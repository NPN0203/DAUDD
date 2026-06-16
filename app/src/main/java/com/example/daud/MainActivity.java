package com.example.daud;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.util.LunarCalendar;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private ConstraintLayout mainLayout;
    private View homeContainer, profileContainer, channelsContainer, exploreContainer;
    private LinearLayout btnNavHome, btnNavProfile, btnNavExplore;
    private ImageView ivNavHome, ivNavProfile, ivNavExplore;
    private TextView tvNavHome, tvNavProfile, tvNavExplore;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;

    private NewsViewModel viewModel;
    private boolean isNightMode = false;
    private final Calendar currentCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        initViews();
        setupRecyclerView();
        setupNavigation();
        setupTabs();
        setupExploreUtilities();
        setupProfileMenu();
        
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                initializeDefaultData();
            }
        });
    }

    private void initViews() {
        mainLayout = findViewById(R.id.mainLayout);
        homeContainer = findViewById(R.id.homeContainer);
        profileContainer = findViewById(R.id.profileContainer);
        channelsContainer = findViewById(R.id.channelsContainer);
        exploreContainer = findViewById(R.id.exploreContainer);

        btnNavHome = findViewById(R.id.btnNavHome);
        btnNavProfile = findViewById(R.id.btnNavProfile);
        btnNavExplore = findViewById(R.id.btnNavExplore);
        
        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavProfile = findViewById(R.id.ivNavProfile);
        ivNavExplore = findViewById(R.id.ivNavExplore);
        
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavProfile = findViewById(R.id.tvNavProfile);
        tvNavExplore = findViewById(R.id.tvNavExplore);

        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabBongDa = findViewById(R.id.tabBongDa);
        tabVideo = findViewById(R.id.tabVideo);
        tabXaHoi = findViewById(R.id.tabXaHoi);
        tabGiaiTri = findViewById(R.id.tabGiaiTri);
        tabTheGioi = findViewById(R.id.tabTheGioi);
    }

    private void setupRecyclerView() {
        rvNews = findViewById(R.id.rvNews);
        if (rvNews != null) {
            rvNews.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NewsAdapter(articleList);
            rvNews.setAdapter(adapter);
        }
    }

    private void setupNavigation() {
        if (btnNavHome != null) btnNavHome.setOnClickListener(v -> showHomePage());
        if (btnNavProfile != null) btnNavProfile.setOnClickListener(v -> showProfilePage());
        if (btnNavExplore != null) btnNavExplore.setOnClickListener(v -> showExplorePage());

        View btnOpen = findViewById(R.id.btnOpenChannels);
        if (btnOpen != null) btnOpen.setOnClickListener(v -> channelsContainer.setVisibility(View.VISIBLE));
        
        View btnClose = findViewById(R.id.btnCloseChannels);
        if (btnClose != null) btnClose.setOnClickListener(v -> channelsContainer.setVisibility(View.GONE));

        View btnNight = findViewById(R.id.btnNightMode);
        if (btnNight != null) btnNight.setOnClickListener(v -> toggleNightMode());
    }

    private void showHomePage() {
        homeContainer.setVisibility(View.VISIBLE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        updateNavColor(true, false, false);
    }

    private void showExplorePage() {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);
        updateNavColor(false, true, false);
    }

    private void showProfilePage() {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.VISIBLE);
        updateNavColor(false, false, true);
    }

    private void updateNavColor(boolean isHome, boolean isExplore, boolean isProfile) {
        int red = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int gray = isNightMode ? Color.LTGRAY : Color.GRAY;
        
        if (ivNavHome != null) ivNavHome.setColorFilter(isHome ? red : gray);
        if (tvNavHome != null) tvNavHome.setTextColor(isHome ? red : gray);
        if (ivNavExplore != null) ivNavExplore.setColorFilter(isExplore ? red : gray);
        if (tvNavExplore != null) tvNavExplore.setTextColor(isExplore ? red : gray);
        if (ivNavProfile != null) ivNavProfile.setColorFilter(isProfile ? red : gray);
        if (tvNavProfile != null) tvNavProfile.setTextColor(isProfile ? red : gray);
    }

    private void setupTabs() {
        View.OnClickListener listener = v -> {
            if (v instanceof TextView) {
                String name = ((TextView) v).getText().toString();
                switchCategory(v.getId(), name);
            }
        };
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView t : tabs) if (t != null) t.setOnClickListener(listener);
    }

    private void switchCategory(int id, String name) {
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        int gray = isNightMode ? Color.LTGRAY : Color.DKGRAY;
        for (TextView t : tabs) {
            if (t != null) {
                t.setTextColor(t.getId() == id ? Color.RED : gray);
                t.setTypeface(null, t.getId() == id ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
        viewModel.setCategory(name);
    }

    private void setupExploreUtilities() {
        TextView utLunar = findViewById(R.id.utLunar);
        if (utLunar != null) {
            utLunar.setText(LunarCalendar.getTodayLunar());
            utLunar.setOnClickListener(v -> Toast.makeText(this, "Lịch âm: " + LunarCalendar.getTodayLunar(), Toast.LENGTH_SHORT).show());
        }
    }

    private void setupProfileMenu() {
        View btnLuu = findViewById(R.id.btnMenuLuu);
        if (btnLuu != null) btnLuu.setOnClickListener(v -> startActivity(new Intent(this, SavedArticlesActivity.class)));
        
        View btnHis = findViewById(R.id.btnMenuLichSu);
        if (btnHis != null) btnHis.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void toggleNightMode() {
        isNightMode = !isNightMode;
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        if (mainLayout != null) mainLayout.setBackgroundColor(isNightMode ? Color.DKGRAY : Color.LTGRAY);
        homeContainer.setBackgroundColor(bgColor);
        exploreContainer.setBackgroundColor(bgColor);
        profileContainer.setBackgroundColor(bgColor);
        if (adapter != null) adapter.setNightMode(isNightMode);
        updateNavColor(homeContainer.getVisibility() == View.VISIBLE, exploreContainer.getVisibility() == View.VISIBLE, profileContainer.getVisibility() == View.VISIBLE);
    }

    private void initializeDefaultData() {
        viewModel.insertCategories(Arrays.asList(new Category("Trang chủ", true), new Category("Bóng đá", true)));
        viewModel.insertArticles(Collections.singletonList(new Article("Chào mừng", "Trang chủ", "Vừa xong", null, 1, "Chào mừng bạn đến với ứng dụng!")));
    }
}
