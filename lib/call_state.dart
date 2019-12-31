import 'dart:async';

import 'package:flutter/services.dart';

class CallState {
  static const EventChannel _channel = const EventChannel('com.zhangkong100.plugin/callstate');

  static StreamSubscription<T> listener<T>(String phone, void onData(T event), {Function onError, void onDone(), bool cancelOnError}) {
    return _channel.receiveBroadcastStream({
      'phone': phone,
    }).listen(onData, onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }
}
