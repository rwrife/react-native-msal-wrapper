
#import "RNMsalWrapper.h"
#import <MSAL/MSAL.h>

@interface RNMsalWrapper() {
    id _params;
}
@end

@implementation RNMsalWrapper

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(auth:(id)json successCallback:(RCTResponseSenderBlock)successCallback errorCallback:(RCTResponseErrorBlock)errorCallback) {
    NSError *msalError = nil;
        
    MSALPublicClientApplicationConfig *config = [[MSALPublicClientApplicationConfig alloc] initWithClientId:@"d7d0748f-6b82-4463-8f6c-d3fed1985dcf"];
    NSArray<NSString *> *scopes = @[@"https://graph.windows.net//.default"];
        
    MSALPublicClientApplication *application = [[MSALPublicClientApplication alloc] initWithConfiguration:config error:&msalError];
        
    UIViewController *viewController = (UIViewController *)window.rootViewController;
    MSALWebviewParameters *webParameters = [[MSALWebviewParameters alloc] initWithParentViewController:viewController];
       
    MSALInteractiveTokenParameters *interactiveParams = [[MSALInteractiveTokenParameters alloc] initWithScopes:scopes webviewParameters:webParameters];
    [application acquireTokenWithParameters:interactiveParams completionBlock:^(MSALResult *result, NSError *error) {
        if (!error)
        {
            NSString *accountIdentifier = result.account.identifier;                
            NSString *accessToken = result.accessToken;

            successCallback(@[accessToken]);
        }
        else
        {
            errorCallback(@[]);
        }
    }];
}

- (id)initWithParams:(id)params successCallback:(RCTResponseSenderBlock)successCallback errorCallback:(RCTResponseErrorBlock)errorCallback {
    self = [super init];

    return self;
}

@end
  