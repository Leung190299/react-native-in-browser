# WebView Component - Usage Examples

> **Cross-Platform Support**: WebView component works on both iOS and Android with consistent API.

## Ví dụ cơ bản

```tsx
import { WebView } from 'react-native-in-browser';
import { View, StyleSheet } from 'react-native';

function BasicWebView() {
  return (
    <View style={styles.container}>
      <WebView url="https://google.com" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
```

## Sử dụng với Ref để điều khiển WebView

```tsx
import { useRef } from 'react';
import { WebView, WebViewRef } from 'react-native-in-browser';
import { View, Button, StyleSheet } from 'react-native';

function WebViewWithControls() {
  const webViewRef = useRef<WebViewRef>(null);

  return (
    <View style={styles.container}>
      <View style={styles.controls}>
        <Button title="← Back" onPress={() => webViewRef.current?.goBack()} />
        <Button title="Forward →" onPress={() => webViewRef.current?.goForward()} />
        <Button title="↻ Reload" onPress={() => webViewRef.current?.reload()} />
        <Button title="✕ Stop" onPress={() => webViewRef.current?.stopLoading()} />
      </View>

      <WebView
        ref={webViewRef}
        url="https://example.com"
        style={styles.webView}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
    backgroundColor: '#f0f0f0',
  },
  webView: {
    flex: 1,
  },
});
```

## Sử dụng với Events và Children (overlay)

```tsx
import { useState } from 'react';
import { WebView } from 'react-native-in-browser';
import { View, Text, ActivityIndicator, StyleSheet } from 'react-native';

function WebViewWithOverlay() {
  const [isLoading, setIsLoading] = useState(true);
  const [currentUrl, setCurrentUrl] = useState('');
  const [canGoBack, setCanGoBack] = useState(false);

  return (
    <View style={styles.container}>
      <WebView
        url="https://example.com"
        onLoadStart={(e) => {
          setIsLoading(true);
          console.log('Loading started:', e.nativeEvent.url);
        }}
        onLoadEnd={(e) => {
          setIsLoading(false);
          setCanGoBack(e.nativeEvent.canGoBack || false);
          console.log('Loading finished:', e.nativeEvent.url);
        }}
        onUrlChange={(e) => {
          setCurrentUrl(e.nativeEvent.url);
          console.log('URL changed:', e.nativeEvent.url);
        }}
        onLoadError={(e) => {
          setIsLoading(false);
          console.error('Load error:', e.nativeEvent.description);
        }}
        style={styles.webView}
      >
        {/* Children được render trên WebView như overlay */}
        {isLoading && (
          <View style={styles.loadingOverlay}>
            <ActivityIndicator size="large" color="#0000ff" />
            <Text style={styles.loadingText}>Đang tải...</Text>
          </View>
        )}

        {/* URL bar overlay */}
        <View style={styles.urlBar}>
          <Text numberOfLines={1} style={styles.urlText}>
            {currentUrl || 'Loading...'}
          </Text>
        </View>
      </WebView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  webView: {
    flex: 1,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
  },
  urlBar: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    padding: 10,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
  },
  urlText: {
    color: 'white',
    fontSize: 12,
  },
});
```

## Ví dụ đầy đủ với tất cả tính năng

```tsx
import { useRef, useState } from 'react';
import { WebView, WebViewRef } from 'react-native-in-browser';
import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  StyleSheet,
  SafeAreaView,
} from 'react-native';

function FullFeaturedBrowser() {
  const webViewRef = useRef<WebViewRef>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [currentUrl, setCurrentUrl] = useState('https://google.com');
  const [title, setTitle] = useState('');
  const [canGoBack, setCanGoBack] = useState(false);
  const [canGoForward, setCanGoForward] = useState(false);

  return (
    <SafeAreaView style={styles.container}>
      {/* Address bar */}
      <View style={styles.addressBar}>
        <Text numberOfLines={1} style={styles.urlText}>
          {title || currentUrl}
        </Text>
      </View>

      {/* Navigation controls */}
      <View style={styles.controls}>
        <TouchableOpacity
          onPress={() => webViewRef.current?.goBack()}
          disabled={!canGoBack}
          style={[styles.button, !canGoBack && styles.buttonDisabled]}
        >
          <Text style={styles.buttonText}>←</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => webViewRef.current?.goForward()}
          disabled={!canGoForward}
          style={[styles.button, !canGoForward && styles.buttonDisabled]}
        >
          <Text style={styles.buttonText}>→</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => webViewRef.current?.reload()}
          style={styles.button}
        >
          <Text style={styles.buttonText}>↻</Text>
        </TouchableOpacity>

        {isLoading && (
          <TouchableOpacity
            onPress={() => webViewRef.current?.stopLoading()}
            style={styles.button}
          >
            <Text style={styles.buttonText}>✕</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* WebView */}
      <WebView
        ref={webViewRef}
        url={currentUrl}
        onLoadStart={(e) => {
          setIsLoading(true);
          setCurrentUrl(e.nativeEvent.url);
        }}
        onLoadEnd={(e) => {
          setIsLoading(false);
          setTitle(e.nativeEvent.title || '');
          setCanGoBack(e.nativeEvent.canGoBack || false);
          setCanGoForward(e.nativeEvent.canGoForward || false);
        }}
        onUrlChange={(e) => {
          setCurrentUrl(e.nativeEvent.url);
        }}
        onLoadError={(e) => {
          setIsLoading(false);
          alert(`Error: ${e.nativeEvent.description}`);
        }}
        style={styles.webView}
      >
        {/* Loading indicator */}
        {isLoading && (
          <View style={styles.loadingOverlay}>
            <ActivityIndicator size="large" color="#007AFF" />
          </View>
        )}
      </WebView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  addressBar: {
    padding: 10,
    backgroundColor: '#f0f0f0',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  urlText: {
    fontSize: 14,
    color: '#333',
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  button: {
    padding: 10,
    minWidth: 50,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#007AFF',
    borderRadius: 5,
  },
  buttonDisabled: {
    backgroundColor: '#ccc',
  },
  buttonText: {
    fontSize: 20,
    color: '#fff',
    fontWeight: 'bold',
  },
  webView: {
    flex: 1,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.5)',
  },
});

export default FullFeaturedBrowser;
```

## API Reference

### Props

- **url** (string, required): URL để load trong WebView
- **onLoadStart** (function): Callback khi bắt đầu load
- **onLoadEnd** (function): Callback khi load xong
- **onLoadError** (function): Callback khi có lỗi
- **onUrlChange** (function): Callback khi URL thay đổi
- **style** (ViewStyle): Style cho container
- **children** (ReactNode): Các component con sẽ được render trên WebView như overlay

### Ref Methods

- **goBack()**: Quay lại trang trước
- **goForward()**: Tiến tới trang tiếp theo
- **reload()**: Tải lại trang hiện tại
- **stopLoading()**: Dừng việc tải trang

## So sánh với openInAppBrowser

| Feature | WebView | openInAppBrowser |
|---------|---------|------------------|
| Hiển thị | Nhúng trong UI | Modal fullscreen |
| Có ref | ✅ | ❌ |
| Có children | ✅ | ❌ |
| Custom UI | ✅ Full control | ⚠️ Limited |
| Navigation control | ✅ Via ref | ❌ |
| Close handling | ❌ | ✅ Promise-based |
| iOS Support | ✅ | ✅ |
| Android Support | ✅ | ✅ |

## Platform-Specific Notes

### iOS
- Uses `WKWebView` for optimal performance
- Full WebRTC support with camera/microphone permissions
- Native iOS scrolling behavior
- Commands dispatched via UIManager config

### Android
- Uses native `WebView` with enhanced settings
- JavaScript enabled by default
- DOM storage enabled for web apps
- Media playback without user gesture
- Automatic WebRTC permission handling (requires app permissions)
- Commands dispatched via command IDs

### Permissions

**Android**: Add to `AndroidManifest.xml` if using WebRTC:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

**iOS**: Add to `Info.plist` if using camera/microphone:
```xml
<key>NSCameraUsageDescription</key>
<string>We need camera access for video calls</string>
<key>NSMicrophoneUsageDescription</key>
<string>We need microphone access for audio calls</string>
```
