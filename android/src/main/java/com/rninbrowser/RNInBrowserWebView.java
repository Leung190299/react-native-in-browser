package com.rninbrowser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class RNInBrowserWebView extends WebView {
  private String currentUrl;

  @SuppressLint("SetJavaScriptEnabled")
  public RNInBrowserWebView(Context context) {
    super(context);

    // Enable JavaScript and other settings
    getSettings().setJavaScriptEnabled(true);
    getSettings().setDomStorageEnabled(true);
    getSettings().setMediaPlaybackRequiresUserGesture(false);
    getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    getSettings().setAllowFileAccess(true);

    // Set up WebChromeClient for permissions
    setWebChromeClient(new WebChromeClient() {
      @Override
      public void onPermissionRequest(final PermissionRequest request) {
        // Check if we have permissions
        boolean hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED;
        boolean hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED;

        if (hasCamera && hasAudio) {
          request.grant(request.getResources());
        } else {
          // User needs to grant permissions from settings
          request.deny();
        }
      }
    });

    // Set up WebViewClient for navigation events
    setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        currentUrl = url;

        // Send onLoadStart event
        WritableMap event = Arguments.createMap();
        event.putString("url", url);
        sendEvent("onLoadStart", event);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // Send onLoadEnd event
        WritableMap event = Arguments.createMap();
        event.putString("url", url);
        event.putString("title", view.getTitle() != null ? view.getTitle() : "");
        event.putBoolean("canGoBack", view.canGoBack());
        event.putBoolean("canGoForward", view.canGoForward());
        sendEvent("onLoadEnd", event);
      }

      @Override
      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        // Send onLoadError event
        WritableMap event = Arguments.createMap();
        event.putString("url", failingUrl);
        event.putInt("code", errorCode);
        event.putString("description", description);
        sendEvent("onLoadError", event);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Send onUrlChange event when URL is about to change
        WritableMap event = Arguments.createMap();
        event.putString("url", url);
        sendEvent("onUrlChange", event);
        return false;
      }
    });
  }

  public void setUrl(String url) {
    if (url != null && !url.equals(currentUrl)) {
      currentUrl = url;
      loadUrl(url);
    }
  }

  private void sendEvent(String eventName, WritableMap params) {
    ReactContext reactContext = (ReactContext) getContext();
    reactContext
        .getJSModule(RCTEventEmitter.class)
        .receiveEvent(getId(), eventName, params);
  }
}
