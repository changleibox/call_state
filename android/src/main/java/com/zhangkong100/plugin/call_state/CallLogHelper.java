/*
 * Copyright © 2019 CHANGLEI. All rights reserved.
 */

package com.zhangkong100.plugin.call_state;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog.Calls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.util.Date;

/**
 * Created by box on 2017/11/29.
 * <p>
 * 获取通话记录
 */

class CallLogHelper {

    private static final String[] PROJECTION = {Calls.NUMBER, Calls.CACHED_NAME, Calls.TYPE, Calls.DATE, Calls.DURATION};

    @Nullable
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    static ICallLog getFirstCallLog(@NonNull Context context) {
        ContentResolver cr = context.getContentResolver();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        final Cursor cursor = cr.query(Calls.CONTENT_URI, PROJECTION, null, null, Calls.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));    //呼叫号码
        String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));   //联系人姓名
        int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));  //来电:1,拨出:2,未接:3
        long duration = cursor.getLong(cursor.getColumnIndex(Calls.DURATION));
        Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Calls.DATE))));
        cursor.close();

        return new ICallLog(type, duration, date, number, name);
    }

    @SuppressWarnings("unused")
    static class ICallLog {

        private int type;
        private long duration;
        private Date date;
        private String number;
        private String name;

        ICallLog(int type, long duration, Date date, String number, String name) {
            this.type = type;
            this.duration = duration;
            this.date = date;
            this.number = number;
            this.name = name;
        }

        int getType() {
            return type;
        }

        long getDuration() {
            return duration;
        }

        Date getDate() {
            return date;
        }

        String getNumber() {
            return number;
        }

        String getName() {
            return name;
        }

        boolean isMissed() {
            return type == Calls.MISSED_TYPE;
        }
    }
}
