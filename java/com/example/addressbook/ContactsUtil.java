package com.example.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsUtil {

    private static String TAG = "Address-Book: ";
    private static final Uri uri = Uri.parse("content://call_log/calls");

    /**
     * 获取通话记录
     * @return
     */
    public static List<Map<String, String>> getContacts(Context context) {
        List<Map<String, String>> myContacts = new ArrayList<>();
        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        //获取通讯录须获得授权
        //checkPermission();
        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                //按时间逆序排序，最新的在前
                CallLog.Calls.DEFAULT_SORT_ORDER
        );
        Log.i(TAG,"count:"+cursor.getCount());

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            Long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(dateLong));
            Integer duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            Integer type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "呼入";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "呼出";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "未接";
                    break;
                default:
                    break;
            }
            Map<String, String> map = new HashMap<>();
            map.put("name", (name == null) ? "未备注联系人" : name);
            map.put("number", number);
            map.put("date", date);
            if (duration > 60) {
                map.put("duration", (duration / 60) + "分钟");
            }else {
                map.put("duration", duration  + "秒");
            }
            map.put("type", typeString);
            myContacts.add(map);
        }
        myContacts.forEach(System.out::println);
        //关闭cursor
        cursor.close();
        return myContacts;
    }

    /**
     * 增加通话记录用于测试
     * @param name
     * @param number
     * @param date   长整型，自1970-01-01开始以毫秒计算
     * @param duration
     * @param type  1 呼出，2 呼出， 3未接
     */
    public static void addContent(Context context, String name, String number, String date, String duration ,String type){
        Log.i(TAG,"增加通话记录");
        ContentValues values = new ContentValues();
        values.clear();
        values.put(CallLog.Calls.CACHED_NAME,name);
        values.put(CallLog.Calls.NUMBER,number);
        values.put(CallLog.Calls.DATE,date);
        values.put(CallLog.Calls.DURATION,duration);
        values.put(CallLog.Calls.TYPE,type);

        context.getContentResolver().insert(uri, values);
    }
}
