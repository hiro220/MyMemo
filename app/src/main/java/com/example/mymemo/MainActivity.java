package com.example.mymemo;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements DeleteCheckDialogFlagment.DeleteCheckDialogListener {

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
        // 編集していたデータの更新
        if (id != "") {
            helper = new MemoHelper(this);
            ListItem item = helper.getOneItem(id);
            adapter.update(position, item);
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

    private void setFirstAdapter() {
        // ヘルパーの準備
        helper = new MemoHelper(this);
        // データベースから全てのメモを取得
        final ArrayList<ListItem> items = helper.getAllItem();
        // アダプターの作成
        adapter = new MyListAdapter(this, items, R.layout.item_list);
        // リストビューにセット
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

        // 長押しでメモを削除(確認ダイアログの表示)
        list.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // ダイアログ
                        DialogFragment dialog = new DeleteCheckDialogFlagment();
                        // ダイアログに渡すパラメータ設定
                        Bundle args = new Bundle();
                        args.putInt("position", i);
                        dialog.setArguments(args);
                        // ダイアログの表示
                        dialog.show(getSupportFragmentManager(), "dialog_button");

                        // クリックの処理は発生させない
                        return true;
                    }
                }
        );
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
        ListItem item = helper.getOneItem(uuid);
        adapter.add(item);
        startIntent(uuid);
    }

    private void deleteData(int i){
        // データベースから指定のuuidのデータを削除
        String data_id = adapter.getUUID(i);
        Log.i(TAG, "データの削除: "+data_id);
        helper = new MemoHelper(this);
        helper.deleteMemo(data_id);
        // adapterから削除
        adapter.remove(i);
    }

    private void startIntent(String data_id){
        // インテントの作成
        Log.i(TAG, "インテントのスタート");
        Intent intent = new Intent(this, com.example.mymemo.MemoActivity.class);
        intent.putExtra("id", data_id);
        // メモのアクティビティを開始
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int i) {
        // ダイアログのPositiveButton(「はい」)クリック時処理
        // ダイアログ表示時に長押ししたメモのadapterでの添字を引数で受け取っている
        Log.i(TAG, "onDialogPositiveClick: 添字i = "+String.valueOf(i));
        deleteData(i);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int i) {
        // ダイアログのNegativeButton(「いいえ」)クリック時処理
    }
}
