import 'package:call_state/call_state.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _duration = 0;

  @override
  void initState() {
    super.initState();
    CallState.listener('13076000705', (duration) {
      _duration = duration;
      print(_duration);
      if (!mounted) {
        return;
      }
      setState(() {});
    }, onError: (error) {
      print('发生错误-$error');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('通话时间$_duration秒'),
              FlatButton(
                color: Colors.blue,
                onPressed: () {
                  launch('tel:13076000705');
                },
                child: Text('拨打'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
