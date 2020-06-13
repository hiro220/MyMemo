package com.example.mymemo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> data;
    private int resource;

    // コンストラクタ(コンテキスト、データソース、レイアウトファイル)
    MyListAdapter(Context context, ArrayList<ListItem> data, int resource) {
        this.context = context;
        this.data = data;
        this.resource = resource;
    }

    // データ項目のサイズを取得
    @Override
    public int getCount() {
        return data.size();
    }

    // 設定された項目を取得
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    // 設定された項目のidを取得
    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    // リスト項目を表示するためのViewを取得
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) context;
        ListItem item = (ListItem) getItem(position);
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(resource, null);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(item.getTitle());
        String[] date = item.getDate();
        ((TextView) convertView.findViewById(R.id.date1)).setText(date[0]);
        ((TextView) convertView.findViewById(R.id.date2)).setText(date[1]);
        ((TextView) convertView.findViewById(R.id.date3)).setText(date[2]);

        return convertView;
    }

    // 設定された項目のuuidを取得
    public String getUUID(int position) {
        return data.get(position).getUuid();
    }

    // 指定の要素を除去
    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();     // これを呼ばないと画面更新がされない
    }

    // 要素を追加
    public void add(ListItem item) {
        data.add(item);
    }

    // 要素を更新
    public void update(int position, ListItem data) {
        ListItem item = (ListItem) getItem(position);
        item.setTitle(data.getTitle());
        item.setDate(data.getDate());
        notifyDataSetChanged();
    }
}
