import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:call_state/call_state.dart';

void main() {
  const MethodChannel channel = MethodChannel('call_state');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(CallState.listener(null, (state) {}), '42');
  });
}
