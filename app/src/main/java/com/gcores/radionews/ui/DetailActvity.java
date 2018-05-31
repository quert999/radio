package com.gcores.radionews.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcores.radionews.R;
import com.gcores.radionews.ui.view.base.BaseActivity;
import com.gcores.radionews.ui.wedget.TouchiableWebview;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class DetailActvity extends BaseActivity implements OnRefreshListener {

    private String url;
    private TouchiableWebview mContent;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean loadFrist = true;
    private LinearLayout llBackTop;
    private int commentnum;
    private TextView tvCommentNum;
    private String currentUserid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        url = getIntent().getStringExtra("url");
        commentnum = getIntent().getIntExtra("commentnum",0);
        currentUserid = getIntent().getIntExtra("userid",0)+"";
        mContent = findViewById(R.id.web_content);

        smartRefreshLayout = findViewById(R.id.refreshLayout);
        llBackTop = findViewById(R.id.ll_backtop);
        llBackTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvCommentNum = findViewById(R.id.tv_comment_num);
        tvCommentNum.setText(commentnum+"");
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setEnableHeaderTranslationContent(true);
        mContent.setWebChromeClient(new WebChromeClient());
        mContent.setWebViewClient(new WebViewClient() {

                                      @Override
                                      public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                          super.onPageStarted(view, url, favicon);
                                      }

                                      @Override
                                      public void onPageFinished(WebView view, String url) {
                                          super.onPageFinished(view, url);
                                          smartRefreshLayout.finishRefresh();
                                      }

                                      @Override
                                      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                          super.onReceivedError(view, request, error);
                                          smartRefreshLayout.finishRefresh();
                                      }


                                      @TargetApi(Build.VERSION_CODES.N)
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                          String requstUrl = request.getUrl().toString();
                                          if (requstUrl.equals("ios://pageLoadComplete")){
                                              //加载完毕
                                              return true;
                                          }
                                          if (requstUrl.equals("ios://playedVideo")){
                                              Log.e("111","playvideo");
                                              //播放视频
                                              return true;
                                          }
                                          if (requstUrl.startsWith("ios://showAuthor")){
                                              //用户中心
                                              /*String[] arr =  requstUrl.split("/");
                                              String userid;
                                              if (arr[arr.length-1].length()==0){
                                                  //当前用户
                                                  userid = currentUserid;
                                              }else{
                                                  //其他用户
                                                  userid = arr[arr.length-1];
                                              }*/
                                              String url = "https://www.g-cores.com/api/users/"+currentUserid+"/show_page?auth_exclusive="+ Constant.AUTH_EXCLUSIVE;
                                              Intent intent = new Intent(DetailActvity.this,UserInfoActivity.class);
                                              intent.putExtra("url",url);
                                              startActivity(intent);
                                              return true;
                                          }

                                          if (requstUrl.startsWith("ios://showUser")){
                                              //用户中心
                                              String[] arr =  requstUrl.split("/");
                                              String userid = arr[arr.length-1];
                                              String url = "https://www.g-cores.com/api/users/"+userid+"/show_page?auth_exclusive="+ Constant.AUTH_EXCLUSIVE;
                                              Intent intent = new Intent(DetailActvity.this,UserInfoActivity.class);
                                              intent.putExtra("url",url);
                                              startActivity(intent);
                                              return true;
                                          }
                                          if (requstUrl.startsWith("ios://showOriginal")){
                                              //文章
                                              String[] arr =  requstUrl.split("/");
                                                  //其他文章
                                              String articleid = arr[arr.length-1];
                                              String url = "https://www.g-cores.com/api/originals/"+articleid+"/html_content?auth_exclusive="+ Constant.AUTH_EXCLUSIVE+"&auth_token="+Constant.AUTH_TOKEN;
                                              Intent intent = new Intent(DetailActvity.this,DetailActvity.class);
                                              intent.putExtra("url",url);
                                              startActivity(intent);
                                              return true;
                                          }

                                          if (requstUrl.startsWith("ios://showCategory")){
                                              //分类
                                              String[] arr =  requstUrl.split("/");
                                              //其他分类
                                              String cateid = arr[arr.length-1];

                                              return true;
                                          }
                                          return false;
                                      }

                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String requstUrl) {
//                                          String requstUrl = request.getUrl().toString();
                                          if (requstUrl.equals("ios://pageLoadComplete")){
                                              return true;
                                          }
                                          if (requstUrl.equals("ios://playedVideo")){
                                              Log.e("111","playvideo");
                                              return true;
                                          }
                                          if (requstUrl.startsWith("ios://showAuthor")){
                                              //用户中心
                                              String[] arr =  requstUrl.split("/");

                                              if (arr[arr.length-1].length()==0){
                                                  //当前用户

                                              }else{

                                                  //其他用户
                                                  String userid = arr[arr.length-1];

                                              }
                                              return true;
                                          }
                                          if (requstUrl.startsWith("ios://showOriginal")){
                                              //文章
                                              String[] arr =  requstUrl.split("/");
                                              //其他文章
                                              String articleid = arr[arr.length-1];

                                              return true;
                                          }

                                          if (requstUrl.startsWith("ios://showCategory")){
                                              //分类
                                              String[] arr =  requstUrl.split("/");
                                              //其他分类
                                              String cateid = arr[arr.length-1];

                                              return true;
                                          }
                                          return false;

                                      }
                                  }

        );
        WebSettings webSettings = mContent.getSettings();
        // 启动应用缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式
        //  页面加载好以后，再放开图片
        webSettings.setBlockNetworkImage(false);
        // 排版适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);
        smartRefreshLayout.autoRefresh();
//        mContent.loadUrl(url);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        /*if (loadFrist){
            mContent.loadUrl(url);
            loadFrist = !loadFrist;
        }else{*/
           mContent.loadUrl(url);
//        }
    }
}
