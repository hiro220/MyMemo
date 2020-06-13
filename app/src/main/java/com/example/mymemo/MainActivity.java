package com.example.mymemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private MemoHelper helper = null;       // メモのデータベース操作オブジェクト
    private String id = "";                 // 編集中のメモid
    private int position;
    private MyListAdapter adapter;          // 自作adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // addボタン(メモの新規作成)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewMemo();
            }
        });

        setFirstAdapter();

        Log.i(TAG, "onCreate()");

    }

    @Override
    protected void onResume() {
        // アクティビティのスタート処理
        // データの取得
        Log.i(TAG, "onResume()");
        Log.i(TAG, "編集していたメモ: "+id);
        helper = new MemoHelper(this);
        Cursor c;
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            // データベースから全てのメモを取得
            String[] args = {id};
            c = db.rawQuery("SELECT title, date FROM MEMO_TABLE WHERE uuid=?", args);
            boolean eog = c.moveToFirst();
            if (eog) {
                String title = c.getString(0);
                String date = c.getString(1);
                Log.i(TAG, "アップデート:"+id+title+date);
                adapter.update(position, id, c.getString(0), c.getString(1));
            }

        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 以下、オリジナルのメソッド */

    private ListItem setListItemParam(String uuid, String title, String date) {
        // adapterにセットするItemを作成
        ListItem item = new ListItem();
        item.setUuid(uuid);
        item.setTitle(title);
        item.setDate(date);
        return item;
    }

    private void setFirstAdapter() {
        Cursor c;
        // ヘルパーの準備
        helper = new MemoHelper(this);
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            // データベースから全てのメモを取得
            c = db.rawQuery("SELECT uuid, title FROM MEMO_TABLE", null);
            // 一つ一つリストに追加する
            final ArrayList<ListItem> data = new ArrayList<>();
            boolean eog = c.moveToFirst();
            while (eog) {
                Cursor c_date = db.rawQuery("SELECT date FROM DATE_TABLE WHERE uuid=" + c.getString(0),
                                            null);
                ListItem item = setListItemParam(c.getString(0),
                                                 c.getString(1),
                                                 c_date.getString(0));
                data.add(item);
                eog = c.moveToNext();
            }
            // リストビューにセット
            adapter = new MyListAdapter(this, data, R.layout.item_list);
            ListView list = findViewById(R.id.list);
            list.setAdapter(adapter);
            // リストのアイテムを選択したとき
            list.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            id = adapter.getUUID(i);
                            position = i;
                            startIntent(id);
                        }
                    }
            );

            // 長押しでメモを削除
            list.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            deleteData(adapter.getUUID(i));
                            adapter.remove(i);
                            return true;    // クリックの処理は発生させない
                        }
                    }
            );
        }
    }

    private void createNewMemo(){
        // 新しいidの作成
        String uuid = UUID.randomUUID().toString();
        Log.i(TAG, "新しいメモの作成: "+uuid);
        // 編集するメモのuuidを保持
        id = uuid;
        // 新規メモをデータベースに格納
        position = adapter.getCount();
        helper = new MemoHelper(this);
        helper.createData(uuid);
        // 新規メモをadapterに追加
        Cursor c;
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            // データベースから作成したメモを取得
            String[] args = {uuid};
            c = db.rawQuery("SELECT title FROM MEMO_TABLE WHERE uuid=?", args);
            c.moveToFirst();
            String title = c.getString(0);
        }
        ListItem item = setListItemParam(id, c.getString(0), "null");
        adapter.add(item);
        startIntent(uuid);
    }

    private void deleteData(String data_id){
        // データベースから指定のuuidのデータを削除
        Log.i(TAG, "データの削除: "+data_id);
        helper = new MemoHelper(this);
        helper.deleteMemo(data_id);
    }

    private void startIntent(String data_id){
        // インテントの作成
        Log.i(TAG, "インテントのスタート");
        Intent intent = new Intent(this, com.example.mymemo.MemoActivity.class);
        intent.putExtra("id", data_id);
        // メモのアクティビティを開始
        startActivity(intent);
    }
}
