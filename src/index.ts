import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-in-browser' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- Run 'pod install' in the ios directory\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// Native module interface
interface RNInBrowserAppInterface {
  open(
    url: string,
    options: Omit<InBrowserOptions, 'onUrlChange'>
  ): Promise<InBrowserResult>;
}

/**
 * Options for opening in-app browser
 */
export interface InBrowserOptions {
  /**
   * Show or hide the close button (default: true)
   * - iOS: Changes dismiss button style
   * - Android: Shows/hides the close button overlay
   */
  showCloseButton?: boolean;

  /**
   * Callback function called when URL changes
   * @param url - The new URL
   */
  onUrlChange?: (url: string) => void;
}

/**
 * Result returned when browser is closed
 */
export interface InBrowserResult {
  /**
   * How the browser was closed:
   * - 'close': User pressed the close button
   * - 'dismiss': User dismissed the browser (back button/gesture)
   */
  type: 'close' | 'dismiss';
}

// Get the native module
const RNInBrowserApp: RNInBrowserAppInterface = NativeModules.RNInBrowserApp
  ? NativeModules.RNInBrowserApp
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// Create event emitter
const eventEmitter = new NativeEventEmitter(NativeModules.RNInBrowserApp);

/**
 * Opens a URL in an in-app browser
 *
 * @param url - The URL to open
 * @param options - Optional configuration
 * @returns Promise that resolves with result when browser is closed
 *
 * @example
 * ```typescript
 * import { openInAppBrowser } from 'react-native-in-browser';
 *
 * const result = await openInAppBrowser('https://example.com');
 * console.log(result.type); // 'close' or 'dismiss'
 *
 * // With options and URL change listener
 * await openInAppBrowser('https://example.com', {
 *   showCloseButton: false,
 *   onUrlChange: (url) => {
 *     console.log('URL changed to:', url);
 *   }
 * });
 * ```
 */
export const openInAppBrowser = async (
  url: string,
  options?: InBrowserOptions
): Promise<InBrowserResult> => {
  const { onUrlChange, ...nativeOptions } = options || {};

  // Set up URL change listener if provided
  let urlChangeSubscription: any;
  if (onUrlChange) {
    urlChangeSubscription = eventEmitter.addListener('onUrlChange', (event: { url: string }) => {
      onUrlChange(event.url);
    });
  }

  try {
    const result = await RNInBrowserApp.open(url, nativeOptions);
    return result;
  } finally {
    // Clean up listener when browser closes
    if (urlChangeSubscription) {
      urlChangeSubscription.remove();
    }
  }
};

export {
  WebView,
  type WebViewProps,
  type WebViewRef,
  type WebViewLoadEvent,
  type WebViewErrorEvent,
  type WebViewNavigationEvent,
} from './WebView';

export default RNInBrowserApp;
