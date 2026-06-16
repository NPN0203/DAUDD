package com.example.daud;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private View homeContainer, profileContainer, channelsContainer, exploreContainer;
    private ImageView ivNavHome, ivNavExplore, ivNavProfile;
    private TextView tvNavHome, tvNavExplore, tvNavProfile;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;

    private NewsViewModel viewModel;
    private boolean isNightMode = false;

    private final int[] allChannelIds = {
            R.id.chanTrangChu, R.id.chanBongDa, R.id.chanXaHoi, R.id.chanGiaiTri, R.id.chanTheGioi,
            R.id.chanKinhTe, R.id.chanCongNghe, R.id.chanThoiTrang, R.id.chanTheThao, R.id.chanPhapLuat,
            R.id.chanDuLich, R.id.chanGame, R.id.chanSucKhoe, R.id.chanAmThuc, R.id.chanXeCo,
            R.id.chanDoiSong, R.id.chanGiaoDuc, R.id.chanHotGirls, R.id.chanLamDep, R.id.chanTinhYeu
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        
        initViews();
        setupRecyclerView();
        setupNavigation();
        setupTabs();
        setupChannelClicks();
        observeData();
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
    }

    private void setupRecyclerView() {
        rvNews = findViewById(R.id.rvNews);
        if (rvNews != null) {
            rvNews.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NewsAdapter(articleList);
            rvNews.setAdapter(adapter);
        }
    }

    private void observeData() {
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                if (adapter != null) adapter.notifyDataSetChanged();
                if (rvNews != null) rvNews.scrollToPosition(0);
            }
        });
        viewModel.getCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                viewModel.insertCategories(Arrays.asList(new Category("Trang chủ", true), new Category("Bóng đá", true)));
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
        
        View rootProfile = findViewById(R.id.profileRootLayout);
        if (rootProfile != null) {
            rootProfile.findViewById(R.id.btnNightMode).setOnClickListener(v -> toggleNightMode());
            rootProfile.findViewById(R.id.btnMenuLuu).setOnClickListener(v -> startActivity(new Intent(this, SavedArticlesActivity.class).putExtra("nightMode", isNightMode)));
            rootProfile.findViewById(R.id.btnMenuLichSu).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class).putExtra("nightMode", isNightMode)));
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

    private void setupChannelClicks() {
        View.OnClickListener listener = v -> {
            String name = ((TextView) v).getText().toString();
            showPage(homeContainer);
            int tabId = -1;
            if (name.equals("Trang chủ")) tabId = R.id.tabTrangChu;
            else if (name.equals("Bóng đá")) tabId = R.id.tabBongDa;
            else if (name.equals("Video")) tabId = R.id.tabVideo;
            else if (name.equals("Xã hội")) tabId = R.id.tabXaHoi;
            else if (name.equals("Giải trí")) tabId = R.id.tabGiaiTri;
            else if (name.equals("Thế giới")) tabId = R.id.tabTheGioi;
            switchCategory(tabId, name);
        };
        for (int id : allChannelIds) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(listener);
        }
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
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        homeContainer.setBackgroundColor(bgColor);
        exploreContainer.setBackgroundColor(bgColor);
        profileContainer.setBackgroundColor(bgColor);
        channelsContainer.setBackgroundColor(bgColor);
        if (adapter != null) adapter.setNightMode(isNightMode);
        showPage(profileContainer);
    }
}
