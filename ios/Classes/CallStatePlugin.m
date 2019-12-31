#import "CallStatePlugin.h"
#if __has_include(<call_state/call_state-Swift.h>)
#import <call_state/call_state-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "call_state-Swift.h"
#endif

@implementation CallStatePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCallStatePlugin registerWithRegistrar:registrar];
}
@end
