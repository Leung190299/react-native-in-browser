# Quick Start - react-native-in-browser

Hướng dẫn nhanh để bắt đầu sử dụng thư viện.

## 📦 Cài đặt

### Bước 1: Thêm vào package.json

```json
{
  "dependencies": {
    "react-native-in-browser": "file:./packages/react-native-in-browser"
  }
}
```

### Bước 2: Cài đặt dependencies

```bash
yarn install
```

### Bước 3: Cấu hình Android

**AndroidManifest.xml:**
```xml
<activity
  android:name="com.rninbrowser.WebViewActivity"
  android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
```

**MainApplication.kt:**
```kotlin
import com.rninbrowser.RNInBrowserAppPackage

override fun getPackages(): List<ReactPackage> =
    PackageList(this).packages.apply {
      add(RNInBrowserAppPackage())
    }
```

### Bước 4: Cấu hình iOS

```bash
cd ios && pod install && cd ..
```

## 🚀 Sử dụng

### Import

```typescript
import { openInAppBrowser } from 'react-native-in-browser';
```

### Mở URL

```typescript
const result = await openInAppBrowser('https://example.com');
console.log(result.type); // 'close' hoặc 'dismiss'
```

### Với options

```typescript
await openInAppBrowser('https://example.com', {
  showCloseButton: false
});
```

## 📱 Test

1. Build lại app:
   ```bash
   # Android
   npx react-native run-android

   # iOS
   npx react-native run-ios
   ```

2. Sử dụng InAppBrowserTestScreen để test các tính năng

## 🔧 Troubleshooting

### Module không tìm thấy
```bash
rm -rf node_modules && yarn install
```

### Android build error
```bash
cd android && ./gradlew clean && cd ..
```

### iOS build error
```bash
cd ios && rm -rf Pods Podfile.lock && pod install && cd ..
```

## 📚 Docs đầy đủ

- [README.md](./README.md) - Documentation đầy đủ
- [INTEGRATION_GUIDE.md](./INTEGRATION_GUIDE.md) - Hướng dẫn tích hợp chi tiết
- [CHANGELOG.md](./CHANGELOG.md) - Lịch sử thay đổi

## ✨ Ví dụ hoàn chỉnh

```typescript
import React from 'react';
import { View, Button, Alert } from 'react-native';
import { openInAppBrowser } from 'react-native-in-browser';

export default function App() {
  const handleOpenMeeting = async () => {
    try {
      const result = await openInAppBrowser(
        'https://meet.jit.si/test-room',
        { showCloseButton: true }
      );

      Alert.alert('Closed', `Browser closed: ${result.type}`);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Button title="Join Meeting" onPress={handleOpenMeeting} />
    </View>
  );
}
```

## 🎯 Use Cases

- ✅ Video conferencing (Jitsi, Zoom web)
- ✅ OAuth authentication
- ✅ Payment gateways
- ✅ External content/documentation
- ✅ Terms of service / Privacy policy
- ✅ Any web content with native look & feel

## 💡 Tips

1. **Camera/Microphone**: Đảm bảo đã thêm permissions trong AndroidManifest.xml
2. **Full-screen**: WebView sẽ tự động full-screen trên Android
3. **Native feel**: iOS sử dụng SFSafariViewController cho trải nghiệm native
4. **TypeScript**: Thư viện có full TypeScript support

## ⚡ Performance

- Lightweight (~15KB gzipped)
- Native implementation (không dùng JavaScript bridge nặng)
- Tối ưu cho video conferencing
- Hỗ trợ WebRTC out of the box

## 🆘 Support

Nếu gặp vấn đề, check:
1. [INTEGRATION_GUIDE.md](./INTEGRATION_GUIDE.md)
2. [README.md](./README.md) - Troubleshooting section
3. Console logs để debug

Happy coding! 🎉
