package com.zhangkong100.plugin.call_state;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import rx.functions.Action1;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
class CallStateReceiver extends BroadcastReceiver implements Handler.Callback {
    private static boolean isOutgoingCall;
    private static boolean isFirstCall;

    private final Object arguments;
    private final EventChannel.EventSink events;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), this);

    CallStateReceiver(Object arguments, EventChannel.EventSink events) {
        this.arguments = arguments;
        this.events = events;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (tManager == null) {
            events.error("UNAVAILABLE", "不支持监听电话状态", null);
            return;
        }
        final String action = intent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_NEW_OUTGOING_CALL) || TextUtils.equals(action, TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            isOutgoingCall = true;
        }
        tManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: // 挂断
                        if (isOutgoingCall && isFirstCall) {
                            isOutgoingCall = false;
                            mHandler.removeMessages(0);
                            final Message message = new Message();
                            message.obj = context;
                            mHandler.sendMessageDelayed(message, 1000);
                        }
                        isFirstCall = false;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: // 接听
                        isFirstCall = true;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: // 来电
                        break;
                    default:
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void getLeastRecord(final Context context) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.READ_CALL_LOG)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            try {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                    events.error("NOT_OERMISSION", "没有获取记录权限", null);
                                    return;
                                }

                                final CallLogHelper.ICallLog firstCallLog = CallLogHelper.getFirstCallLog(context);
                                if (firstCallLog == null || firstCallLog.getType() != 2) {
                                    events.error("READ_LOG_FALURE", "获取通话记录失败", null);
                                    return;
                                }

                                Object callPhone = null;
                                if (arguments instanceof Map) {
                                    callPhone = ((Map) arguments).get("phone");
                                }
                                final String number = firstCallLog.getNumber();
                                if (callPhone != null && callPhone.toString().equals(number)) {
                                    events.success(firstCallLog.getDuration());
                                } else {
                                    events.error("READ_LOG_FALURE", "获取通话记录失败", null);
                                }
                            } catch (Exception e) {
                                events.error("READ_LOG_FALURE", "获取通话记录失败", null);
                            }
                        } else {
                            events.error("NOT_OERMISSION", "没有获取记录权限", null);
                        }
                    }
                });
    }

    @Override
    public boolean handleMessage(Message msg) {
        getLeastRecord((Context) msg.obj);
        return false;
    }
}