# Integration Guide - react-native-in-browser

Hướng dẫn tích hợp thư viện `react-native-in-browser` vào dự án React Native.

## Cài đặt

### 1. Cài đặt package

#### Từ local (development)

Trong `package.json` của dự án chính, thêm:

```json
{
  "dependencies": {
    "react-native-in-browser": "file:./packages/react-native-in-browser"
  }
}
```

Sau đó chạy:

```bash
yarn install
# hoặc
npm install
```

#### Từ npm (production)

```bash
yarn add react-native-in-browser
# hoặc
npm install react-native-in-browser
```

### 2. Cấu hình Android

#### 2.1. Thêm Activity vào AndroidManifest.xml

Mở `android/app/src/main/AndroidManifest.xml` và thêm:

```xml
<application ...>
  <!-- Các activity khác -->

  <activity
    android:name="com.rninbrowser.WebViewActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"
    android:configChanges="orientation|screenSize" />
</application>
```

#### 2.2. Thêm permissions (tùy chọn - nếu cần camera/microphone)

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

#### 2.3. Đăng ký Package trong MainApplication

Mở `android/app/src/main/java/com/[yourapp]/MainApplication.kt` (hoặc `.java`):

**Kotlin:**
```kotlin
import com.rninbrowser.RNInBrowserAppPackage

class MainApplication : Application(), ReactApplication {
  override val reactNativeHost: ReactNativeHost =
      object : DefaultReactNativeHost(this) {
        override fun getPackages(): List<ReactPackage> =
            PackageList(this).packages.apply {
              // Thêm package ở đây
              add(RNInBrowserAppPackage())
            }
        // ...
      }
}
```

**Java:**
```java
import com.rninbrowser.RNInBrowserAppPackage;

public class MainApplication extends Application implements ReactApplication {
  @Override
  protected List<ReactPackage> getPackages() {
    List<ReactPackage> packages = new PackageList(this).getPackages();
    // Thêm package ở đây
    packages.add(new RNInBrowserAppPackage());
    return packages;
  }
}
```

### 3. Cấu hình iOS

#### 3.1. Cài đặt Pods

```bash
cd ios
pod install
cd ..
```

#### 3.2. Rebuild project

```bash
npx react-native run-ios
```

### 4. Build lại project

#### Android
```bash
cd android
./gradlew clean
cd ..
npx react-native run-android
```

#### iOS
```bash
cd ios
rm -rf Pods Podfile.lock
pod install
cd ..
npx react-native run-ios
```

## Sử dụng

### Import thư viện

```typescript
import { openInAppBrowser } from 'react-native-in-browser';
// hoặc
import RNInBrowserApp from 'react-native-in-browser';
```

### Ví dụ cơ bản

```typescript
import React from 'react';
import { Button, View } from 'react-native';
import { openInAppBrowser } from 'react-native-in-browser';

const MyComponent = () => {
  const handlePress = async () => {
    try {
      const result = await openInAppBrowser('https://example.com');
      console.log('Browser closed:', result.type);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <View>
      <Button title="Open Browser" onPress={handlePress} />
    </View>
  );
};
```

### Với options

```typescript
await openInAppBrowser('https://meet.jit.si/test-room', {
  showCloseButton: true
});
```

## Troubleshooting

### Android: "WebViewActivity not found"

Đảm bảo bạn đã:
1. Thêm `WebViewActivity` vào `AndroidManifest.xml`
2. Đăng ký `RNInBrowserAppPackage()` trong `MainApplication`
3. Clean và rebuild project

### iOS: Build errors

```bash
cd ios
rm -rf Pods Podfile.lock
pod install --repo-update
cd ..
npx react-native run-ios
```

### Module not found

```bash
# Xóa node_modules và reinstall
rm -rf node_modules
yarn install
# hoặc
npm install

# iOS: reinstall pods
cd ios && pod install && cd ..
```

### TypeScript errors

Đảm bảo TypeScript đã compile thư viện:

```bash
cd packages/react-native-in-browser
npm run build
cd ../..
```

## Xóa module cũ

Nếu bạn đã có module RNInBrowserApp trong code cũ:

1. Xóa các file cũ:
   - `android/app/src/main/java/com/rnskyfisdk/RNInBrowserAppModule.java`
   - `android/app/src/main/java/com/rnskyfisdk/RNInBrowserAppPackage.java`
   - `android/app/src/main/java/com/rnskyfisdk/WebViewActivity.java`
   - `ios/RNSkyFiSdk/RNInBrowserApp.swift`
   - `ios/RNSkyFiSdk/RNInBrowserApp.m`
   - `src/modules/RNInBrowserApp.ts`

2. Xóa đăng ký package cũ trong `MainApplication.kt`:
   ```kotlin
   // Xóa dòng này nếu có
   add(RNInBrowserAppPackage())  // từ com.rnskyfisdk
   ```

3. Update imports trong code:
   ```typescript
   // Cũ
   import RNInBrowserApp from '../../modules/RNInBrowserApp';

   // Mới
   import RNInBrowserApp from 'react-native-in-browser';
   ```

## Publish lên npm (optional)

Nếu muốn publish thư viện lên npm registry:

```bash
cd packages/react-native-in-browser

# Build TypeScript
npm run build

# Login npm
npm login

# Publish
npm publish
```

## Support

Để được hỗ trợ, vui lòng tạo issue trên GitHub repository.
