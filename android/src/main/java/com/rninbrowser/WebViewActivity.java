package com.rninbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class WebViewActivity extends Activity {
    private RNInBrowserWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create main layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Get intent data
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        boolean showCloseButton = intent.getBooleanExtra("showCloseButton", true);

        // Create close button if needed
        if (showCloseButton) {
            Button closeButton = new Button(this);
            closeButton.setText("Close");
            closeButton.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            closeButton.setOnClickListener(v -> {
                Intent result = new Intent();
                result.putExtra("type", "dismiss");
                setResult(RESULT_OK, result);
                finish();
            });
            layout.addView(closeButton);
        }

        // Create WebView
        webView = new RNInBrowserWebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        ));

        layout.addView(webView);
        setContentView(layout);

        // Load URL
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            Intent result = new Intent();
            result.putExtra("type", "dismiss");
            setResult(RESULT_OK, result);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
