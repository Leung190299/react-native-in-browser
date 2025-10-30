//
//  RNInBrowserWebView.m
//  react-native-in-browser
//
//  Created by Le Ung on 29/10/25.
//

#import "RNInBrowserWebView.h"

@interface RNInBrowserWebView ()
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, copy) NSString *lastReportedURL;
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

  // Observe URL changes to catch all navigations including hash/query param changes
  [self.webView addObserver:self forKeyPath:@"URL" options:NSKeyValueObservingOptionNew context:nil];

  [self addSubview:self.webView];
}

- (void)layoutSubviews {
  [super layoutSubviews];
  self.webView.frame = self.bounds;
}

- (void)dealloc {
  [self.webView removeObserver:self forKeyPath:@"URL"];
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary<NSKeyValueChangeKey,id> *)change
                       context:(void *)context {
  if ([keyPath isEqualToString:@"URL"] && object == self.webView) {
    NSString *newURL = self.webView.URL.absoluteString ?: @"";

    // Only fire onUrlChange if the URL actually changed
    if (self.onUrlChange && ![newURL isEqualToString:self.lastReportedURL]) {
      self.lastReportedURL = newURL;
      self.onUrlChange(@{
        @"url": newURL
      });
    }
  }
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
