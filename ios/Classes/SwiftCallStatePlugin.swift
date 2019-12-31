import Flutter
import UIKit
import CoreTelephony

let CHANNEL = "com.zhangkong100.plugin/callstate"

public class SwiftCallStatePlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    private var instance: CTCallCenter = CTCallCenter()
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = SwiftCallStatePlugin()
        let channel = FlutterEventChannel(name: CHANNEL, binaryMessenger: registrar.messenger())
        channel.setStreamHandler(instance)
    }
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        var begin: Date = Date()
        var ended: Date = Date()
        instance.callEventHandler = { (call) in
            if call.callState == CTCallStateIncoming {
                // 电话进来了
            } else if call.callState == CTCallStateDialing {
                // 开始打电话
            } else if call.callState == CTCallStateConnected {
                // 电话接通了
                begin = Date()
            } else if call.callState == CTCallStateDisconnected {
                // 电话挂断了
                ended = Date()
                let time = Int(ended.timeIntervalSince1970 - begin.timeIntervalSince1970)
                events(time)
            } else {
                // 未知状态
            }
        }
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        instance.callEventHandler = nil
        return nil
    }
}
