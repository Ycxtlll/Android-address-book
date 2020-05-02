package com.example.addressbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.telecom.Call;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContent("llii","9981556","154155122","20","3");
        getContacts();
    }


    private String TAG = "Address-Book: ";
    //申请未授权的权限列表
    private List<String> unPerimissionList = new ArrayList<>();
    //获取apk包名
    private String mPackName;
    private AlertDialog mPermissionDialog;
    //申请的权限列表
    private String[] permissionList = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    /**
     * 申请判断权限
     */
    public void checkPermission() {
        //清除未通过权限
        this.unPerimissionList.clear();
        //判断是否还有未通过的权限
        for (int i = 0; i < permissionList.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionList[i]) != PackageManager.PERMISSION_GRANTED) {
                unPerimissionList.add(permissionList[i]);
            }
        }
        //有未通过权限，申请
        if (unPerimissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionList, 100);
            Log.i(TAG, "有权限未通过");
        } else {
            Log.i(TAG, "权限全部通过");
        }
    }

    /**
     * 请求权限后回调的方法
     *
     * @param requestCode  自己定义的权限请求码
     * @param permissions  请求的权限名称数组
     * @param grantResults 在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，
     *                     数组的数据0表示允许权限，-1表示点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "申请结果反馈");
        boolean hasPermissionDismiss = false;
        if (100 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    //有权限没有通过
                    Log.i(TAG, "有权限没有被通过");
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {
            //如果有没有被允许的权限
            showPermissionDialog();
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.i(TAG, "onRequestPermissionsResult 权限都已经申请通过");
        }
    }

    /**
     * 提示申请权限的对话框
     */
    public void showPermissionDialog() {
        Log.i(TAG, "mPackName: " + mPackName);
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动设置授权")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            //去设置里面设置
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.
                                    ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            mPermissionDialog.cancel();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    /**
     * 获取通话记录
     * @return
     */
    public List<MyContacts> getContacts() {
        List<MyContacts> myContacts = new ArrayList<>();

        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        //获取通讯录须获得授权
        checkPermission();
        Cursor cursor = this.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER
        );
        Log.i(TAG,"count:"+cursor.getCount());

        while (cursor.moveToNext()){
            MyContacts c1 = new MyContacts();
            //获取name
            c1.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
            //获取电话号码
            c1.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
            //获取通话日期
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            System.out.println("date long:"+dateLong);
            c1.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong)));
            //获取通话时长
            c1.setDuration(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)));
            //获取通话状态
            c1.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
            System.out.println(c1);
            myContacts.add(c1);
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
    public void addContent(String name, String number, String date, String duration ,String type){
        Log.i(TAG,"增加通话记录");
        ContentValues values = new ContentValues();
        values.clear();
        values.put(CallLog.Calls.CACHED_NAME,name);
        values.put(CallLog.Calls.NUMBER,number);
        values.put(CallLog.Calls.DATE,date);
        values.put(CallLog.Calls.DURATION,duration);
        values.put(CallLog.Calls.TYPE,type);
        //需检查权限
        checkPermission();
        this.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }
}
