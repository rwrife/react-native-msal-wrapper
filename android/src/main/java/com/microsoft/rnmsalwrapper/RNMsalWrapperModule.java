
package com.microsoft.rnmsalwrapper;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
      void onSuccess(IMultipleAccountPublicClientApplication application);
      void onError(MsalException error);
  }

  @ReactMethod
  public void acquireToken(final ReadableMap options, final Callback successCallback) {
      Log.d("msal","acquire token");
    final ArrayList<Object> scopeObjectList = options.hasKey("scopes") ? options.getArray("scopes").toArrayList() : null;
    final String[] scopes = objectArrayListToStringArray(scopeObjectList);
    final String clientId = options.hasKey("clientId") ? options.getString("clientId") : null;
    final String redirectUri = options.hasKey("redirectUri") ? options.getString("clientId")
            : buildRedirectUri().toString();
    final Activity currentActivity = this.getReactApplicationContext().getCurrentActivity();

    Log.d("msal", clientId);

    getApplication(clientId, redirectUri, new applicationCallback() {
        @Override
        public void onSuccess(IMultipleAccountPublicClientApplication application) {
            Log.d("msal","acq_token");

            AcquireTokenParameters interactiveParameters = new AcquireTokenParameters.Builder()
                    .startAuthorizationFromActivity(getCurrentActivity())
                    .withScopes(Arrays.asList(scopes))
                    .withCallback(getAuthenticationCallback(successCallback))
                    .build();

            application.acquireToken(interactiveParameters);
        }

        @Override
        public void onError(MsalException error) {
            Log.d("msal", error.getMessage());
        }
    });

  }

  @ReactMethod
  public void acquireTokenSilent(final ReadableMap options, final Callback successCallback) {
      final ArrayList<Object> scopeObjectList = options.hasKey("scopes") ? options.getArray("scopes").toArrayList() : null;
      final String[] scopes = objectArrayListToStringArray(scopeObjectList);
      final String clientId = options.hasKey("clientId") ? options.getString("clientId") : null;
      final String accountId = options.hasKey("accountId") ? options.getString("accountId") : null;
      final String redirectUri = options.hasKey("redirectUri") ? options.getString("clientId")
              : buildRedirectUri().toString();

      getApplication(clientId, redirectUri, new applicationCallback() {
          @Override
          public void onSuccess(final IMultipleAccountPublicClientApplication application) {
            if(application != null) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IAccount account = application.getAccount(accountId);

                            AcquireTokenSilentParameters silentParameters = new AcquireTokenSilentParameters.Builder()
                                    .forAccount(account)
                                    .fromAuthority(application.getConfiguration().getDefaultAuthority().getAuthorityUri().toString())
                                    .withScopes(Arrays.asList(scopes))
                                    .withCallback(getAuthenticationCallback(successCallback))
                                    .build();

                            application.acquireTokenSilentAsync(silentParameters);
                        } catch (MsalException ex) {

                        } catch (InterruptedException ex) {

                        }
                    }
                });


            }
          }

          @Override
          public void onError(MsalException error) {

          }
      });


  }

    private AuthenticationCallback getAuthenticationCallback(final Callback successCallback) {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                if(authenticationResult != null && authenticationResult.getAccessToken() != null) {

                    final IAccount cachedAccount = authenticationResult.getAccount();

                    WritableMap response = new WritableNativeMap();

                    response.putString("accessToken", authenticationResult.getAccessToken());
                    response.putString("accountId", cachedAccount.getId());
                    response.putString("userName", cachedAccount.getUsername());

                    WritableMap accountDetails = new WritableNativeMap();
                    Map<String, ?> claims = cachedAccount.getClaims();
                    for(String claimName: claims.keySet()) {
                        if(claims.get(claimName) instanceof String) {
                            accountDetails.putString(claimName, ((String) claims.get(claimName)));
                        }
                    }
                    response.putMap("accountDetails", accountDetails);

                    successCallback.invoke(null, response);
                }
            }

            @Override
            public void onError(MsalException exception) {

            }

            @Override
            public void onCancel() {
                // User cancelled the flow
            }
        };
    }

  @ReactMethod
  public void removeAccount(ReadableMap options, Callback successCallback) {

      /// do STUFF
    successCallback.invoke();
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

  private Uri buildRedirectUri() {
      final String packageName = this.getReactApplicationContext().getPackageName();
      final Uri.Builder builder = new Uri.Builder();
      final Uri uri = builder.scheme("msauth")
              .authority(packageName)
              .appendPath(getSignatureHash())
              .build();
      return uri;
  }

  private String getSignatureHash() {
      final String packageName = this.getReactApplicationContext().getPackageName();
      try {
          final PackageInfo info = this.getReactApplicationContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
          if(info.signatures.length > 0) {
              Signature sig = info.signatures[0];
              final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
              messageDigest.update(sig.toByteArray());
              final String signatureHash = android.util.Base64.encodeToString(messageDigest.digest(), Base64.NO_WRAP);
              return signatureHash;
          }
      } catch (PackageManager.NameNotFoundException nnfe) {

      } catch (NoSuchAlgorithmException nsae) {

      }

      return "";
  }

  private void getApplication(final String clientId, final String redirectUri, final applicationCallback callback) {
      MultipleAccountPublicClientApplication.create(this.getReactApplicationContext(), clientId, null, redirectUri,
              new IPublicClientApplication.ApplicationCreatedListener() {
                  @Override
                  public void onCreated(IPublicClientApplication application) {
                      callback.onSuccess((IMultipleAccountPublicClientApplication) application);
                  }

                  @Override
                  public void onError(MsalException ex) {
                      callback.onError(ex);
                  }
              }
      );
  }


}