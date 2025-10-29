# React Native In-App Browser

A powerful React Native module for displaying web content with native support for iOS and Android. Provides both a modal browser experience and an embeddable WebView component.

[![npm version](https://img.shields.io/npm/v/react-native-in-browser.svg)](https://www.npmjs.com/package/react-native-in-browser)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## ✨ Features

### 🎯 Two Ways to Display Web Content

- **Modal Browser** (`openInAppBrowser`): Full-screen modal experience with automatic close handling
- **WebView Component**: Embed directly in your UI with full control over layout and styling

### 🚀 Core Features

- ✅ **Cross-platform**: iOS and Android with consistent API
- ✅ **TypeScript**: Full type definitions included
- ✅ **Modern API**: Promise-based with async/await support
- ✅ **URL Tracking**: Real-time URL change events
- ✅ **Navigation Control**: goBack, goForward, reload, stopLoading
- ✅ **Custom UI**: Add overlays and controls via children
- ✅ **WebRTC Support**: Camera and microphone permissions handled
- ✅ **Native Performance**: Uses WKWebView (iOS) and WebView (Android)

## 📦 Installation

### Step 1: Install the package

```bash
# Using npm
npm install react-native-in-browser

# Using yarn
yarn add react-native-in-browser
```

### Step 2: iOS Setup

```bash
cd ios
pod install
cd ..
```

### Step 3: Android Setup

Add the WebViewActivity to your `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest ...>
  <application ...>
    <!-- Add this activity -->
    <activity
      android:name="com.rninbrowser.WebViewActivity"
      android:theme="@style/Theme.AppCompat.Light.NoActionBar"
      android:configChanges="orientation|screenSize" />
  </application>
</manifest>
```

#### Optional: WebRTC Permissions

If you need camera/microphone access (for video calls, etc.), add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

For iOS, add to `ios/YourApp/Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>We need camera access for video calls</string>
<key>NSMicrophoneUsageDescription</key>
<string>We need microphone access for audio calls</string>
```

### Step 4: Rebuild

```bash
# iOS
npx react-native run-ios

# Android
npx react-native run-android
```

---

## 🎯 Usage Guide

### Option 1: Modal Browser (`openInAppBrowser`)

Perfect for opening external links, OAuth flows, or temporary content that should appear in a full-screen modal.

#### Basic Example

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

// Simple usage
const result = await openInAppBrowser('https://example.com');
console.log(result.type); // 'close' or 'dismiss'
```

#### With Options

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

const result = await openInAppBrowser('https://example.com', {
  showCloseButton: true, // Show/hide close button
  onUrlChange: (url) => {
    console.log('URL changed to:', url);
    // Track navigation, detect OAuth callbacks, etc.
  }
});

if (result.type === 'close') {
  console.log('User closed via button');
} else {
  console.log('User dismissed via back gesture');
}
```

#### Real-World Example: OAuth Flow

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

async function handleLogin() {
  try {
    const result = await openInAppBrowser('https://auth.example.com/login', {
      onUrlChange: (url) => {
        // Detect OAuth callback
        if (url.startsWith('myapp://oauth-callback')) {
          const code = new URL(url).searchParams.get('code');
          console.log('OAuth code:', code);
          // Process authentication
        }
      }
    });

    console.log('Auth flow completed:', result.type);
  } catch (error) {
    console.error('Auth error:', error);
  }
}
```

---

### Option 2: WebView Component

Perfect for embedding web content directly in your app's UI with full control over appearance and behavior.

#### Basic Example

```typescript
import { WebView } from 'react-native-in-browser';
import { View } from 'react-native';

function MyScreen() {
  return (
    <View style={{ flex: 1 }}>
      <WebView
        url="https://example.com"
        style={{ flex: 1 }}
      />
    </View>
  );
}
```

#### With Navigation Controls

```typescript
import { useRef } from 'react';
import { WebView, WebViewRef } from 'react-native-in-browser';
import { View, Button, StyleSheet } from 'react-native';

function BrowserScreen() {
  const webViewRef = useRef<WebViewRef>(null);

  return (
    <View style={styles.container}>
      {/* Navigation Controls */}
      <View style={styles.toolbar}>
        <Button
          title="← Back"
          onPress={() => webViewRef.current?.goBack()}
        />
        <Button
          title="Forward →"
          onPress={() => webViewRef.current?.goForward()}
        />
        <Button
          title="↻ Reload"
          onPress={() => webViewRef.current?.reload()}
        />
        <Button
          title="✕ Stop"
          onPress={() => webViewRef.current?.stopLoading()}
        />
      </View>

      {/* WebView */}
      <WebView
        ref={webViewRef}
        url="https://example.com"
        style={styles.webview}
        onLoadEnd={(e) => {
          console.log('Loaded:', e.nativeEvent.url);
          console.log('Can go back:', e.nativeEvent.canGoBack);
        }}
        onUrlChange={(e) => {
          console.log('URL changed:', e.nativeEvent.url);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  toolbar: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
    backgroundColor: '#f0f0f0'
  },
  webview: { flex: 1 }
});
```

#### With Custom Overlays (Children)

```typescript
import { useState } from 'react';
import { WebView } from 'react-native-in-browser';
import { View, ActivityIndicator, Text, StyleSheet } from 'react-native';

function WebViewWithOverlay() {
  const [loading, setLoading] = useState(true);
  const [currentUrl, setCurrentUrl] = useState('');

  return (
    <WebView
      url="https://example.com"
      onLoadStart={() => setLoading(true)}
      onLoadEnd={(e) => {
        setLoading(false);
        setCurrentUrl(e.nativeEvent.url);
      }}
      onUrlChange={(e) => setCurrentUrl(e.nativeEvent.url)}
      style={{ flex: 1 }}
    >
      {/* Loading Overlay */}
      {loading && (
        <View style={styles.loadingOverlay}>
          <ActivityIndicator size="large" color="#0000ff" />
          <Text style={styles.loadingText}>Loading...</Text>
        </View>
      )}

      {/* URL Bar Overlay */}
      <View style={styles.urlBar}>
        <Text numberOfLines={1} style={styles.urlText}>
          {currentUrl}
        </Text>
      </View>
    </WebView>
  );
}

const styles = StyleSheet.create({
  loadingOverlay: {
    position: 'absolute',
    top: 0, left: 0, right: 0, bottom: 0,
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
    top: 0, left: 0, right: 0,
    padding: 10,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
  },
  urlText: {
    color: 'white',
    fontSize: 12,
  },
});
```

---

## 📚 API Reference

### `openInAppBrowser(url, options?)`

Opens a URL in a full-screen modal browser.

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `url` | `string` | Yes | The URL to open |
| `options` | `InBrowserOptions` | No | Configuration options |

#### InBrowserOptions

```typescript
interface InBrowserOptions {
  // Show or hide the close button (default: true)
  showCloseButton?: boolean;

  // Callback when URL changes
  onUrlChange?: (url: string) => void;
}
```

#### Returns

```typescript
Promise<InBrowserResult>

interface InBrowserResult {
  type: 'close' | 'dismiss';
  // 'close': User pressed close button
  // 'dismiss': User dismissed via back gesture/button
}
```

#### Example

```typescript
const result = await openInAppBrowser('https://example.com', {
  showCloseButton: true,
  onUrlChange: (url) => console.log('Navigated to:', url)
});

console.log('Browser closed via:', result.type);
```

---

### `<WebView />` Component

Embeddable WebView component for displaying web content.

#### Props

| Prop | Type | Required | Description |
|------|------|----------|-------------|
| `url` | `string` | Yes | The URL to load |
| `onLoadStart` | `(event) => void` | No | Called when page starts loading |
| `onLoadEnd` | `(event) => void` | No | Called when page finishes loading |
| `onLoadError` | `(event) => void` | No | Called when load error occurs |
| `onUrlChange` | `(event) => void` | No | Called when URL changes |
| `style` | `ViewStyle` | No | Style for the container |
| `children` | `ReactNode` | No | Components to overlay on WebView |

#### Event Data Types

```typescript
// onLoadStart, onUrlChange
interface WebViewNavigationEvent {
  url: string;
}

// onLoadEnd
interface WebViewLoadEvent {
  url: string;
  title?: string;
  canGoBack?: boolean;
  canGoForward?: boolean;
}

// onLoadError
interface WebViewErrorEvent {
  url: string;
  code: number;
  description: string;
}
```

#### Ref Methods

```typescript
interface WebViewRef {
  goBack: () => void;      // Navigate to previous page
  goForward: () => void;   // Navigate to next page
  reload: () => void;      // Reload current page
  stopLoading: () => void; // Stop loading current page
}
```

#### Example

```typescript
import { useRef } from 'react';
import { WebView, WebViewRef } from 'react-native-in-browser';

const webViewRef = useRef<WebViewRef>(null);

<WebView
  ref={webViewRef}
  url="https://example.com"
  onLoadStart={(e) => console.log('Loading:', e.nativeEvent.url)}
  onLoadEnd={(e) => console.log('Loaded:', e.nativeEvent.title)}
  onLoadError={(e) => console.error('Error:', e.nativeEvent.description)}
  onUrlChange={(e) => console.log('URL:', e.nativeEvent.url)}
  style={{ flex: 1 }}
>
  {/* Optional overlay children */}
</WebView>

// Later...
webViewRef.current?.goBack();
```

---

## 🎨 Complete TypeScript Example

```typescript
import React, { useRef, useState } from 'react';
import {
  View,
  Button,
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  ActivityIndicator
} from 'react-native';
import {
  openInAppBrowser,
  WebView,
  WebViewRef,
  type InBrowserResult
} from 'react-native-in-browser';

export default function App() {
  const webViewRef = useRef<WebViewRef>(null);
  const [loading, setLoading] = useState(false);
  const [currentUrl, setCurrentUrl] = useState('https://google.com');
  const [canGoBack, setCanGoBack] = useState(false);
  const [canGoForward, setCanGoForward] = useState(false);

  // Modal browser handler
  const handleOpenModal = async () => {
    try {
      const result: InBrowserResult = await openInAppBrowser(
        'https://example.com',
        {
          showCloseButton: true,
          onUrlChange: (url) => {
            console.log('Modal URL changed:', url);
          }
        }
      );
      console.log('Modal closed:', result.type);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      {/* Modal Browser Button */}
      <View style={styles.section}>
        <Button
          title="Open Modal Browser"
          onPress={handleOpenModal}
        />
      </View>

      {/* Embedded WebView */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Embedded WebView:</Text>

        {/* Navigation Bar */}
        <View style={styles.navbar}>
          <TouchableOpacity
            onPress={() => webViewRef.current?.goBack()}
            disabled={!canGoBack}
            style={[styles.navButton, !canGoBack && styles.navButtonDisabled]}
          >
            <Text style={styles.navButtonText}>←</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => webViewRef.current?.goForward()}
            disabled={!canGoForward}
            style={[styles.navButton, !canGoForward && styles.navButtonDisabled]}
          >
            <Text style={styles.navButtonText}>→</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => webViewRef.current?.reload()}
            style={styles.navButton}
          >
            <Text style={styles.navButtonText}>↻</Text>
          </TouchableOpacity>

          {loading && (
            <TouchableOpacity
              onPress={() => webViewRef.current?.stopLoading()}
              style={styles.navButton}
            >
              <Text style={styles.navButtonText}>✕</Text>
            </TouchableOpacity>
          )}
        </View>

        {/* URL Bar */}
        <View style={styles.urlBar}>
          <Text numberOfLines={1} style={styles.urlText}>
            {currentUrl}
          </Text>
        </View>

        {/* WebView */}
        <WebView
          ref={webViewRef}
          url={currentUrl}
          onLoadStart={() => setLoading(true)}
          onLoadEnd={(e) => {
            setLoading(false);
            setCanGoBack(e.nativeEvent.canGoBack || false);
            setCanGoForward(e.nativeEvent.canGoForward || false);
          }}
          onUrlChange={(e) => setCurrentUrl(e.nativeEvent.url)}
          onLoadError={(e) => {
            setLoading(false);
            console.error('Load error:', e.nativeEvent.description);
          }}
          style={styles.webview}
        >
          {loading && (
            <View style={styles.loadingOverlay}>
              <ActivityIndicator size="large" color="#007AFF" />
            </View>
          )}
        </WebView>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  section: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    padding: 10,
    backgroundColor: '#f0f0f0',
  },
  navbar: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  navButton: {
    padding: 10,
    minWidth: 50,
    alignItems: 'center',
    backgroundColor: '#007AFF',
    borderRadius: 5,
  },
  navButtonDisabled: {
    backgroundColor: '#ccc',
  },
  navButtonText: {
    fontSize: 20,
    color: '#fff',
    fontWeight: 'bold',
  },
  urlBar: {
    padding: 10,
    backgroundColor: '#f9f9f9',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  urlText: {
    fontSize: 12,
    color: '#333',
  },
  webview: {
    flex: 1,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0, left: 0, right: 0, bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
  },
});
```

---

## 🎯 Common Use Cases

### OAuth Authentication

```typescript
await openInAppBrowser('https://auth.provider.com/login', {
  onUrlChange: (url) => {
    if (url.startsWith('myapp://oauth-callback')) {
      const code = new URL(url).searchParams.get('code');
      // Handle OAuth code
    }
  }
});
```

### Payment Processing

```typescript
await openInAppBrowser('https://payment.gateway.com/checkout', {
  onUrlChange: (url) => {
    if (url.includes('/payment/success')) {
      // Payment successful
    } else if (url.includes('/payment/cancel')) {
      // Payment cancelled
    }
  }
});
```

### Video Conferencing

```typescript
// Full-screen modal
await openInAppBrowser('https://meet.jit.si/my-room', {
  showCloseButton: true,
  onUrlChange: (url) => console.log('Meeting URL:', url)
});

// Or embedded
<WebView
  url="https://meet.jit.si/my-room"
  style={{ flex: 1 }}
/>
```

### Documentation Viewer

```typescript
<WebView
  url="https://docs.example.com"
  onUrlChange={(e) => {
    // Track page views
    analytics.logPageView(e.nativeEvent.url);
  }}
  style={{ flex: 1 }}
/>
```

---

## 🔧 Platform Differences

### iOS

- Uses **WKWebView** for optimal performance
- Native iOS animations and gestures
- Full WebRTC support with camera/microphone
- URL changes tracked via `didCommitNavigation`
- Commands dispatched via UIManager config

### Android

- Uses native **WebView** with enhanced settings
- JavaScript and DOM storage enabled by default
- Media playback without user gesture
- WebRTC permission handling (requires app permissions)
- URL changes tracked via `onPageStarted`
- Commands dispatched via command IDs

---

## 🆚 Comparison: Modal vs Component

| Feature | `openInAppBrowser` | `<WebView />` |
|---------|-------------------|---------------|
| **Display** | Full-screen modal | Embedded in UI |
| **Ref control** | ❌ | ✅ |
| **Custom overlays** | ❌ | ✅ (via children) |
| **Layout control** | ❌ Fixed fullscreen | ✅ Full control |
| **Navigation methods** | ❌ | ✅ goBack, etc. |
| **Close handling** | ✅ Promise-based | ❌ Manual |
| **URL tracking** | ✅ | ✅ |
| **Use cases** | OAuth, Payments, External links | Docs, Chat, Embedded content |

---

## 🐛 Troubleshooting

### Module not found

**iOS:**
```bash
cd ios
pod install
cd ..
npx react-native run-ios
```

**Android:**
```bash
cd android
./gradlew clean
cd ..
npx react-native run-android
```

### Android: WebViewActivity not found

Make sure you added the activity to `AndroidManifest.xml`:

```xml
<activity
  android:name="com.rninbrowser.WebViewActivity"
  android:theme="@style/Theme.AppCompat.Light.NoActionBar"
  android:configChanges="orientation|screenSize" />
```

### Commands not working (Android)

If `goBack()`, `goForward()`, etc. don't work on Android, try:

```bash
cd android
./gradlew clean
cd ..
npx react-native run-android
```

### WebRTC permissions not working

**Android:** Ensure permissions are in `AndroidManifest.xml`
**iOS:** Ensure usage descriptions are in `Info.plist`

Then request permissions at runtime:

```typescript
import { PermissionsAndroid, Platform } from 'react-native';

if (Platform.OS === 'android') {
  await PermissionsAndroid.requestMultiple([
    PermissionsAndroid.PERMISSIONS.CAMERA,
    PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
  ]);
}
```

---

## 📋 Requirements

- React Native >= 0.60.0
- iOS >= 12.0
- Android minSdkVersion >= 24

---

## 📖 Additional Documentation

For more detailed examples, see:
- [WEBVIEW_EXAMPLE.md](./WEBVIEW_EXAMPLE.md) - Comprehensive WebView examples with platform-specific notes

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📄 License

MIT

---

## 💬 Support

For issues and feature requests, please use the [GitHub issue tracker](https://github.com/yourusername/react-native-in-browser/issues).

---

## 🙏 Credits

Built with ❤️ using:
- **iOS**: WKWebView
- **Android**: WebView with WebChromeClient
- **React Native**: Native Modules and ViewManagers
