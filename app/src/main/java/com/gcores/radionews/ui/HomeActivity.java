package com.gcores.radionews.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gcores.radionews.R;
import com.gcores.radionews.ui.api.NewsService;
import com.gcores.radionews.ui.api.RetrofitClient;
import com.gcores.radionews.ui.api.TestService;
import com.gcores.radionews.ui.api.UrlPath;
import com.gcores.radionews.ui.model.MenuBean;
import com.gcores.radionews.ui.model.news.Banner;
import com.gcores.radionews.ui.resmoel.BannerRes;
import com.gcores.radionews.ui.view.base.BaseActivity;
import com.gcores.radionews.ui.view.base.adapter.LeftMenuAdapter;
import com.gcores.radionews.ui.wedget.toolbar.GAppBar;
import com.gcores.radionews.ui.wedget.transformations.GateTransformation;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends BaseActivity {


    private CoordinatorLayout coordinatorLayout;
    private long exitTime = 0;
    private final long COUNT = 2000;//两秒退出
    private SlidingRootNav slidingRootNav;//菜单
    private List<MenuBean> menuBeanList = new ArrayList<>();
    private RecyclerView menuList;
    private LeftMenuAdapter leftMenuAdapter;

    private int notification_size = 0;//通知
    private int subscrible_size = 0;//订阅

//    private RecyclerViewPager mRecyclerViewPage;
    private ViewPager mBanner;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList = new ArrayList<>();

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        notification_size = getIntent().getIntExtra("notifications_size", 0);
        subscrible_size = getIntent().getIntExtra("subscriptions_size", 0);

        GAppBar gbar = initThemeToolBar();
//        Toolbar toolbar =  findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        coordinatorLayout = findViewById(R.id.container);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(gbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        menuList = findViewById(R.id.menu_list);
        initMenu(menuList);

        mBanner = findViewById(R.id.banner_pager);
        mBanner.setPageTransformer(true,new GateTransformation());
        // setLayoutManager like normal RecyclerView, you do not need to change any thing.
//        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerViewPage.setLayoutManager(layout);

        retrofit = RetrofitClient.getRetrofit(UrlPath.base_url_api);
        NewsService newsService =  retrofit.create(NewsService.class);
        getBannerList(newsService);


        //        mRecyclerViewPage
//        ViewPager
        //set adapter
        //You just need to implement ViewPageAdapter by yourself like a normal RecyclerView.Adpater.


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        getLogin();
    }

    private void getBannerList(NewsService newsService) {
        bannerList.clear();
        Call<BannerRes>  call  =  newsService.getBanner();
        call.enqueue(new Callback<BannerRes>() {
            @Override
            public void onResponse(Call<BannerRes> call, Response<BannerRes> response) {

                if (response.body().getStatus()==UrlPath.NET_SUCESS){
                    bannerList =  response.body().getResults();
                    setPager();
//                    bannerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BannerRes> call, Throwable t) {
                Log.e("eee",t.getMessage());
//
            }
        });


    }

    private void setPager() {
        bannerAdapter = new BannerAdapter(bannerList,this);
        mBanner.setAdapter(bannerAdapter);
    }


    private void initMenu(RecyclerView menuList) {
        menuBeanList = getMenuList();
        menuList.setLayoutManager(new LinearLayoutManager(this));
        leftMenuAdapter = new LeftMenuAdapter(menuBeanList);
        menuList.setAdapter(leftMenuAdapter);
    }

    private List<MenuBean> getMenuList() {
        menuBeanList.clear();
        String[] menuNames = getResources().getStringArray(R.array.main_menu_name);
        for (int x = 0; x < menuNames.length; x++) {
            MenuBean menuBean = new MenuBean();
            menuBean.setText(menuNames[x]);
            menuBeanList.add(menuBean);
        }
        return menuBeanList;
    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApplication(HomeActivity.this);
        }
        return false;
    }

    /**
     * 退出
     *
     * @param homeActivity
     */
    protected void exitApplication(HomeActivity homeActivity) {
        if (slidingRootNav.isMenuOpened()) {
            slidingRootNav.closeMenu();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - exitTime >= COUNT) {
            exitTime = currentTime;
            Snackbar.make(coordinatorLayout, "再按一次退出", Snackbar.LENGTH_SHORT).show();
        } else {
            HomeActivity.this.finishAffinity();
        }
    }

    private void getLogin() {
        Retrofit retrofit = RetrofitClient.getRetrofit(UrlPath.URL_BASE);
       /* Retrofit retrofit = new Retrofit.Builder().
                baseUrl(UrlPath.URL_BASE).build();*/
        TestService ts = retrofit.create(TestService.class);
        Call<ResponseBody> call = ts.Login();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    String content = null;
                    try {
                        content = response.body().string();
                        ((TextView) findViewById(R.id.content)).setText(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("eee", t.getMessage());
            }
        });

    }



    public class BannerAdapter  extends PagerAdapter {
        private List<Banner> bannerList;
        private LayoutInflater layoutInflater;
        private Context mContext;
        public BannerAdapter(List<Banner> data, Context context){
            this.bannerList = data;
            this.mContext = context;
            this.layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return bannerList.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            View item = layoutInflater.inflate(R.layout.banner_item,container,false);
            ImageView imagBanner = item.findViewById(R.id.image_banner);
            Glide.with(mContext).load(((Banner)bannerList.get(position)).getImage()).into(imagBanner);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }
    }

}
