package com.example.addressbook;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,String>> dalist;

    public MyAdapter(Context context, List<Map<String, String>> dalist) {
        this.context = context;
        this.dalist = dalist;
    }

    @Override
    public int getCount() {
        return dalist.size();
    }

    @Override
    public Object getItem(int position) {
        return dalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 自定义布局样式
     */
    class ContactHolder{
        private TextView tvName,tvNumber,tvDate,tvType,tvDuration,newFive;
    }
    private ContactHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_items,parent,false);
            holder = new ContactHolder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvNumber = convertView.findViewById(R.id.tv_number);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.tvType = convertView.findViewById(R.id.tv_type);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            holder.newFive = convertView.findViewById(R.id.new_five);
            convertView.setTag(holder);
        }else {
            holder = (ContactHolder) convertView.getTag();
        }
        holder.tvName.setText(dalist.get(position).get("name"));
        holder.tvNumber.setText(dalist.get(position).get("number"));
        holder.tvDate.setText(dalist.get(position).get("date"));
        holder.tvType.setText(dalist.get(position).get("type"));

        if (TextUtils.equals((dalist.get(position).get("type")+""),"未接")){
            holder.tvDuration.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvDuration.setText("未接");
        }else if (TextUtils.equals((dalist.get(position).get("type")+""),"呼出") ||
                TextUtils.equals((dalist.get(position).get("type")+""),"呼入")){
            int time = Integer.parseInt(dalist.get(position).get("duration"));
            if (time>60){
                holder.tvDuration.setText((time/60)+"分钟");
            }else {
                holder.tvDuration.setText(time+"秒");
            }
        }

        if (position<5){
            holder.newFive.setText("最近联系");
            holder.tvName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        return convertView;
    }
}
