package com.tomridder.sms_app.utils;

import android.database.Cursor;

/**
 * <pre>
 * author: LiangTao
 * date  : 2018/11/30 17:17
 * -------------------------------
 * 万能的主啊！啊啊啊啊！！
 * 求您保佑我写下的代码没有bug
 * 求求您一定保佑我，求求您 ...
 * -------------------------------
 *
 * </pre>
 */
public final class CursorUtils {
    public final static Long getColumnLongValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if(index != -1) {
            return cursor.getLong(index);
        } else {
            return null;
        }
    }

    public final static String getColumnStringValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if(index != -1) {
            return cursor.getString(index);
        } else {
            return null;
        }
    }

    public final static Integer getColumnIntegerValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if(index != -1) {
            return cursor.getInt(index);
        } else {
            return null;
        }
    }
}
