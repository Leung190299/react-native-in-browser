//
//  RNInBrowserWebView.m
//  react-native-in-browser
//
//  Created by Le Ung on 29/10/25.
//

#import "RNInBrowserWebView.h"

@interface RNInBrowserWebView ()
@property (nonatomic, strong) WKWebView *webView;
@end

@implementation RNInBrowserWebView

- (instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    [self setupWebView];
  }
  return self;
}

- (void)setupWebView {
  WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
  config.allowsInlineMediaPlayback = YES;
  config.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;

  self.webView = [[WKWebView alloc] initWithFrame:self.bounds configuration:config];
  self.webView.navigationDelegate = self;
  self.webView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
  self.webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;

  [self addSubview:self.webView];
}

- (void)layoutSubviews {
  [super layoutSubviews];
  self.webView.frame = self.bounds;
}

- (void)setUrl:(NSString *)url {
  _url = url;
  if (url && url.length > 0) {
    NSURL *nsUrl = [NSURL URLWithString:url];
    if (nsUrl) {
      NSURLRequest *request = [NSURLRequest requestWithURL:nsUrl];
      [self.webView loadRequest:request];
    }
  }
}

#pragma mark - Public Methods

- (void)goBack {
  if ([self.webView canGoBack]) {
    [self.webView goBack];
  }
}

- (void)goForward {
  if ([self.webView canGoForward]) {
    [self.webView goForward];
  }
}

- (void)reload {
  [self.webView reload];
}

- (void)stopLoading {
  [self.webView stopLoading];
}

#pragma mark - WKNavigationDelegate

- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
  if (self.onLoadStart) {
    self.onLoadStart(@{
      @"url": webView.URL.absoluteString ?: @""
    });
  }
}

- (void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation {
  if (self.onUrlChange) {
    self.onUrlChange(@{
      @"url": webView.URL.absoluteString ?: @""
    });
  }
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
  if (self.onLoadEnd) {
    self.onLoadEnd(@{
      @"url": webView.URL.absoluteString ?: @"",
      @"title": webView.title ?: @"",
      @"canGoBack": @([webView canGoBack]),
      @"canGoForward": @([webView canGoForward])
    });
  }
}

- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error {
  if (self.onLoadError) {
    self.onLoadError(@{
      @"url": webView.URL.absoluteString ?: @"",
      @"code": @(error.code),
      @"description": error.localizedDescription
    });
  }
}

- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
  if (self.onLoadError) {
    self.onLoadError(@{
      @"url": webView.URL.absoluteString ?: @"",
      @"code": @(error.code),
      @"description": error.localizedDescription
    });
  }
}

@end
