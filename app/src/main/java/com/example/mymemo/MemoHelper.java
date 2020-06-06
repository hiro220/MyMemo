package com.example.mymemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

public class MemoHelper extends SQLiteOpenHelper {

    private static String TAG = "MemoHelper";
    static final private String DBName = "Memo_DB";         // データベースの名前
    static final private int VERSION = 3;                   // データベースのバージョン

    static final private String TABLE_Name = "MEMO_TABLE";  // テーブルの名前

    public MemoHelper(Context context){
        super(context, DBName, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        // CREATE TABLE
        db.execSQL("CREATE TABLE MEMO_TABLE (" +
                "uuid TEXT PRIMARY KEY, " +
                "title TEXT, " +
                "body TEXT, " +
                "date TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // DROP TABLE
        db.execSQL("DROP TABLE IF EXISTS MEMO_TABLE");
        onCreate(db);
    }

    /* 以下、他クラスから呼び出すメソッド */

    public void createDate(String id) {
        // 新しくメモをデータベースに保存
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("uuid", id);
            cv.put("title", "名前のないメモ");
            cv.put("body", "");
            cv.put("date", "0000/00/00");
            db.insert(TABLE_Name, null, cv);

            Log.i(TAG, "新しいデータの登録: "+id);
        }
    }

    public void saveData(String id, String title, String text, String date) {
        // データを更新
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("title", title);
            cv.put("body", text);
            cv.put("date", date);
            String[] args = {id};
            db.update(TABLE_Name, cv, "uuid=?", args);

            Log.i(TAG, "データの更新: "+id);
        }
    }

    public void deleteMemo(String uuid){
        // データを削除
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String[] args = {uuid};
            db.delete(TABLE_Name, "uuid=?", args);

            Log.i(TAG, "データの削除: "+uuid);
        }
    }
}
