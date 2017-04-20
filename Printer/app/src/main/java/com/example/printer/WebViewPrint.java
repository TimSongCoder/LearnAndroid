package com.example.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class WebViewPrint extends AppCompatActivity {

    private static final String TAG = "WebViewPrint";
    private boolean mPrintable, mPageLoaded;
    private WebView mWebView;
    private static final String DEFAULT_URL = "https://cn.bing.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_print);
        mPrintable = PrintHelper.systemSupportsPrint();
        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPageLoaded = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: " + url);
                mPageLoaded = true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_print_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.webview_print:
                if(mPrintable && mPageLoaded){
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    PrintDocumentAdapter mWebViewPrintAdapter = mWebView.createPrintDocumentAdapter();
                    printManager.print(mWebView.getTitle(), mWebViewPrintAdapter, new PrintAttributes.Builder().build());
                }else{
                    if(!mPrintable)
                    Toast.makeText(this, "Your device does not support print.", Toast.LENGTH_SHORT).show();
                    if(!mPageLoaded){
                        Toast.makeText(this, "Your page is not loaded, please try later on.", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadPage(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String url = editText.getText().toString();
        if(TextUtils.isEmpty(url)){
            url = DEFAULT_URL;
        }
        // Load a web page. Need internet permission.
        mWebView.loadUrl(url);
    }
}
