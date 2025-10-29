package com.rninbrowser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WebViewActivity extends Activity {
  private static final int PERMISSION_REQUEST_CODE = 1;
  private PermissionRequest mPermissionRequest;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String url = getIntent().getStringExtra("url");
    boolean showCloseButton = getIntent().getBooleanExtra("showCloseButton", true);

    // Root layout
    FrameLayout root = new FrameLayout(this);
    root.setBackgroundColor(Color.WHITE);

    // WebView
    WebView webView = new WebView(this);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    webView.getSettings().setAllowFileAccess(true);

    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onPermissionRequest(final PermissionRequest request) {
        mPermissionRequest = request;

        if (ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

          ActivityCompat.requestPermissions(WebViewActivity.this,
              new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
              PERMISSION_REQUEST_CODE);
        } else {
          request.grant(request.getResources());
        }
      }
    });

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // Notify URL change
        android.util.Log.d("WebViewActivity", "🔄 URL changed to: " + url);
        RNInBrowserAppModule.sendUrlChangeEvent(url);
      }
    });
    webView.loadUrl(url);

    // Close button
    ImageButton closeButton = new ImageButton(this);
    closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    closeButton.setBackgroundColor(Color.TRANSPARENT);
    FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(100, 100);
    closeParams.leftMargin = 20;
    closeParams.topMargin = 60;

    closeButton.setOnClickListener(v -> {
      Intent resultIntent = new Intent();
      resultIntent.putExtra("type", "close");
      setResult(RESULT_OK, resultIntent);
      finish();
    });

    root.addView(webView);

    if (showCloseButton) {
      root.addView(closeButton, closeParams);
    }

    setContentView(root);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == PERMISSION_REQUEST_CODE && mPermissionRequest != null) {
      boolean allGranted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          allGranted = false;
          break;
        }
      }

      if (allGranted) {
        mPermissionRequest.grant(mPermissionRequest.getResources());
      } else {
        mPermissionRequest.deny();
      }
      mPermissionRequest = null;
    }
  }

  @Override
  public void onBackPressed() {
    Intent resultIntent = new Intent();
    resultIntent.putExtra("type", "dismiss");
    setResult(RESULT_OK, resultIntent);
    super.onBackPressed();
  }
}
