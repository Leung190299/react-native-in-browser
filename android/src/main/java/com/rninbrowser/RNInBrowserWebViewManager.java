package com.rninbrowser;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class RNInBrowserWebViewManager extends SimpleViewManager<RNInBrowserWebView> {

  public static final String REACT_CLASS = "RNInBrowserWebView";

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  protected RNInBrowserWebView createViewInstance(@NonNull ThemedReactContext reactContext) {
    RNInBrowserWebView webView = new RNInBrowserWebView(reactContext);

    // ✅ Cho phép WebView match với style từ React Native (flex hoặc width/height)
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    );
    webView.setLayoutParams(params);

    return webView;
  }

  @ReactProp(name = "url")
  public void setUrl(RNInBrowserWebView view, @Nullable String url) {
    if (url != null && !url.isEmpty()) {
      view.setUrl(url);
    }
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.<String, Object>builder()
        .put("onLoadStart", MapBuilder.of("registrationName", "onLoadStart"))
        .put("onLoadEnd", MapBuilder.of("registrationName", "onLoadEnd"))
        .put("onLoadError", MapBuilder.of("registrationName", "onLoadError"))
        .put("onUrlChange", MapBuilder.of("registrationName", "onUrlChange"))
        .build();
  }

  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
        "goBack", 1,
        "goForward", 2,
        "reload", 3,
        "stopLoading", 4
    );
  }

  @Override
  public void receiveCommand(@NonNull RNInBrowserWebView view, int commandId, @Nullable ReadableArray args) {
    switch (commandId) {
      case 1: // goBack
        if (view.canGoBack()) view.goBack();
        break;
      case 2: // goForward
        if (view.canGoForward()) view.goForward();
        break;
      case 3: // reload
        view.reload();
        break;
      case 4: // stopLoading
        view.stopLoading();
        break;
    }
  }
}
