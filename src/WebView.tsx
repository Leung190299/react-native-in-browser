import React, {
  forwardRef,
  useImperativeHandle,
  useRef,
} from 'react';
import {
  requireNativeComponent,
  UIManager,
  findNodeHandle,
  StyleSheet,
  ViewStyle,
  NativeSyntheticEvent,
  StyleProp,
  View,
  Platform,
} from 'react-native';

/**
 * WebView load event data
 */
export interface WebViewLoadEvent {
  url: string;
  title?: string;
  canGoBack?: boolean;
  canGoForward?: boolean;
}

/**
 * WebView error event data
 */
export interface WebViewErrorEvent {
  url: string;
  code: number;
  description: string;
}

/**
 * WebView navigation event data
 */
export interface WebViewNavigationEvent {
  url: string;
}

/**
 * Props for the WebView component
 */
export interface WebViewProps {
  /**
   * The URL to load in the WebView
   */
  url: string;

  /**
   * Callback called when the WebView starts loading
   */
  onLoadStart?: (event: NativeSyntheticEvent<WebViewNavigationEvent>) => void;

  /**
   * Callback called when the WebView finishes loading
   */
  onLoadEnd?: (event: NativeSyntheticEvent<WebViewLoadEvent>) => void;

  /**
   * Callback called when the WebView encounters an error
   */
  onLoadError?: (event: NativeSyntheticEvent<WebViewErrorEvent>) => void;

  /**
   * Callback called when the URL changes
   */
  onUrlChange?: (event: NativeSyntheticEvent<WebViewNavigationEvent>) => void;

  /**
   * Style for the WebView container
   */
  style?: StyleProp<ViewStyle>;

  /**
   * Children to render on top of the WebView (e.g., loading indicators, overlays)
   */
  children?: React.ReactNode;
}

/**
 * Methods exposed via ref
 */
export interface WebViewRef {
  /**
   * Go back to the previous page
   */
  goBack: () => void;

  /**
   * Go forward to the next page
   */
  goForward: () => void;

  /**
   * Reload the current page
   */
  reload: () => void;

  /**
   * Stop loading the current page
   */
  stopLoading: () => void;
}

const RNInBrowserWebView = requireNativeComponent<WebViewProps>('RNInBrowserWebView');

// Helper function to dispatch commands
const dispatchCommand = (ref: any, commandName: string, commandId?: number) => {
  const handle = findNodeHandle(ref);
  if (handle) {
    if (Platform.OS === 'android' && commandId !== undefined) {
      // Android uses command IDs
      UIManager.dispatchViewManagerCommand(handle, commandId, []);
    } else {
      // iOS uses command names from UIManager config
      const command =
        // @ts-ignore
        UIManager.getViewManagerConfig('RNInBrowserWebView')?.Commands?.[commandName];
      if (command !== undefined) {
        UIManager.dispatchViewManagerCommand(handle, command, []);
      }
    }
  }
};

/**
 * WebView component for displaying web content
 *
 * @example
 * ```tsx
 * import { WebView, WebViewRef } from 'react-native-in-browser';
 *
 * function MyScreen() {
 *   const webViewRef = useRef<WebViewRef>(null);
 *
 *   return (
 *     <View style={{ flex: 1 }}>
 *       <WebView
 *         ref={webViewRef}
 *         url="https://example.com"
 *         onLoadEnd={(e) => console.log('Loaded:', e.nativeEvent.url)}
 *         onUrlChange={(e) => console.log('URL changed:', e.nativeEvent.url)}
 *       >
 *         <Button title="Go Back" onPress={() => webViewRef.current?.goBack()} />
 *       </WebView>
 *     </View>
 *   );
 * }
 * ```
 */
export const WebView = forwardRef<WebViewRef, WebViewProps>(
  ({ url, onLoadStart, onLoadEnd, onLoadError, onUrlChange, style, children }, ref) => {
    const nativeRef = useRef(null);

    useImperativeHandle(ref, () => ({
      goBack: () => {
        dispatchCommand(nativeRef.current, 'goBack', 1);
      },
      goForward: () => {
        dispatchCommand(nativeRef.current, 'goForward', 2);
      },
      reload: () => {
        dispatchCommand(nativeRef.current, 'reload', 3);
      },
      stopLoading: () => {
        dispatchCommand(nativeRef.current, 'stopLoading', 4);
      },
    }));

    return (
      <View style={[styles.container, style]}>
        <RNInBrowserWebView
          ref={nativeRef}
          url={url}
          onLoadStart={onLoadStart}
          onLoadEnd={onLoadEnd}
          onLoadError={onLoadError}
          onUrlChange={onUrlChange}
          style={styles.webView}
        />
        {children}
      </View>
    );
  }
);

WebView.displayName = 'WebView';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  webView: {
    flex: 1,
  },
});
