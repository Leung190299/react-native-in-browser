package com.rninbrowser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class RNInBrowserWebView extends WebView {
    private String currentUrl;
    private String lastReportedUrl;

    public RNInBrowserWebView(Context context) {
        super(context);
        init(context);
    }

    public RNInBrowserWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        // Background + hardware accel
        setBackgroundColor(0xFFFFFFFF);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        WebView.setWebContentsDebuggingEnabled(true);

        // Settings
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webSettings.setUserAgentString(webSettings.getUserAgentString() + " RNInBrowserWebView/1.0");

        // Cookie support (important for Jitsi)
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this, true);

        // WebChromeClient to handle permission requests (WebRTC)
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                // run on UI thread
                post(() -> {
                    boolean hasCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED;
                    boolean hasAudio = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED;

                    if (hasCamera && hasAudio) {
                        try {
                            request.grant(request.getResources());
                        } catch (Exception e) {
                            request.deny();
                        }
                    } else {
                        // deny here — Activity should request runtime perms before loading
                        request.deny();
                    }
                });
            }
        });

        // WebViewClient for navigation events and error handling
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                currentUrl = url;
                WritableMap event = Arguments.createMap();
                event.putString("url", url);
                sendEvent("onLoadStart", event);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WritableMap event = Arguments.createMap();
                event.putString("url", url);
                event.putString("title", view.getTitle() != null ? view.getTitle() : "");
                event.putBoolean("canGoBack", view.canGoBack());
                event.putBoolean("canGoForward", view.canGoForward());
                sendEvent("onLoadEnd", event);
                checkUrlChange();
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                checkUrlChange();
            }

            @Override
            @Deprecated
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                WritableMap event = Arguments.createMap();
                event.putString("url", failingUrl);
                event.putInt("code", errorCode);
                event.putString("description", description);
                sendEvent("onLoadError", event);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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

    private void checkUrlChange() {
        String newUrl = getUrl();
        if (newUrl != null && !newUrl.equals(lastReportedUrl)) {
            lastReportedUrl = newUrl;
            WritableMap event = Arguments.createMap();
            event.putString("url", newUrl);
            sendEvent("onUrlChange", event);
        }
    }

    private void sendEvent(String eventName, WritableMap params) {
        ReactContext reactContext = (ReactContext) getContext();
        reactContext
            .getJSModule(RCTEventEmitter.class)
            .receiveEvent(getId(), eventName, params);
    }
}
