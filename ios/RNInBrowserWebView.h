//
//  RNInBrowserWebView.h
//  react-native-in-browser
//
//  Created by Le Ung on 29/10/25.
//

#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import <React/RCTComponent.h>

@interface RNInBrowserWebView : UIView <WKNavigationDelegate>

@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) RCTDirectEventBlock onLoadStart;
@property (nonatomic, copy) RCTDirectEventBlock onLoadEnd;
@property (nonatomic, copy) RCTDirectEventBlock onLoadError;
@property (nonatomic, copy) RCTDirectEventBlock onUrlChange;

- (void)goBack;
- (void)goForward;
- (void)reload;
- (void)stopLoading;

@end
