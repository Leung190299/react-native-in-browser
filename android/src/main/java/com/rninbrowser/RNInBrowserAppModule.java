package com.rninbrowser;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNInBrowserAppModule extends ReactContextBaseJavaModule implements ActivityEventListener {
  private static final int REQUEST_CODE = 1001;
  private Promise mPromise;
  private static ReactApplicationContext sReactContext;

  public RNInBrowserAppModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(this);
    sReactContext = reactContext;
  }

  @NonNull
  @Override
  public String getName() {
    return "RNInBrowserApp";
  }

  @ReactMethod
  public void open(String url, ReadableMap options, Promise promise) {
    Activity currentActivity = getCurrentActivity();

    if (currentActivity == null) {
      promise.reject("NO_ACTIVITY", "Activity doesn't exist");
      return;
    }

    mPromise = promise;

    Intent intent = new Intent(currentActivity, WebViewActivity.class);
    intent.putExtra("url", url);

    // Parse options
    boolean showCloseButton = true;
    if (options.hasKey("showCloseButton")) {
      showCloseButton = options.getBoolean("showCloseButton");
    }
    intent.putExtra("showCloseButton", showCloseButton);

    currentActivity.startActivityForResult(intent, REQUEST_CODE);
  }

  public static void sendUrlChangeEvent(String url) {
    if (sReactContext != null) {
      WritableMap params = Arguments.createMap();
      params.putString("url", url);
      sReactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("onUrlChange", params);
    }
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_CODE && mPromise != null) {
      if (resultCode == Activity.RESULT_OK && data != null) {
        String type = data.getStringExtra("type");

        WritableMap result = Arguments.createMap();
        result.putString("type", type != null ? type : "dismiss");

        mPromise.resolve(result);
      } else {
        WritableMap result = Arguments.createMap();
        result.putString("type", "dismiss");
        mPromise.resolve(result);
      }
      mPromise = null;
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    // Not used
  }
}
