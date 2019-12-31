package com.zhangkong100.plugin.call_state;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tbruyelle.rxpermissions.RxPermissions;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import rx.functions.Action1;

/**
 * CallStatePlugin
 */
public class CallStatePlugin implements FlutterPlugin, EventChannel.StreamHandler {
    private static final String CHANNEL = "com.zhangkong100.plugin/callstate";

    private Context mContext;
    private EventChannel mEventChannel;
    @Nullable
    private CallStateReceiver mCallStateReceiver;

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final CallStatePlugin callStatePlugin = new CallStatePlugin();
        callStatePlugin.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        this.mContext = applicationContext;
        mEventChannel = new EventChannel(messenger, CHANNEL);
        mEventChannel.setStreamHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        mContext = null;
        mEventChannel.setStreamHandler(null);
        mEventChannel = null;
    }

    @Override
    public void onListen(final Object arguments, final EventChannel.EventSink events) {
        RxPermissions.getInstance(mContext)
                .request(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            mCallStateReceiver = new CallStateReceiver(arguments, events);
                            final IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
                            intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
                            mContext.registerReceiver(mCallStateReceiver, intentFilter);
                        } else {
                            events.error("UNAVAILABLE", "不支持监听电话状态", null);
                        }
                    }
                });
    }

    @Override
    public void onCancel(Object arguments) {
        if (mCallStateReceiver != null) {
            mContext.unregisterReceiver(mCallStateReceiver);
        }
    }
}
