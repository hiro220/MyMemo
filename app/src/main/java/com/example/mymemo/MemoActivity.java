package com.example.mymemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoActivity extends AppCompatActivity {

    private static String TAG = "MemoActivity";
    private MemoHelper helper = null;   // データベース操作オブジェクト
    private String id;             // メモのid

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.memo_layout);

        // intentからデータベースidを取得
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");

        // ヘルパーの準備
        helper = new MemoHelper(this);
        // データベースから指定のメモを取得
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            String[] args = {id};
            Cursor c = (Cursor) db.rawQuery("SELECT title, body FROM MEMO_TABLE WHERE uuid=?", args);

            // データベースから取得したタイトル、テキストをEditTextにセット
            c.moveToFirst();
            EditText field = findViewById(R.id.textfield);
            EditText title_field = findViewById(R.id.titlefield);
            String title = c.getString(0);
            String text = c.getString(1);
            title_field.setText(title);
            field.setText(text);
        }
    }

    @Override
    protected void onPause() {
        // サブアクティビティ終了時
        // データベースに保存
        EditText field = findViewById(R.id.textfield);
        EditText title_field = findViewById(R.id.titlefield);

        Log.i(TAG, "データの保存: "+id);

        String text = field.getText().toString();
        String title = title_field.getText().toString();
        // 現在時刻の取得
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String now = sdf.format(date);
        helper.saveData(id, title, text, now);

        Log.i(TAG, "アクティビティの終了");
        super.onPause();
    }


}
