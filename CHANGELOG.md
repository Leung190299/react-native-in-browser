# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2024-10-28

### Added
- Initial release of react-native-in-browser
- iOS support using SFSafariViewController
- Android support using custom WebView with full-screen presentation
- TypeScript support with full type definitions
- Camera and microphone permission handling for Android
- Configurable close button visibility
- Promise-based API with result callbacks
- Complete documentation and integration guides

### Features
- Cross-platform support (iOS 12.0+ and Android API 24+)
- Native browser experience on both platforms
- WebRTC support for video conferencing applications
- Customizable options for UI elements
- Proper error handling and promise resolution

### Platform Implementations

#### iOS
- Uses `SFSafariViewController` for native Safari experience
- Full support for web features via Safari
- Native iOS dismiss animations and gestures
- Automatic handling of permissions

#### Android
- Custom WebView with JavaScript and DOM storage enabled
- Automatic permission requests for camera and microphone
- Customizable close button overlay
- Full-screen presentation mode
- Support for media playback without user gesture

### Documentation
- Comprehensive README with examples
- Integration guide for both platforms
- API documentation with TypeScript types
- Troubleshooting section for common issues
