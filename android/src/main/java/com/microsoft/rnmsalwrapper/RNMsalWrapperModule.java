
package com.microsoft.rnmsalwrapper;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.*;

import java.util.*;

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

  public interface applicationCallback {
      void onSuccess(IPublicClientApplication application);
      void onError(MsalException error);
  }

  @ReactMethod
  public void acquireToken(ReadableMap options, Callback successCallback) {
      Log.d("msal","acquire token");
    final ArrayList<Object> scopeObjectList = options.hasKey("scopes") ? options.getArray("scopes").toArrayList() : null;
    final String[] scopes = objectArrayListToStringArray(scopeObjectList);
    final String clientId = options.hasKey("clientId") ? options.getString("clientId") : null;
    final Activity currentActivity = this.getReactApplicationContext().getCurrentActivity();

    Log.d("msal", clientId);

    getApplication(clientId, new applicationCallback() {
        @Override
        public void onSuccess(IPublicClientApplication application) {
            Log.d("msal","acq_token");
            application.acquireToken(currentActivity, scopes, new AuthenticationCallback() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSuccess(IAuthenticationResult authenticationResult) {
                    Log.d("msal","acq_suc");
                }

                @Override
                public void onError(MsalException exception) {
                    Log.d("msal","acq_err");
                }
            });
        }

        @Override
        public void onError(MsalException error) {

        }
    });

  }

  @ReactMethod
  public void acquireTokenSilent(ReadableMap options, Callback successCallback) {
      ArrayList<Object> scopeObjectList = options.hasKey("scopes") ? options.getArray("scopes").toArrayList() : null;
      String[] scopes = objectArrayListToStringArray(scopeObjectList);
      String clientId = options.hasKey("clientId") ? options.getString("clientId") : null;
      String accountId = options.hasKey("accountId") ? options.getString("accountId") : null;

      getApplication(clientId, new applicationCallback() {
          @Override
          public void onSuccess(IPublicClientApplication application) {

          }

          @Override
          public void onError(MsalException error) {

          }
      });


  }

  @ReactMethod
  public void removeAccount(ReadableMap options, Callback successCallback) {

  }

  private String[] objectArrayListToStringArray(ArrayList<Object> objectList) {
      ArrayList<String> stringList = new ArrayList<String>();
      for(Object item: objectList) {
          stringList.add(item.toString());
      }

      String[] items = new String[stringList.size()];
      items = stringList.toArray(items);

      return items;
  }

  private void getApplication(final String clientId, final applicationCallback callback) {
      Log.d("msal","getapp " + clientId);

      PublicClientApplication.create(this.getReactApplicationContext(), clientId,
              new IPublicClientApplication.ApplicationCreatedListener() {
                  @Override
                  public void onCreated(IPublicClientApplication application) {
                      Log.d("msal","getapp_suc");
                      callback.onSuccess(application);
                  }

                  @Override
                  public void onError(MsalException exception) {
                      Log.d("msal","getapp_err"); callback.onError(exception);
                  }
              }
      );
  }
}