package com.example.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通话记录工具类
 */
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
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
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
            map.put("name", (name == null) ? "未备注" : name);
            map.put("number", number);
            map.put("date", date);
            map.put("duration",duration.toString());
            map.put("type", typeString);
            myContacts.add(map);
        }
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

    /**
     * 计算出现次数最多的号码及其次数
     * @param strings
     * @return
     */
    public static String[] mostTouchNumber(String[] strings){
        Map<String,Integer> map = new HashMap<>();
        for (String string : strings) {
            map.merge(string, 1, Integer::sum);
        }
        Iterator iterator = map.entrySet().iterator();
        Map.Entry entry = (Map.Entry) iterator.next();
        String maxKey = (String) entry.getKey();
        int maxValue = (int) entry.getValue();
        while (iterator.hasNext()){
            entry = (Map.Entry) iterator.next();
            String tmpKey = (String) entry.getKey();
            int tmpV = (int) entry.getValue();
            if (maxValue < tmpV){
                maxKey = tmpKey;
                maxValue = tmpV;
            }
        }
        String value = Integer.toString(maxValue);
        String[] result = {maxKey,value};
        return result;
    }

    /**
     * 计算通话时间最长的记录
     * @param map
     * @return
     */
    public static String[] getCallTime(Map<String,Integer> map){
        Iterator iterator = map.entrySet().iterator();
        Map.Entry entry = (Map.Entry) iterator.next();
        String num = (String) entry.getKey();
        int value = (int) entry.getValue();
        while (iterator.hasNext()){
            entry = (Map.Entry) iterator.next();
            int tmp = (int) entry.getValue();
            if (value < tmp){
                num = (String) entry.getKey();
                value = tmp;
            }
        }
        String duration = Integer.toString(value);
        String[] res= {num,duration};
        return res;
    }

    /**
     * 通过号码获取联系人姓名
     * 数据来自CallLogs
     * @param context
     * @param phoneNum
     * @return
     */
    public static String getName(Context context,String phoneNum){
//        System.out.println("util number:"+phoneNum);
        String displayName = "未备注";
        String[] projection = { CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER };
        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                CallLog.Calls.NUMBER  + " =?",
                new String[]{phoneNum},  null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                displayName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                if (!TextUtils.isEmpty(displayName)) {
                    break;
                }
                cursor.close();
            }
        }
        return displayName;
    }
}
