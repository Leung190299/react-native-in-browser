# react-native-in-browser

A React Native native module for opening URLs in an in-app browser with native support for iOS and Android.

## Features

- ✅ **Cross-platform**: Works on both iOS and Android
- ✅ **Native experience**: Uses WKWebView on iOS and custom WebView on Android
- ✅ **TypeScript**: Full TypeScript support with type definitions
- ✅ **Promise-based**: Modern async/await API
- ✅ **Customizable**: Configure close button visibility
- ✅ **Camera & Microphone**: Full support for WebRTC and media permissions (Android)
- ✅ **URL Change Listener**: Track navigation and URL changes in real-time

## Installation

### Using npm

```bash
npm install react-native-in-browser
```

### Using yarn

```bash
yarn add react-native-in-browser
```

### iOS Setup

Navigate to your iOS folder and install pods:

```bash
cd ios && pod install
```

### Android Setup

The module should work out of the box on Android. Make sure you have the following permissions in your `AndroidManifest.xml` if you need camera/microphone access:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

Also add the WebViewActivity to your `AndroidManifest.xml`:

```xml
<activity
    android:name="com.rninbrowser.WebViewActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"
    android:configChanges="orientation|screenSize" />
```

## Usage

### Basic Example

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

// Open a URL
const result = await openInAppBrowser('https://example.com');
console.log(result.type); // 'close' or 'dismiss'
```

### With Options

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

// Hide close button
const result = await openInAppBrowser('https://example.com', {
  showCloseButton: false
});
```

### With URL Change Listener

```typescript
import { openInAppBrowser } from 'react-native-in-browser';

// Track URL changes
const result = await openInAppBrowser('https://example.com', {
  showCloseButton: true,
  onUrlChange: (url) => {
    console.log('🔄 URL changed to:', url);
    // Handle URL changes here
    // Examples:
    // - Track navigation history
    // - Detect specific pages
    // - Log analytics events
    // - Extract URL parameters
  }
});
```

### Full Example

```typescript
import React, { useState } from 'react';
import { Button, View, Alert, Text } from 'react-native';
import { openInAppBrowser } from 'react-native-in-browser';

const App = () => {
  const [currentUrl, setCurrentUrl] = useState('');

  const handleOpenBrowser = async () => {
    try {
      const result = await openInAppBrowser('https://meet.jit.si/test-room', {
        showCloseButton: true,
        onUrlChange: (url) => {
          console.log('🔄 Navigated to:', url);
          setCurrentUrl(url);

          // Example: Detect when user joins a meeting
          if (url.includes('/meeting-joined')) {
            console.log('User joined the meeting!');
          }
        }
      });

      Alert.alert('Browser closed', `Type: ${result.type}`);
      setCurrentUrl('');
    } catch (error) {
      console.error('Error opening browser:', error);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Button title="Open Browser" onPress={handleOpenBrowser} />
      {currentUrl ? (
        <Text style={{ marginTop: 20 }}>
          Current URL: {currentUrl}
        </Text>
      ) : null}
    </View>
  );
};

export default App;
```

## API

### `openInAppBrowser(url, options?)`

Opens a URL in the in-app browser.

#### Parameters

- `url` (string, required): The URL to open
- `options` (object, optional):
  - `showCloseButton` (boolean): Show or hide the close button. Default: `true`
  - `onUrlChange` (function): Callback function called when the URL changes
    - Parameters: `url` (string) - The new URL
    - Called on every navigation, redirect, or URL change
    - Works on both iOS and Android

#### Returns

`Promise<InBrowserResult>`

- `type`: `'close'` | `'dismiss'`
  - `'close'`: User pressed the close button
  - `'dismiss'`: User dismissed the browser (back button/gesture)

#### Examples

**Basic usage:**
```typescript
const result = await openInAppBrowser('https://example.com', {
  showCloseButton: true
});

if (result.type === 'close') {
  console.log('User closed the browser');
} else {
  console.log('User dismissed the browser');
}
```

**With URL change tracking:**
```typescript
const result = await openInAppBrowser('https://example.com', {
  showCloseButton: true,
  onUrlChange: (url) => {
    console.log('Navigated to:', url);

    // Track specific pages
    if (url.includes('/checkout/success')) {
      console.log('Payment successful!');
    }
  }
});
```

## Advanced Usage

### URL Change Listener Use Cases

The `onUrlChange` callback provides powerful capabilities for tracking and responding to navigation events:

#### 1. OAuth Redirect Detection

```typescript
await openInAppBrowser('https://auth.example.com/login', {
  onUrlChange: (url) => {
    // Detect OAuth callback
    if (url.startsWith('myapp://oauth-callback')) {
      const code = new URL(url).searchParams.get('code');
      console.log('OAuth code received:', code);
      // Process authentication
    }
  }
});
```

#### 2. Payment Gateway Tracking

```typescript
await openInAppBrowser('https://payment.example.com/checkout', {
  onUrlChange: (url) => {
    if (url.includes('/payment/success')) {
      console.log('Payment completed successfully!');
      // Update order status, show confirmation
    } else if (url.includes('/payment/cancel')) {
      console.log('Payment cancelled');
    }
  }
});
```

#### 3. Meeting Room Tracking

```typescript
await openInAppBrowser('https://meet.jit.si/my-room', {
  onUrlChange: (url) => {
    const meetingId = url.split('/').pop();
    console.log('Current meeting:', meetingId);

    // Track when users switch rooms
    if (meetingId !== 'my-room') {
      console.log('User navigated to different room:', meetingId);
    }
  }
});
```

#### 4. Multi-step Form Progress

```typescript
await openInAppBrowser('https://example.com/signup/step1', {
  onUrlChange: (url) => {
    // Track progress through signup flow
    if (url.includes('/step1')) console.log('Step 1: Personal Info');
    if (url.includes('/step2')) console.log('Step 2: Account Details');
    if (url.includes('/step3')) console.log('Step 3: Verification');
    if (url.includes('/complete')) console.log('Signup complete!');
  }
});
```

#### 5. Analytics Tracking

```typescript
await openInAppBrowser('https://docs.example.com', {
  onUrlChange: (url) => {
    // Send page view events to analytics
    analytics.logEvent('page_view', {
      url: url,
      timestamp: Date.now()
    });
  }
});
```

## Platform Differences

### iOS

- Uses `WKWebView` with native navigation delegate
- URL changes tracked via `didCommitNavigation` (fired when navigation is committed)
- Native iOS animations and gestures
- Full WebRTC support with camera/microphone permissions

### Android

- Custom full-screen WebView implementation
- URL changes tracked via `onPageStarted` (fired when page begins loading)
- JavaScript enabled with DOM storage support
- Automatic permission requests for camera/microphone
- Custom close button overlay

## TypeScript Support

This library is written in TypeScript and includes type definitions:

```typescript
import {
  openInAppBrowser,
  InBrowserOptions,
  InBrowserResult
} from 'react-native-in-browser';

const options: InBrowserOptions = {
  showCloseButton: true,
  onUrlChange: (url: string) => {
    console.log('URL changed:', url);
  }
};

const result: InBrowserResult = await openInAppBrowser(
  'https://example.com',
  options
);
```

### Type Definitions

```typescript
interface InBrowserOptions {
  showCloseButton?: boolean;
  onUrlChange?: (url: string) => void;
}

interface InBrowserResult {
  type: 'close' | 'dismiss';
}
```

## Troubleshooting

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

### Build Errors

1. Clean your build folders:
```bash
# iOS
cd ios && rm -rf Pods Podfile.lock && pod install && cd ..

# Android
cd android && ./gradlew clean && cd ..
```

2. Rebuild your project:
```bash
npx react-native run-ios
# or
npx react-native run-android
```

### Android: WebViewActivity not found

Make sure you've added the activity to your `AndroidManifest.xml`:

```xml
<activity
    android:name="com.rninbrowser.WebViewActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
```

## Use Cases

- **Opening external links** without leaving your app
- **Video conferencing** (Jitsi, Zoom web, Google Meet, etc.)
  - Track when users join/leave meetings via URL changes
- **OAuth flows** with redirect detection
  - Monitor callback URLs to complete authentication
- **Payment gateways** with success/failure tracking
  - Detect payment completion by watching for success URLs
- **Multi-step forms** and checkout flows
  - Track user progress through different pages
- **Deep link handling**
  - Intercept and handle custom URL schemes
- **Analytics and tracking**
  - Log page views and navigation patterns
- **Help/documentation** pages
- **Terms of service / Privacy policy**

## Requirements

- React Native >= 0.60.0
- iOS >= 12.0
- Android minSdkVersion >= 24

## License

MIT

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and feature requests, please use the [GitHub issue tracker](https://github.com/yourusername/react-native-in-browser/issues).
