//
//  RNInBrowserApp.h
//  react-native-in-browser
//
//  Created by Le Ung on 28/10/25.
//

#import <React/RCTBridgeModule.h>
#import <WebKit/WebKit.h>

@interface RNInBrowserApp : NSObject <RCTBridgeModule, WKNavigationDelegate>
@end
