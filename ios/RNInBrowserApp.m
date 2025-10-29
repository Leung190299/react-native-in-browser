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
@property (nonatomic, copy) RCTResponseSenderBlock urlChangeCallback;
@end

@implementation RNInBrowserApp

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(open:(NSString *)urlString options:(NSDictionary *)options urlChangeCallback:(RCTResponseSenderBlock)urlChangeCallback resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSURL *url = [NSURL URLWithString:urlString];
    if (!url) {
      reject(@"invalid_url", @"Invalid URL", nil);
      return;
    }

    // Store the resolve block to call it when closed
    self.openResolveBlock = resolve;

    // Store URL change callback
    self.urlChangeCallback = urlChangeCallback;

    // Tạo WKWebView
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    config.allowsInlineMediaPlayback = YES;
    config.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;

    self.webView = [[WKWebView alloc] initWithFrame:[UIScreen mainScreen].bounds configuration:config];
    self.webView.navigationDelegate = self;

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
    [self.webVC dismissViewControllerAnimated:YES completion:^{
      // Resolve with "close" when the screen is closed
      if (self.openResolveBlock) {
        self.openResolveBlock(@{@"type": @"close"});
        self.openResolveBlock = nil;
      }
      self.urlChangeCallback = nil;
      self.webVC = nil;
      self.webView = nil;
    }];
  }
}

#pragma mark - WKNavigationDelegate
- (void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation {
  // Called when URL changes (navigation committed)
  NSString *currentURL = webView.URL.absoluteString;
  NSLog(@"🔄 URL changed to: %@", currentURL);

  // Call the URL change callback if it exists
  if (self.urlChangeCallback && currentURL) {
    self.urlChangeCallback(@[currentURL]);
  }
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
  NSLog(@"✅ WebView finished loading: %@", webView.URL.absoluteString);
}

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

@end
