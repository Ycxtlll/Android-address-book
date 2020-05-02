package com.example.addressbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        //ContactsUtil.addContent(this,"lttyy","911119","1588379390990","200","1");
        button1 = findViewById(R.id.read_contacts);
        button1.setOnClickListener(this);
        button2 = findViewById(R.id.most_contact);
        button2.setOnClickListener(this);
    }

    private String TAG = "Address-Book: ";

    @Override
    public void onClick(View v) {
        List<Map<String,String>> datalist;
        //获得授权才可以点击
        if (checkPermission()){
            if (v.getId() == R.id.read_contacts) {
                Log.i(TAG, "读取通话记录：");
                datalist =ContactsUtil.getContacts(this);
                //datalist.forEach(System.out::println);
                viewCk(datalist);
            }
            if (v.getId() == R.id.most_contact){
                Log.i(TAG,"通话次数最多或时间最长:");
                datalist =ContactsUtil.getContacts(this);
                mostTalk(datalist);
            }
        }
    }

    /**
     * 获取通话次数最多的记录
     * @param datalist
     */
    public void mostTalk(List<Map<String,String>> datalist){

    }

    /**
     * 展示listview
     * @param datalist
     */
    public void viewCk(List<Map<String,String>> datalist){
        ListView lv = findViewById(R.id.contents_item_five);
        List<Map<String,String>> ff = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ff.add(datalist.get(i));
        }
        setView(lv,ff);
        if (datalist.size()>5){
            List<Map<String,String>> ll = new ArrayList<>();
            ListView llv = findViewById(R.id.contents_item_old);
            for (int i = 5; i < datalist.size(); i++) {
                ll.add(datalist.get(i));
            }
            setView(llv,ll);
            TextView textView = findViewById(R.id.tv_old);
            textView.setText("较早些时候");
        }
    }

    /**
     * 抽象adapter
     * @param lv
     * @param datalist
     */
    public void setView(ListView lv, List<Map<String,String>> datalist){
        SimpleAdapter adapter = new SimpleAdapter(this,datalist, R.layout.contact_items,
                new String[]{"name","number","date","duration","type"},
                new int[]{R.id.tv_name,R.id.tv_number,R.id.tv_date,R.id.tv_duration,R.id.tv_type});
        lv.setAdapter(adapter);
    }


    //申请未授权的权限列表
    private List<String> unPerimissionList = new ArrayList<>();
    //获取apk包名
    private String myPackName = "com.example.addressbook";
    private AlertDialog mPermissionDialog;
    //申请的权限列表
    private String[] permissionList = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    /**
     * 申请判断权限
     */
    public boolean checkPermission() {
        boolean checked = false;
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
            checked = true;
        }
        return checked;
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
            Log.i(TAG, "onRequestPermissionsResult: 权限都已经申请通过");
        }
    }

    /**
     * 提示申请权限的对话框
     */
    public void showPermissionDialog() {
        Log.i(TAG,"PackName: " + myPackName);
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("申请授权失败，请手动设置授权")
                    .setPositiveButton("设置", (dialog, which) -> {
                        //跳转到设置
                        Uri packageURI = Uri.parse("package:" + myPackName);
                        Intent intent = new Intent(Settings.
                                ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        //关闭对话框
                        mPermissionDialog.cancel();
                    })
                    .create();
        }
        mPermissionDialog.show();
    }


}
