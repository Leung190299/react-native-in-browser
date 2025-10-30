//
//  RNInBrowserApp.m
//  react-native-in-browser
//
//  Created by Le Ung on 28/10/25.
//

#import "RNInBrowserApp.h"
#import <React/RCTUtils.h>

@interface RNInBrowserApp ()
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, strong) UIViewController *webVC;
@property (nonatomic, copy) RCTPromiseResolveBlock openResolveBlock;
@property (nonatomic, copy) NSString *lastTrackedURL;
@end

@implementation RNInBrowserApp

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(open:(NSString *)urlString options:(NSDictionary *)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSURL *url = [NSURL URLWithString:urlString];
    if (!url) {
      reject(@"invalid_url", @"Invalid URL", nil);
      return;
    }

    // Store the resolve block to call it when closed
    self.openResolveBlock = resolve;

    // Tạo WKWebView
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    config.allowsInlineMediaPlayback = YES;
    config.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;

    self.webView = [[WKWebView alloc] initWithFrame:[UIScreen mainScreen].bounds configuration:config];
    self.webView.navigationDelegate = self;

    // Add KVO observer to track URL changes (including params)
    [self.webView addObserver:self forKeyPath:@"URL" options:NSKeyValueObservingOptionNew context:nil];

    // Load URL
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];

    // Tạo UIViewController để chứa webview
    self.webVC = [UIViewController new];
    self.webVC.view = self.webView;
    self.webVC.modalPresentationStyle = UIModalPresentationFullScreen;

    // Nếu muốn có nút "Close":
    BOOL showCloseButton = options[@"showCloseButton"] ? [options[@"showCloseButton"] boolValue] : YES;
    if (showCloseButton) {
      UIButton *closeButton = [UIButton buttonWithType:UIButtonTypeSystem];
      [closeButton setTitle:@"✕" forState:UIControlStateNormal];
      closeButton.frame = CGRectMake(20, 50, 40, 40);
      [closeButton addTarget:self action:@selector(closeWebView) forControlEvents:UIControlEventTouchUpInside];
      [self.webVC.view addSubview:closeButton];
    }

    UIViewController *root = RCTPresentedViewController();
    [root presentViewController:self.webVC animated:YES completion:nil];
    // Don't resolve here, will resolve when closed
  });
}

- (void)closeWebView {
  if (self.webVC) {
    // Remove KVO observer before dismissing
    if (self.webView) {
      [self.webView removeObserver:self forKeyPath:@"URL"];
    }

    [self.webVC dismissViewControllerAnimated:YES completion:^{
      // Resolve with "close" when the screen is closed
      if (self.openResolveBlock) {
        self.openResolveBlock(@{@"type": @"close"});
        self.openResolveBlock = nil;
      }
      self.webVC = nil;
      self.webView = nil;
      self.lastTrackedURL = nil;
    }];
  }
}

#pragma mark - KVO for URL changes (including params)
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
  if ([keyPath isEqualToString:@"URL"] && object == self.webView) {
    NSString *currentURL = self.webView.URL.absoluteString;

    // Only emit if URL actually changed (avoid duplicates)
    if (currentURL && ![currentURL isEqualToString:self.lastTrackedURL]) {
      self.lastTrackedURL = currentURL;
      NSLog(@"🔄 URL changed to: %@", currentURL);
      [self sendEventWithName:@"onUrlChange" body:@{@"url": currentURL}];
    }
  }
}

#pragma mark - WKNavigationDelegate
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
  NSLog(@"✅ WebView finished loading: %@", webView.URL.absoluteString);
}

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

- (NSArray<NSString *> *)supportedEvents {
  return @[@"onUrlChange"];
}

@end
