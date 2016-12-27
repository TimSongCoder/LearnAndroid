package com.example.tim.simplewebview;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebviewActivity extends AppCompatActivity {

    private static final String URL_TEST = "http://developer.android.com/";
    private static final String HTML_VIDEO_URL = "http://www.quirksmode.org/html5/tests/video.html";
    private static final String TAG = "WebviewActivity";

    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private WebChromeClient webChromeClient;
    private WebView webview;
    private String lastUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG){
            // Verify the runtime in use.
            Log.d(TAG, "RUNTIME VERSION: " + System.getProperty("java.vm.version"));
            // if the property's value is 2.0.0 or higher, the Android Runtime is in use.
        }
        // request feature must be called before adding content.
//        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_webview);
        webview = (WebView) findViewById(R.id.webview_main);

        final ViewGroup customViewLayout = (ViewGroup) findViewById(R.id.customViewLayout);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // scale down the potentially too large page to fit the device width, last resort for pages don't contain viewport meta tag.
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setBuiltInZoomControls(true);
        if(BuildConfig.DEBUG){
            Log.d(TAG, "getUseWideViewPort: " + webSettings.getUseWideViewPort());
        }

        webChromeClient = new WebChromeClient(){
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.d(TAG, "onShowCustomView: " + view);
                webview.setVisibility(View.INVISIBLE);
                if(getSupportActionBar()!=null){
                    getSupportActionBar().hide();
                }
                customViewLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                customViewLayout.setVisibility(View.VISIBLE);
                mCustomViewCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                Log.d(TAG, "onHideCustomView");
                customViewLayout.removeAllViews();
                customViewLayout.setVisibility(View.INVISIBLE);
                webview.setVisibility(View.VISIBLE);
                if(getSupportActionBar()!=null){
                    getSupportActionBar().show();
                }
                mCustomViewCallback.onCustomViewHidden();
            }
        };

//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
//                view.loadUrl(url);
//                return false;
//            }
//        });

        webview.setWebChromeClient(webChromeClient);

//        webview.addJavascriptInterface(new JavascriptInterface(), "_VideoEnabledWebView");
//        webview.loadUrl(HTML_VIDEO_URL);
        webview.loadUrl("file:///android_asset/LocalAsset.html");
    }

    public class JavascriptInterface
    {
        @android.webkit.JavascriptInterface @SuppressWarnings("unused")
        public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            Log.d("___", "GOT IT");
            // This code is not executed in the UI thread, so we must force that to happen
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (webChromeClient != null)
                    {
                        webChromeClient.onHideCustomView();
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webview!=null){
            lastUrl = webview.getUrl();
            webview.loadUrl("about:blank");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(webview!=null && lastUrl!=null){
            webview.loadUrl(lastUrl);
        }
    }
}
