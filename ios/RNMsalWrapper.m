
#import "RNMsalWrapper.h"
#import <MSAL/MSAL.h>

@implementation RNMsalWrapper

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(acquireToken:(NSDictionary *) options successCallback:(RCTResponseSenderBlock)callback) {
    NSError *msalError = nil;

    NSArray<NSString *> *scopes = [options objectForKey:@"scopes"];
    NSString *clientId = [options objectForKey:@"clientId"];
        
    MSALPublicClientApplicationConfig *config = [[MSALPublicClientApplicationConfig alloc] initWithClientId:clientId];
    MSALPublicClientApplication *application = [[MSALPublicClientApplication alloc] initWithConfiguration:config error:&msalError];
        
    UIViewController *viewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    MSALWebviewParameters *webParameters = [[MSALWebviewParameters alloc] initWithParentViewController:viewController];
       
    MSALInteractiveTokenParameters *interactiveParams = [[MSALInteractiveTokenParameters alloc] initWithScopes:scopes webviewParameters:webParameters];
    [application acquireTokenWithParameters:interactiveParams completionBlock:^(MSALResult *result, NSError *error) {
        if (!error)
        {
            if(result && result.accessToken) {
                NSDictionary *accountClaims = [[NSMutableDictionary alloc] init];
                if(result.account.accountClaims) {
                    for(NSString *key in result.account.accountClaims.allKeys) {
                        if([result.account.accountClaims[key] isKindOfClass:[NSString class]]) {
                            [accountClaims setValue:result.account.accountClaims[key] forKey:key];
                        }
                    }
                }
                        
                NSDictionary *aadResult = @{
                    @"accessToken": result.accessToken,
                    @"accountId": result.account.identifier,
                    @"userName": result.account.username,
                    @"accountDetails": accountClaims
                };
                
                callback(@[[NSNull null], aadResult]);
            }
        }
        else
        {
            callback(@[error, [NSNull null]]);
        }
    }];
}

RCT_EXPORT_METHOD(acquireTokenSilent:(NSDictionary *) options successCallback:(RCTResponseSenderBlock) callback) {
    NSError *msalError = nil;
        
    NSString *clientId = [options objectForKey:@"clientId"];
    NSArray<NSString *> *scopes = [options objectForKey:@"scopes"];
    NSString *accountIdentifier = [options objectForKey:@"accountId"];
    
    MSALPublicClientApplicationConfig *config = [[MSALPublicClientApplicationConfig alloc] initWithClientId:clientId];
    MSALPublicClientApplication *application = [[MSALPublicClientApplication alloc] initWithConfiguration:config error:&msalError];
    
    MSALAccount *account = [application accountForIdentifier:accountIdentifier error:&msalError];
    if (!account)
    {
        return;
    }

    MSALSilentTokenParameters *silentParams = [[MSALSilentTokenParameters alloc] initWithScopes:scopes account:account];
    [application acquireTokenSilentWithParameters:silentParams completionBlock:^(MSALResult *result, NSError *error) {
        if (!error)
        {
            if(result && result.accessToken) {
                NSDictionary *aadResult = @{
                    @"accessToken": result.accessToken,
                    @"accountId": result.account.identifier,
                    @"userName": result.account.username,
                    @"accountDetails": result.account.accountClaims
                };
                
                callback(@[[NSNull null], aadResult]);
            }
        }
        else
        {
            if ([error.domain isEqual:MSALErrorDomain] && error.code == MSALErrorInteractionRequired)
            {
                // Interactive auth will be required
            }
                
            callback(@[error, [NSNull null]]);
        }
    }];
}

RCT_EXPORT_METHOD(removeAccount:(NSDictionary *) options successCallback:(RCTResponseSenderBlock) callback) {
    NSError *msalError = nil;
        
    NSString *clientId = [options objectForKey:@"clientId"];
    NSString *accountIdentifier = [options objectForKey:@"accountId"];
    
    MSALPublicClientApplicationConfig *config = [[MSALPublicClientApplicationConfig alloc] initWithClientId:clientId];
    MSALPublicClientApplication *application = [[MSALPublicClientApplication alloc] initWithConfiguration:config error:&msalError];
    
    MSALAccount *account = [application accountForIdentifier:accountIdentifier error:&msalError];
    if (!account)
    {
        return;
    }

    [application removeAccount:account error:&msalError];
    if(!msalError) {
        callback(@[[NSNull null]]);
    } else {
        callback(@[msalError]);
    }
}


@end
  
