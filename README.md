# react-native-in-browser

A React Native native module for opening URLs in an in-app browser with native support for iOS and Android.

## Features

- ✅ **Cross-platform**: Works on both iOS and Android
- ✅ **Native experience**: Uses `SFSafariViewController` on iOS and custom WebView on Android
- ✅ **TypeScript**: Full TypeScript support with type definitions
- ✅ **Promise-based**: Modern async/await API
- ✅ **Customizable**: Configure close button visibility
- ✅ **Camera & Microphone**: Full support for WebRTC and media permissions (Android)

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

### Full Example

```typescript
import React from 'react';
import { Button, View, Alert } from 'react-native';
import { openInAppBrowser } from 'react-native-in-browser';

const App = () => {
  const handleOpenBrowser = async () => {
    try {
      const result = await openInAppBrowser('https://meet.jit.si/test-room', {
        showCloseButton: true
      });

      Alert.alert('Browser closed', `Type: ${result.type}`);
    } catch (error) {
      console.error('Error opening browser:', error);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Button title="Open Browser" onPress={handleOpenBrowser} />
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

#### Returns

`Promise<InBrowserResult>`

- `type`: `'close'` | `'dismiss'`
  - `'close'`: User pressed the close button
  - `'dismiss'`: User dismissed the browser (back button/gesture)

#### Example

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

## Platform Differences

### iOS

- Uses `SFSafariViewController` for a native Safari experience
- Includes Safari's built-in features (cookie sharing, autofill, etc.)
- Native iOS animations and gestures
- Camera/microphone permissions handled by Safari

### Android

- Custom full-screen WebView implementation
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
  showCloseButton: true
};

const result: InBrowserResult = await openInAppBrowser(
  'https://example.com',
  options
);
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

- Opening external links without leaving your app
- Embedding video conferencing (Jitsi, Zoom web, etc.)
- OAuth flows
- Payment gateways
- Help/documentation pages
- Terms of service / Privacy policy

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
