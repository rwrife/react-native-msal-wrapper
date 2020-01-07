
package com.microsoft.rnmsalwrapper;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNMsalWrapperModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNMsalWrapperModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNMsalWrapper";
  }
}