package club.yuanwanji.magickit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

public class Cinema extends AppCompatActivity {
    WebView wvBookPlay;
    private FrameLayout flVideoContainer;
    ActionBar actionBar;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(wvBookPlay.canGoBack()){
                wvBookPlay.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);
        actionBar = getSupportActionBar();
        String url = "http://video.yuanwanji.club";
        wvBookPlay = (WebView)findViewById(R.id.wvBookPlay);
        flVideoContainer = findViewById(R.id.flVideoContainer);
        wvBookPlay.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvBookPlay.getSettings().setJavaScriptEnabled(true);
        wvBookPlay.getSettings().setUseWideViewPort(true);
        wvBookPlay.getSettings().setLoadWithOverviewMode(true);
        wvBookPlay.getSettings().setAllowFileAccess(true);
        wvBookPlay.getSettings().setSupportZoom(true);
        wvBookPlay.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = wvBookPlay.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(wvBookPlay.getSettings(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        wvBookPlay.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvBookPlay.getSettings().setDomStorageEnabled(true);// 必须保留，否则无法播放优酷视频，其他的OK
        wvBookPlay.setWebChromeClient(new MyWebChromeClient());// 重写一下，有的时候可能会出现问题

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wvBookPlay.getSettings().setMixedContentMode(wvBookPlay.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("android");

        cookieManager.setCookie(url, stringBuffer.toString());
        cookieManager.setAcceptCookie(true);

        wvBookPlay.loadUrl(url);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("ToVmp","onShowCustomView");
            fullScreen();

            wvBookPlay.setVisibility(View.GONE);
            flVideoContainer.setVisibility(View.VISIBLE);
            flVideoContainer.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            Log.i("ToVmp","onHideCustomView");
            fullScreen();

            wvBookPlay.setVisibility(View.VISIBLE);
            flVideoContainer.setVisibility(View.GONE);
            flVideoContainer.removeAllViews();
            super.onHideCustomView();

        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i("ToVmp","横屏");
            actionBar.hide();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.i("ToVmp","竖屏");
            actionBar.show();
        }
    }

    @Override
    protected void onDestroy() {
        if (wvBookPlay != null) {
            wvBookPlay.destroy();
        }
        super.onDestroy();
    }

}

