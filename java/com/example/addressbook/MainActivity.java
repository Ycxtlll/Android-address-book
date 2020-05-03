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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查授权
        checkPermission();

        //Long time = System.currentTimeMillis();
        //ContactsUtil.addContent(this,"oty","123756",time.toString(),"60","2");

        Button button1 = findViewById(R.id.read_contacts);
        button1.setOnClickListener(this);
        Button button2 = findViewById(R.id.most_contact);
        button2.setOnClickListener(this);
        Button button3 = findViewById(R.id.max_length);
        button3.setOnClickListener(this);
    }

    private String TAG = "Address-Book: ";

    @Override
    public void onClick(View v) {
        List<Map<String,String>> datalist;
        //获得授权才可以点击
        if (checkPermission()){
            datalist =ContactsUtil.getContacts(this);
            if (v.getId() == R.id.read_contacts) {
                Log.i(TAG, "读取通话记录：");
                viewCk(datalist);
            }
            if (v.getId() == R.id.most_contact){
                Log.i(TAG,"通话次数最多:");
                mostTalk(datalist);
            }
            if (v.getId() == R.id.max_length){
                Log.i(TAG,"通话时长最长:");
                longestCall(datalist);
            }
        }
    }

    private AlertDialog conDialog;
    /**
     * 获取通话时长最长的记录
     * @param datalist
     */
    public void longestCall(List<Map<String,String>> datalist){
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < datalist.size(); i++) {
            map.put(datalist.get(i).get("number"),
                    Integer.parseInt(datalist.get(i).get("duration")));
        }
        String[] longC = ContactsUtil.getCallTime(map);
        System.out.println("num:"+longC[0]+",length:"+longC[1]);
        String name = ContactsUtil.getName(this,longC[0]);
        //清空会话框
        conDialog = null;
        contactDialog("您与他的通话时间最久-> ", name, longC[0]);
    }

    /**
     * 获取通话次数最多的记录
     * @param datalist
     */
    public void mostTalk(List<Map<String,String>> datalist){
        String[] numberArr = new String[datalist.size()];
        for (int i = 0; i < datalist.size(); i++) {
            numberArr[i] = datalist.get(i).get("number");
        }
        String[] mostT = ContactsUtil.mostTouchNumber(numberArr);
        Log.i("MainAc mostTouch:",mostT[0]+","+mostT[1]);
        String name = ContactsUtil.getName(this,mostT[0]);
        Log.i("MainAc name:",name);
        //清空会话框
        conDialog = null;
        //拨打电话对话框
        String msg = "您与他联系的最频繁-> ";
        contactDialog(msg,name,mostT[0]);
    }

    /**
     * 拨打电话
     * @param name
     * @param pNumber
     */
    public void contactDialog(String msg ,String name, String pNumber){
        if (conDialog == null){
            conDialog = new AlertDialog.Builder(this)
                    .setMessage(msg + name+" : "+ pNumber)
                    .setPositiveButton("打给他",(dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + pNumber);
                        intent.setData(data);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消",(dialog, which) -> {
                        conDialog.cancel();
                    }).create();
        }
        conDialog.show();
    }

    /**
     * 展示listview
     * @param datalist
     */
    public void viewCk(List<Map<String,String>> datalist){
        //datalist.forEach(System.out::println);
        ListView lv = findViewById(R.id.contents_item_five);
        MyAdapter adapter = new MyAdapter(this,datalist);
        lv.setAdapter(adapter);
    }

    /**
     * 抽象SimpleAdapter
     * 废弃
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
            Manifest.permission.WRITE_CALL_LOG,
            //获取联系人权限，没有也可
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            //拨打电话权限
            Manifest.permission.CALL_PHONE
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
