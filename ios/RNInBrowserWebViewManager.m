//
//  RNInBrowserWebViewManager.m
//  react-native-in-browser
//
//  Created by Le Ung on 29/10/25.
//

#import "RNInBrowserWebViewManager.h"
#import "RNInBrowserWebView.h"
#import <React/RCTUIManager.h>

@implementation RNInBrowserWebViewManager

RCT_EXPORT_MODULE(RNInBrowserWebView)

- (UIView *)view {
  return [[RNInBrowserWebView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(url, NSString)
RCT_EXPORT_VIEW_PROPERTY(onLoadStart, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadEnd, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onUrlChange, RCTDirectEventBlock)

RCT_EXPORT_METHOD(goBack:(nonnull NSNumber *)reactTag) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    RNInBrowserWebView *view = (RNInBrowserWebView *)viewRegistry[reactTag];
    if ([view isKindOfClass:[RNInBrowserWebView class]]) {
      [view goBack];
    }
  }];
}

RCT_EXPORT_METHOD(goForward:(nonnull NSNumber *)reactTag) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    RNInBrowserWebView *view = (RNInBrowserWebView *)viewRegistry[reactTag];
    if ([view isKindOfClass:[RNInBrowserWebView class]]) {
      [view goForward];
    }
  }];
}

RCT_EXPORT_METHOD(reload:(nonnull NSNumber *)reactTag) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    RNInBrowserWebView *view = (RNInBrowserWebView *)viewRegistry[reactTag];
    if ([view isKindOfClass:[RNInBrowserWebView class]]) {
      [view reload];
    }
  }];
}

RCT_EXPORT_METHOD(stopLoading:(nonnull NSNumber *)reactTag) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    RNInBrowserWebView *view = (RNInBrowserWebView *)viewRegistry[reactTag];
    if ([view isKindOfClass:[RNInBrowserWebView class]]) {
      [view stopLoading];
    }
  }];
}

@end
